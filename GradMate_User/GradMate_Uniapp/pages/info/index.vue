<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">院校资讯</text>
		</view>
		
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-box">
				<view class="search-icon"></view>
				<input 
					class="search-input" 
					v-model="searchKeyword" 
					placeholder="搜索院校名称"
					@confirm="handleSearch"
				/>
				<view v-if="searchKeyword" class="clear-icon" @click="clearSearch">×</view>
			</view>
			<view class="search-btn" @click="handleSearch">搜索</view>
		</view>
		
		<!-- 标签页 -->
		<view class="tabs">
			<view 
				v-for="(tab, index) in tabs" 
				:key="index"
				class="tab-item"
				:class="{ active: currentTab === index }"
				@click="switchTab(index)"
			>
				<text class="tab-text">{{ tab }}</text>
				<view v-if="currentTab === index" class="tab-line"></view>
			</view>
		</view>
		
		<!-- 内容区 -->
		<scroll-view class="content" scroll-y="true" @scrolltolower="loadMore">
			<!-- 招生简章 -->
			<view v-if="currentTab === 0" class="info-list">
				<view v-for="(item, index) in brochureList" :key="index" class="info-item" @click="viewDetail(item)">
					<view class="info-header">
						<text class="info-school">{{ item.schoolName }}</text>
						<text class="info-date">{{ formatDate(item.publishDate) }}</text>
					</view>
					<text class="info-title">{{ item.title }}</text>
				</view>
				
				<view v-if="brochureLoading" class="loading-text">加载中...</view>
				<view v-if="!brochureLoading && brochureList.length === 0" class="empty-text">暂无数据</view>
				<view v-if="!brochureLoading && brochureNoMore" class="nomore-text">没有更多了</view>
			</view>
			
			<!-- 调剂信息 -->
			<view v-if="currentTab === 1" class="info-list">
				<view v-for="(item, index) in adjustList" :key="index" class="info-item" @click="viewDetail(item)">
					<view class="info-header">
						<text class="info-school">{{ item.schoolName }}</text>
						<text class="info-date">{{ formatDate(item.publishDate) }}</text>
					</view>
					<text class="info-title">{{ item.title }}</text>
				</view>
				
				<view v-if="adjustLoading" class="loading-text">加载中...</view>
				<view v-if="!adjustLoading && adjustList.length === 0" class="empty-text">暂无数据</view>
				<view v-if="!adjustLoading && adjustNoMore" class="nomore-text">没有更多了</view>
			</view>
			
			<!-- 院校信息 -->
			<view v-if="currentTab === 2" class="info-list">
				<view v-for="(item, index) in informationList" :key="index" class="info-item" @click="viewDetail(item)">
					<view class="info-header">
						<text class="info-school">{{ item.schoolName }}</text>
						<text class="info-date">{{ formatDate(item.publishDate) }}</text>
					</view>
					<text class="info-title">{{ item.title }}</text>
				</view>
				
				<view v-if="informationLoading" class="loading-text">加载中...</view>
				<view v-if="!informationLoading && informationList.length === 0" class="empty-text">暂无数据</view>
				<view v-if="!informationLoading && informationNoMore" class="nomore-text">没有更多了</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const currentTab = ref(0)
const tabs = ['招生简章', '调剂信息', '院校信息']
const searchKeyword = ref('')

// 招生简章
const brochureList = ref([])
const brochureLoading = ref(false)
const brochurePageNum = ref(1)
const brochureNoMore = ref(false)

// 调剂信息
const adjustList = ref([])
const adjustLoading = ref(false)
const adjustPageNum = ref(1)
const adjustNoMore = ref(false)

// 院校信息
const informationList = ref([])
const informationLoading = ref(false)
const informationPageNum = ref(1)
const informationNoMore = ref(false)

onMounted(() => {
	loadBrochureList()
})

const switchTab = (index) => {
	currentTab.value = index
	
	// 切换标签时加载对应数据
	if (index === 0 && brochureList.value.length === 0) {
		loadBrochureList()
	} else if (index === 1 && adjustList.value.length === 0) {
		loadAdjustList()
	} else if (index === 2 && informationList.value.length === 0) {
		loadInformationList()
	}
}

const loadBrochureList = async () => {
	if (brochureLoading.value || brochureNoMore.value) return
	
	brochureLoading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/info/brochure/list'),
			method: 'GET',
			data: {
				pageNum: brochurePageNum.value,
				pageSize: 20,
				schoolName: searchKeyword.value || undefined
			}
		})
		
		if (res.data.code === 200 && res.data.data) {
			const records = res.data.data.records || []
			brochureList.value = [...brochureList.value, ...records]
			
			if (records.length < 20) {
				brochureNoMore.value = true
			} else {
				brochurePageNum.value++
			}
		}
	} catch (error) {
		console.error('加载招生简章失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		brochureLoading.value = false
	}
}

const loadAdjustList = async () => {
	if (adjustLoading.value || adjustNoMore.value) return
	
	adjustLoading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/info/adjust/list'),
			method: 'GET',
			data: {
				pageNum: adjustPageNum.value,
				pageSize: 20,
				schoolName: searchKeyword.value || undefined
			}
		})
		
		if (res.data.code === 200 && res.data.data) {
			const records = res.data.data.records || []
			adjustList.value = [...adjustList.value, ...records]
			
			if (records.length < 20) {
				adjustNoMore.value = true
			} else {
				adjustPageNum.value++
			}
		}
	} catch (error) {
		console.error('加载调剂信息失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		adjustLoading.value = false
	}
}

const loadInformationList = async () => {
	if (informationLoading.value || informationNoMore.value) return
	
	informationLoading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/info/information/list'),
			method: 'GET',
			data: {
				pageNum: informationPageNum.value,
				pageSize: 20,
				schoolName: searchKeyword.value || undefined
			}
		})
		
		if (res.data.code === 200 && res.data.data) {
			const records = res.data.data.records || []
			informationList.value = [...informationList.value, ...records]
			
			if (records.length < 20) {
				informationNoMore.value = true
			} else {
				informationPageNum.value++
			}
		}
	} catch (error) {
		console.error('加载院校信息失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		informationLoading.value = false
	}
}

const loadMore = () => {
	if (currentTab.value === 0) {
		loadBrochureList()
	} else if (currentTab.value === 1) {
		loadAdjustList()
	} else if (currentTab.value === 2) {
		loadInformationList()
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

const viewDetail = (item) => {
	if (item.url) {
		// #ifdef H5
		window.open(item.url, '_blank')
		// #endif
		
		// #ifdef APP-PLUS
		plus.runtime.openURL(item.url)
		// #endif
		
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
		uni.showToast({
			title: '暂无链接',
			icon: 'none'
		})
	}
}

const goBack = () => {
	uni.navigateBack()
}

const handleSearch = () => {
	// 重置所有列表
	brochureList.value = []
	brochurePageNum.value = 1
	brochureNoMore.value = false
	
	adjustList.value = []
	adjustPageNum.value = 1
	adjustNoMore.value = false
	
	informationList.value = []
	informationPageNum.value = 1
	informationNoMore.value = false
	
	// 重新加载当前标签页的数据
	if (currentTab.value === 0) {
		loadBrochureList()
	} else if (currentTab.value === 1) {
		loadAdjustList()
	} else if (currentTab.value === 2) {
		loadInformationList()
	}
}

const clearSearch = () => {
	searchKeyword.value = ''
	handleSearch()
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f6f7fb;
}

/* 导航栏 */
.navbar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	display: flex;
	align-items: center;
	position: relative;
}

.nav-back {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	font-size: 48rpx;
	color: #333;
	font-weight: bold;
}

.nav-title {
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
	font-size: 32rpx;
	font-weight: 600;
	color: #006bff;
}

/* 搜索栏 */
.search-bar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.search-box {
	flex: 1;
	display: flex;
	align-items: center;
	background: #f5f5f5;
	border-radius: 50rpx;
	padding: 16rpx 24rpx;
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

.search-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.clear-icon {
	width: 32rpx;
	height: 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
	color: #999;
	flex-shrink: 0;
}

.search-btn {
	padding: 16rpx 32rpx;
	background: #006bff;
	color: #ffffff;
	font-size: 28rpx;
	border-radius: 50rpx;
}

/* 标签页 */
.tabs {
	background: #ffffff;
	display: flex;
	padding: 0 30rpx;
}

.tab-item {
	flex: 1;
	padding: 24rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	position: relative;
}

.tab-text {
	font-size: 28rpx;
	color: #666;
}

.tab-item.active .tab-text {
	color: #006bff;
	font-weight: 600;
}

.tab-line {
	position: absolute;
	bottom: 0;
	width: 60rpx;
	height: 4rpx;
	background: #006bff;
	border-radius: 2rpx;
}

/* 内容区 */
.content {
	height: calc(100vh - 240rpx);
	padding: 24rpx 30rpx;
	box-sizing: border-box;
}

/* 资讯列表 */
.info-list {
	padding-top: 20rpx;
}

.info-item {
	background: #ffffff;
	border-radius: 12rpx;
	padding: 24rpx 20rpx;
	margin-bottom: 16rpx;
}

.info-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.info-school {
	font-size: 24rpx;
	color: #006bff;
	font-weight: 500;
}

.info-date {
	font-size: 22rpx;
	color: #999;
}

.info-title {
	font-size: 28rpx;
	color: #333;
	line-height: 1.5;
	display: block;
}

/* 加载状态 */
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

.nomore-text {
	text-align: center;
	padding: 32rpx 0;
	font-size: 24rpx;
	color: #ccc;
}
</style>
