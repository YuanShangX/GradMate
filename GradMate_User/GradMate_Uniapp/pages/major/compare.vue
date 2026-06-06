<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<view class="back-icon"></view>
			</view>
			<text class="nav-title">专业对比</text>
		</view>
		
		<!-- 对比表格 -->
		<scroll-view class="content" scroll-y>
			<view v-if="majors.length > 0" class="compare-container">
				<!-- 专业卡片 -->
				<view class="major-cards">
					<view 
						v-for="(major, index) in majors" 
						:key="index"
						class="major-card"
					>
						<text class="school-name">{{ major.schoolName }}</text>
						<text class="major-name">{{ major.majorName }}</text>
					</view>
				</view>
				
				<!-- 对比项 -->
				<view class="compare-table">
					<!-- 专业代码 -->
					<view class="compare-row">
						<view class="row-label">专业代码</view>
						<view class="row-values">
							<view 
								v-for="(major, index) in majors" 
								:key="index"
								class="row-value"
							>
								<text>{{ major.majorCode || '-' }}</text>
							</view>
						</view>
					</view>
					
					<!-- 学位类型 -->
					<view class="compare-row">
						<view class="row-label">学位类型</view>
						<view class="row-values">
							<view 
								v-for="(major, index) in majors" 
								:key="index"
								class="row-value"
							>
								<text>{{ major.degreeType || '-' }}</text>
							</view>
						</view>
					</view>
					
					<!-- 学习方式 -->
					<view class="compare-row">
						<view class="row-label">学习方式</view>
						<view class="row-values">
							<view 
								v-for="(major, index) in majors" 
								:key="index"
								class="row-value"
							>
								<text>{{ major.studyMode || '-' }}</text>
							</view>
						</view>
					</view>
					
					<!-- 拟招生人数 -->
					<view class="compare-row">
						<view class="row-label">拟招生人数</view>
						<view class="row-values">
							<view 
								v-for="(major, index) in majors" 
								:key="index"
								class="row-value"
							>
								<text>{{ major.plannedEnrollment || '-' }}</text>
							</view>
						</view>
					</view>
					
					<!-- 考试科目 -->
					<view class="compare-row">
						<view class="row-label">考试科目</view>
						<view class="row-values">
							<view 
								v-for="(major, index) in majors" 
								:key="index"
								class="row-value"
							>
								<view class="subjects">
									<text v-if="major.examSubject1">{{ major.examSubject1 }}</text>
									<text v-if="major.examSubject2">{{ major.examSubject2 }}</text>
									<text v-if="major.examSubject3">{{ major.examSubject3 }}</text>
									<text v-if="major.examSubject4">{{ major.examSubject4 }}</text>
									<text v-if="!major.examSubject1">-</text>
								</view>
							</view>
						</view>
					</view>
				</view>
				
				<!-- AI分析 -->
				<view v-if="analysis" class="analysis-card">
					<view class="analysis-header">
						<view class="ai-icon"></view>
						<text class="analysis-title">AI对比分析</text>
					</view>
					<text class="analysis-content">{{ analysis }}</text>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view v-else class="empty-state">
				<text class="empty-text">请选择专业进行对比</text>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { buildApiUrl } from '@/utils/request.js'

const majors = ref([])
const analysis = ref('')

onLoad((options) => {
	// 从URL参数获取专业ID
	if (options.majorIds) {
		const majorIds = JSON.parse(decodeURIComponent(options.majorIds))
		loadCompareData(majorIds)
	}
})

const loadCompareData = async (majorIds) => {
	try {
		const res = await uni.request({
			url: buildApiUrl('/compare/majors'),
			method: 'POST',
			data: { majorIds },
			header: {
				'Content-Type': 'application/json'
			}
		})
		
		if (res.data.code === 200) {
			majors.value = res.data.data.majors
			analysis.value = res.data.data.analysis
		} else {
			uni.showToast({
				title: res.data.message || '加载失败',
				icon: 'none'
			})
		}
	} catch (error) {
		console.error('加载对比数据失败:', error)
		uni.showToast({
			title: '加载失败',
			icon: 'none'
		})
	}
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

.content {
	flex: 1;
	padding: 20rpx 30rpx;
	box-sizing: border-box;
	width: 100%;
}

.compare-container {
	padding-bottom: 40rpx;
	max-width: 100%;
	box-sizing: border-box;
}

.major-cards {
	display: flex;
	gap: 20rpx;
	margin-bottom: 30rpx;
	max-width: 100%;
	box-sizing: border-box;
}

.major-card {
	flex: 1;
	background: linear-gradient(135deg, #e8f4ff 0%, #f5f9ff 100%);
	border-radius: 16rpx;
	padding: 30rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	box-sizing: border-box;
	max-width: 100%;
}

.school-name {
	font-size: 24rpx;
	color: #666;
	text-align: center;
	word-wrap: break-word;
	max-width: 100%;
}

.major-name {
	font-size: 26rpx;
	font-weight: 600;
	color: #333;
	text-align: center;
	line-height: 1.4;
	word-wrap: break-word;
	max-width: 100%;
}

.compare-table {
	background: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
	margin-bottom: 30rpx;
	max-width: 100%;
	box-sizing: border-box;
}

.compare-row {
	display: flex;
	border-bottom: 1rpx solid #f0f0f0;
	box-sizing: border-box;
	max-width: 100%;
}

.compare-row:last-child {
	border-bottom: none;
}

.row-label {
	width: 160rpx;
	padding: 30rpx 20rpx;
	font-size: 26rpx;
	font-weight: 600;
	color: #666;
	background: #f8f9fa;
	display: flex;
	align-items: center;
	box-sizing: border-box;
	word-wrap: break-word;
	flex-shrink: 0;
}

.row-values {
	flex: 1;
	display: flex;
	box-sizing: border-box;
	max-width: calc(100% - 160rpx);
}

.row-value {
	flex: 1;
	padding: 30rpx 20rpx;
	font-size: 26rpx;
	color: #333;
	display: flex;
	align-items: center;
	justify-content: center;
	text-align: center;
	border-left: 1rpx solid #f0f0f0;
	box-sizing: border-box;
	word-wrap: break-word;
	max-width: 100%;
}

.subjects {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
	font-size: 24rpx;
	line-height: 1.4;
	word-wrap: break-word;
	max-width: 100%;
}

.analysis-card {
	background: linear-gradient(135deg, #e8f4ff 0%, #f5f9ff 100%);
	border-radius: 16rpx;
	padding: 30rpx;
	box-sizing: border-box;
	max-width: 100%;
}

.analysis-header {
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 20rpx;
}

.ai-icon {
	width: 32rpx;
	height: 32rpx;
	background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
	border-radius: 6rpx;
	position: relative;
}

.ai-icon::before {
	content: '';
	position: absolute;
	width: 8rpx;
	height: 8rpx;
	background: #ffffff;
	border-radius: 50%;
	top: 8rpx;
	left: 6rpx;
}

.ai-icon::after {
	content: '';
	position: absolute;
	width: 8rpx;
	height: 8rpx;
	background: #ffffff;
	border-radius: 50%;
	top: 8rpx;
	right: 6rpx;
}

.analysis-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.analysis-content {
	font-size: 26rpx;
	color: #666;
	line-height: 1.8;
	word-wrap: break-word;
	max-width: 100%;
}

.empty-state {
	padding: 200rpx 0;
	text-align: center;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>
