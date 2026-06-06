<template>
	<view class="page">
		<!-- 自定义导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">院校详情</text>
		</view>
		
		<scroll-view class="content" scroll-y="true">
			<view v-if="loading" class="loading-container">
				<text class="loading-text">加载中...</text>
			</view>
			
			<view v-else-if="schoolDetail">
				<!-- 学校头部卡片 -->
				<view class="header-card">
					<view class="school-header">
						<image 
							class="school-logo" 
							:src="schoolDetail.school.logo || '/static/logo.png'" 
							mode="aspectFill"
						/>
						<view class="follow-btn" @click="handleFollow">
							<text class="follow-icon">{{ isFollowed ? '✓' : '+' }}</text>
							<text class="follow-text">{{ isFollowed ? '已关注' : '关注' }}</text>
						</view>
					</view>
					
					<text class="school-name">{{ schoolDetail.school.schoolName }}</text>
					
					<view class="school-meta">
						<view class="meta-item">
							<text class="meta-icon">📍</text>
							<text class="meta-text">{{ schoolDetail.school.location }}</text>
						</view>
						<text class="meta-divider">·</text>
						<text class="meta-text">院校属层：{{ schoolDetail.school.subordinate }}</text>
					</view>
					
					<view class="school-tags">
						<view v-if="schoolDetail.is985" class="tag">985</view>
						<view v-if="schoolDetail.is211" class="tag">211</view>
						<view v-if="schoolDetail.isSyl" class="tag">双一流</view>
						<view v-if="schoolDetail.isZhx" class="tag">自划线</view>
					</view>
				</view>
				
				<!-- 功能图标 -->
				<view class="function-icons">
					<view class="icon-item" @click="goToMajorList">
						<view class="icon-bg blue">
							<text class="icon-text">📚</text>
						</view>
						<text class="icon-label">专业目录</text>
					</view>
					<view class="icon-item" @click="showTip('招生简章')">
						<view class="icon-bg yellow">
							<text class="icon-text">📋</text>
						</view>
						<text class="icon-label">招生简章</text>
					</view>
					<view class="icon-item" @click="showTip('分数线')">
						<view class="icon-bg blue2">
							<text class="icon-text">📊</text>
						</view>
						<text class="icon-label">分数线</text>
					</view>
					<view class="icon-item" @click="showTip('招生简章')">
						<view class="icon-bg blue3">
							<text class="icon-text">📄</text>
						</view>
						<text class="icon-label">招生简章</text>
					</view>
					<view class="icon-item" @click="showTip('1v1择校')">
						<view class="icon-bg orange">
							<text class="icon-text">🎓</text>
						</view>
						<text class="icon-label">1v1择校</text>
					</view>
				</view>
				
				<!-- 院校概况 -->
				<view class="info-card">
					<view class="card-header" @click="toggleOverview">
						<text class="card-title">院校概况</text>
						<text class="expand-icon">{{ showOverview ? '︿' : '﹀' }}</text>
					</view>
					<view v-if="showOverview" class="card-content">
						<text class="overview-text">{{ schoolDetail.school.introduction || '暂无院校概况信息' }}</text>
						
						<view class="info-row">
							<text class="info-label">邮编</text>
							<text class="info-value">{{ schoolDetail.school.postcode || '--' }}</text>
						</view>
						<view class="info-row">
							<text class="info-label">邮箱</text>
							<text class="info-value">{{ schoolDetail.school.email || '--' }}</text>
						</view>
						<view class="info-row">
							<text class="info-label">地址</text>
							<text class="info-value">{{ schoolDetail.school.address || '--' }}</text>
						</view>
						
						<view class="action-buttons">
							<view class="action-btn" @click="callPhone">
								<text class="btn-icon">📞</text>
								<text class="btn-text">联系电话</text>
							</view>
							<view class="action-btn" @click="openWebsite">
								<text class="btn-icon">🔗</text>
								<text class="btn-text">研招办网址</text>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 优势学科 -->
				<view class="info-card">
					<view class="card-header">
						<text class="card-title">优势学科</text>
						<text class="help-icon">?</text>
					</view>
					<view class="card-content">
						<view class="discipline-header">
							<text class="discipline-col">学科名称</text>
							<text class="discipline-col">评估</text>
							<text class="discipline-col">博士点</text>
						</view>
						<view v-for="(item, index) in advantageDisciplines" :key="index" class="discipline-row">
							<text class="discipline-name">{{ item.name }}</text>
							<text class="discipline-rating">{{ item.rating }}</text>
							<text class="discipline-doctor">{{ item.hasDoctor ? '有' : '--' }}</text>
							<text class="arrow-icon">›</text>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const schoolId = ref(null)
const schoolName = ref('')
const schoolDetail = ref(null)
const loading = ref(true)
const isFollowed = ref(false)
const showOverview = ref(true)

// 模拟优势学科数据
const advantageDisciplines = ref([
	{ name: '政治学', rating: 'A+', hasDoctor: true }
])

onMounted(() => {
	const pages = getCurrentPages()
	const currentPage = pages[pages.length - 1]
	const options = currentPage.options
	
	schoolId.value = options.id
	schoolName.value = decodeURIComponent(options.name || '')
	
	loadSchoolDetail()
})

const loadSchoolDetail = async () => {
	loading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl(`/school/${schoolId.value}`),
			method: 'GET'
		})
		
		if (res.data.code === 200) {
			schoolDetail.value = res.data.data
		}
	} catch (error) {
		console.error('加载学校详情失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		loading.value = false
	}
}

const handleFollow = () => {
	isFollowed.value = !isFollowed.value
	uni.showToast({
		title: isFollowed.value ? '关注成功' : '取消关注',
		icon: 'success'
	})
}

const toggleOverview = () => {
	showOverview.value = !showOverview.value
}

const goToMajorList = () => {
	uni.setStorageSync('searchSchoolName', schoolName.value)
	uni.switchTab({
		url: '/pages/major/search'
	})
}

const showTip = (name) => {
	uni.showToast({
		title: `${name}功能开发中`,
		icon: 'none'
	})
}

const callPhone = () => {
	if (schoolDetail.value && schoolDetail.value.school.phone) {
		uni.makePhoneCall({
			phoneNumber: schoolDetail.value.school.phone
		})
	} else {
		uni.showToast({
			title: '暂无联系电话',
			icon: 'none'
		})
	}
}

const openWebsite = () => {
	if (schoolDetail.value && schoolDetail.value.school.link) {
		uni.showToast({
			title: '即将打开网页',
			icon: 'none'
		})
	} else {
		uni.showToast({
			title: '暂无网址',
			icon: 'none'
		})
	}
}

const goBack = () => {
	uni.navigateBack()
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f5f6fa;
}

/* 导航栏 */
.navbar {
	background: #ffffff;
	padding: 30rpx 30rpx;
	padding-top: calc(30rpx + env(safe-area-inset-top));
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.nav-back {
	position: absolute;
	left: 30rpx;
	top: 50%;
	transform: translateY(-50%);
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	font-size: 56rpx;
	color: #333;
	font-weight: 300;
	line-height: 1;
}

.nav-title {
	font-size: 36rpx;
	font-weight: 700;
	color: #333;
}

/* 内容区 */
.content {
	height: calc(100vh - 88rpx - env(safe-area-inset-top));
	padding: 20rpx 30rpx 40rpx;
	box-sizing: border-box;
}

.loading-container {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 120rpx 0;
}

.loading-text {
	font-size: 28rpx;
	color: #999;
}

/* 头部卡片 */
.header-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 40rpx 32rpx;
	margin-bottom: 20rpx;
}

.school-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 24rpx;
}

.school-logo {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
}

.follow-btn {
	background: #1677ff;
	border-radius: 40rpx;
	padding: 16rpx 32rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.follow-icon {
	font-size: 28rpx;
	color: #ffffff;
	font-weight: 600;
}

.follow-text {
	font-size: 28rpx;
	color: #ffffff;
	font-weight: 500;
}

.school-name {
	font-size: 40rpx;
	font-weight: 700;
	color: #333;
	margin-bottom: 16rpx;
	display: block;
}

.school-meta {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
}

.meta-item {
	display: flex;
	align-items: center;
	gap: 6rpx;
}

.meta-icon {
	font-size: 24rpx;
}

.meta-text {
	font-size: 26rpx;
	color: #666;
}

.meta-divider {
	margin: 0 12rpx;
	color: #ddd;
}

.school-tags {
	display: flex;
	gap: 16rpx;
}

.tag {
	background: #1677ff;
	color: #ffffff;
	font-size: 24rpx;
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
}

/* 功能图标 */
.function-icons {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 32rpx 20rpx;
	margin-bottom: 20rpx;
	display: flex;
	justify-content: space-around;
}

.icon-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12rpx;
}

.icon-bg {
	width: 88rpx;
	height: 88rpx;
	border-radius: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.icon-bg.blue {
	background: #e6f4ff;
}

.icon-bg.yellow {
	background: #fff7e6;
}

.icon-bg.blue2 {
	background: #e6f7ff;
}

.icon-bg.blue3 {
	background: #f0f5ff;
}

.icon-bg.orange {
	background: #fff2e8;
}

.icon-text {
	font-size: 40rpx;
}

.icon-label {
	font-size: 24rpx;
	color: #666;
}

/* 信息卡片 */
.info-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 32rpx;
	margin-bottom: 20rpx;
}

.card-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 24rpx;
}

.card-title {
	font-size: 32rpx;
	font-weight: 700;
	color: #333;
}

.expand-icon {
	font-size: 24rpx;
	color: #999;
}

.help-icon {
	width: 32rpx;
	height: 32rpx;
	border-radius: 50%;
	border: 2rpx solid #999;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 20rpx;
	color: #999;
}

.card-content {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.overview-text {
	font-size: 28rpx;
	color: #666;
	line-height: 1.8;
	margin-bottom: 16rpx;
}

.info-row {
	display: flex;
	align-items: center;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.info-label {
	font-size: 28rpx;
	color: #999;
	width: 120rpx;
}

.info-value {
	font-size: 28rpx;
	color: #333;
	flex: 1;
}

.action-buttons {
	display: flex;
	gap: 20rpx;
	margin-top: 16rpx;
}

.action-btn {
	flex: 1;
	background: #f5f6fa;
	border-radius: 12rpx;
	padding: 24rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 12rpx;
}

.btn-icon {
	font-size: 32rpx;
}

.btn-text {
	font-size: 26rpx;
	color: #666;
}

/* 优势学科 */
.discipline-header {
	display: flex;
	padding: 16rpx 0;
	border-bottom: 2rpx solid #f0f0f0;
}

.discipline-col {
	font-size: 26rpx;
	color: #999;
	text-align: center;
}

.discipline-col:nth-child(1) {
	flex: 2;
	text-align: left;
}

.discipline-col:nth-child(2),
.discipline-col:nth-child(3) {
	flex: 1;
}

.discipline-row {
	display: flex;
	align-items: center;
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.discipline-name {
	flex: 2;
	font-size: 28rpx;
	color: #333;
}

.discipline-rating {
	flex: 1;
	font-size: 28rpx;
	color: #ff6b6b;
	font-weight: 600;
	text-align: center;
}

.discipline-doctor {
	flex: 1;
	font-size: 28rpx;
	color: #666;
	text-align: center;
}

.arrow-icon {
	font-size: 32rpx;
	color: #ccc;
	margin-left: 16rpx;
}
</style>
