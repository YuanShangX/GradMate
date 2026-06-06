<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">专业招生详情</text>
		</view>
		
		<scroll-view class="content" scroll-y="true">
			<!-- 学校专业头部卡片 -->
			<view class="header-card">
				<view class="school-info">
					<view class="school-logo-container">
						<image 
							v-if="schoolLogo" 
							class="school-logo" 
							:src="schoolLogo"
							mode="aspectFill"
						/>
						<view v-else class="school-logo-placeholder"></view>
					</view>
					<view class="school-text">
						<text class="school-name">{{ schoolName }}</text>
						<view class="major-info">
							<text class="major-code">{{ majorCode }}</text>
							<text class="major-name">{{ majorName }}</text>
						</view>
					</view>
				</view>
				
				<!-- 收藏按钮 -->
				<view class="action-button" @click="handleFavorite">
					<text class="action-icon">{{ isFavorited ? '★' : '☆' }}</text>
					<text class="action-text">{{ isFavorited ? '已收藏' : '收藏' }}</text>
				</view>
			</view>
			
			<view v-if="loading" class="loading-container">
				<text class="loading-text">加载中...</text>
			</view>
			
			<!-- 招生信息列表 -->
			<view v-if="!loading && majorList.length > 0">
				<view v-for="(item, index) in majorList" :key="index" class="info-card">
					<!-- 研究方向标题 -->
					<view v-if="item.researchDirection" class="direction-header">
						<view class="direction-badge"></view>
						<text class="direction-title">{{ item.researchDirection }}</text>
					</view>
					
					<!-- 基本信息 -->
					<view class="info-section">
						<view class="section-title">
							<view class="title-icon"></view>
							<text class="title-text">基本信息</text>
						</view>
						<view class="info-grid">
							<view class="info-item">
								<text class="info-label">院系所</text>
								<text class="info-value">{{ item.department || '--' }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">学习方式</text>
								<text class="info-value">{{ item.studyMode || '--' }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">考试方式</text>
								<text class="info-value">{{ item.examType || '--' }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">拟招生人数</text>
								<text class="info-value highlight">{{ item.plannedEnrollment || '--' }}</text>
							</view>
						</view>
					</view>
					
					<!-- 考试科目 -->
					<view class="info-section">
						<view class="section-title">
							<view class="title-icon"></view>
							<text class="title-text">考试科目</text>
						</view>
						<view class="subject-container">
							<view v-if="item.examSubject1" class="subject-card">
								<text class="subject-number">①</text>
								<text class="subject-name">{{ item.examSubject1 }}</text>
							</view>
							<view v-if="item.examSubject2" class="subject-card">
								<text class="subject-number">②</text>
								<text class="subject-name">{{ item.examSubject2 }}</text>
							</view>
							<view v-if="item.examSubject3" class="subject-card">
								<text class="subject-number">③</text>
								<text class="subject-name">{{ item.examSubject3 }}</text>
							</view>
							<view v-if="item.examSubject4" class="subject-card">
								<text class="subject-number">④</text>
								<text class="subject-name">{{ item.examSubject4 }}</text>
							</view>
						</view>
					</view>
					
					<!-- 指导教师 -->
					<view v-if="item.advisor" class="info-section">
						<view class="section-title">
							<view class="title-icon"></view>
							<text class="title-text">指导教师</text>
						</view>
						<text class="advisor-text">{{ item.advisor }}</text>
					</view>
				</view>
			</view>
			
			<view v-if="!loading && majorList.length === 0" class="empty-container">
				<text class="empty-icon">📋</text>
				<text class="empty-text">暂无该专业的招生信息</text>
				<text class="empty-hint">请联系学校研招办获取最新信息</text>
			</view>
			
			<!-- 底部提示 -->
			<view v-if="!loading && majorList.length > 0" class="footer-tip">
				<text class="tip-text">以上信息仅供参考，具体以学校官方公布为准</text>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const schoolName = ref('')
const majorCode = ref('')
const majorName = ref('')
const majorList = ref([])
const loading = ref(false)
const isFavorited = ref(false)
const schoolLogo = ref('')
const userId = ref(1) // 临时使用固定用户ID，实际应从登录状态获取
const majorId = ref(null) // 专业ID

onMounted(() => {
	const pages = getCurrentPages()
	const currentPage = pages[pages.length - 1]
	const options = currentPage.options
	
	schoolName.value = decodeURIComponent(options.schoolName || '')
	majorCode.value = options.majorCode || ''
	majorName.value = decodeURIComponent(options.majorName || '')
	
	// 尝试从本地存储获取用户ID
	const userInfo = uni.getStorageSync('userInfo')
	if (userInfo && userInfo.id) {
		userId.value = userInfo.id
	}
	
	loadMajorDetail()
	checkFavoriteStatus()
})

const loadMajorDetail = async () => {
	loading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/major/schoolMajorDetail'),
			method: 'GET',
			data: {
				schoolName: schoolName.value,
				majorCode: majorCode.value
			}
		})
		
		if (res.data.code === 200 && res.data.data) {
			majorList.value = res.data.data
			// 获取学校logo（所有记录的logo相同）
			if (res.data.data.length > 0) {
				schoolLogo.value = res.data.data[0].schoolLogo || ''
			}
		}
	} catch (error) {
		console.error('加载专业详情失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		loading.value = false
	}
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
	try {
		const res = await uni.request({
			url: buildApiUrl('/favorite/checkMajor'),
			method: 'GET',
			data: {
				userId: userId.value,
				schoolName: schoolName.value,
				majorCode: majorCode.value
			}
		})
		
		if (res.data.code === 200) {
			isFavorited.value = res.data.data
		}
	} catch (error) {
		console.error('检查收藏状态失败:', error)
	}
}

const handleFavorite = async () => {
	try {
		if (isFavorited.value) {
			// 取消收藏
			const res = await uni.request({
				url: buildApiUrl('/favorite/removeMajor'),
				method: 'GET',
				data: {
					userId: userId.value,
					schoolName: schoolName.value,
					majorCode: majorCode.value
				}
			})
			
			if (res.data.code === 200) {
				isFavorited.value = false
				uni.showToast({
					title: '取消收藏成功',
					icon: 'success'
				})
			}
		} else {
			// 添加收藏
			const res = await uni.request({
				url: buildApiUrl('/favorite/addMajor'),
				method: 'POST',
				data: {
					userId: userId.value,
					schoolName: schoolName.value,
					majorCode: majorCode.value,
					majorName: majorName.value
				},
				header: {
					'Content-Type': 'application/json'
				}
			})
			
			if (res.data.code === 200) {
				isFavorited.value = true
				uni.showToast({
					title: '收藏成功',
					icon: 'success'
				})
			} else {
				uni.showToast({
					title: res.data.message || '收藏失败',
					icon: 'none'
				})
			}
		}
	} catch (error) {
		console.error('收藏操作失败:', error)
		uni.showToast({
			title: '操作失败',
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
	background: #f0f3fa;
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
	min-height: 88rpx;
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
	z-index: 10;
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
	text-align: center;
}

/* 内容区 */
.content {
	height: calc(100vh - 88rpx - env(safe-area-inset-top));
	padding: 20rpx 30rpx 40rpx;
	box-sizing: border-box;
}

/* 头部卡片 */
.header-card {
	background: linear-gradient(135deg, #e0f6ff 0%, #ffffff 100%);
	border-radius: 20rpx;
	padding: 40rpx 32rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.15);
}

.school-info {
	display: flex;
	align-items: center;
	gap: 24rpx;
	margin-bottom: 32rpx;
}

.school-logo-container {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	background: rgba(91, 143, 249, 0.15);
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	overflow: hidden;
}

.school-logo {
	width: 100%;
	height: 100%;
	border-radius: 50%;
}

.school-logo-placeholder {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	background: rgba(91, 143, 249, 0.3);
}

.school-text {
	flex: 1;
}

.school-name {
	font-size: 36rpx;
	font-weight: 700;
	color: #333;
	display: block;
	margin-bottom: 12rpx;
}

.major-info {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.major-code {
	font-size: 26rpx;
	color: #5b8ff9;
	background: rgba(91, 143, 249, 0.1);
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
	font-weight: 500;
}

.major-name {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
}

/* 收藏按钮 */
.action-button {
	background: rgba(91, 143, 249, 0.1);
	border-radius: 16rpx;
	padding: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 12rpx;
	margin-top: 24rpx;
}

.action-icon {
	font-size: 32rpx;
	color: #5b8ff9;
}

.action-text {
	font-size: 28rpx;
	color: #5b8ff9;
	font-weight: 500;
}

/* 信息卡片 */
.info-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 32rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

/* 研究方向标题 */
.direction-header {
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 32rpx;
	padding-bottom: 24rpx;
	border-bottom: 2rpx solid #f0f0f0;
}

.direction-badge {
	width: 8rpx;
	height: 32rpx;
	background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
	border-radius: 4rpx;
}

.direction-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

/* 信息区块 */
.info-section {
	margin-bottom: 32rpx;
}

.info-section:last-child {
	margin-bottom: 0;
}

.section-title {
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 24rpx;
}

.title-icon {
	width: 6rpx;
	height: 28rpx;
	background: #5b8ff9;
	border-radius: 3rpx;
}

.title-text {
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
}

/* 信息网格 */
.info-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 24rpx;
}

.info-item {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.info-label {
	font-size: 24rpx;
	color: #999;
}

.info-value {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.info-value.highlight {
	color: #5b8ff9;
	font-weight: 600;
}

/* 考试科目 */
.subject-container {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.subject-card {
	background: #f8f9ff;
	border-radius: 12rpx;
	padding: 20rpx 24rpx;
	display: flex;
	align-items: center;
	gap: 16rpx;
	border-left: 4rpx solid #5b8ff9;
}

.subject-number {
	font-size: 28rpx;
	color: #5b8ff9;
	font-weight: 600;
	flex-shrink: 0;
}

.subject-name {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	flex: 1;
}

/* 指导教师 */
.advisor-text {
	font-size: 28rpx;
	color: #333;
	line-height: 1.8;
	display: block;
}

/* 加载状态 */
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

/* 空状态 */
.empty-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 0;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 24rpx;
	opacity: 0.5;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
	margin-bottom: 12rpx;
}

.empty-hint {
	font-size: 24rpx;
	color: #ccc;
}

/* 底部提示 */
.footer-tip {
	padding: 40rpx 0 20rpx;
	text-align: center;
}

.tip-text {
	font-size: 24rpx;
	color: #999;
	line-height: 1.6;
}
</style>
