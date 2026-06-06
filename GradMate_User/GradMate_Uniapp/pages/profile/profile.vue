<template>
	<view class="page">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 蓝色顶部区域 -->
		<view class="header-section">
			<text class="page-title">个人中心</text>
		</view>
		
		<!-- 白色内容区（带圆角） -->
		<view class="content-section">
			<!-- 未登录状态 -->
			<view v-if="!isLogin" class="not-login">
				<view class="login-icon"></view>
				<text class="login-tip">请先登录</text>
				<button class="login-btn" @click="goLogin">立即登录</button>
			</view>

			<!-- 已登录状态 -->
			<view v-else class="profile-content">
				<!-- 用户信息卡片 -->
				<view class="user-card">
					<image class="avatar" src="/static/logo.png" mode="aspectFill"></image>
					<view class="user-info">
						<text class="nickname">{{ userInfo.nickname || '未设置昵称' }}</text>
						<text class="phone">{{ userInfo.phone || '未绑定手机号' }}</text>
					</view>
					<view class="edit-btn" @click="goToEdit">
						<view class="edit-icon"></view>
					</view>
				</view>

				<!-- 目标设定 -->
				<view class="section">
					<view class="section-title">我的目标</view>
					<view class="target-card">
						<!-- 目标院校 -->
						<view v-if="targetDetail.targetSchool" class="target-item-enhanced">
							<view class="target-header">
								<view class="target-icon school-target-icon"></view>
								<text class="target-label">目标院校</text>
							</view>
							<text class="target-value-large">{{ targetDetail.targetSchool.name }}</text>
							<button class="detail-mini-btn" @click="goToSchoolDetail">查看详情</button>
						</view>
						<view v-else class="target-item">
							<view class="target-icon school-target-icon"></view>
							<text class="target-label">目标院校：</text>
							<text class="target-value">{{ userInfo.targetSchool || '未设置' }}</text>
						</view>
						
						<!-- 目标专业 -->
						<view v-if="targetDetail.targetMajor" class="target-item-enhanced">
							<view class="target-header">
								<view class="target-icon major-target-icon"></view>
								<text class="target-label">目标专业</text>
							</view>
							<text class="target-value-large">{{ targetDetail.targetMajor.name }}</text>
							<button class="detail-mini-btn" @click="goToMajorDetail">查看详情</button>
						</view>
						<view v-else class="target-item">
							<view class="target-icon major-target-icon"></view>
							<text class="target-label">目标专业：</text>
							<text class="target-value">{{ userInfo.targetMajor || '未设置' }}</text>
						</view>
						
						<button class="set-target-btn" @click="goToTargetPage">{{ (targetDetail.targetSchool || targetDetail.targetMajor) ? '重新设置目标' : '设置目标' }}</button>
					</view>
				</view>
				
				<!-- 数据统计 -->
				<view class="section">
					<view class="section-title">我的数据</view>
					<view class="stats-grid">
						<view class="stat-item stat-blue">
							<view class="stat-icon study-icon"></view>
							<text class="stat-value">{{ stats.totalStudyDuration || 0 }}</text>
							<text class="stat-label">学习时长(分钟)</text>
						</view>
						<view class="stat-item stat-orange">
							<view class="stat-icon checkin-icon"></view>
							<text class="stat-value">{{ stats.continuousDays || 0 }}</text>
							<text class="stat-label">连续打卡(天)</text>
						</view>
						<view class="stat-item stat-purple">
							<view class="stat-icon favorite-icon"></view>
							<text class="stat-value">{{ stats.totalFavorites || 0 }}</text>
							<text class="stat-label">收藏数量</text>
						</view>
						<view class="stat-item stat-green">
							<view class="stat-icon task-icon"></view>
							<text class="stat-value">{{ stats.completedTasks || 0 }}</text>
							<text class="stat-label">完成任务</text>
						</view>
					</view>
				</view>

				<!-- 功能入口 -->
				<view class="section">
					<view class="section-title">功能入口</view>
					<view class="menu-list">
						<view class="menu-item" @click="goToFavorite">
							<view class="menu-left">
								<view class="menu-icon favorite-menu-icon"></view>
								<text class="menu-text">我的收藏</text>
							</view>
							<view class="menu-right">
								<text class="menu-count">{{ favoriteCount }}</text>
								<text class="menu-arrow">›</text>
							</view>
						</view>
						<view class="menu-item" @click="goToStudy">
							<view class="menu-left">
								<view class="menu-icon study-menu-icon"></view>
								<text class="menu-text">学习统计</text>
							</view>
							<text class="menu-arrow">›</text>
						</view>
						<view class="menu-item" @click="handleLogout">
							<view class="menu-left">
								<view class="menu-icon logout-menu-icon"></view>
								<text class="menu-text logout-text">退出登录</text>
							</view>
							<text class="menu-arrow">›</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { put, get } from '@/utils/request.js'

const statusBarHeight = ref(0)
const isLogin = ref(false)
const userInfo = ref({})
const favoriteCount = ref(0)
const stats = ref({
	totalStudyDuration: 0,
	continuousDays: 0,
	totalFavorites: 0,
	completedTasks: 0
})
const targetDetail = ref({
	targetSchool: null,
	targetMajor: null
})

onMounted(() => {
	const systemInfo = uni.getSystemInfoSync()
	statusBarHeight.value = systemInfo.statusBarHeight || 0
	checkLoginStatus()
	if (isLogin.value) {
		loadFavoriteCount()
		loadUserStats()
		loadTargetDetail()
	}
})

// 检查登录状态
const checkLoginStatus = () => {
	const loginStatus = uni.getStorageSync('isLogin')
	const userData = uni.getStorageSync('userInfo')
	
	isLogin.value = !!loginStatus
	if (loginStatus && userData) {
		userInfo.value = userData
	}
}

// 加载收藏数量
const loadFavoriteCount = async () => {
	try {
		const res = await get(`/favorite/count?userId=${userInfo.value.id}`)
		if (res.code === 200) {
			favoriteCount.value = res.data.total || 0
			stats.value.totalFavorites = res.data.total || 0
		}
	} catch (error) {
		console.error('加载收藏数量失败:', error)
	}
}

// 加载用户统计数据
const loadUserStats = async () => {
	try {
		const res = await get(`/study/stats?userId=${userInfo.value.id}`)
		if (res.code === 200) {
			stats.value = {
				...stats.value,
				...res.data
			}
		}
	} catch (error) {
		console.error('加载统计数据失败:', error)
	}
}

// 加载目标详细信息
const loadTargetDetail = async () => {
	try {
		const res = await get(`/user/target/${userInfo.value.id}`)
		if (res.code === 200 && res.data) {
			targetDetail.value = {
				targetSchool: res.data.targetSchool ? {
					id: res.data.targetSchoolId,
					name: res.data.targetSchool
				} : null,
				targetMajor: res.data.targetMajor ? {
					id: res.data.targetMajorId,
					name: res.data.targetMajor
				} : null
			}
		}
	} catch (error) {
		console.error('加载目标详情失败:', error)
	}
}

// 跳转登录
const goLogin = () => {
	uni.navigateTo({
		url: '/pages/login/login'
	})
}

// 跳转到编辑页面
const goToEdit = () => {
	uni.navigateTo({
		url: '/pages/profile/edit'
	})
}

// 跳转到收藏列表
const goToFavorite = () => {
	uni.navigateTo({
		url: '/pages/profile/favorite'
	})
}

// 跳转到目标设定页面
const goToTargetPage = () => {
	uni.navigateTo({
		url: '/pages/profile/target'
	})
}

// 跳转到学习统计
const goToStudy = () => {
	uni.navigateTo({
		url: '/pages/study/stats'
	})
}

// 跳转到院校详情
const goToSchoolDetail = () => {
	if (targetDetail.value.targetSchool && targetDetail.value.targetSchool.id) {
		uni.navigateTo({
			url: `/pages/school/detail?id=${targetDetail.value.targetSchool.id}`
		})
	}
}

// 跳转到专业详情
const goToMajorDetail = () => {
	if (targetDetail.value.targetMajor && targetDetail.value.targetMajor.id) {
		uni.navigateTo({
			url: `/pages/major/detail?id=${targetDetail.value.targetMajor.id}`
		})
	}
}

// 退出登录
const handleLogout = () => {
	uni.showModal({
		title: '提示',
		content: '确定要退出登录吗？',
		success: (res) => {
			if (res.confirm) {
				uni.removeStorageSync('userInfo')
				uni.removeStorageSync('isLogin')
				isLogin.value = false
				userInfo.value = {}
				uni.showToast({
					title: '已退出登录',
					icon: 'success'
				})
			}
		}
	})
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #006cff;
}

.status-bar {
	background: #006cff;
}

/* 蓝色顶部区域 */
.header-section {
	background: #006cff;
	padding: 20rpx 30rpx 80rpx;
}

.page-title {
	display: block;
	font-size: 56rpx;
	font-weight: bold;
	color: #ffffff;
}

/* 白色内容区 */
.content-section {
	background: linear-gradient(180deg, #e8f3ff 0%, #f6f7fb 15%, #f6f7fb 100%);
	border-radius: 32rpx 32rpx 0 0;
	margin-top: -32rpx;
	padding: 24rpx 30rpx 40rpx;
	min-height: calc(100vh - 200rpx);
}

/* 未登录状态 */
.not-login {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 40rpx;
}

.login-icon {
	width: 120rpx;
	height: 120rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 60rpx;
	margin-bottom: 40rpx;
	position: relative;
}

.login-icon::before {
	content: '';
	position: absolute;
	width: 50rpx;
	height: 50rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	top: 20rpx;
	left: 35rpx;
}

.login-icon::after {
	content: '';
	position: absolute;
	width: 70rpx;
	height: 35rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 0 0 35rpx 35rpx;
	border-top: none;
	bottom: 15rpx;
	left: 25rpx;
}

.login-tip {
	font-size: 32rpx;
	color: #999;
	margin-bottom: 60rpx;
}

.login-btn {
	width: 300rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	color: #ffffff;
	border: none;
	border-radius: 40rpx;
	font-size: 28rpx;
	line-height: 80rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 107, 255, 0.3);
}

/* 已登录状态 */
.profile-content {
	padding-bottom: 40rpx;
}

.user-card {
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	padding: 32rpx;
	display: flex;
	align-items: center;
	margin-bottom: 24rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	margin-right: 24rpx;
	border: 4rpx solid rgba(0, 107, 255, 0.1);
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.15);
}

.user-info {
	flex: 1;
}

.nickname {
	display: block;
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.phone {
	display: block;
	font-size: 24rpx;
	color: #666;
}

.edit-btn {
	width: 64rpx;
	height: 64rpx;
	background: linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%);
	border-radius: 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 2rpx 8rpx rgba(0, 107, 255, 0.1);
}

.edit-icon {
	width: 32rpx;
	height: 32rpx;
	position: relative;
}

.edit-icon::before {
	content: '';
	position: absolute;
	width: 20rpx;
	height: 20rpx;
	border: 2rpx solid #006cff;
	border-radius: 4rpx;
	bottom: 0;
	left: 6rpx;
}

.edit-icon::after {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-left: 8rpx solid transparent;
	border-right: 8rpx solid transparent;
	border-bottom: 12rpx solid #006cff;
	top: 0;
	right: 0;
	transform: rotate(45deg);
}

/* 功能区块 */
.section {
	margin-bottom: 24rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 16rpx;
	padding-left: 4rpx;
}

.target-card {
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	padding: 28rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

.target-item {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
	padding: 16rpx;
	background: linear-gradient(135deg, #f0f8ff 0%, #e8f3ff 100%);
	border-radius: 12rpx;
	border: 1rpx solid rgba(0, 107, 255, 0.08);
}

.target-item-enhanced {
	background: linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%);
	border-radius: 12rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	border: 2rpx solid rgba(0, 107, 255, 0.15);
	box-shadow: 0 2rpx 8rpx rgba(0, 107, 255, 0.08);
}

.target-header {
	display: flex;
	align-items: center;
	margin-bottom: 12rpx;
	gap: 8rpx;
}

.target-icon {
	width: 32rpx;
	height: 32rpx;
	border-radius: 8rpx;
	flex-shrink: 0;
	position: relative;
}

.school-target-icon {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}

.school-target-icon::before {
	content: '';
	position: absolute;
	width: 16rpx;
	height: 12rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 6rpx;
	left: 8rpx;
	border-radius: 2rpx;
}

.school-target-icon::after {
	content: '';
	position: absolute;
	width: 20rpx;
	height: 2rpx;
	background: rgba(255, 255, 255, 0.9);
	bottom: 6rpx;
	left: 6rpx;
	border-radius: 1rpx;
}

.major-target-icon {
	background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}

.major-target-icon::before {
	content: '';
	position: absolute;
	width: 18rpx;
	height: 14rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 8rpx;
	left: 7rpx;
	border-radius: 2rpx 2rpx 3rpx 3rpx;
}

.major-target-icon::after {
	content: '';
	position: absolute;
	width: 8rpx;
	height: 4rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 4rpx;
	left: 12rpx;
	border-radius: 2rpx 2rpx 0 0;
}

.target-label {
	font-size: 26rpx;
	color: #666;
	font-weight: 500;
}

.target-value {
	font-size: 26rpx;
	color: #333;
	flex: 1;
}

.target-value-large {
	font-size: 30rpx;
	color: #333;
	font-weight: 600;
	display: block;
	margin-bottom: 12rpx;
}

.detail-mini-btn {
	width: 140rpx;
	height: 50rpx;
	line-height: 50rpx;
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	color: #ffffff;
	border: none;
	border-radius: 25rpx;
	font-size: 22rpx;
	padding: 0;
	margin: 0;
	box-shadow: 0 2rpx 8rpx rgba(0, 107, 255, 0.2);
}

.set-target-btn {
	width: 100%;
	height: 70rpx;
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	color: #ffffff;
	border: none;
	border-radius: 12rpx;
	font-size: 26rpx;
	margin-top: 8rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.25);
}

/* 数据统计样式 */
.stats-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 16rpx;
}

.stat-item {
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	padding: 28rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	position: relative;
	overflow: hidden;
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

.stat-item::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 4rpx;
}

.stat-blue::before {
	background: linear-gradient(90deg, #378cff 0%, #1b7dff 100%);
}

.stat-orange::before {
	background: linear-gradient(90deg, #ffb84d 0%, #ff9500 100%);
}

.stat-purple::before {
	background: linear-gradient(90deg, #a78bfa 0%, #8b5cf6 100%);
}

.stat-green::before {
	background: linear-gradient(90deg, #52c990 0%, #00a870 100%);
}

.stat-icon {
	width: 48rpx;
	height: 48rpx;
	border-radius: 24rpx;
	margin-bottom: 12rpx;
	position: relative;
}

.study-icon {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}

.study-icon::before {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 24rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.study-icon::after {
	content: '';
	position: absolute;
	width: 2rpx;
	height: 10rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 50%;
	left: 50%;
	transform: translate(-50%, -70%);
}

.checkin-icon {
	background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}

.checkin-icon::before {
	content: '';
	position: absolute;
	width: 20rpx;
	height: 20rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 4rpx;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.checkin-icon::after {
	content: '';
	position: absolute;
	width: 10rpx;
	height: 2rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.favorite-icon {
	background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
}

.favorite-icon::before {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-left: 10rpx solid transparent;
	border-right: 10rpx solid transparent;
	border-bottom: 16rpx solid rgba(255, 255, 255, 0.9);
	top: 12rpx;
	left: 14rpx;
}

.favorite-icon::after {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-left: 10rpx solid transparent;
	border-right: 10rpx solid transparent;
	border-top: 16rpx solid rgba(255, 255, 255, 0.9);
	bottom: 12rpx;
	left: 14rpx;
}

.task-icon {
	background: linear-gradient(135deg, #52c990 0%, #00a870 100%);
}

.task-icon::before {
	content: '';
	position: absolute;
	width: 6rpx;
	height: 12rpx;
	border-right: 3rpx solid rgba(255, 255, 255, 0.9);
	border-bottom: 3rpx solid rgba(255, 255, 255, 0.9);
	top: 14rpx;
	left: 18rpx;
	transform: rotate(45deg);
}

.stat-value {
	font-size: 40rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.stat-label {
	font-size: 22rpx;
	color: #666;
	text-align: center;
}

.menu-list {
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

.menu-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 28rpx 24rpx;
	border-bottom: 1rpx solid rgba(0, 107, 255, 0.05);
	transition: background 0.2s ease;
}

.menu-item:active {
	background: rgba(0, 107, 255, 0.03);
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-left {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.menu-icon {
	width: 48rpx;
	height: 48rpx;
	border-radius: 12rpx;
	flex-shrink: 0;
	position: relative;
}

.favorite-menu-icon {
	background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
}

.favorite-menu-icon::before {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-left: 10rpx solid transparent;
	border-right: 10rpx solid transparent;
	border-bottom: 16rpx solid rgba(255, 255, 255, 0.9);
	top: 12rpx;
	left: 14rpx;
}

.favorite-menu-icon::after {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-left: 10rpx solid transparent;
	border-right: 10rpx solid transparent;
	border-top: 16rpx solid rgba(255, 255, 255, 0.9);
	bottom: 12rpx;
	left: 14rpx;
}

.study-menu-icon {
	background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}

.study-menu-icon::before {
	content: '';
	position: absolute;
	width: 18rpx;
	height: 14rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 14rpx;
	left: 15rpx;
	border-radius: 2rpx 2rpx 3rpx 3rpx;
}

.study-menu-icon::after {
	content: '';
	position: absolute;
	width: 8rpx;
	height: 4rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 10rpx;
	left: 20rpx;
	border-radius: 2rpx 2rpx 0 0;
}

.logout-menu-icon {
	background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
}

.logout-menu-icon::before {
	content: '';
	position: absolute;
	width: 16rpx;
	height: 20rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-left: none;
	border-radius: 0 6rpx 6rpx 0;
	top: 50%;
	left: 18rpx;
	transform: translateY(-50%);
}

.logout-menu-icon::after {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-top: 5rpx solid transparent;
	border-bottom: 5rpx solid transparent;
	border-right: 8rpx solid rgba(255, 255, 255, 0.9);
	top: 50%;
	left: 12rpx;
	transform: translateY(-50%);
}

.menu-text {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.menu-right {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.menu-count {
	font-size: 24rpx;
	color: #999;
}

.logout-text {
	color: #ff6b6b;
}

.menu-arrow {
	font-size: 32rpx;
	color: #ccc;
	font-weight: bold;
}
</style>