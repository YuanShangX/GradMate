<template>
	<view class="page">
		<!-- 白色导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">院校详情</text>
		</view>
		
		<scroll-view class="content" scroll-y="true">
			<!-- 学校信息卡片 -->
			<view class="school-card">
				<!-- 学校logo -->
				<view class="logo-container">
					<image 
						v-if="schoolDetail && schoolDetail.school && schoolDetail.school.logo" 
						class="school-logo" 
						:src="schoolDetail.school.logo"
						mode="aspectFill"
					/>
					<view v-else class="logo-placeholder"></view>
				</view>
				
				<!-- 学校名称 -->
				<text class="school-name">{{ schoolDetail?.school?.schoolName || '' }}</text>
				
				<!-- 地区和隶属 -->
				<view class="school-meta">
					<view class="meta-item">
						<text class="meta-icon">📍</text>
						<text class="meta-text">{{ schoolDetail?.school?.location || '' }}</text>
					</view>
					<text class="meta-divider">|</text>
					<text class="meta-text">院校隶属：{{ schoolDetail?.school?.subordinate || '教育部' }}</text>
				</view>
				
				<!-- 标签 -->
				<view class="school-tags">
					<view v-if="schoolDetail?.is985" class="tag">985</view>
					<view v-if="schoolDetail?.is211" class="tag">211</view>
					<view v-if="schoolDetail?.isSyl" class="tag">双一流</view>
					<view v-if="schoolDetail?.isZhx" class="tag">自划线</view>
				</view>
				
				<!-- 关注按钮 -->
				<view class="follow-button" @click="handleFavorite">
					<text class="follow-icon">{{ isFavorited ? '♥' : '♡' }}</text>
					<text class="follow-text">{{ isFavorited ? '已关注' : '关注' }}</text>
				</view>
			</view>
			
			<!-- 功能按钮卡片 -->
			<view class="function-card">
				<view class="action-buttons">
					<view class="action-item">
						<text class="action-text">专业目录</text>
					</view>
					<view class="action-item">
						<text class="action-text">招生简章</text>
					</view>
					<view class="action-item">
						<text class="action-text">校园资讯</text>
					</view>
					<view class="action-item">
						<text class="action-text">调剂通知</text>
					</view>
				</view>
			</view>
			
			
			<view v-if="loading" class="loading-container">
				<text class="loading-text">加载中...</text>
			</view>
			
			<view v-else-if="schoolDetail">
				<!-- 招生办网址卡片 -->
				<view v-if="schoolDetail.school.link" class="info-card">
					<view class="card-title">
						<view class="title-bar"></view>
						<text class="title-text">招生办网址</text>
					</view>
					<view class="website-btn" @click="openWebsite">
						<text class="website-icon">🔗</text>
						<text class="website-text">点击访问研招办官网</text>
						<text class="website-arrow">›</text>
					</view>
				</view>
				
				<!-- 专业目录 -->
				<view id="major" class="info-card">
					<view class="card-title">
						<view class="title-bar"></view>
						<text class="title-text">专业目录</text>
					</view>
					<view v-if="majorList.length > 0" class="major-list">
						<view 
							v-for="(major, index) in (showAllMajors ? majorList : majorList.slice(0, 8))" 
							:key="index"
							class="major-item"
							@click="goToMajorDetail(major)"
						>
							<text class="major-code">{{ major.majorCode }}</text>
							<text class="major-name">{{ major.majorName }}</text>
							<text class="major-arrow">›</text>
						</view>
						<view v-if="!showAllMajors && majorList.length > 8" class="more-btn" @click="showAllMajors = true">
							<text class="more-text">查看全部 ({{ majorList.length }}个) ›</text>
						</view>
						<view v-if="showAllMajors && majorList.length > 8" class="more-btn" @click="showAllMajors = false">
							<text class="more-text">收起 ︿</text>
						</view>
					</view>
					<view v-else class="empty-hint">
						<text class="empty-text">暂无专业信息</text>
					</view>
				</view>
				
				<!-- 院校资讯 -->
				<view id="info" class="info-card">
					<view class="card-title">
						<view class="title-bar"></view>
						<text class="title-text">院校资讯</text>
					</view>
					<view v-if="infoList.length > 0" class="info-list">
						<view 
							v-for="(info, index) in infoList.slice(0, 5)" 
							:key="index"
							class="info-item"
							@click="openInfoUrl(info)"
						>
							<text class="info-title">{{ info.title }}</text>
							<view class="info-meta">
								<text class="info-type">{{ info.type }}</text>
								<text class="info-date">{{ formatDate(info.publishDate) }}</text>
							</view>
						</view>
					</view>
					<view v-else class="empty-hint">
						<text class="empty-text">暂无资讯信息</text>
					</view>
				</view>
			</view>
			
			<!-- 底部提示 -->
			<view v-if="!loading && schoolDetail" class="footer-tip">
				<text class="tip-text">以上信息仅供参考，具体以学校官方公布为准</text>
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
const majorList = ref([])
const infoList = ref([])
const loading = ref(true)
const isFavorited = ref(false)
const userId = ref(1)
const showAllMajors = ref(false)

onMounted(() => {
	const pages = getCurrentPages()
	const currentPage = pages[pages.length - 1]
	const options = currentPage.options
	
	schoolId.value = options.id
	schoolName.value = decodeURIComponent(options.name || '')
	
	// 获取用户ID
	const userInfo = uni.getStorageSync('userInfo')
	if (userInfo && userInfo.id) {
		userId.value = userInfo.id
	}
	
	loadSchoolDetail()
	checkFavoriteStatus()
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
			// 加载专业和资讯
			if (schoolDetail.value && schoolDetail.value.school) {
				loadMajorList(schoolDetail.value.school.schoolName)
				loadInfoList()
			}
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

const loadMajorList = async (name) => {
	if (!name) {
		console.error('学校名称为空')
		return
	}
	
	try {
		const res = await uni.request({
			url: buildApiUrl(`/major/school/${encodeURIComponent(name)}`),
			method: 'GET',
			data: {
				pageNum: 1,
				pageSize: 100  // 获取更多专业，以便显示全部
			}
		})
		
		if (res.data.code === 200 && res.data.data && res.data.data.records) {
			majorList.value = res.data.data.records
		}
	} catch (error) {
		console.error('加载专业列表失败:', error)
	}
}

const loadInfoList = async () => {
	if (!schoolId.value) {
		console.error('学校ID为空')
		return
	}
	
	try {
		// 并行加载三种类型的资讯
		const [brochureRes, adjustRes, informationRes] = await Promise.all([
			// 招生简章
			uni.request({
				url: buildApiUrl('/info/brochure/list'),
				method: 'GET',
				data: {
					schoolId: schoolId.value,
					pageNum: 1,
					pageSize: 5
				}
			}),
			// 调剂公告
			uni.request({
				url: buildApiUrl('/info/adjust/list'),
				method: 'GET',
				data: {
					schoolId: schoolId.value,
					pageNum: 1,
					pageSize: 5
				}
			}),
			// 招生通知
			uni.request({
				url: buildApiUrl('/info/information/list'),
				method: 'GET',
				data: {
					schoolId: schoolId.value,
					pageNum: 1,
					pageSize: 5
				}
			})
		])
		
		// 合并所有资讯
		const allInfo = []
		
		if (brochureRes.data.code === 200 && brochureRes.data.data && brochureRes.data.data.records) {
			allInfo.push(...brochureRes.data.data.records.map(item => ({
				...item,
				type: '招生简章'
			})))
		}
		
		if (adjustRes.data.code === 200 && adjustRes.data.data && adjustRes.data.data.records) {
			allInfo.push(...adjustRes.data.data.records.map(item => ({
				...item,
				type: '调剂公告'
			})))
		}
		
		if (informationRes.data.code === 200 && informationRes.data.data && informationRes.data.data.records) {
			allInfo.push(...informationRes.data.data.records.map(item => ({
				...item,
				type: '招生通知'
			})))
		}
		
		// 按发布日期排序
		allInfo.sort((a, b) => {
			const dateA = new Date(a.publishDate || 0)
			const dateB = new Date(b.publishDate || 0)
			return dateB - dateA
		})
		
		// 只取前10条
		infoList.value = allInfo.slice(0, 10)
	} catch (error) {
		console.error('加载资讯列表失败:', error)
	}
}

const checkFavoriteStatus = async () => {
	try {
		const res = await uni.request({
			url: buildApiUrl('/favorite/check'),
			method: 'GET',
			data: {
				userId: userId.value,
				type: 'school',
				targetId: schoolId.value
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
			const res = await uni.request({
				url: buildApiUrl('/favorite/remove'),
				method: 'GET',
				data: {
					userId: userId.value,
					type: 'school',
					targetId: schoolId.value
				}
			})
			
			if (res.data.code === 200) {
				isFavorited.value = false
				uni.showToast({
					title: '取消关注成功',
					icon: 'success'
				})
			}
		} else {
			const res = await uni.request({
				url: buildApiUrl('/favorite/add'),
				method: 'POST',
				data: {
					userId: userId.value,
					type: 'school',
					targetId: schoolId.value,
					targetName: schoolDetail.value.school.schoolName
				},
				header: {
					'Content-Type': 'application/json'
				}
			})
			
			if (res.data.code === 200) {
				isFavorited.value = true
				uni.showToast({
					title: '关注成功',
					icon: 'success'
				})
			}
		}
	} catch (error) {
		console.error('关注操作失败:', error)
		uni.showToast({
			title: '操作失败',
			icon: 'none'
		})
	}
}

const goToMajorList = () => {
	showAllMajors.value = true
}

const callPhone = () => {
	uni.showToast({
		title: '拨打电话功能',
		icon: 'none'
	})
}

const goToMajorDetail = (major) => {
	if (schoolDetail.value && schoolDetail.value.school) {
		uni.navigateTo({
			url: `/pages/major/schoolDetail?schoolName=${encodeURIComponent(schoolDetail.value.school.schoolName)}&majorCode=${major.majorCode}&majorName=${encodeURIComponent(major.majorName)}`
		})
	}
}

const openWebsite = () => {
	if (schoolDetail.value && schoolDetail.value.school && schoolDetail.value.school.link) {
		// 使用uni.navigateTo跳转到webview页面，或者直接打开外部链接
		// H5环境直接打开
		// #ifdef H5
		window.open(schoolDetail.value.school.link, '_blank')
		// #endif
		
		// 小程序环境需要使用web-view
		// #ifndef H5
		uni.navigateTo({
			url: `/pages/webview/index?url=${encodeURIComponent(schoolDetail.value.school.link)}`
		})
		// #endif
	} else {
		uni.showToast({
			title: '暂无官网链接',
			icon: 'none'
		})
	}
}

const openInfoUrl = (info) => {
	if (info && info.url) {
		// H5环境直接打开
		// #ifdef H5
		window.open(info.url, '_blank')
		// #endif
		
		// 小程序环境需要使用web-view
		// #ifndef H5
		uni.navigateTo({
			url: `/pages/webview/index?url=${encodeURIComponent(info.url)}`
		})
		// #endif
	} else {
		uni.showToast({
			title: '暂无资讯链接',
			icon: 'none'
		})
	}
}

const formatDate = (dateStr) => {
	if (!dateStr) return '--'
	try {
		const date = new Date(dateStr)
		return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
	} catch {
		return '--'
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

/* 白色导航栏 */
.navbar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
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

/* 学校信息卡片 */
.school-card {
	background: linear-gradient(180deg, #e8f4ff 0%, #f5f9ff 100%);
	border-radius: 20rpx;
	padding: 40rpx 32rpx;
	margin-bottom: 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	position: relative;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

/* logo容器 */
.logo-container {
	width: 140rpx;
	height: 140rpx;
	border-radius: 50%;
	background: #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 16rpx rgba(91, 143, 249, 0.15);
	margin-bottom: 24rpx;
	overflow: hidden;
}

.school-logo {
	width: 100%;
	height: 100%;
	border-radius: 50%;
}

.logo-placeholder {
	width: 90rpx;
	height: 90rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
}

/* 学校名称 */
.school-name {
	font-size: 40rpx;
	font-weight: 700;
	color: #333;
	text-align: center;
	margin-bottom: 20rpx;
}

/* 地区和隶属信息 */
.school-meta {
	display: flex;
	align-items: center;
	gap: 16rpx;
	margin-bottom: 24rpx;
}

.meta-item {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.meta-icon {
	font-size: 24rpx;
}

.meta-text {
	font-size: 24rpx;
	color: #666;
}

.meta-divider {
	font-size: 24rpx;
	color: #ddd;
}

/* 标签 */
.school-tags {
	display: flex;
	gap: 16rpx;
	flex-wrap: wrap;
	justify-content: center;
}

.tag {
	background: rgba(91, 143, 249, 0.1);
	color: #5b8ff9;
	font-size: 22rpx;
	padding: 6rpx 18rpx;
	border-radius: 20rpx;
	font-weight: 500;
}

/* 关注按钮 */
.follow-button {
	position: absolute;
	right: 32rpx;
	top: 32rpx;
	background: #1b7dff;
	border-radius: 48rpx;
	padding: 14rpx 28rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	box-shadow: 0 4rpx 12rpx rgba(27, 125, 255, 0.3);
}

.follow-icon {
	font-size: 24rpx;
	color: #ffffff;
}

.follow-text {
	font-size: 26rpx;
	color: #ffffff;
	font-weight: 500;
}

/* 功能按钮卡片 */
.function-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 32rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

/* 功能按钮组 */
.action-buttons {
	display: flex;
	justify-content: space-around;
}

.action-item {
	display: flex;
	align-items: center;
	justify-content: center;
}

.action-text {
	font-size: 26rpx;
	color: #999;
	font-weight: 400;
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

/* 信息卡片 */
.info-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 32rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

/* 卡片标题 */
.card-title {
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 24rpx;
}

.title-bar {
	width: 6rpx;
	height: 32rpx;
	background: #5b8ff9;
	border-radius: 3rpx;
}

.title-text {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

/* 招生办网址按钮 */
.website-btn {
	display: flex;
	align-items: center;
	padding: 24rpx;
	background: #f8f9ff;
	border-radius: 12rpx;
	gap: 16rpx;
}

.website-icon {
	font-size: 32rpx;
}

.website-text {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.website-arrow {
	font-size: 32rpx;
	color: #ccc;
}

/* 专业列表 */
.major-list {
	display: flex;
	flex-direction: column;
}

.major-item {
	display: flex;
	align-items: center;
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.major-item:last-child {
	border-bottom: none;
}

.major-code {
	font-size: 26rpx;
	color: #5b8ff9;
	width: 140rpx;
	flex-shrink: 0;
}

.major-name {
	font-size: 26rpx;
	color: #333;
	flex: 1;
}

.major-arrow {
	font-size: 32rpx;
	color: #ccc;
}

/* 资讯列表 */
.info-list {
	display: flex;
	flex-direction: column;
}

.info-item {
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.info-item:last-child {
	border-bottom: none;
}

.info-title {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	display: block;
	margin-bottom: 12rpx;
}

.info-meta {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.info-type {
	font-size: 22rpx;
	color: #5b8ff9;
	background: rgba(91, 143, 249, 0.1);
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
}

.info-date {
	font-size: 22rpx;
	color: #999;
}

/* 更多按钮 */
.more-btn {
	padding: 24rpx 0;
	display: flex;
	align-items: center;
	justify-content: center;
	border-top: 1rpx solid #f0f0f0;
}

.more-text {
	font-size: 26rpx;
	color: #5b8ff9;
}

/* 空状态 */
.empty-hint {
	text-align: center;
	padding: 60rpx 0;
}

.empty-text {
	font-size: 26rpx;
	color: #999;
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
