<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<view class="back-icon"></view>
			</view>
			<text class="nav-title">选择对比院校</text>
		</view>
		
		<!-- 已选择的院校 -->
		<view v-if="selectedSchools.length > 0" class="selected-bar">
			<view class="selected-list">
				<view 
					v-for="(school, index) in selectedSchools" 
					:key="school.id"
					class="selected-item"
				>
					<text class="selected-name">{{ school.schoolName }}</text>
					<text class="selected-remove" @click="removeSchool(index)">×</text>
				</view>
			</view>
			<view class="compare-btn" @click="goCompare">
				<text class="compare-text">开始对比({{ selectedSchools.length }}/3)</text>
			</view>
		</view>
		
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-icon"></view>
			<input 
				class="search-input" 
				v-model="searchKeyword" 
				placeholder="搜索院校名称"
				@confirm="handleSearch"
			/>
		</view>
		
		<!-- 院校列表 -->
		<scroll-view class="school-list" scroll-y>
			<view 
				v-for="school in schoolList" 
				:key="school.id"
				class="school-item"
				@click="toggleSchool(school)"
			>
				<image 
					v-if="school.logo" 
					:src="school.logo" 
					class="school-logo"
					mode="aspectFit"
				/>
				<view v-else class="school-logo-placeholder"></view>
				<view class="school-info">
					<text class="school-name">{{ school.schoolName }}</text>
					<text class="school-location">{{ school.location }}</text>
				</view>
				<view 
					class="checkbox"
					:class="{ checked: isSelected(school.id) }"
				>
					<text v-if="isSelected(school.id)" class="check-icon">✓</text>
				</view>
			</view>
			
			<view v-if="loading" class="loading-text">加载中...</view>
			<view v-if="!loading && schoolList.length === 0" class="empty-text">暂无数据</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { buildApiUrl } from '@/utils/request.js'

const searchKeyword = ref('')
const schoolList = ref([])
const selectedSchools = ref([])
const loading = ref(false)

onLoad(() => {
	loadSchools()
})

const loadSchools = async () => {
	loading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/school/list'),
			method: 'GET',
			data: {
				schoolName: searchKeyword.value || '',
				pageNum: 1,
				pageSize: 100
			}
		})
		
		if (res.data.code === 200) {
			schoolList.value = res.data.data.records || []
		}
	} catch (error) {
		console.error('加载院校列表失败:', error)
	} finally {
		loading.value = false
	}
}

const handleSearch = async () => {
	loadSchools()
}

const toggleSchool = (school) => {
	const index = selectedSchools.value.findIndex(s => s.id === school.id)
	
	if (index > -1) {
		// 已选中，取消选择
		selectedSchools.value.splice(index, 1)
	} else {
		// 未选中，添加选择
		if (selectedSchools.value.length >= 3) {
			uni.showToast({
				title: '最多选择3个院校',
				icon: 'none'
			})
			return
		}
		selectedSchools.value.push(school)
	}
}

const removeSchool = (index) => {
	selectedSchools.value.splice(index, 1)
}

const isSelected = (schoolId) => {
	return selectedSchools.value.some(s => s.id === schoolId)
}

const goCompare = () => {
	if (selectedSchools.value.length < 2) {
		uni.showToast({
			title: '请至少选择2个院校',
			icon: 'none'
		})
		return
	}
	
	const schoolIds = selectedSchools.value.map(s => s.id)
	uni.navigateTo({
		url: `/pages/school/compare?schoolIds=${encodeURIComponent(JSON.stringify(schoolIds))}`
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

.school-list {
	flex: 1;
	padding: 0 30rpx;
	box-sizing: border-box;
	width: 100%;
}

.school-item {
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

.school-logo {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	flex-shrink: 0;
}

.school-logo-placeholder {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%);
	flex-shrink: 0;
}

.school-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 8rpx;
	min-width: 0;
	margin-right: 20rpx;
}

.school-name {
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

.school-location {
	font-size: 24rpx;
	color: #666;
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
.empty-text {
	text-align: center;
	padding: 40rpx 0;
	font-size: 26rpx;
	color: #999;
}
</style>
