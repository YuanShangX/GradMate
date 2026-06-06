<template>
	<view class="page">
		<!-- 顶部Tab -->
		<view class="top-tabs">
			<view class="tab-item" :class="{ active: currentTab === 'plan' }" @click="switchTab('plan')">
				<text class="tab-text">备考计划</text>
			</view>
			<view class="tab-item active" :class="{ active: currentTab === 'record' }" @click="switchTab('record')">
				<text class="tab-text">学习记录</text>
			</view>
		</view>

		<!-- 用户信息卡片 -->
		<view class="user-card">
			<view class="user-info">
				<image class="user-avatar" :src="userAvatar" mode="aspectFill"></image>
				<text class="user-name">{{ userName }}</text>
			</view>
			<view class="user-badge">
				<text class="badge-text">坚持中</text>
			</view>
		</view>

		<!-- 统计数据卡片 -->
		<view class="stats-card">
			<view class="stat-item">
				<text class="stat-value">{{ todayStudy }}</text>
				<text class="stat-label">今日学习</text>
			</view>
			<view class="stat-divider"></view>
			<view class="stat-item">
				<text class="stat-value">{{ totalHours }}</text>
				<text class="stat-label">本周学习</text>
			</view>
			<view class="stat-divider"></view>
			<view class="stat-item">
				<text class="stat-value">{{ totalDays }}</text>
				<text class="stat-label">累计天数</text>
			</view>
		</view>

		<!-- Tab切换 -->
		<view class="period-tabs">
			<text 
				v-for="tab in periodTabs" 
				:key="tab"
				class="period-tab"
				:class="{ active: currentPeriod === tab }"
				@click="currentPeriod = tab"
			>{{ tab }}</text>
		</view>

		<!-- 学习时长趋势图 -->
		<view class="chart-container">
			<view class="chart-wrapper">
				<view class="chart-y-axis">
					<text class="y-label">1.4h</text>
					<text class="y-label">1.2h</text>
					<text class="y-label">1.0h</text>
					<text class="y-label">0.8h</text>
					<text class="y-label">0.4h</text>
					<text class="y-label">0h</text>
				</view>
				<view class="chart-content">
					<view class="chart-grid">
						<view class="grid-line" v-for="i in 6" :key="i"></view>
					</view>
					<view class="chart-line">
						<view 
							v-for="(point, index) in chartData" 
							:key="index"
							class="chart-point"
							:style="{ 
								left: point.x + '%', 
								bottom: point.y + '%',
								backgroundColor: point.isToday ? '#FF9500' : '#FFD700'
							}"
						>
							<view v-if="point.isToday" class="today-label">今日</view>
						</view>
						<svg class="chart-path" viewBox="0 0 100 100" preserveAspectRatio="none">
							<polyline 
								:points="chartPoints" 
								fill="none" 
								stroke="#FFD700" 
								stroke-width="2"
							/>
						</svg>
					</view>
					<view class="chart-x-axis">
						<text 
							v-for="label in xLabels" 
							:key="label"
							class="x-label"
						>{{ label }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 学习概览 -->
		<view class="overview-section">
			<text class="section-title">学习概览</text>
			<view class="overview-grid">
				<view class="overview-item">
					<text class="overview-value">{{ stats.weeklyDays }}</text>
					<text class="overview-label">周学习</text>
				</view>
				<view class="overview-item">
					<text class="overview-value">{{ stats.monthlyDays }}</text>
					<text class="overview-label">本月前期</text>
				</view>
				<view class="overview-item">
					<text class="overview-value">{{ stats.averageDays }}</text>
					<text class="overview-label">平均前期</text>
				</view>
				<view class="overview-item">
					<text class="overview-value">{{ stats.mustDays }}</text>
					<text class="overview-label">必修学习</text>
				</view>
				<view class="overview-item">
					<text class="overview-value">{{ stats.longTermDays }}</text>
					<text class="overview-label">长期坚持学习</text>
				</view>
				<view class="overview-item">
					<text class="overview-value">{{ stats.eachDays }}</text>
					<text class="overview-label">每日学习</text>
				</view>
			</view>
		</view>

		<!-- 学习类型分布 -->
		<view class="type-section">
			<view class="type-chart">
				<view class="donut-chart">
					<view class="donut-center">
						<text class="donut-label">共计</text>
						<text class="donut-value">100%</text>
					</view>
				</view>
			</view>
			<view class="type-legend">
				<view 
					v-for="type in typeList" 
					:key="type.name"
					class="legend-item"
				>
					<view class="legend-dot" :style="{ backgroundColor: type.color }"></view>
					<text class="legend-name">{{ type.name }}</text>
					<text class="legend-value">{{ type.duration }}</text>
				</view>
			</view>
		</view>

		<!-- 分享按钮 -->
		<view class="share-btn-container">
			<button class="share-btn" @click="shareRecord">
				<text class="share-text">分享我的成长</text>
			</button>
		</view>
	</view>
</template>

<script>
import { get } from '@/utils/request.js';

export default {
	data() {
		return {
			userId: 1,
			currentTab: 'record',
			currentPeriod: '日',
			periodTabs: ['日', '周', '总计'],
			userName: '用户07UXIo',
			userAvatar: '/static/logo.png',
			todayStudy: '1',
			totalHours: '2.46',
			totalDays: '23',
			stats: {
				weeklyDays: 0,
				monthlyDays: 0,
				averageDays: 0,
				mustDays: 0,
				longTermDays: 0,
				eachDays: 0
			},
			chartData: [],
			xLabels: [],
			typeList: [
				{ name: '听课', duration: '0min', color: '#4A90E2' },
				{ name: '刷题', duration: '0min', color: '#FF6B6B' },
				{ name: '闪过', duration: '0min', color: '#FF4444' },
				{ name: '阅读', duration: '0min', color: '#FFD700' },
				{ name: '长难句学习', duration: '0min', color: '#9B59B6' },
				{ name: '专业', duration: '0min', color: '#3498DB' },
				{ name: '其他', duration: '1min', color: '#1ABC9C' }
			]
		};
	},
	
	computed: {
		chartPoints() {
			return this.chartData.map(p => `${p.x},${100 - p.y}`).join(' ');
		}
	},
	
	onShow() {
		this.userId = uni.getStorageSync('userId') || 1;
		this.loadAllData();
	},
	
	methods: {
		async loadAllData() {
			await Promise.all([
				this.loadStats(),
				this.loadChartData(),
				this.loadTypeStats()
			]);
		},
		
		async loadStats() {
			try {
				const res = await get(`/study/stats?userId=${this.userId}`);
				if (res.code === 200) {
					const data = res.data;
					this.todayStudy = Math.floor((data.todayDuration || 0) / 60) || '1';
					this.totalHours = ((data.totalDuration || 0) / 60).toFixed(2);
					this.totalDays = data.totalDays || 23;
					
					this.stats = {
						weeklyDays: data.weeklyDays || 0,
						monthlyDays: data.monthlyDays || 0,
						averageDays: data.averageDays || 0,
						mustDays: 0,
						longTermDays: 0,
						eachDays: 0
					};
				}
			} catch (error) {
				console.error('加载统计失败:', error);
			}
		},
		
		async loadChartData() {
			try {
				const days = this.currentPeriod === '日' ? 7 : 30;
				const today = new Date();
				const startDate = new Date(today);
				startDate.setDate(today.getDate() - days + 1);
				
				const res = await get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(startDate)}&endDate=${this.formatDate(today)}`);
				
				if (res.code === 200) {
					const dataMap = {};
					res.data.forEach(record => {
						const date = record.checkDate.split(' ')[0];
						dataMap[date] = (dataMap[date] || 0) + (record.duration || 0);
					});
					
					const maxDuration = Math.max(...Object.values(dataMap), 1);
					this.chartData = [];
					this.xLabels = [];
					
					for (let i = 0; i < days; i++) {
						const date = new Date(startDate);
						date.setDate(startDate.getDate() + i);
						const dateStr = this.formatDate(date);
						const duration = dataMap[dateStr] || 0;
						const isToday = this.isSameDate(date, today);
						
						this.chartData.push({
							x: (i / (days - 1)) * 100,
							y: (duration / maxDuration) * 80 + 10,
							isToday: isToday
						});
						
						if (days === 7) {
							this.xLabels.push(`${date.getMonth() + 1}/${date.getDate()}`);
						} else if (i % 5 === 0) {
							this.xLabels.push(`${date.getMonth() + 1}/${date.getDate()}`);
						}
					}
				}
			} catch (error) {
				console.error('加载图表数据失败:', error);
			}
		},
		
		async loadTypeStats() {
			try {
				const today = new Date();
				const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
				
				const res = await get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(monthStart)}&endDate=${this.formatDate(today)}`);
				
				if (res.code === 200) {
					const typeMap = {};
					res.data.forEach(record => {
						const type = record.studyType || 'other';
						typeMap[type] = (typeMap[type] || 0) + (record.duration || 0);
					});
					
					const typeNameMap = {
						major: '专业',
						english: '听课',
						math: '刷题',
						coding: '阅读',
						practice: '闪过',
						reading: '长难句学习',
						other: '其他'
					};
					
					this.typeList = this.typeList.map(item => {
						const typeKey = Object.keys(typeNameMap).find(k => typeNameMap[k] === item.name) || 'other';
						const duration = typeMap[typeKey] || 0;
						return {
							...item,
							duration: duration > 0 ? `${duration}min` : '0min'
						};
					});
				}
			} catch (error) {
				console.error('加载类型统计失败:', error);
			}
		},
		
		switchTab(tab) {
			if (tab === 'plan') {
				uni.navigateTo({
					url: '/pages/study/index'
				});
			}
		},
		
		shareRecord() {
			uni.showToast({
				title: '分享功能开发中',
				icon: 'none'
			});
		},
		
		formatDate(date) {
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		
		isSameDate(date1, date2) {
			return this.formatDate(date1) === this.formatDate(date2);
		}
	}
};
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: linear-gradient(180deg, #4A90E2 0%, #FFFFFF 40%);
	padding-bottom: 40rpx;
}

/* 顶部Tab */
.top-tabs {
	display: flex;
	background-color: transparent;
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 20rpx 0;
}

.tab-text {
	font-size: 32rpx;
	color: rgba(255, 255, 255, 0.7);
	font-weight: 500;
}

.tab-item.active .tab-text {
	color: #FFFFFF;
	font-weight: 600;
	border-bottom: 4rpx solid #FFFFFF;
	padding-bottom: 8rpx;
}

/* 用户信息卡片 */
.user-card {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	margin: 0 30rpx 20rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 20rpx;
	backdrop-filter: blur(10rpx);
}

.user-info {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.user-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	border: 4rpx solid #FFFFFF;
}

.user-name {
	font-size: 32rpx;
	color: #FFFFFF;
	font-weight: 600;
}

.user-badge {
	background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
	padding: 10rpx 25rpx;
	border-radius: 30rpx;
}

.badge-text {
	font-size: 24rpx;
	color: #FFFFFF;
	font-weight: 600;
}

/* 统计数据卡片 */
.stats-card {
	display: flex;
	align-items: center;
	justify-content: space-around;
	background: #FFFFFF;
	margin: 0 30rpx 30rpx;
	padding: 40rpx 20rpx;
	border-radius: 24rpx;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

.stat-item {
	text-align: center;
}

.stat-value {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.stat-label {
	font-size: 24rpx;
	color: #999;
}

.stat-divider {
	width: 2rpx;
	height: 60rpx;
	background-color: #E0E0E0;
}

/* 周期Tab */
.period-tabs {
	display: flex;
	gap: 20rpx;
	padding: 0 30rpx 20rpx;
}

.period-tab {
	padding: 15rpx 30rpx;
	font-size: 26rpx;
	color: #666;
	background-color: #F5F5F5;
	border-radius: 30rpx;
}

.period-tab.active {
	color: #FFFFFF;
	background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
	font-weight: 600;
}

/* 图表容器 */
.chart-container {
	background: #FFFFFF;
	margin: 0 30rpx 30rpx;
	padding: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.chart-wrapper {
	display: flex;
	gap: 20rpx;
}

.chart-y-axis {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 20rpx 0;
}

.y-label {
	font-size: 20rpx;
	color: #999;
}

.chart-content {
	flex: 1;
	position: relative;
	height: 400rpx;
}

.chart-grid {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 60rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.grid-line {
	height: 1rpx;
	background-color: #F0F0F0;
}

.chart-line {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 60rpx;
}

.chart-path {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
}

.chart-point {
	position: absolute;
	width: 16rpx;
	height: 16rpx;
	border-radius: 50%;
	transform: translate(-50%, 50%);
	box-shadow: 0 2rpx 8rpx rgba(255, 215, 0, 0.5);
}

.today-label {
	position: absolute;
	top: -40rpx;
	left: 50%;
	transform: translateX(-50%);
	background-color: #FF9500;
	color: #FFFFFF;
	font-size: 20rpx;
	padding: 6rpx 12rpx;
	border-radius: 10rpx;
	white-space: nowrap;
}

.chart-x-axis {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	display: flex;
	justify-content: space-between;
	padding: 10rpx 0;
}

.x-label {
	font-size: 20rpx;
	color: #999;
}

/* 学习概览 */
.overview-section {
	background: #FFFFFF;
	margin: 0 30rpx 30rpx;
	padding: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 25rpx;
	display: block;
}

.overview-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 30rpx;
}

.overview-item {
	text-align: center;
}

.overview-value {
	display: block;
	font-size: 40rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.overview-label {
	font-size: 22rpx;
	color: #999;
}

/* 学习类型分布 */
.type-section {
	background: #FFFFFF;
	margin: 0 30rpx 30rpx;
	padding: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
	display: flex;
	gap: 40rpx;
}

.type-chart {
	width: 200rpx;
	height: 200rpx;
}

.donut-chart {
	width: 100%;
	height: 100%;
	border-radius: 50%;
	background: conic-gradient(
		#1ABC9C 0deg 51.84deg,
		#E0E0E0 51.84deg 360deg
	);
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
}

.donut-center {
	width: 120rpx;
	height: 120rpx;
	background: #FFFFFF;
	border-radius: 50%;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.donut-label {
	font-size: 20rpx;
	color: #999;
	margin-bottom: 5rpx;
}

.donut-value {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.type-legend {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.legend-item {
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.legend-dot {
	width: 16rpx;
	height: 16rpx;
	border-radius: 50%;
}

.legend-name {
	flex: 1;
	font-size: 24rpx;
	color: #666;
}

.legend-value {
	font-size: 24rpx;
	color: #333;
	font-weight: 500;
}

/* 分享按钮 */
.share-btn-container {
	padding: 0 30rpx;
}

.share-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
	color: #FFFFFF;
	border: none;
	border-radius: 44rpx;
	font-size: 30rpx;
	font-weight: 600;
	box-shadow: 0 8rpx 20rpx rgba(74, 144, 226, 0.3);
}

.share-text {
	font-size: 28rpx;
}
</style>
