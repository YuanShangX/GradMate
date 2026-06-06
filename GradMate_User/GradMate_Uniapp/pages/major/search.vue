<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">报考专业</text>
		</view>
		
		<!-- 学硕/专硕切换 -->
		<view class="type-tabs">
			<view 
				class="type-tab" 
				:class="{ active: degreeType === 'academic' }"
				@click="degreeType = 'academic'"
			>
				<text class="type-tab-text">学硕</text>
			</view>
			<view 
				class="type-tab" 
				:class="{ active: degreeType === 'professional' }"
				@click="degreeType = 'professional'"
			>
				<text class="type-tab-text">专硕</text>
			</view>
		</view>
		
		<!-- 主内容区 -->
		<view class="content-container">
			<!-- 左侧学科门类 -->
			<scroll-view class="category-list" scroll-y="true">
				<view 
					v-for="(category, index) in categoryList" 
					:key="index"
					class="category-item"
					:class="{ active: selectedCategory === category.code }"
					@click="selectCategory(category.code)"
				>
					<text class="category-text">{{ category.name }}</text>
				</view>
			</scroll-view>
			
			<!-- 右侧一级学科和专业列表 -->
			<scroll-view class="major-list" scroll-y="true">
				<view v-for="(discipline, index) in disciplineList" :key="index" class="discipline-group">
					<view class="discipline-header" @click="toggleDiscipline(discipline.code)">
						<text class="discipline-name">{{ discipline.code }}-{{ discipline.name }}</text>
						<text class="discipline-arrow">{{ expandedDisciplines.includes(discipline.code) ? '︿' : '﹀' }}</text>
					</view>
					
					<view v-if="expandedDisciplines.includes(discipline.code)" class="major-items">
						<view 
							v-for="(major, idx) in discipline.majors" 
							:key="idx"
							class="major-item"
							@click="goToMajorDetail(major)"
						>
							<text class="major-text">{{ major.code }}-{{ major.name }}</text>
						</view>
					</view>
				</view>
				
				<view v-if="loading" class="loading-text">加载中...</view>
				<view v-if="!loading && disciplineList.length === 0" class="empty-text">暂无专业数据</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const degreeType = ref('academic')
const selectedCategory = ref('02')
const expandedDisciplines = ref([])
const loading = ref(false)

const disciplineList = ref([])

// 学科门类列表
const categoryList = ref([
	{ code: '01', name: '哲学' },
	{ code: '02', name: '经济学' },
	{ code: '03', name: '法学' },
	{ code: '04', name: '教育学' },
	{ code: '05', name: '文学' },
	{ code: '06', name: '历史学' },
	{ code: '07', name: '理学' },
	{ code: '08', name: '工学' },
	{ code: '09', name: '农学' },
	{ code: '10', name: '医学' },
	{ code: '11', name: '军事学' },
	{ code: '12', name: '管理学' },
	{ code: '13', name: '艺术学' },
	{ code: '14', name: '交叉学科' }
])

onMounted(() => {
	loadDisciplines()
})

watch(selectedCategory, () => {
	expandedDisciplines.value = []
	loadDisciplines()
})

watch(degreeType, () => {
	expandedDisciplines.value = []
	loadDisciplines()
})

const selectCategory = (code) => {
	selectedCategory.value = code
}

const toggleDiscipline = (code) => {
	const index = expandedDisciplines.value.indexOf(code)
	if (index > -1) {
		expandedDisciplines.value.splice(index, 1)
	} else {
		expandedDisciplines.value.push(code)
	}
}

const loadDisciplines = async () => {
	loading.value = true
	try {
		const res = await uni.request({
			url: buildApiUrl('/major/disciplines-v2'),
			method: 'GET',
			data: {
				categoryCode: selectedCategory.value,
				degreeType: degreeType.value
			}
		})
		
		if (res.data.code === 200 && res.data.data) {
			disciplineList.value = res.data.data
		}
	} catch (error) {
		console.error('加载学科列表失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	} finally {
		loading.value = false
	}
}

const goToMajorDetail = (major) => {
	uni.navigateTo({
		url: `/pages/major/detail?code=${major.code}&name=${encodeURIComponent(major.name)}`
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
	display: flex;
	flex-direction: column;
}

/* 导航栏 */
.navbar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
	display: flex;
	align-items: center;
	gap: 20rpx;
	flex-shrink: 0;
}

.nav-back {
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
	flex: 1;
	text-align: center;
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	margin-right: 48rpx;
}

/* 学硕/专硕切换 */
.type-tabs {
	background: #ffffff;
	padding: 20rpx 30rpx;
	display: flex;
	gap: 20rpx;
	flex-shrink: 0;
}

.type-tab {
	flex: 1;
	height: 64rpx;
	border-radius: 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f5f5f5;
}

.type-tab.active {
	background: #5b8ff9;
}

.type-tab-text {
	font-size: 28rpx;
	color: #666;
}

.type-tab.active .type-tab-text {
	color: #ffffff;
	font-weight: 600;
}

/* 主内容区 */
.content-container {
	flex: 1;
	display: flex;
	overflow: hidden;
}

/* 左侧学科门类 */
.category-list {
	width: 180rpx;
	background: #f8f9fa;
	flex-shrink: 0;
}

.category-item {
	padding: 32rpx 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
}

.category-item.active {
	background: #ffffff;
}

.category-item.active::before {
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

.category-text {
	font-size: 26rpx;
	color: #666;
}

.category-item.active .category-text {
	color: #333;
	font-weight: 600;
}

/* 右侧专业列表 */
.major-list {
	flex: 1;
	background: #ffffff;
	padding: 20rpx 0;
}

.discipline-group {
	border-bottom: 1rpx solid #f0f0f0;
}

.discipline-header {
	padding: 24rpx 30rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.discipline-name {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.discipline-arrow {
	font-size: 24rpx;
	color: #999;
}

.major-items {
	background: #f8f9fa;
}

.major-item {
	padding: 24rpx 30rpx;
	padding-left: 50rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.major-item:last-child {
	border-bottom: none;
}

.major-text {
	font-size: 26rpx;
	color: #666;
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
</style>
