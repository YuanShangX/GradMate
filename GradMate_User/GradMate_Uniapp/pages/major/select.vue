<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<view class="back-icon"></view>
			</view>
			<text class="nav-title">选择对比专业</text>
		</view>
		
		<!-- 已选择的专业 -->
		<view v-if="selectedMajors.length > 0" class="selected-bar">
			<view class="selected-list">
				<view 
					v-for="(major, index) in selectedMajors" 
					:key="major.id"
					class="selected-item"
				>
					<text class="selected-name">{{ major.majorName }}</text>
					<text class="selected-remove" @click="removeMajor(index)">×</text>
				</view>
			</view>
			<view class="compare-btn" @click="goCompare">
				<text class="compare-text">开始对比({{ selectedMajors.length }}/3)</text>
			</view>
		</view>
		
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-icon"></view>
			<input 
				class="search-input" 
				v-model="searchKeyword" 
				placeholder="搜索专业名称或院校"
				@confirm="handleSearch"
			/>
		</view>
		
		<!-- 专业列表 -->
		<scroll-view class="major-list" scroll-y @scrolltolower="loadMore">
			<view 
				v-for="major in majorList" 
				:key="major.id"
				class="major-item"
				@click="toggleMajor(major)"
			>
				<view class="major-icon"></view>
				<view class="major-info">
					<text class="major-name">{{ major.majorName }}</text>
					<text class="school-name">{{ major.schoolName }}</text>
					<text class="major-code">{{ major.majorCode }}</text>
				</view>
				<view 
					class="checkbox"
					:class="{ checked: isSelected(major.id) }"
				>
					<text v-if="isSelected(major.id)" class="check-icon">✓</text>
				</view>
			</view>
			
			<view v-if="loading" class="loading-text">加载中...</view>
			<view v-if="!loading && majorList.length === 0" class="empty-text">暂无数据</view>
			<view v-if="!loading && noMore && majorList.length > 0" class="nomore-text">没有更多了</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { buildApiUrl } from '@/utils/request.js'

const searchKeyword = ref('')
const majorList = ref([])
const selectedMajors = ref([])
const loading = ref(false)
const page = ref(1)
const noMore = ref(false)

onLoad(() => {
	loadMajors()
})

const loadMajors = async () => {
	if (loading.value || noMore.value) return
	
	loading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/major/search'),
			method: 'GET',
			data: {
				majorName: searchKeyword.value || '',
				pageNum: page.value,
				pageSize: 20
			}
		})
		
		if (res.data.code === 200) {
			const newData = res.data.data.records || []
			if (newData.length === 0) {
				noMore.value = true
			} else {
				majorList.value = [...majorList.value, ...newData]
				page.value++
			}
		}
	} catch (error) {
		console.error('加载专业列表失败:', error)
	} finally {
		loading.value = false
	}
}

const loadMore = () => {
	loadMajors()
}

const handleSearch = async () => {
	majorList.value = []
	page.value = 1
	noMore.value = false
	loadMajors()
}

const toggleMajor = (major) => {
	const index = selectedMajors.value.findIndex(m => m.id === major.id)
	
	if (index > -1) {
		selectedMajors.value.splice(index, 1)
	} else {
		if (selectedMajors.value.length >= 3) {
			uni.showToast({
				title: '最多选择3个专业',
				icon: 'none'
			})
			return
		}
		selectedMajors.value.push(major)
	}
}

const removeMajor = (index) => {
	selectedMajors.value.splice(index, 1)
}

const isSelected = (majorId) => {
	return selectedMajors.value.some(m => m.id === majorId)
}

const goCompare = () => {
	if (selectedMajors.value.length < 2) {
		uni.showToast({
			title: '请至少选择2个专业',
			icon: 'none'
		})
		return
	}
	
	const majorIds = selectedMajors.value.map(m => m.id)
	uni.navigateTo({
		url: `/pages/major/compare?majorIds=${encodeURIComponent(JSON.stringify(majorIds))}`
	})
}

const goBack = () => {
	uni.navigateBack()
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f5f9ff;
	display: flex;
	flex-direction: column;
	width: 100%;
	overflow-x: hidden;
}

.navbar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
	display: flex;
	align-items: center;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	width: 100%;
	box-sizing: border-box;
}

.nav-back {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	width: 20rpx;
	height: 20rpx;
	border-left: 3rpx solid #333;
	border-bottom: 3rpx solid #333;
	transform: rotate(45deg);
}

.nav-title {
	flex: 1;
	text-align: center;
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	margin-right: 48rpx;
}

.selected-bar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	display: flex;
	align-items: center;
	gap: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	width: 100%;
	box-sizing: border-box;
}

.selected-list {
	flex: 1;
	display: flex;
	gap: 12rpx;
	flex-wrap: wrap;
	min-width: 0;
}

.selected-item {
	background: #e8f4ff;
	padding: 8rpx 16rpx;
	border-radius: 8rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	box-sizing: border-box;
	max-width: 100%;
}

.selected-name {
	font-size: 24rpx;
	color: #5b8ff9;
	word-wrap: break-word;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.selected-remove {
	font-size: 32rpx;
	color: #5b8ff9;
	line-height: 1;
}

.compare-btn {
	background: #5b8ff9;
	padding: 12rpx 24rpx;
	border-radius: 8rpx;
	flex-shrink: 0;
	box-sizing: border-box;
}

.compare-text {
	font-size: 26rpx;
	color: #ffffff;
	white-space: nowrap;
}

.search-bar {
	background: #ffffff;
	margin: 20rpx 30rpx;
	padding: 16rpx 24rpx;
	border-radius: 12rpx;
	display: flex;
	align-items: center;
	gap: 12rpx;
	box-sizing: border-box;
	width: calc(100% - 60rpx);
}

.search-icon {
	width: 32rpx;
	height: 32rpx;
	border: 3rpx solid #999;
	border-radius: 50%;
	position: relative;
}

.search-icon::after {
	content: '';
	position: absolute;
	width: 3rpx;
	height: 12rpx;
	background: #999;
	bottom: -8rpx;
	right: -6rpx;
	transform: rotate(-45deg);
}

.search-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.major-list {
	flex: 1;
	padding: 0 30rpx;
	box-sizing: border-box;
	width: 100%;
}

.major-item {
	background: #ffffff;
	padding: 30rpx;
	margin-bottom: 20rpx;
	border-radius: 12rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	box-sizing: border-box;
	width: 100%;
	gap: 20rpx;
}

.major-icon {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #e8f4ff 0%, #d4e6ff 100%);
	flex-shrink: 0;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
}

.major-icon::before {
	content: '';
	width: 40rpx;
	height: 40rpx;
	border: 4rpx solid #5b8ff9;
	border-radius: 6rpx;
	position: absolute;
}

.major-icon::after {
	content: '';
	width: 20rpx;
	height: 4rpx;
	background: #5b8ff9;
	position: absolute;
	box-shadow: 0 8rpx 0 #5b8ff9, 0 -8rpx 0 #5b8ff9;
}

.major-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 8rpx;
	min-width: 0;
	margin-right: 20rpx;
}

.major-name {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	word-wrap: break-word;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.school-name {
	font-size: 24rpx;
	color: #666;
	word-wrap: break-word;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.major-code {
	font-size: 22rpx;
	color: #999;
	word-wrap: break-word;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.checkbox {
	width: 40rpx;
	height: 40rpx;
	border: 2rpx solid #ddd;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.checkbox.checked {
	background: #5b8ff9;
	border-color: #5b8ff9;
}

.check-icon {
	font-size: 24rpx;
	color: #ffffff;
	font-weight: bold;
}

.loading-text,
.empty-text,
.nomore-text {
	text-align: center;
	padding: 40rpx 0;
	font-size: 26rpx;
	color: #999;
}
</style>
