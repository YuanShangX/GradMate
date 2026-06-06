<template>
	<view class="page">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 蓝色顶部区域 -->
		<view class="header-section">
			<view class="nav-bar">
				<view class="nav-back" @click="goBack">
					<text class="back-icon">‹</text>
				</view>
				<text class="nav-title">我的收藏</text>
				<view class="nav-placeholder"></view>
			</view>
		</view>
		
		<!-- 白色内容区 -->
		<view class="content-section">
			<!-- Tab切换 -->
			<view class="tab-bar">
				<view 
					v-for="tab in tabs" 
					:key="tab.value"
					class="tab-item"
					:class="{ active: currentTab === tab.value }"
					@click="switchTab(tab.value)"
				>
					<text class="tab-text">{{ tab.label }}</text>
					<text class="tab-count">({{ tab.count }})</text>
				</view>
			</view>
			
			<!-- 列表内容 -->
			<scroll-view class="list-scroll" scroll-y @scrolltolower="loadMore">
				<view v-if="list.length > 0" class="list-content">
					<view 
						v-for="item in list" 
						:key="item.id"
						class="list-item"
						@click="goToDetail(item)"
					>
						<view class="item-icon" :class="getIconClass(item.type)"></view>
						<view class="item-content">
							<text class="item-title">{{ item.targetName }}</text>
							<text class="item-time">{{ formatTime(item.createTime) }}</text>
						</view>
						<view class="item-action" @click.stop="removeFavorite(item)">
							<view class="delete-icon"></view>
						</view>
					</view>
				</view>
				
				<view v-else class="empty">
					<view class="empty-icon"></view>
					<text class="empty-text">暂无收藏</text>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request.js'

const statusBarHeight = ref(0)
const userId = ref(1)
const currentTab = ref('school')
const tabs = ref([
	{ label: '院校', value: 'school', count: 0 },
	{ label: '专业', value: 'major', count: 0 }
])
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)

onMounted(() => {
	const systemInfo = uni.getSystemInfoSync()
	statusBarHeight.value = systemInfo.statusBarHeight || 0
	
	const userInfo = uni.getStorageSync('userInfo')
	if (userInfo && userInfo.id) {
		userId.value = userInfo.id
	}
	loadCounts()
	loadList()
})

const loadCounts = async () => {
	try {
		const res = await get(`/favorite/count?userId=${userId.value}`)
		if (res.code === 200) {
			tabs.value[0].count = res.data.school || 0
			tabs.value[1].count = res.data.major || 0
		}
	} catch (error) {
		console.error('加载数量失败:', error)
	}
}

const loadList = async () => {
	try {
		uni.showLoading({ title: '加载中...' })
		
		const type = currentTab.value
		const res = await get(`/favorite/list?userId=${userId.value}&type=${type}&pageNum=${pageNum.value}&pageSize=${pageSize.value}`)
		
		if (res.code === 200) {
			const newList = res.data.records || []
			list.value = pageNum.value === 1 ? newList : [...list.value, ...newList]
			hasMore.value = newList.length === pageSize.value
		}
	} catch (error) {
		console.error('加载列表失败:', error)
	} finally {
		uni.hideLoading()
	}
}

const switchTab = (tab) => {
	currentTab.value = tab
	pageNum.value = 1
	list.value = []
	loadList()
}

const loadMore = () => {
	if (hasMore.value) {
		pageNum.value++
		loadList()
	}
}

const removeFavorite = async (item) => {
	try {
		const res = await get(`/favorite/remove?userId=${userId.value}&type=${item.type}&targetId=${item.targetId}`)
		if (res.code === 200) {
			uni.showToast({ title: '取消成功', icon: 'success' })
			pageNum.value = 1
			list.value = []
			loadCounts()
			loadList()
		}
	} catch (error) {
		console.error('取消收藏失败:', error)
	}
}

const goToDetail = (item) => {
	if (item.type === 'school') {
		uni.navigateTo({ 
			url: `/pages/school/detail?id=${item.targetId}` 
		})
	} else if (item.type === 'major') {
		// 解析 metadata 获取学校ID
		let schoolId = ''
		let schoolName = ''
		
		if (item.metadata) {
			try {
				const metadata = JSON.parse(item.metadata)
				schoolId = metadata.schoolId || ''
				schoolName = metadata.schoolName || ''
			} catch (e) {
				console.error('解析 metadata 失败:', e)
			}
		}
		
		// 如果没有从 metadata 获取到学校名称，尝试从 targetName 中提取
		// targetName 格式: "学校名 - 专业名"
		if (!schoolName && item.targetName && item.targetName.includes(' - ')) {
			const nameParts = item.targetName.split(' - ')
			schoolName = nameParts[0]
		}
		
		// 优先使用 schoolId 跳转到院校详情页
		if (schoolId) {
			uni.navigateTo({ 
				url: `/pages/school/detail?id=${schoolId}` 
			})
		} else if (schoolName) {
			// 如果没有 schoolId，通过学校名称查询
			querySchoolByName(schoolName)
		} else {
			uni.showToast({
				title: '无法打开详情',
				icon: 'none'
			})
		}
	} else {
		// 资讯类跳转到资讯页面
		uni.navigateTo({ url: '/pages/info/index' })
	}
}

// 通过学校名称查询学校ID
const querySchoolByName = async (schoolName) => {
	try {
		uni.showLoading({ title: '加载中...' })
		const res = await get(`/school/list?schoolName=${encodeURIComponent(schoolName)}&pageNum=1&pageSize=1`)
		uni.hideLoading()
		
		if (res.code === 200 && res.data.records && res.data.records.length > 0) {
			const school = res.data.records[0]
			uni.navigateTo({ 
				url: `/pages/school/detail?id=${school.id}` 
			})
		} else {
			uni.showToast({
				title: '未找到该院校',
				icon: 'none'
			})
		}
	} catch (error) {
		uni.hideLoading()
		console.error('查询院校失败:', error)
		uni.showToast({
			title: '查询失败',
			icon: 'none'
		})
	}
}

const getIconClass = (type) => {
	const classMap = {
		'school': 'school-icon',
		'major': 'major-icon',
		'brochure': 'info-icon',
		'adjust': 'info-icon',
		'information': 'info-icon'
	}
	return classMap[type] || 'default-icon'
}

const goBack = () => {
	uni.navigateBack()
}

const formatTime = (time) => {
	if (!time) return ''
	const date = new Date(time)
	return `${date.getMonth() + 1}-${date.getDate()}`
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #006cff;
	display: flex;
	flex-direction: column;
}

.status-bar {
	background: #006cff;
}

/* 蓝色顶部区域 */
.header-section {
	background: #006cff;
	padding: 0 30rpx 60rpx;
}

.nav-bar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	height: 88rpx;
	padding-top: 20rpx;
}

.nav-back {
	width: 64rpx;
	height: 64rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	width: 20rpx;
	height: 20rpx;
	border-left: 3rpx solid #ffffff;
	border-bottom: 3rpx solid #ffffff;
	transform: rotate(45deg);
}

.nav-title {
	flex: 1;
	text-align: center;
	font-size: 36rpx;
	font-weight: 600;
	color: #ffffff;
}

.nav-placeholder {
	width: 64rpx;
}

/* 白色内容区 */
.content-section {
	flex: 1;
	background: #f6f7fb;
	border-radius: 32rpx 32rpx 0 0;
	margin-top: -32rpx;
	display: flex;
	flex-direction: column;
}

.tab-bar {
	background: #fff;
	display: flex;
	padding: 0;
	border-radius: 32rpx 32rpx 0 0;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 28rpx 0;
	position: relative;
}

.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 50%;
	transform: translateX(-50%);
	width: 60rpx;
	height: 4rpx;
	background: linear-gradient(90deg, #378cff 0%, #1b7dff 100%);
	border-radius: 2rpx;
}

.tab-text {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
}

.tab-item.active .tab-text {
	color: #006cff;
	font-weight: 600;
}

.tab-count {
	font-size: 22rpx;
	color: #999;
	margin-left: 6rpx;
}

.tab-item.active .tab-count {
	color: #006cff;
}

.list-scroll {
	flex: 1;
	padding: 20rpx 30rpx;
}

.list-item {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	margin-bottom: 16rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.item-icon {
	width: 48rpx;
	height: 48rpx;
	border-radius: 12rpx;
	margin-right: 16rpx;
	flex-shrink: 0;
	position: relative;
}

.school-icon {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}

.school-icon::before {
	content: '';
	position: absolute;
	width: 20rpx;
	height: 16rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 10rpx;
	left: 14rpx;
	border-radius: 3rpx;
}

.school-icon::after {
	content: '';
	position: absolute;
	width: 28rpx;
	height: 3rpx;
	background: rgba(255, 255, 255, 0.9);
	bottom: 10rpx;
	left: 10rpx;
	border-radius: 1.5rpx;
}

.major-icon {
	background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}

.major-icon::before {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 18rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 12rpx;
	left: 12rpx;
	border-radius: 3rpx 3rpx 4rpx 4rpx;
}

.major-icon::after {
	content: '';
	position: absolute;
	width: 10rpx;
	height: 5rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 7rpx;
	left: 19rpx;
	border-radius: 3rpx 3rpx 0 0;
}

.info-icon {
	background: linear-gradient(135deg, #52c990 0%, #00a870 100%);
}

.info-icon::before {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 28rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 4rpx;
	top: 8rpx;
	left: 10rpx;
}

.info-icon::after {
	content: '';
	position: absolute;
	width: 12rpx;
	height: 2rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 20rpx;
	left: 16rpx;
}

.item-content {
	flex: 1;
	min-width: 0;
}

.item-title {
	font-size: 28rpx;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.item-time {
	font-size: 22rpx;
	color: #999;
}

.item-action {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.delete-icon {
	width: 32rpx;
	height: 32rpx;
	position: relative;
}

.delete-icon::before {
	content: '';
	position: absolute;
	width: 20rpx;
	height: 24rpx;
	border: 2rpx solid #ff6b6b;
	border-top: none;
	border-radius: 0 0 4rpx 4rpx;
	bottom: 0;
	left: 6rpx;
}

.delete-icon::after {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 3rpx;
	background: #ff6b6b;
	top: 6rpx;
	left: 4rpx;
	border-radius: 1.5rpx;
}

.empty {
	text-align: center;
	padding: 200rpx 0;
}

.empty-icon {
	width: 120rpx;
	height: 120rpx;
	margin: 0 auto 30rpx;
	background: linear-gradient(135deg, #e0e0e0 0%, #d0d0d0 100%);
	border-radius: 60rpx;
	position: relative;
}

.empty-icon::before {
	content: '';
	position: absolute;
	width: 60rpx;
	height: 50rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.9);
	border-top: none;
	border-radius: 0 0 30rpx 30rpx;
	bottom: 20rpx;
	left: 30rpx;
}

.empty-icon::after {
	content: '';
	position: absolute;
	width: 70rpx;
	height: 4rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 35rpx;
	left: 25rpx;
	border-radius: 2rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>
