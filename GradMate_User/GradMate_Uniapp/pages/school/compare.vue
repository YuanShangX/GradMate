<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<view class="back-icon"></view>
			</view>
			<text class="nav-title">院校对比</text>
		</view>
		
		<!-- 对比表格 -->
		<scroll-view class="content" scroll-y>
			<view v-if="schools.length > 0" class="compare-container">
				<!-- 院校卡片 -->
				<view class="school-cards">
					<view 
						v-for="(school, index) in schools" 
						:key="index"
						class="school-card"
					>
						<image 
							v-if="school.logo" 
							:src="school.logo" 
							class="school-logo"
							mode="aspectFit"
						/>
						<view v-else class="school-logo-placeholder"></view>
						<text class="school-name">{{ school.schoolName }}</text>
					</view>
				</view>
				
				<!-- 对比项 -->
				<view class="compare-table">
					<!-- 地理位置 -->
					<view class="compare-row">
						<view class="row-label">地理位置</view>
						<view class="row-values">
							<view 
								v-for="(school, index) in schools" 
								:key="index"
								class="row-value"
							>
								<text>{{ school.location || '-' }}</text>
							</view>
						</view>
					</view>
					
					<!-- 主管部门 -->
					<view class="compare-row">
						<view class="row-label">主管部门</view>
						<view class="row-values">
							<view 
								v-for="(school, index) in schools" 
								:key="index"
								class="row-value"
							>
								<text>{{ school.subordinate || '-' }}</text>
							</view>
						</view>
					</view>
					
					<!-- 院校特征 -->
					<view class="compare-row">
						<view class="row-label">院校特征</view>
						<view class="row-values">
							<view 
								v-for="(school, index) in schools" 
								:key="index"
								class="row-value"
							>
								<view class="tags">
									<view v-if="school.isSyl === 1" class="tag tag-syl">双一流</view>
									<view v-if="school.is211 === 1" class="tag tag-211">211</view>
									<view v-if="school.isZhx === 1" class="tag tag-zhx">自划线</view>
									<text v-if="!school.isSyl && !school.is211 && !school.isZhx">-</text>
								</view>
							</view>
						</view>
					</view>
					
					<!-- 专业数量 -->
					<view class="compare-row">
						<view class="row-label">专业数量</view>
						<view class="row-values">
							<view 
								v-for="(school, index) in schools" 
								:key="index"
								class="row-value"
							>
								<text>{{ school.majorCount || 0 }}个</text>
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
				<text class="empty-text">请选择院校进行对比</text>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { buildApiUrl } from '@/utils/request.js'

const schools = ref([])
const analysis = ref('')

onLoad((options) => {
	// 从URL参数获取院校ID
	if (options.schoolIds) {
		const schoolIds = JSON.parse(decodeURIComponent(options.schoolIds))
		loadCompareData(schoolIds)
	}
})

const loadCompareData = async (schoolIds) => {
	try {
		console.log('请求对比数据，schoolIds:', schoolIds)
		
		const res = await uni.request({
			url: buildApiUrl('/compare/schools'),
			method: 'POST',
			data: { schoolIds },
			header: {
				'Content-Type': 'application/json'
			}
		})
		
		console.log('对比接口响应:', res)
		
		if (res.data.code === 200) {
			schools.value = res.data.data.schools
			analysis.value = res.data.data.analysis
		} else {
			console.error('对比失败:', res.data)
			uni.showToast({
				title: res.data.message || '加载失败',
				icon: 'none'
			})
		}
	} catch (error) {
		console.error('加载对比数据失败:', error)
		uni.showToast({
			title: '加载失败: ' + (error.message || '网络错误'),
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

.school-cards {
	display: flex;
	gap: 20rpx;
	margin-bottom: 30rpx;
	max-width: 100%;
	box-sizing: border-box;
}

.school-card {
	flex: 1;
	background: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	box-sizing: border-box;
	max-width: 100%;
}

.school-logo {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
}

.school-logo-placeholder {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%);
}

.school-name {
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

.tags {
	display: flex;
	flex-wrap: wrap;
	gap: 8rpx;
	justify-content: center;
}

.tag {
	padding: 6rpx 16rpx;
	border-radius: 8rpx;
	font-size: 22rpx;
	font-weight: 500;
}

.tag-syl {
	background: #ffefdf;
	color: #ff8c00;
}

.tag-211 {
	background: #eafff3;
	color: #00c853;
}

.tag-zhx {
	background: #e0f6ff;
	color: #00c6ef;
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
