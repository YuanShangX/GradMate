<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<view class="search-container">
				<view class="search-icon"></view>
				<input 
					class="search-input" 
					v-model="searchKeyword" 
					placeholder="输入要查询的院校"
					placeholder-style="color: #999; font-size: 28rpx;"
					@confirm="handleSearch"
				/>
			</view>
		</view>
		
		<!-- 筛选栏 -->
		<view class="filter-bar">
			<view class="filter-item" @click="showLocationPicker = true">
				<text class="filter-text">{{ selectedLocation || '所在地区' }}</text>
				<text class="filter-arrow">▼</text>
			</view>
			<view class="filter-item" @click="showTagPicker = true">
				<text class="filter-text">{{ selectedTagLabel || '院校标签' }}</text>
				<text class="filter-arrow">▼</text>
			</view>
		</view>
		
		<!-- 院校列表 -->
		<scroll-view class="school-list" scroll-y="true" @scrolltolower="loadMore">
			<view v-for="(school, index) in schoolList" :key="index" class="school-card" @click="goToDetail(school)">
				<view class="school-header">
					<view class="school-logo-container">
						<image v-if="school.logo" class="school-logo" :src="school.logo" mode="aspectFill" />
						<view v-else class="school-logo-placeholder"></view>
					</view>
					<view class="school-info">
						<view class="school-name-row">
							<text class="school-name">{{ school.schoolName }}</text>
							<view class="school-location">
								<view class="location-icon"></view>
								<text class="location-text">{{ school.location }}</text>
							</view>
						</view>
						<view class="school-tags">
							<view v-if="school.yjsy === 1" class="tag">211</view>
							<view v-if="school.syl === 1" class="tag">双一流</view>
							<view v-if="school.zhx === 1" class="tag">自划线</view>
						</view>
					</view>
				</view>
				
				<view class="divider"></view>
				
				<view class="school-stats">
					<view class="stat-row">
						<text class="stat-text">{{ school.followCount || '8214' }}关注</text>
						<text class="stat-dot">·</text>
						<text class="stat-text">{{ school.viewCount || '24.1w' }}浏览</text>
					</view>
					<view class="stat-row stat-row-right">
						<text class="stat-label">招生数</text>
						<text class="stat-value">{{ school.enrollmentCount || '3058' }}</text>
						<text class="stat-label">专业数</text>
						<text class="stat-value">{{ school.majorCount || '142' }}</text>
					</view>
				</view>
			</view>
			
			<view v-if="loading" class="loading-text">加载中...</view>
			<view v-if="!loading && schoolList.length === 0" class="empty-text">暂无院校数据</view>
			<view v-if="!loading && noMore && schoolList.length > 0" class="nomore-text">没有更多了</view>
		</scroll-view>
		
		<!-- 返回顶部按钮 -->
		<view v-if="showBackTop" class="back-top" @click="scrollToTop">
			<text class="back-top-icon">↑</text>
		</view>
		
		<!-- 地区选择器 -->
		<view v-if="showLocationPicker" class="picker-mask" @click="showLocationPicker = false">
			<view class="picker-content" @click.stop>
				<view class="picker-header">
					<text class="picker-title">所在地区</text>
					<text class="picker-close" @click="showLocationPicker = false">×</text>
				</view>
				
				<view class="region-container">
					<view class="region-tabs">
						<view 
							class="region-tab" 
							:class="{ active: selectedRegion === 1 }"
							@click="selectedRegion = 1"
						>
							<text class="region-tab-text">一区</text>
						</view>
						<view 
							class="region-tab" 
							:class="{ active: selectedRegion === 2 }"
							@click="selectedRegion = 2"
						>
							<text class="region-tab-text">二区</text>
						</view>
					</view>
					
					<scroll-view class="region-list" scroll-y="true">
						<view v-if="selectedRegion === 1" class="province-grid">
							<view 
								v-for="(item, index) in region1List" 
								:key="index" 
								class="province-item"
								@click="selectLocation(item.name)"
							>
								<text class="province-name">{{ item.name }}</text>
								<text class="province-count">{{ item.count }}所</text>
							</view>
						</view>
						
						<view v-if="selectedRegion === 2" class="province-grid">
							<view 
								v-for="(item, index) in region2List" 
								:key="index" 
								class="province-item"
								@click="selectLocation(item.name)"
							>
								<text class="province-name">{{ item.name }}</text>
								<text class="province-count">{{ item.count }}所</text>
							</view>
						</view>
					</scroll-view>
				</view>
				
				<view class="picker-footer">
					<view class="footer-btn footer-btn-reset" @click="selectLocation('')">
						<text class="footer-btn-text">重置</text>
					</view>
					<view class="footer-btn footer-btn-confirm" @click="showLocationPicker = false">
						<text class="footer-btn-text">确定</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 主管部门选择器 -->
		<view v-if="showSchoolTypePicker" class="picker-mask" @click="showSchoolTypePicker = false">
			<view class="picker-content" @click.stop>
				<view class="picker-header">
					<text class="picker-title">主管部门</text>
					<text class="picker-close" @click="showSchoolTypePicker = false">×</text>
				</view>
				
				<scroll-view class="tag-list" scroll-y="true">
					<view class="tag-grid">
						<view 
							v-for="(item, index) in schoolTypeList" 
							:key="index" 
							class="tag-item"
							@click="selectSchoolType(item.value)"
						>
							<text class="tag-item-text">{{ item.label }}</text>
						</view>
					</view>
				</scroll-view>
				
				<view class="picker-footer">
					<view class="footer-btn footer-btn-reset" @click="selectSchoolType('')">
						<text class="footer-btn-text">重置</text>
					</view>
					<view class="footer-btn footer-btn-confirm" @click="showSchoolTypePicker = false">
						<text class="footer-btn-text">确定</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 标签选择器 -->
		<view v-if="showTagPicker" class="picker-mask" @click="showTagPicker = false">
			<view class="picker-content" @click.stop>
				<view class="picker-header">
					<text class="picker-title">院校标签</text>
					<text class="picker-close" @click="showTagPicker = false">×</text>
				</view>
				
				<scroll-view class="tag-list" scroll-y="true">
					<view class="tag-grid">
						<view 
							v-for="(item, index) in tagList" 
							:key="index" 
							class="tag-item"
							@click="selectTag(item.value)"
						>
							<text class="tag-item-text">{{ item.label }}</text>
						</view>
					</view>
				</scroll-view>
				
				<view class="picker-footer">
					<view class="footer-btn footer-btn-reset" @click="selectTag('')">
						<text class="footer-btn-text">重置</text>
					</view>
					<view class="footer-btn footer-btn-confirm" @click="showTagPicker = false">
						<text class="footer-btn-text">确定</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const searchKeyword = ref('')
const selectedLocation = ref('')
const selectedSchoolType = ref('')
const selectedTag = ref('')

// 计算选中标签的显示文本
const selectedTagLabel = computed(() => {
	if (!selectedTag.value) return ''
	const tag = tagList.value.find(item => item.value === selectedTag.value)
	return tag ? tag.label : ''
})

const showLocationPicker = ref(false)
const showSchoolTypePicker = ref(false)
const showTagPicker = ref(false)
const showBackTop = ref(false)
const selectedRegion = ref(1)

const schoolList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const noMore = ref(false)

// 一区省份列表
const region1List = ref([
	{ name: '北京', count: 148 },
	{ name: '天津', count: 28 },
	{ name: '河北', count: 26 },
	{ name: '山西', count: 17 },
	{ name: '辽宁', count: 45 },
	{ name: '吉林', count: 22 },
	{ name: '黑龙江', count: 31 },
	{ name: '上海', count: 49 },
	{ name: '江苏', count: 56 },
	{ name: '浙江', count: 36 },
	{ name: '安徽', count: 25 },
	{ name: '福建', count: 21 },
	{ name: '江西', count: 20 },
	{ name: '山东', count: 41 },
	{ name: '河南', count: 29 },
	{ name: '湖北', count: 51 },
	{ name: '湖南', count: 29 },
	{ name: '广东', count: 37 },
	{ name: '重庆', count: 19 },
	{ name: '四川', count: 38 },
	{ name: '陕西', count: 58 }
])

// 二区省份列表
const region2List = ref([
	{ name: '内蒙古', count: 13 },
	{ name: '广西', count: 17 },
	{ name: '海南', count: 5 },
	{ name: '贵州', count: 13 },
	{ name: '云南', count: 18 },
	{ name: '西藏', count: 3 },
	{ name: '甘肃', count: 17 },
	{ name: '青海', count: 3 },
	{ name: '宁夏', count: 5 },
	{ name: '新疆', count: 11 }
])

const locationList = ref([
	'北京', '上海', '天津', '重庆',
	'河北', '山西', '辽宁', '吉林', '黑龙江',
	'江苏', '浙江', '安徽', '福建', '江西', '山东',
	'河南', '湖北', '湖南', '广东', '广西', '海南',
	'四川', '贵州', '云南', '西藏', '陕西', '甘肃',
	'青海', '宁夏', '新疆', '内蒙古', '台湾', '香港', '澳门'
])

const schoolTypeList = ref([
	{ label: '教育部', value: '教育部' },
	{ label: '工业与信息化部', value: '工业与信息化部' },
	{ label: '中国科学院', value: '中国科学院' },
	{ label: '北京市', value: '北京市' },
	{ label: '上海市', value: '上海市' },
	{ label: '天津市', value: '天津市' },
	{ label: '重庆市', value: '重庆市' },
	{ label: '国家民族事务委员会', value: '国家民族事务委员会' },
	{ label: '国家体育总局', value: '国家体育总局' },
	{ label: '中央办公厅', value: '中央办公厅' }
])

const tagList = ref([
	{ label: '211', value: 'yjsy' },
	{ label: '双一流', value: 'syl' },
	{ label: '自划线', value: 'zhx' },
	{ label: '普通院校', value: 'normal' }
])

onMounted(() => {
	loadSchoolList()
})

const loadSchoolList = async () => {
	if (loading.value || noMore.value) return
	
	loading.value = true
	try {
		const params = {
			pageNum: pageNum.value,
			pageSize: 20
		}
		
		if (searchKeyword.value) params.schoolName = searchKeyword.value
		if (selectedLocation.value) params.location = selectedLocation.value
		
		// 根据选择的标签设置筛选条件
		if (selectedTag.value === 'yjsy') params.yjsy = 1
		if (selectedTag.value === 'syl') params.syl = 1
		if (selectedTag.value === 'zhx') params.zhx = 1
		if (selectedTag.value === 'normal') {
			// 普通院校：没有任何特殊标签
			params.yjsy = 0
			params.syl = 0
			params.zhx = 0
		}
		
		const res = await uni.request({
			url: buildApiUrl('/school/list'),
			method: 'GET',
			data: params
		})
		
		if (res.data.code === 200 && res.data.data) {
			const records = res.data.data.records || []
			schoolList.value = [...schoolList.value, ...records]
			
			if (records.length < 20) {
				noMore.value = true
			} else {
				pageNum.value++
			}
		}
	} catch (error) {
		console.error('加载院校列表失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		loading.value = false
	}
}

const handleSearch = () => {
	resetList()
	loadSchoolList()
}

const selectLocation = (location) => {
	selectedLocation.value = location
	showLocationPicker.value = false
	resetList()
	loadSchoolList()
}

const selectSchoolType = (type) => {
	selectedSchoolType.value = type
	showSchoolTypePicker.value = false
	resetList()
	loadSchoolList()
}

const selectTag = (tag) => {
	selectedTag.value = tag
	showTagPicker.value = false
	resetList()
	loadSchoolList()
}

const resetList = () => {
	schoolList.value = []
	pageNum.value = 1
	noMore.value = false
}

const loadMore = () => {
	loadSchoolList()
}

const scrollToTop = () => {
	// 实现返回顶部功能
	uni.pageScrollTo({
		scrollTop: 0,
		duration: 300
	})
}

const goToDetail = (school) => {
	uni.navigateTo({
		url: `/pages/school/detail?id=${school.id}`
	})
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
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.nav-back {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.back-icon {
	font-size: 56rpx;
	color: #333;
	font-weight: 300;
	line-height: 1;
}

.search-container {
	flex: 1;
	display: flex;
	align-items: center;
	background: #f5f5f5;
	border-radius: 50rpx;
	padding: 14rpx 28rpx;
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

/* 筛选栏 */
.filter-bar {
	background: #ffffff;
	padding: 24rpx 30rpx;
	display: flex;
	gap: 16rpx;
}

.filter-item {
	display: flex;
	align-items: center;
	gap: 6rpx;
	padding: 10rpx 20rpx;
	background: #f8f9fa;
	border-radius: 8rpx;
}

.filter-text {
	font-size: 26rpx;
	color: #666;
}

.filter-arrow {
	font-size: 18rpx;
	color: #999;
}

/* 院校列表 */
.school-list {
	height: calc(100vh - 220rpx);
	padding: 20rpx 30rpx;
	padding-bottom: 40rpx;
	box-sizing: border-box;
}

.school-card {
	background: #ffffff;
	border-radius: 16rpx;
	padding: 32rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
	box-sizing: border-box;
}

.school-header {
	display: flex;
	align-items: flex-start;
	gap: 24rpx;
	margin-bottom: 24rpx;
}

.school-logo-container {
	width: 120rpx;
	height: 120rpx;
	flex-shrink: 0;
}

.school-logo {
	width: 100%;
	height: 100%;
	border-radius: 50%;
	border: 1rpx solid #f0f0f0;
}

.school-logo-placeholder {
	width: 100%;
	height: 100%;
	border-radius: 50%;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.school-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.school-name-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.school-name {
	font-size: 34rpx;
	font-weight: 500;
	color: #333;
	line-height: 1.3;
}

.school-location {
	display: flex;
	align-items: center;
	gap: 6rpx;
	flex-shrink: 0;
}

.location-icon {
	width: 18rpx;
	height: 18rpx;
	border: 3rpx solid #c0c4cc;
	border-radius: 50% 50% 50% 0;
	transform: rotate(-45deg);
	position: relative;
	flex-shrink: 0;
	margin-right: 4rpx;
	box-sizing: border-box;
}

.location-icon::after {
	content: '';
	position: absolute;
	width: 6rpx;
	height: 6rpx;
	background: #c0c4cc;
	border-radius: 50%;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%) rotate(45deg);
}

.location-text {
	font-size: 26rpx;
	color: #999;
}

.school-tags {
	display: flex;
	gap: 12rpx;
	flex-wrap: wrap;
}

.tag {
	padding: 6rpx 16rpx;
	border-radius: 6rpx;
	font-size: 22rpx;
	color: #5b8ff9;
	background: #e8f4ff;
	border: 1rpx solid #d4e8ff;
}

.divider {
	height: 1rpx;
	background: #f0f0f0;
	margin: 24rpx 0;
}

.school-stats {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.stat-row {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.stat-row-right {
	gap: 12rpx;
}

.stat-text {
	font-size: 24rpx;
	color: #c0c4cc;
}

.stat-dot {
	font-size: 24rpx;
	color: #e0e0e0;
}

.stat-label {
	font-size: 24rpx;
	color: #999;
}

.stat-value {
	font-size: 28rpx;
	color: #333;
	font-weight: 600;
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

/* 返回顶部 */
.back-top {
	position: fixed;
	right: 30rpx;
	bottom: 120rpx;
	width: 80rpx;
	height: 80rpx;
	background: #006bff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.3);
}

.back-top-icon {
	font-size: 36rpx;
	color: #ffffff;
	font-weight: bold;
}

/* 选择器 */
.picker-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	z-index: 999;
	display: flex;
	align-items: flex-end;
}

.picker-content {
	width: 100%;
	max-height: 80vh;
	background: #ffffff;
	border-radius: 24rpx 24rpx 0 0;
	display: flex;
	flex-direction: column;
}

.picker-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	flex-shrink: 0;
}

.picker-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.picker-close {
	font-size: 48rpx;
	color: #999;
	line-height: 1;
}

/* 地区选择器 */
.region-container {
	display: flex;
	flex: 1;
	overflow: hidden;
}

.region-tabs {
	width: 160rpx;
	background: #f8f9fa;
	flex-shrink: 0;
}

.region-tab {
	padding: 32rpx 0;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
}

.region-tab.active {
	background: #ffffff;
}

.region-tab.active::before {
	content: '';
	position: absolute;
	left: 0;
	top: 50%;
	transform: translateY(-50%);
	width: 6rpx;
	height: 40rpx;
	background: #5b8ff9;
	border-radius: 0 4rpx 4rpx 0;
}

.region-tab-text {
	font-size: 28rpx;
	color: #666;
}

.region-tab.active .region-tab-text {
	color: #333;
	font-weight: 600;
}

.region-list {
	flex: 1;
	padding: 30rpx 20rpx;
	box-sizing: border-box;
}

.province-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 16rpx;
}

.province-item {
	background: #f8f9fa;
	border-radius: 12rpx;
	padding: 24rpx 16rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 8rpx;
	box-sizing: border-box;
}

.province-name {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.province-count {
	font-size: 22rpx;
	color: #999;
}

.picker-footer {
	display: flex;
	gap: 20rpx;
	padding: 20rpx 30rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	border-top: 1rpx solid #f0f0f0;
	flex-shrink: 0;
}

.footer-btn {
	flex: 1;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.footer-btn-reset {
	background: #e8f1ff;
}

.footer-btn-confirm {
	background: #5b8ff9;
}

.footer-btn-text {
	font-size: 30rpx;
	font-weight: 500;
}

.footer-btn-reset .footer-btn-text {
	color: #5b8ff9;
}

.footer-btn-confirm .footer-btn-text {
	color: #ffffff;
}

/* 其他选择器 */
.picker-list {
	max-height: 60vh;
}

/* 标签选择器 */
.tag-list {
	flex: 1;
	padding: 30rpx 20rpx;
	overflow-y: auto;
	box-sizing: border-box;
}

.tag-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 16rpx;
}

.tag-item {
	background: #f8f9fa;
	border-radius: 12rpx;
	padding: 28rpx 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
}

.tag-item-text {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.picker-item {
	padding: 30rpx;
	border-bottom: 1rpx solid #f8f8f8;
}

.picker-text {
	font-size: 28rpx;
	color: #333;
}
</style>
