<template>
	<view class="search-page">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input-box">
				<image class="search-icon" src="/static/icons/search.png" mode="aspectFit"></image>
				<input 
					class="search-input" 
					v-model="keyword"
					placeholder="输入要查询的院校或专业"
					confirm-type="search"
					@confirm="handleSearch"
					@input="handleInput"
					focus
				/>
				<image 
					v-if="keyword" 
					class="clear-icon" 
					src="/static/icons/close.png" 
					mode="aspectFit"
					@click="clearKeyword"
				></image>
			</view>
			<text class="cancel-btn" @click="goBack">取消</text>
		</view>
		
		<!-- 搜索建议（输入时显示） -->
		<view v-if="keyword && !showResult" class="search-content">
			<!-- 这里可以添加搜索建议 -->
		</view>
		
		<!-- 默认内容（未搜索时） -->
		<view v-if="!keyword && !showResult" class="default-content">
			<!-- 热门搜索 -->
			<view class="section">
				<text class="section-title">热门搜索</text>
				<view class="tag-list">
					<view 
						v-for="(tag, index) in hotTags" 
						:key="index"
						class="tag-item"
						@click="searchTag(tag)"
					>
						<text class="tag-text">{{ tag }}</text>
					</view>
				</view>
			</view>
			
			<!-- 热门资讯和院校人气榜 -->
			<view class="info-section">
				<view class="info-card hot-info">
					<view class="card-header">
						<text class="card-title">热门资讯</text>
						<text class="more-icon">›</text>
					</view>
					<view class="info-list">
						<view 
							v-for="(item, index) in hotNews" 
							:key="index"
							class="info-item"
							@click="showToast('查看资讯')"
						>
							<text class="info-rank" :class="{ top: index < 3 }">{{ index + 1 }}</text>
							<text class="info-text">{{ item }}</text>
						</view>
					</view>
				</view>
				
				<view class="info-card hot-school">
					<view class="card-header">
						<text class="card-title">院校人气榜</text>
						<text class="more-icon">›</text>
					</view>
					<view class="info-list">
						<view 
							v-for="(item, index) in hotSchools" 
							:key="index"
							class="info-item"
							@click="showToast('查看院校')"
						>
							<text class="info-rank" :class="{ top: index < 3 }">{{ index + 1 }}</text>
							<text class="info-text">{{ item }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 搜索结果 -->
		<view v-if="showResult" class="result-content">
			<!-- Tab切换 -->
			<view class="result-tabs">
				<view 
					class="tab-item"
					:class="{ active: resultTab === 'school' }"
					@click="resultTab = 'school'"
				>
					<text class="tab-text">院校</text>
					<view v-if="resultTab === 'school'" class="tab-line"></view>
				</view>
				<view 
					class="tab-item"
					:class="{ active: resultTab === 'major' }"
					@click="resultTab = 'major'"
				>
					<text class="tab-text">专业</text>
					<view v-if="resultTab === 'major'" class="tab-line"></view>
				</view>
			</view>
			
			<!-- 院校结果 -->
			<scroll-view v-if="resultTab === 'school'" class="result-list" scroll-y>
				<view 
					v-for="(school, index) in schoolResults" 
					:key="index"
					class="result-item"
					@click="goToSchoolDetail(school)"
				>
					<image class="school-logo" :src="school.logo || '/static/default-school.png'" mode="aspectFit"></image>
					<view class="school-info">
						<text class="school-name">{{ school.schoolName }}</text>
						<text class="school-tag">{{ school.tag }}</text>
					</view>
				</view>
				
				<!-- 空状态 -->
				<view v-if="schoolResults.length === 0" class="empty-state">
					<image class="empty-icon" src="/static/empty.png" mode="aspectFit"></image>
					<text class="empty-text">暂无相关院校</text>
				</view>
			</scroll-view>
			
			<!-- 专业结果 -->
			<scroll-view v-if="resultTab === 'major'" class="result-list" scroll-y>
				<view 
					v-for="(major, index) in majorResults" 
					:key="index"
					class="result-item major-item"
					@click="goToMajorDetail(major)"
				>
					<view class="major-info">
						<text class="major-name">{{ major.majorName }}</text>
						<text class="major-school">{{ major.schoolName }}</text>
					</view>
				</view>
				
				<!-- 空状态 -->
				<view v-if="majorResults.length === 0" class="empty-state">
					<image class="empty-icon" src="/static/empty.png" mode="aspectFit"></image>
					<text class="empty-text">暂无相关专业</text>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { get } from '@/utils/request.js'

const keyword = ref('')
const showResult = ref(false)
const resultTab = ref('school')
const schoolResults = ref([])
const majorResults = ref([])

// 热门标签
const hotTags = ref([
	'人工智能',
	'中国科学院大学',
	'中国科学技术大学',
	'哈尔滨工业大学',
	'储能科学与工程',
	'南京大学'
])

// 热门资讯
const hotNews = ref([
	'复试备考全流程规划',
	'2026研考网上确认时间...',
	'26考研网上确认，一次...',
	'26考研预报名流程图',
	'2026年统考考生需准备...',
	'2026考研时间定了！',
	'别学错！这些学校参考...',
	'超重要！2026院校初试...',
	'提醒！这些学校专业学...',
	'快检漏！这些学校专业...'
])

// 热门院校
const hotSchools = ref([
	'北京大学',
	'中国科学院大学',
	'清华大学',
	'中国人民大学',
	'苏州大学',
	'浙江大学',
	'南京大学',
	'北京师范大学',
	'郑州大学',
	'厦门大学'
])

// 输入处理
const handleInput = () => {
	if (keyword.value) {
		showResult.value = false
	}
}

// 搜索
const handleSearch = async () => {
	if (!keyword.value.trim()) {
		uni.showToast({
			title: '请输入搜索内容',
			icon: 'none'
		})
		return
	}
	
	showResult.value = true
	
	try {
		// 搜索院校
		const schoolRes = await get('/school/list', {
			schoolName: keyword.value,
			pageNum: 1,
			pageSize: 20
		})
		if (schoolRes.code === 200) {
			schoolResults.value = schoolRes.data.records.map(item => ({
				...item,
				tag: '双一流'
			}))
		}
		
		// 搜索专业
		const majorRes = await get('/major/search', {
			majorName: keyword.value,
			pageNum: 1,
			pageSize: 20
		})
		if (majorRes.code === 200) {
			majorResults.value = majorRes.data.records
		}
	} catch (error) {
		console.error('搜索失败:', error)
	}
}

// 点击标签搜索
const searchTag = (tag) => {
	keyword.value = tag
	handleSearch()
}

// 清空关键词
const clearKeyword = () => {
	keyword.value = ''
	showResult.value = false
	schoolResults.value = []
	majorResults.value = []
}

// 返回
const goBack = () => {
	uni.navigateBack()
}

// 跳转院校详情
const goToSchoolDetail = (school) => {
	uni.navigateTo({
		url: `/pages/school/detail?id=${school.id}`
	})
}

// 跳转专业详情
const goToMajorDetail = (major) => {
	uni.navigateTo({
		url: `/pages/major/detail?id=${major.id}`
	})
}

// 提示
const showToast = (title) => {
	uni.showToast({
		title,
		icon: 'none'
	})
}
</script>

<style scoped>
.search-page {
	min-height: 100vh;
	background-color: #FFFFFF;
}

/* 搜索栏 */
.search-bar {
	display: flex;
	align-items: center;
	padding: 16rpx 32rpx;
	background-color: #FFFFFF;
	gap: 20rpx;
	border-bottom: 1rpx solid #F0F0F0;
}

.search-input-box {
	flex: 1;
	display: flex;
	align-items: center;
	background-color: #F5F5F5;
	border-radius: 8rpx;
	padding: 16rpx 28rpx;
}

.search-icon {
	width: 32rpx;
	height: 32rpx;
	margin-right: 12rpx;
}

.search-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.clear-icon {
	width: 32rpx;
	height: 32rpx;
	margin-left: 12rpx;
}

.cancel-btn {
	font-size: 28rpx;
	color: #1E88E5;
}

/* 默认内容 */
.default-content {
	padding: 32rpx;
}

.section {
	margin-bottom: 32rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 24rpx;
	display: block;
}

.tag-list {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.tag-item {
	background-color: #F5F5F5;
	border-radius: 8rpx;
	padding: 16rpx 32rpx;
}

.tag-text {
	font-size: 26rpx;
	color: #666;
}

/* 资讯区域 */
.info-section {
	display: flex;
	gap: 20rpx;
}

.info-card {
	flex: 1;
	background-color: #F8F8F8;
	border-radius: 12rpx;
	padding: 24rpx;
}

.hot-info {
	background-color: #FFF5F5;
}

.hot-school {
	background-color: #F0F8FF;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.card-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.more-icon {
	font-size: 36rpx;
	color: #999;
}

.info-list {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.info-item {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.info-rank {
	width: 36rpx;
	height: 36rpx;
	background-color: #E8E8E8;
	border-radius: 6rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 22rpx;
	color: #999;
	flex-shrink: 0;
	text-align: center;
	line-height: 36rpx;
}

.info-rank.top {
	background: linear-gradient(135deg, #FF6B6B 0%, #FF5252 100%);
	color: #FFFFFF;
	font-weight: bold;
}

.info-text {
	font-size: 24rpx;
	color: #666;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

/* 搜索结果 */
.result-content {
	display: flex;
	flex-direction: column;
	height: calc(100vh - 100rpx);
}

.result-tabs {
	display: flex;
	background-color: #FFFFFF;
	padding: 0 32rpx;
}

.tab-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 28rpx 0;
	position: relative;
}

.tab-text {
	font-size: 30rpx;
	color: #666;
}

.tab-item.active .tab-text {
	color: #1E88E5;
	font-weight: bold;
}

.tab-line {
	position: absolute;
	bottom: 0;
	width: 60rpx;
	height: 6rpx;
	background-color: #1E88E5;
	border-radius: 3rpx;
}

.result-list {
	flex: 1;
	padding: 24rpx 32rpx;
	background-color: #F8F8F8;
}

.result-item {
	display: flex;
	align-items: center;
	background-color: #FFFFFF;
	border-radius: 12rpx;
	padding: 28rpx 24rpx;
	margin-bottom: 20rpx;
	gap: 24rpx;
}

.school-logo {
	width: 96rpx;
	height: 96rpx;
	border-radius: 12rpx;
	flex-shrink: 0;
}

.school-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.school-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.school-tag {
	font-size: 24rpx;
	color: #999;
}

.major-item {
	padding: 32rpx 24rpx;
}

.major-info {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.major-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.major-school {
	font-size: 26rpx;
	color: #999;
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 0;
}

.empty-icon {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 32rpx;
	opacity: 0.5;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>
