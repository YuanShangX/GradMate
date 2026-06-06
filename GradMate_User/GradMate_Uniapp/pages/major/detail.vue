<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">{{ majorCode }}-{{ majorName }}</text>
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
		
		<!-- 学校列表 -->
		<scroll-view class="school-list" scroll-y="true">
			<view 
				v-for="(school, index) in schoolList" 
				:key="index"
				class="school-card"
				@click="goToSchoolDetail(school)"
			>
				<view class="school-header">
					<view class="school-logo-container">
						<image 
							v-if="school.logo" 
							class="school-logo" 
							:src="school.logo"
							mode="aspectFill"
						/>
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
							<view v-if="school.is211" class="tag">211</view>
							<view v-if="school.isSyl" class="tag">双一流</view>
							<view v-if="school.isZhx" class="tag">自划线</view>
						</view>
						<view class="school-detail">
							<text class="detail-text">{{ school.studyMode }} · {{ school.examType }} · {{ school.department }}</text>
						</view>
					</view>
				</view>
				
				<view class="divider"></view>
				
				<view class="school-stats">
					<view class="stat-row">
						<text class="stat-text">{{ school.followCount || 106 }}关注</text>
						<text class="stat-dot">·</text>
						<text class="stat-text">{{ school.viewCount || 5840 }}浏览</text>
					</view>
					<view class="stat-row stat-row-right">
						<text class="stat-label">招生数</text>
						<text class="stat-value">{{ school.plannedEnrollment || '--' }}</text>
						<text class="stat-label">分数线</text>
						<text class="stat-value">{{ school.admissionScore || '--' }}</text>
					</view>
				</view>
			</view>
			
			<view v-if="loading" class="loading-text">加载中...</view>
			<view v-if="!loading && schoolList.length === 0" class="empty-text">暂无数据</view>
		</scroll-view>
		
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

const majorCode = ref('')
const majorName = ref('')
const selectedLocation = ref('')
const selectedTag = ref('')
const schoolList = ref([])
const loading = ref(false)

const showLocationPicker = ref(false)
const showTagPicker = ref(false)
const selectedRegion = ref(1)

// 计算选中标签的显示文本
const selectedTagLabel = computed(() => {
	if (!selectedTag.value) return ''
	const tag = tagList.value.find(item => item.value === selectedTag.value)
	return tag ? tag.label : ''
})

// 一区省份列表
const region1List = ref([
	{ name: '北京' },
	{ name: '天津' },
	{ name: '河北' },
	{ name: '山西' },
	{ name: '辽宁' },
	{ name: '吉林' },
	{ name: '黑龙江' },
	{ name: '上海' },
	{ name: '江苏' },
	{ name: '浙江' },
	{ name: '安徽' },
	{ name: '福建' },
	{ name: '江西' },
	{ name: '山东' },
	{ name: '河南' },
	{ name: '湖北' },
	{ name: '湖南' },
	{ name: '广东' },
	{ name: '重庆' },
	{ name: '四川' },
	{ name: '陕西' }
])

// 二区省份列表
const region2List = ref([
	{ name: '内蒙古' },
	{ name: '广西' },
	{ name: '海南' },
	{ name: '贵州' },
	{ name: '云南' },
	{ name: '西藏' },
	{ name: '甘肃' },
	{ name: '青海' },
	{ name: '宁夏' },
	{ name: '新疆' }
])

const tagList = ref([
	{ label: '211', value: '211' },
	{ label: '双一流', value: 'syl' },
	{ label: '自划线', value: 'zhx' }
])

onMounted(() => {
	// 获取路由参数
	const pages = getCurrentPages()
	const currentPage = pages[pages.length - 1]
	const options = currentPage.options
	
	// 支持两种参数方式
	if (options.id) {
		// 旧的方式：通过id查询专业详情
		loadMajorById(options.id)
	} else if (options.code) {
		// 新的方式：通过code查询开设该专业的学校列表
		majorCode.value = options.code || ''
		majorName.value = decodeURIComponent(options.name || '')
		loadSchoolList()
	}
})

// 通过id加载专业详情（旧方式）
const loadMajorById = async (id) => {
	loading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl(`/major/${id}`),
			method: 'GET'
		})
		
		if (res.data.code === 200 && res.data.data) {
			const major = res.data.data
			majorCode.value = major.majorCode || ''
			majorName.value = major.majorName || ''
			// 加载该专业的学校列表
			loadSchoolList()
		} else {
			uni.showToast({
				title: '专业不存在',
				icon: 'none'
			})
			loading.value = false
		}
	} catch (error) {
		console.error('加载专业详情失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
		loading.value = false
	}
}

// 加载开设该专业的学校列表（新方式）
const loadSchoolList = async () => {
	loading.value = true
	try {
		const params = {
			majorCode: majorCode.value
		}
		
		if (selectedLocation.value) params.location = selectedLocation.value
		if (selectedTag.value) params.schoolType = selectedTag.value
		
		const res = await uni.request({
			url: buildApiUrl('/major/schools'),
			method: 'GET',
			data: params
		})
		
		if (res.data.code === 200 && res.data.data) {
			schoolList.value = res.data.data
		}
	} catch (error) {
		console.error('加载学校列表失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		loading.value = false
	}
}

const loadSchools = async () => {
	// 重新加载学校列表（用于筛选）
	loadSchoolList()
}

const selectLocation = (location) => {
	selectedLocation.value = location
	showLocationPicker.value = false
	loadSchools()
}

const selectTag = (tag) => {
	selectedTag.value = tag
	showTagPicker.value = false
	loadSchools()
}

const goToSchoolDetail = (school) => {
	// 跳转到该院校的具体专业信息页面
	// 传递学校名称和专业代码
	uni.navigateTo({
		url: `/pages/major/schoolDetail?schoolName=${encodeURIComponent(school.schoolName)}&majorCode=${majorCode.value}&majorName=${encodeURIComponent(majorName.value)}`
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
	padding: 30rpx 30rpx;
	padding-top: calc(30rpx + env(safe-area-inset-top));
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	min-height: 88rpx;
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
	max-width: 500rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
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

/* 学校列表 */
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

.school-detail {
	margin-top: 4rpx;
}

.detail-text {
	font-size: 24rpx;
	color: #999;
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
</style>
