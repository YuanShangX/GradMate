<template>
	<view class="page">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 蓝色顶部区域 -->
		<view class="header-section">
			<text class="page-title">院校库</text>
			
			<!-- 搜索框 -->
			<view class="search-box" @click="goToSearch">
				<view class="search-icon"></view>
				<text class="search-placeholder">搜索院校或专业</text>
			</view>
		</view>
		
		<!-- 白色内容区（带圆角） -->
		<view class="content-section">
			<!-- 找院校/找专业 -->
			<view class="entry-row">
				<view class="entry-card school-card" @click="navigateTo('/pages/school/list')">
					<view class="entry-content">
						<view class="entry-header">
							<text class="entry-title">找院校</text>
							<view class="entry-arrow blue-arrow">
								<text class="arrow-icon">›</text>
							</view>
						</view>
						<text class="entry-subtitle">26年最新信息</text>
					</view>
					<view class="entry-icon school-icon"></view>
				</view>
				
				<view class="entry-card major-card" @click="navigateTo('/pages/major/search')">
					<view class="entry-content">
						<view class="entry-header">
							<text class="entry-title">找专业</text>
							<view class="entry-arrow orange-arrow">
								<text class="arrow-icon">›</text>
							</view>
						</view>
						<text class="entry-subtitle">比较同专业院校</text>
					</view>
					<view class="entry-icon major-icon"></view>
				</view>
			</view>
			
			<!-- 5个功能图标 -->
			<view class="function-row">
				<view class="func-item">
					<text class="func-label">找院校</text>
				</view>
				<view class="func-item">
					<text class="func-label">找专业</text>
				</view>
				<view class="func-item">
					<text class="func-label">智能择校</text>
				</view>
				<view class="func-item">
					<text class="func-label">院校对比</text>
				</view>
				<view class="func-item">
					<text class="func-label">AI解答</text>
				</view>
			</view>
			
			<!-- 千研万语横幅 -->
			<view class="banner" @click="switchEncouragement">
				<text class="banner-emoji">🌟</text>
				<text class="banner-text" :class="{ 'fade-in': isAnimating }">{{ currentEncouragement }}</text>
				<text class="banner-arrow">›</text>
			</view>
			
			<!-- 实用工具标题 -->
			<text class="section-title">实用工具</text>
			
			<!-- 智能择校助手 & 院校专业对比 -->
			<view class="tool-row">
				<view class="tool-card ai-card" @click="navigateTo('/pages/school/assistant')">
					<view class="tool-content">
						<text class="tool-title">智能择校助手</text>
						<text class="tool-desc">专属择校报告</text>
						<view class="tool-btn">
							<text class="btn-text">领取报告</text>
						</view>
					</view>
					<view class="tool-icon ai-icon"></view>
				</view>
				
				<view class="tool-card pk-card" @click="navigateTo('/pages/school/select')">
					<view class="tool-content">
						<text class="tool-title">院校对比</text>
						<text class="tool-desc">一键锁定报考目标</text>
						<view class="tool-btn">
							<text class="btn-text">开始对比</text>
						</view>
					</view>
					<view class="tool-icon pk-icon"></view>
				</view>
			</view>
			
			<!-- 专业对比 -->
			<view class="tool-row">
				<view class="tool-card major-pk-card" @click="navigateTo('/pages/major/select')">
					<view class="tool-content">
						<text class="tool-title">专业对比</text>
						<text class="tool-desc">对比不同院校专业</text>
						<view class="tool-btn">
							<text class="btn-text">开始对比</text>
						</view>
					</view>
					<view class="tool-icon major-pk-icon"></view>
				</view>
			</view>
			
			<!-- 调剂专区 & 考研日历/小白扫盲 -->
			<view class="tool-row">
				<view class="adjust-card" @click="navigateTo('/pages/info/index')">
					<view class="adjust-content">
						<text class="adjust-title">调剂专区</text>
						<text class="adjust-desc">调剂内容汇总</text>
						<view class="adjust-btn">
							<text class="btn-text">立即查看</text>
						</view>
					</view>
					<view class="adjust-icon"></view>
				</view>
				
				<view class="quick-column">
					<view class="quick-item" @click="navigateTo('/pages/school/calendar')">
						<view class="quick-icon calendar-icon"></view>
						<text class="quick-text">考研日历</text>
						<text class="quick-arrow">›</text>
					</view>
					
					<view class="quick-item" @click="navigateTo('/pages/ai/chat')">
						<view class="quick-icon guide-icon"></view>
						<text class="quick-text">AI解答</text>
						<text class="quick-arrow">›</text>
					</view>
				</view>
			</view>
			
			<!-- 26考研日历 -->
			<view class="calendar-section">
				<view class="calendar-header">
					<text class="calendar-title">26考研日历</text>
					<view class="calendar-more" @click="navigateTo('/pages/school/calendar')">
						<text class="more-arrow">›</text>
					</view>
				</view>
				
				<!-- 时间线进度条 -->
				<scroll-view class="timeline-container" scroll-x="true" show-scrollbar="false">
					<view class="timeline-content">
						<view class="timeline-progress">
							<view class="progress-line"></view>
							<view class="progress-active" style="width: 30%;"></view>
						</view>
						
						<view class="timeline-nodes">
							<view class="timeline-node active">
								<view class="node-bubble">
									<text class="node-text">考研大纲发布</text>
								</view>
								<view class="node-dot active"></view>
								<text class="node-date">9月中旬</text>
							</view>
							
							<view class="timeline-node active">
								<view class="node-bubble">
									<text class="node-text">预报名</text>
								</view>
								<view class="node-dot active"></view>
								<text class="node-date">9月24-27日</text>
							</view>
							
							<view class="timeline-node active">
								<view class="node-bubble">
									<text class="node-text">正式报名</text>
								</view>
								<view class="node-dot active"></view>
								<text class="node-date">10月5-25日</text>
							</view>
							
							<view class="timeline-node active">
								<view class="node-bubble">
									<text class="node-text">网上确认</text>
								</view>
								<view class="node-dot active"></view>
								<text class="node-date">11月初</text>
							</view>
							
							<view class="timeline-node active">
								<view class="node-bubble">
									<text class="node-text">准考证打印</text>
								</view>
								<view class="node-dot active"></view>
								<text class="node-date">12月中旬</text>
							</view>
							
							<view class="timeline-node active">
								<view class="node-bubble">
									<text class="node-text">初试考试</text>
								</view>
								<view class="node-dot active"></view>
								<text class="node-date">12月20-21日</text>
							</view>
							
							<view class="timeline-node">
								<view class="node-bubble gray">
									<text class="node-text gray">初试成绩公布</text>
								</view>
								<view class="node-dot"></view>
								<text class="node-date">26年2月中旬</text>
							</view>
							
							<view class="timeline-node">
								<view class="node-bubble gray">
									<text class="node-text gray">国家线公布</text>
								</view>
								<view class="node-dot"></view>
								<text class="node-date">26年3月中旬</text>
							</view>
							
							<view class="timeline-node">
								<view class="node-bubble gray">
									<text class="node-text gray">复试调剂</text>
								</view>
								<view class="node-dot"></view>
								<text class="node-date">26年3-4月</text>
							</view>
							
							<view class="timeline-node">
								<view class="node-bubble gray">
									<text class="node-text gray">拟录取公示</text>
								</view>
								<view class="node-dot"></view>
								<text class="node-date">26年5月</text>
							</view>
							
							<view class="timeline-node">
								<view class="node-bubble gray">
									<text class="node-text gray">录取通知书</text>
								</view>
								<view class="node-dot"></view>
								<text class="node-date">26年6-7月</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
			
			<!-- 推荐资讯 -->
			<view class="news-header">
				<text class="news-title">推荐资讯</text>
				<text class="news-more" @click="navigateTo('/pages/info/index')">查看更多 ›</text>
			</view>
			
			<view class="news-list">
				<view 
					v-for="(item, index) in newsList" 
					:key="index"
					class="news-item"
					@click="viewNewsDetail(item)"
				>
					<view class="news-tag">{{ item.tag }}</view>
					<text class="news-title-text">{{ item.title }}</text>
					<view class="news-meta">
						<text class="news-source">{{ item.source }}</text>
						<text class="news-time">{{ item.time }}</text>
						<text class="news-comment">{{ item.comment }}条评论</text>
					</view>
				</view>
				
				<view v-if="loading" class="loading-text">加载中...</view>
				<view v-if="!loading && newsList.length === 0" class="empty-text">暂无资讯</view>
			</view>
			
			<view class="load-more" @click="navigateTo('/pages/info/index')">
				<text class="load-text">查看更多</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const statusBarHeight = ref(0)
const currentEncouragement = ref('')
const currentIndex = ref(0)
const isAnimating = ref(false)

const encouragementList = [
	'千研万语陪你，遇见不一样的自己！',
	'每一次努力，都是成功的铺垫',
	'坚持下去，你会感谢现在拼搏的自己',
	'梦想不会逃跑，逃跑的永远是自己',
	'今天的汗水，是明天的荣耀',
	'相信自己，你比想象中更强大',
	'不负韶华，不负梦想，加油！'
]

const newsList = ref([])
const loading = ref(false)

onMounted(() => {
	const systemInfo = uni.getSystemInfoSync()
	statusBarHeight.value = systemInfo.statusBarHeight || 0
	currentEncouragement.value = encouragementList[0]
	
	// 加载资讯列表
	loadNewsList()
})

const loadNewsList = async () => {
	loading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/info/mixed/list'),
			method: 'GET',
			data: {
				pageNum: 1,
				pageSize: 10
			}
		})
		
		if (res.data.code === 200 && res.data.data) {
			const records = res.data.data.records || []
			newsList.value = records.map(item => ({
				id: item.id,
				type: item.type,
				tag: item.tag || '院校资讯',
				title: item.title,
				source: item.schoolName || '未知院校',
				time: formatDate(item.publishDate),
				url: item.url,
				comment: Math.floor(Math.random() * 100) // 模拟评论数
			}))
		}
	} catch (error) {
		console.error('加载资讯失败:', error)
		uni.showToast({
			title: '加载资讯失败',
			icon: 'none'
		})
	} finally {
		loading.value = false
	}
}

const formatDate = (dateStr) => {
	if (!dateStr) return ''
	const date = new Date(dateStr)
	const year = date.getFullYear()
	const month = String(date.getMonth() + 1).padStart(2, '0')
	const day = String(date.getDate()).padStart(2, '0')
	return `${year}-${month}-${day}`
}

const switchEncouragement = () => {
	isAnimating.value = true
	
	setTimeout(() => {
		currentIndex.value = (currentIndex.value + 1) % encouragementList.length
		currentEncouragement.value = encouragementList[currentIndex.value]
		isAnimating.value = false
	}, 300)
}

const navigateTo = (url) => {
	// 检查是否是tabBar页面
	const tabBarPages = [
		'/pages/school/index',
		'/pages/study/tasklist',
		'/pages/ai/chat',
		'/pages/profile/profile'
	]
	
	if (tabBarPages.includes(url)) {
		uni.switchTab({ url })
	} else {
		uni.navigateTo({ url })
	}
}

const goToSearch = () => {
	uni.navigateTo({ url: '/pages/school/search' })
}

const showToast = (title) => {
	uni.showToast({
		title: `${title}功能开发中`,
		icon: 'none'
	})
}

const viewNewsDetail = (item) => {
	if (item.url) {
		// 在H5环境中直接打开链接
		// #ifdef H5
		window.open(item.url, '_blank')
		// #endif
		
		// 在APP环境中使用webview打开
		// #ifdef APP-PLUS
		plus.runtime.openURL(item.url)
		// #endif
		
		// 在小程序环境中复制链接
		// #ifdef MP
		uni.setClipboardData({
			data: item.url,
			success: () => {
				uni.showToast({
					title: '链接已复制',
					icon: 'success'
				})
			}
		})
		// #endif
	} else {
		showToast('暂无链接')
	}
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
	margin-bottom: 24rpx;
}

.search-box {
	display: flex;
	align-items: center;
	background: #ffffff;
	border-radius: 50rpx;
	padding: 24rpx 32rpx;
	gap: 12rpx;
}

.search-icon {
	width: 28rpx;
	height: 28rpx;
	border: 2rpx solid #999;
	border-radius: 50%;
	position: relative;
	flex-shrink: 0;
}

.search-icon::after {
	content: '';
	position: absolute;
	width: 2rpx;
	height: 10rpx;
	background: #999;
	transform: rotate(-45deg);
	bottom: -6rpx;
	right: -3rpx;
}

.search-placeholder {
	font-size: 28rpx;
	color: #ccc;
}

/* 白色内容区 */
.content-section {
	background: linear-gradient(180deg, #e8f3ff 0%, #f6f7fb 15%, #f6f7fb 100%);
	border-radius: 32rpx 32rpx 0 0;
	margin-top: -32rpx;
	padding: 24rpx 30rpx 40rpx;
}

/* 找院校/找专业 */
.entry-row {
	display: flex;
	gap: 16rpx;
	margin-bottom: 20rpx;
}

.entry-card {
	flex: 1;
	border-radius: 16rpx;
	padding: 28rpx 20rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	position: relative;
	overflow: hidden;
	min-height: 120rpx;
}

.entry-card.school-card {
	background: linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%);
}

.entry-card.major-card {
	background: linear-gradient(135deg, #fff5e6 0%, #ffefdf 100%);
}

.entry-content {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
	z-index: 2;
}

.entry-header {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.entry-title {
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
}

.entry-subtitle {
	font-size: 22rpx;
	color: #666;
}

.entry-arrow {
	width: 32rpx;
	height: 32rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.entry-arrow.blue-arrow {
	background: #006bff;
}

.entry-arrow.orange-arrow {
	background: #ff9500;
}

.arrow-icon {
	font-size: 24rpx;
	color: #ffffff;
	font-weight: bold;
	line-height: 1;
}

.entry-icon {
	width: 100rpx;
	height: 100rpx;
	position: relative;
	flex-shrink: 0;
	transform: rotate(-15deg);
	opacity: 0.4;
}

.school-icon {
	background: rgba(91, 143, 249, 0.3);
	border-radius: 16rpx;
	position: relative;
}

.school-icon::before {
	content: '';
	position: absolute;
	width: 50rpx;
	height: 40rpx;
	background: rgba(91, 143, 249, 0.5);
	top: 15rpx;
	left: 25rpx;
	border-radius: 6rpx;
}

.school-icon::after {
	content: '';
	position: absolute;
	width: 70rpx;
	height: 6rpx;
	background: rgba(91, 143, 249, 0.6);
	bottom: 12rpx;
	left: 15rpx;
	border-radius: 3rpx;
}

.major-icon {
	background: rgba(255, 149, 0, 0.3);
	border-radius: 12rpx;
	position: relative;
}

.major-icon::before {
	content: '';
	position: absolute;
	width: 60rpx;
	height: 45rpx;
	background: rgba(255, 149, 0, 0.5);
	top: 20rpx;
	left: 20rpx;
	border-radius: 6rpx 6rpx 10rpx 10rpx;
}

.major-icon::after {
	content: '';
	position: absolute;
	width: 25rpx;
	height: 12rpx;
	background: rgba(255, 149, 0, 0.6);
	top: 12rpx;
	left: 37rpx;
	border-radius: 6rpx 6rpx 0 0;
}

/* 5个功能图标 */
.function-row {
	display: flex;
	justify-content: space-between;
	background: #ffffff;
	border-radius: 12rpx;
	padding: 28rpx 16rpx;
	margin-bottom: 20rpx;
}

.func-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 10rpx;
}

.func-label {
	font-size: 24rpx;
	color: #666;
}

/* 千研万语横幅 */
.banner {
	display: flex;
	align-items: center;
	background: linear-gradient(135deg, #e0f6ff 0%, #ffffff 100%);
	border-radius: 16rpx;
	padding: 28rpx 24rpx;
	margin-bottom: 24rpx;
	gap: 12rpx;
	min-height: 100rpx;
}

.banner-emoji {
	font-size: 48rpx;
	flex-shrink: 0;
}

.banner-text {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	line-height: 1.5;
	transition: opacity 0.3s ease;
}

.banner-text.fade-in {
	opacity: 0;
	animation: fadeIn 0.3s ease forwards;
}

@keyframes fadeIn {
	from {
		opacity: 0;
		transform: translateY(-10rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.banner-arrow {
	font-size: 36rpx;
	color: #006bff;
	flex-shrink: 0;
}

/* 实用工具标题 */
.section-title {
	display: block;
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 16rpx;
}

/* 工具卡片 */
.tool-row {
	display: flex;
	gap: 16rpx;
	margin-bottom: 16rpx;
}

.tool-card {
	flex: 1;
	border-radius: 16rpx;
	padding: 24rpx 20rpx;
	display: flex;
	justify-content: space-between;
	position: relative;
	overflow: hidden;
	min-height: 160rpx;
}

.tool-card.ai-card {
	background: linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%);
}

.tool-card.pk-card {
	background: linear-gradient(135deg, #fff5e6 0%, #ffefdf 100%);
}

.tool-card.major-pk-card {
	background: linear-gradient(135deg, #f0e6ff 0%, #e8d4ff 100%);
	width: 100%;
}

.tool-content {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
	z-index: 2;
}

.tool-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.tool-desc {
	font-size: 22rpx;
	color: #666;
	margin-bottom: 8rpx;
}

.tool-btn {
	align-self: flex-start;
}

.btn-text {
	font-size: 22rpx;
	color: #006bff;
	background: rgba(255, 255, 255, 0.8);
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
	border: 1rpx solid rgba(0, 107, 255, 0.2);
}

.tool-icon {
	width: 100rpx;
	height: 100rpx;
	position: absolute;
	right: 15rpx;
	bottom: 15rpx;
	border-radius: 16rpx;
	opacity: 0.5;
	transform: rotate(-15deg);
}

.ai-icon {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}

.ai-icon::before {
	content: 'AI';
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%) rotate(15deg);
	font-size: 36rpx;
	font-weight: bold;
	color: rgba(255, 255, 255, 0.9);
}

.pk-icon {
	background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}

.pk-icon::before {
	content: 'PK';
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%) rotate(15deg);
	font-size: 32rpx;
	font-weight: bold;
	color: rgba(255, 255, 255, 0.9);
}

.major-pk-icon {
	background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
}

.major-pk-icon::before {
	content: 'VS';
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%) rotate(15deg);
	font-size: 32rpx;
	font-weight: bold;
	color: rgba(255, 255, 255, 0.9);
}

/* 调剂专区 */
.adjust-card {
	width: 310rpx;
	background: linear-gradient(135deg, #eafff3 0%, #e0fff0 100%);
	border-radius: 16rpx;
	padding: 20rpx;
	display: flex;
	justify-content: space-between;
	position: relative;
	overflow: hidden;
	height: 140rpx;
	flex-shrink: 0;
}

.adjust-content {
	display: flex;
	flex-direction: column;
	gap: 6rpx;
	z-index: 2;
}

.adjust-title {
	font-size: 26rpx;
	font-weight: 600;
	color: #333;
}

.adjust-desc {
	font-size: 20rpx;
	color: #666;
	margin-bottom: 6rpx;
}

.adjust-btn {
	align-self: flex-start;
}

.adjust-btn .btn-text {
	color: #00a870;
	border-color: rgba(0, 168, 112, 0.2);
	font-size: 20rpx;
	padding: 6rpx 16rpx;
}

.adjust-icon {
	width: 80rpx;
	height: 80rpx;
	position: absolute;
	right: 12rpx;
	bottom: 12rpx;
	border-radius: 14rpx;
	background: linear-gradient(135deg, #52c990 0%, #00a870 100%);
	opacity: 0.5;
	transform: rotate(-15deg);
	display: flex;
	align-items: center;
	justify-content: center;
}

.adjust-icon::before {
	content: '调';
	font-size: 36rpx;
	font-weight: bold;
	color: rgba(255, 255, 255, 0.9);
	transform: rotate(15deg);
}

/* 快捷入口列 */
.quick-column {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 12rpx;
	height: 140rpx;
}

.quick-item {
	background: #f5f5f5;
	border-radius: 16rpx;
	padding: 18rpx 20rpx;
	display: flex;
	align-items: center;
	gap: 12rpx;
	height: 64rpx;
}

.quick-icon {
	width: 56rpx;
	height: 56rpx;
	border-radius: 14rpx;
	flex-shrink: 0;
	position: relative;
}

.calendar-icon {
	background: #378cff;
}

.calendar-icon::before {
	content: '';
	position: absolute;
	width: 28rpx;
	height: 28rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 6rpx;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.calendar-icon::after {
	content: '';
	position: absolute;
	width: 14rpx;
	height: 3rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.guide-icon {
	background: #ff9500;
}

.guide-icon::before {
	content: '';
	position: absolute;
	width: 28rpx;
	height: 28rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 6rpx;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.quick-text {
	flex: 1;
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
}

.quick-arrow {
	font-size: 32rpx;
	color: #ccc;
}

/* 26考研日历 */
.calendar-section {
	margin: 20rpx 0;
	background: #ffffff;
	border-radius: 16rpx;
	padding: 24rpx;
}

.calendar-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.calendar-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.calendar-more {
	width: 48rpx;
	height: 48rpx;
	background: #006bff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.more-arrow {
	font-size: 28rpx;
	color: #ffffff;
	font-weight: bold;
}

.timeline-container {
	width: 100%;
	overflow-x: auto;
	overflow-y: hidden;
}

.timeline-content {
	min-width: 1800rpx;
	position: relative;
	padding: 20rpx 0;
}

.timeline-progress {
	position: relative;
	height: 4rpx;
	margin: 80rpx 60rpx 60rpx;
}

.progress-line {
	position: absolute;
	width: 100%;
	height: 4rpx;
	background: #e5e5e5;
	border-radius: 2rpx;
}

.progress-active {
	position: absolute;
	height: 4rpx;
	background: #006bff;
	border-radius: 2rpx;
	transition: width 0.3s ease;
}

.timeline-nodes {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	display: flex;
	justify-content: space-between;
	padding: 0 40rpx;
	gap: 40rpx;
}

.timeline-node {
	display: flex;
	flex-direction: column;
	align-items: center;
	position: relative;
	flex-shrink: 0;
}

.node-bubble {
	background: #006bff;
	border-radius: 12rpx;
	padding: 16rpx 20rpx;
	margin-bottom: 12rpx;
	position: relative;
	min-width: 120rpx;
	text-align: center;
}

.node-bubble::after {
	content: '';
	position: absolute;
	bottom: -8rpx;
	left: 50%;
	transform: translateX(-50%);
	width: 0;
	height: 0;
	border-left: 8rpx solid transparent;
	border-right: 8rpx solid transparent;
	border-top: 8rpx solid #006bff;
}

.node-bubble.gray {
	background: #f5f5f5;
}

.node-bubble.gray::after {
	border-top-color: #f5f5f5;
}

.node-text {
	font-size: 24rpx;
	font-weight: 500;
	color: #ffffff;
	white-space: nowrap;
}

.node-text.gray {
	color: #999;
}

.node-dot {
	width: 16rpx;
	height: 16rpx;
	background: #e5e5e5;
	border-radius: 50%;
	margin: 12rpx 0;
	position: relative;
	z-index: 2;
}

.node-dot.active {
	background: #006bff;
	box-shadow: 0 0 0 4rpx rgba(0, 107, 255, 0.2);
}

.node-date {
	font-size: 22rpx;
	color: #666;
	margin-top: 8rpx;
	text-align: center;
	background: #f0f8ff;
	padding: 6rpx 12rpx;
	border-radius: 8rpx;
}

/* 推荐资讯 */
.news-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16rpx;
}

.news-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.news-more {
	font-size: 24rpx;
	color: #999;
}

.news-list {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.news-item {
	background: #ffffff;
	border-radius: 12rpx;
	padding: 20rpx;
}

.news-tag {
	display: inline-block;
	background: #E8E8E8;
	color: #666;
	font-size: 22rpx;
	padding: 4rpx 12rpx;
	border-radius: 6rpx;
	margin-bottom: 8rpx;
}

.news-title-text {
	display: block;
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
	margin-bottom: 8rpx;
	line-height: 1.4;
}

.news-meta {
	display: flex;
	gap: 16rpx;
	font-size: 22rpx;
	color: #999;
}

.load-more {
	text-align: center;
	padding: 32rpx 0;
}

.load-text {
	font-size: 26rpx;
	color: #006bff;
}

.loading-text {
	text-align: center;
	padding: 32rpx 0;
	font-size: 26rpx;
	color: #999;
}

.empty-text {
	text-align: center;
	padding: 60rpx 0;
	font-size: 26rpx;
	color: #999;
}
</style>
