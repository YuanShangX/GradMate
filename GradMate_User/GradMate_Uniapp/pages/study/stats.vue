<template>
	<view class="page">
		<!-- 白色导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">学习统计</text>
		</view>
		
		<scroll-view class="content" scroll-y="true">
			<!-- 核心数据卡片 -->
			<view class="stats-overview">
				<view class="overview-item">
					<text class="overview-value">{{ formatHours(totalStats.totalDuration) }}</text>
					<text class="overview-label">累计学习</text>
				</view>
				<view class="overview-divider"></view>
				<view class="overview-item">
					<text class="overview-value">{{ totalStats.continuousDays || 0 }}</text>
					<text class="overview-label">连续打卡</text>
				</view>
				<view class="overview-divider"></view>
				<view class="overview-item">
					<text class="overview-value">{{ totalStats.totalDays || 0 }}</text>
					<text class="overview-label">累计天数</text>
				</view>
			</view>
			
			<!-- 本周vs上周对比 -->
			<view class="info-card">
				<view class="card-title">
					<view class="title-bar"></view>
					<text class="title-text">本周 vs 上周</text>
					<text class="title-subtitle">{{ weekRange }}</text>
				</view>
				<view class="compare-container">
					<view class="compare-item">
						<text class="compare-label">本周学习</text>
						<text class="compare-value">{{ formatHours(weekStats.thisWeek) }}</text>
						<view class="compare-change" :class="{ positive: weekStats.change >= 0, negative: weekStats.change < 0 }">
							<text class="change-text">{{ weekStats.change >= 0 ? '+' : '' }}{{ weekStats.change }}%</text>
						</view>
					</view>
					<view class="compare-divider"></view>
					<view class="compare-item">
						<text class="compare-label">上周学习</text>
						<text class="compare-value">{{ formatHours(weekStats.lastWeek) }}</text>
						<text class="compare-sub">{{ weekStats.lastWeekDays }}天打卡</text>
					</view>
				</view>
			</view>
			
			<!-- 学习时长趋势 - 折线图 -->
			<view class="info-card">
				<view class="card-title">
					<view class="title-bar"></view>
					<text class="title-text">学习时长趋势</text>
					<view class="tab-switcher">
						<text 
							v-for="tab in trendTabs" 
							:key="tab"
							class="tab-item"
							:class="{ active: currentTrendTab === tab }"
							@click="currentTrendTab = tab"
						>{{ tab }}</text>
					</view>
				</view>
				<view v-if="trendData.length > 0" class="chart-container">
					<view class="line-chart">
						<view class="y-axis">
							<text class="y-label" v-for="(val, idx) in yAxisLabels" :key="idx">{{ val }}</text>
						</view>
						<view class="chart-area">
							<view class="grid-lines">
								<view class="grid-line" v-for="i in 5" :key="i"></view>
							</view>
							<svg class="line-svg" :viewBox="`0 0 ${svgWidth} ${svgHeight}`">
								<polyline 
									:points="linePoints" 
									fill="none" 
									stroke="#4A90E2" 
									stroke-width="2"
									stroke-linecap="round"
									stroke-linejoin="round"
								/>
								<polygon 
									:points="areaPoints" 
									fill="url(#areaGradient)"
								/>
								<circle 
									v-for="(point, index) in dataPoints" 
									:key="index"
									:cx="point.x" 
									:cy="point.y" 
									r="3"
									fill="#4A90E2"
									stroke="#fff"
									stroke-width="2"
								/>
								<defs>
									<linearGradient id="areaGradient" x1="0%" y1="0%" x2="0%" y2="100%">
										<stop offset="0%" style="stop-color:#4A90E2;stop-opacity:0.2" />
										<stop offset="100%" style="stop-color:#4A90E2;stop-opacity:0.02" />
									</linearGradient>
								</defs>
							</svg>
							<view class="x-axis">
								<text 
									class="x-label" 
									v-for="(item, index) in trendData" 
									:key="index"
								>{{ item.label }}</text>
							</view>
						</view>
					</view>
				</view>
				<view v-else class="chart-empty">
					<text class="empty-text">暂无学习数据</text>
					<view class="empty-btn" @click="goToCalendar">
						<text class="empty-btn-text">去打卡</text>
					</view>
				</view>
			</view>
			
			<!-- 学习类型分布 - 饼图 -->
			<view class="info-card" v-if="typeStats.length > 0">
				<view class="card-title">
					<view class="title-bar"></view>
					<text class="title-text">学习类型分布</text>
					<text class="title-subtitle">本月数据</text>
				</view>
				<view class="pie-container">
					<view class="pie-chart">
						<svg :viewBox="`0 0 ${pieSize} ${pieSize}`" class="pie-svg">
							<circle 
								v-for="(segment, index) in pieSegments" 
								:key="index"
								:cx="pieSize / 2" 
								:cy="pieSize / 2" 
								:r="pieRadius"
								fill="none"
								:stroke="segment.color"
								:stroke-width="pieStrokeWidth"
								:stroke-dasharray="`${segment.length} ${pieCircumference}`"
								:stroke-dashoffset="segment.offset"
								:transform="`rotate(-90 ${pieSize / 2} ${pieSize / 2})`"
							/>
							<text 
								:x="pieSize / 2" 
								:y="pieSize / 2 - 8" 
								text-anchor="middle" 
								font-size="20" 
								fill="#999"
							>本月学习</text>
							<text 
								:x="pieSize / 2" 
								:y="pieSize / 2 + 16" 
								text-anchor="middle" 
								font-size="28" 
								font-weight="bold"
								fill="#333"
							>{{ formatHours(totalTypeTime) }}</text>
						</svg>
					</view>
					<view class="pie-legend">
						<view 
							v-for="type in typeStats" 
							:key="type.type"
							class="legend-item"
						>
							<view class="legend-dot" :style="{ backgroundColor: type.color }"></view>
							<text class="legend-name">{{ type.name }}</text>
							<text class="legend-percent">{{ type.percent }}%</text>
							<text class="legend-duration">{{ formatHours(type.duration) }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 学习习惯分析 -->
			<view class="info-card">
				<view class="card-title">
					<view class="title-bar"></view>
					<text class="title-text">学习习惯分析</text>
				</view>
				<view class="habit-grid">
					<view class="habit-item">
						<text class="habit-label">最佳时段</text>
						<text class="habit-value">{{ bestTimeSlot }}</text>
					</view>
					<view class="habit-item">
						<text class="habit-label">日均学习</text>
						<text class="habit-value">{{ formatHours(avgDaily) }}</text>
					</view>
					<view class="habit-item">
						<text class="habit-label">完成率</text>
						<text class="habit-value">{{ completionRate }}%</text>
					</view>
				</view>
			</view>
			
			<!-- 底部提示 -->
			<view class="footer-tip">
				<text class="tip-text">数据每日更新，仅供参考</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { get } from '@/utils/request.js';

export default {
	data() {
		return {
			userId: 1,
			totalStats: {},
			weekStats: {
				thisWeek: 0,
				lastWeek: 0,
				change: 0,
				lastWeekDays: 0
			},
			trendData: [],
			typeStats: [],
			currentTrendTab: '7天',
			trendTabs: ['7天', '30天'],
			bestTimeSlot: '暂无数据',
			avgDaily: 0,
			completionRate: 0,
			svgWidth: 600,
			svgHeight: 180,
			pieSize: 200,
			pieRadius: 70,
			pieStrokeWidth: 40
		};
	},
	
	computed: {
		weekRange() {
			const today = new Date();
			const weekStart = new Date(today);
			weekStart.setDate(today.getDate() - today.getDay());
			const weekEnd = new Date(weekStart);
			weekEnd.setDate(weekStart.getDate() + 6);
			return `${this.formatShortDate(weekStart)} - ${this.formatShortDate(weekEnd)}`;
		},
		
		yAxisLabels() {
			if (this.trendData.length === 0) return ['0', '1h', '2h', '3h', '4h'];
			const maxDuration = Math.max(...this.trendData.map(d => d.duration), 60);
			const maxHours = Math.ceil(maxDuration / 60);
			const step = Math.ceil(maxHours / 4);
			return ['0', `${step}h`, `${step * 2}h`, `${step * 3}h`, `${step * 4}h`].reverse();
		},
		
		dataPoints() {
			if (this.trendData.length === 0) return [];
			const maxDuration = Math.max(...this.trendData.map(d => d.duration), 60);
			const padding = 10;
			const width = this.svgWidth - padding * 2;
			const height = this.svgHeight - padding * 2;
			const stepX = width / (this.trendData.length - 1 || 1);
			
			return this.trendData.map((item, index) => ({
				x: padding + index * stepX,
				y: padding + height - (item.duration / maxDuration) * height
			}));
		},
		
		linePoints() {
			return this.dataPoints.map(p => `${p.x},${p.y}`).join(' ');
		},
		
		areaPoints() {
			if (this.dataPoints.length === 0) return '';
			const points = this.dataPoints.map(p => `${p.x},${p.y}`).join(' ');
			const lastPoint = this.dataPoints[this.dataPoints.length - 1];
			const firstPoint = this.dataPoints[0];
			return `${points} ${lastPoint.x},${this.svgHeight - 10} ${firstPoint.x},${this.svgHeight - 10}`;
		},
		
		totalTypeTime() {
			return this.typeStats.reduce((sum, type) => sum + type.duration, 0);
		},
		
		pieCircumference() {
			return 2 * Math.PI * this.pieRadius;
		},
		
		pieSegments() {
			let currentOffset = 0;
			return this.typeStats.map(type => {
				const length = (type.percent / 100) * this.pieCircumference;
				const segment = {
					color: type.color,
					length: length,
					offset: -currentOffset
				};
				currentOffset += length;
				return segment;
			});
		}
	},
	
	onShow() {
		this.userId = uni.getStorageSync('userId') || 1;
		this.loadAllStats();
	},
	
	watch: {
		currentTrendTab() {
			this.loadTrendData();
		}
	},
	
	methods: {
		async loadAllStats() {
			await Promise.all([
				this.loadTotalStats(),
				this.loadWeekComparison(),
				this.loadTrendData(),
				this.loadTypeStats()
			]);
		},
		
		async loadTotalStats() {
			try {
				const res = await get(`/study/stats?userId=${this.userId}`);
				if (res.code === 200) {
					this.totalStats = res.data;
					this.calculateHabitStats();
				}
			} catch (error) {
				console.error('加载统计失败:', error);
			}
		},
		
		async loadWeekComparison() {
			try {
				const today = new Date();
				const thisWeekStart = new Date(today);
				thisWeekStart.setDate(today.getDate() - today.getDay());
				
				const lastWeekStart = new Date(thisWeekStart);
				lastWeekStart.setDate(thisWeekStart.getDate() - 7);
				const lastWeekEnd = new Date(thisWeekStart);
				lastWeekEnd.setDate(thisWeekStart.getDate() - 1);
				
				const [thisWeekRes, lastWeekRes] = await Promise.all([
					get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(thisWeekStart)}&endDate=${this.formatDate(today)}`),
					get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(lastWeekStart)}&endDate=${this.formatDate(lastWeekEnd)}`)
				]);
				
				if (thisWeekRes.code === 200 && lastWeekRes.code === 200) {
					const thisWeekTotal = thisWeekRes.data.reduce((sum, r) => sum + (r.duration || 0), 0);
					const lastWeekTotal = lastWeekRes.data.reduce((sum, r) => sum + (r.duration || 0), 0);
					
					this.weekStats = {
						thisWeek: thisWeekTotal,
						lastWeek: lastWeekTotal,
						change: lastWeekTotal > 0 ? Math.round(((thisWeekTotal - lastWeekTotal) / lastWeekTotal) * 100) : 0,
						lastWeekDays: lastWeekRes.data.length
					};
				}
			} catch (error) {
				console.error('加载周对比失败:', error);
			}
		},
		
		async loadTrendData() {
			try {
				const days = this.currentTrendTab === '7天' ? 7 : 30;
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
					
					this.trendData = [];
					for (let i = 0; i < days; i++) {
						const date = new Date(startDate);
						date.setDate(startDate.getDate() + i);
						const dateStr = this.formatDate(date);
						this.trendData.push({
							label: this.currentTrendTab === '7天' ? this.getWeekday(date) : `${date.getMonth() + 1}/${date.getDate()}`,
							duration: dataMap[dateStr] || 0
						});
					}
				}
			} catch (error) {
				console.error('加载趋势数据失败:', error);
			}
		},
		
		async loadTypeStats() {
			try {
				const today = new Date();
				const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
				
				const res = await get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(monthStart)}&endDate=${this.formatDate(today)}`);
				
				if (res.code === 200) {
					const typeMap = {};
					let total = 0;
					
					res.data.forEach(record => {
						const type = record.studyType || 'other';
						typeMap[type] = (typeMap[type] || 0) + (record.duration || 0);
						total += record.duration || 0;
					});
					
					const typeConfig = {
						major: { name: '专业课', color: '#4A90E2' },
						english: { name: '英语', color: '#50C878' },
						math: { name: '数学', color: '#FF6B6B' },
						coding: { name: '编程', color: '#9B59B6' },
						practice: { name: '刷题', color: '#F39C12' },
						reading: { name: '阅读', color: '#3498DB' },
						other: { name: '其他', color: '#95A5A6' }
					};
					
					this.typeStats = Object.entries(typeMap)
						.map(([type, duration]) => ({
							type,
							...typeConfig[type],
							duration,
							percent: total > 0 ? Math.round((duration / total) * 100) : 0
						}))
						.sort((a, b) => b.duration - a.duration);
				}
			} catch (error) {
				console.error('加载类型统计失败:', error);
			}
		},
		
		calculateHabitStats() {
			const totalDays = this.totalStats.totalDays || 1;
			this.avgDaily = Math.round((this.totalStats.totalDuration || 0) / totalDays);
			
			const targetDaily = 120;
			this.completionRate = Math.min(100, Math.round((this.avgDaily / targetDaily) * 100));
			
			this.bestTimeSlot = '9:00-12:00';
		},
		
		formatHours(minutes) {
			if (!minutes) return '0.0h';
			return (minutes / 60).toFixed(1) + 'h';
		},
		
		formatDate(date) {
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		
		formatShortDate(date) {
			return `${date.getMonth() + 1}/${date.getDate()}`;
		},
		
		getWeekday(date) {
			const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
			return days[date.getDay()];
		},
		
		goBack() {
			uni.navigateBack();
		},
		
		goToCalendar() {
			uni.navigateTo({
				url: '/pages/study/calendar'
			});
		}
	}
};
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #F5F7FA;
}

/* 导航栏 */
.navbar {
	background: #FFFFFF;
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
	display: flex;
	align-items: center;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	position: sticky;
	top: 0;
	z-index: 100;
}

.nav-back {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	font-size: 48rpx;
	color: #333;
	font-weight: 300;
}

.nav-title {
	flex: 1;
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	text-align: center;
	margin-right: 60rpx;
}

.content {
	height: calc(100vh - 100rpx);
	padding-bottom: 40rpx;
}

/* 核心数据概览 */
.stats-overview {
	background: #FFFFFF;
	margin: 30rpx;
	border-radius: 16rpx;
	padding: 40rpx 30rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);
}

.overview-item {
	flex: 1;
	text-align: center;
}

.overview-value {
	display: block;
	font-size: 44rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 12rpx;
}

.overview-label {
	font-size: 24rpx;
	color: #999;
}

.overview-divider {
	width: 2rpx;
	height: 60rpx;
	background-color: #E8E8E8;
}

/* 信息卡片 */
.info-card {
	background: #FFFFFF;
	margin: 0 30rpx 30rpx;
	border-radius: 16rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);
}

.card-title {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
	gap: 15rpx;
}

.title-bar {
	width: 6rpx;
	height: 28rpx;
	background: #4A90E2;
	border-radius: 3rpx;
}

.title-text {
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
	flex: 1;
}

.title-subtitle {
	font-size: 24rpx;
	color: #999;
}

/* 对比容器 */
.compare-container {
	display: flex;
	align-items: center;
	gap: 30rpx;
}

.compare-item {
	flex: 1;
	text-align: center;
}

.compare-label {
	font-size: 24rpx;
	color: #999;
	display: block;
	margin-bottom: 15rpx;
}

.compare-value {
	font-size: 48rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 12rpx;
}

.compare-change {
	display: inline-block;
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: 600;
}

.compare-change.positive {
	background-color: #E8F5E9;
	color: #4CAF50;
}

.compare-change.negative {
	background-color: #FFEBEE;
	color: #F44336;
}

.change-text {
	font-size: 24rpx;
}

.compare-sub {
	font-size: 22rpx;
	color: #999;
	display: block;
	margin-top: 8rpx;
}

.compare-divider {
	width: 2rpx;
	height: 100rpx;
	background-color: #E8E8E8;
}

/* Tab切换器 */
.tab-switcher {
	display: flex;
	gap: 8rpx;
	background-color: #F5F7FA;
	border-radius: 8rpx;
	padding: 4rpx;
}

.tab-item {
	padding: 8rpx 20rpx;
	font-size: 24rpx;
	color: #666;
	border-radius: 6rpx;
	transition: all 0.3s;
}

.tab-item.active {
	background-color: #FFFFFF;
	color: #4A90E2;
	font-weight: 600;
}

/* 图表容器 */
.chart-container {
	padding: 20rpx 0;
}

.line-chart {
	display: flex;
	gap: 15rpx;
}

.y-axis {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 10rpx 0;
	width: 50rpx;
}

.y-label {
	font-size: 20rpx;
	color: #999;
	text-align: right;
}

.chart-area {
	flex: 1;
	position: relative;
}

.grid-lines {
	position: absolute;
	top: 10rpx;
	left: 0;
	right: 0;
	bottom: 50rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.grid-line {
	height: 1rpx;
	background-color: #F0F0F0;
}

.line-svg {
	width: 100%;
	height: 180rpx;
	display: block;
}

.x-axis {
	display: flex;
	justify-content: space-between;
	margin-top: 15rpx;
	padding: 0 10rpx;
}

.x-label {
	font-size: 22rpx;
	color: #999;
	text-align: center;
	flex: 1;
}

.chart-empty {
	text-align: center;
	padding: 60rpx 0;
}

.empty-text {
	font-size: 26rpx;
	color: #999;
	display: block;
	margin-bottom: 30rpx;
}

.empty-btn {
	display: inline-block;
	background: #4A90E2;
	color: #fff;
	border-radius: 40rpx;
	padding: 16rpx 50rpx;
}

.empty-btn-text {
	font-size: 28rpx;
	color: #FFFFFF;
}

/* 饼图容器 */
.pie-container {
	display: flex;
	gap: 40rpx;
	align-items: center;
}

.pie-chart {
	width: 200rpx;
	height: 200rpx;
	flex-shrink: 0;
}

.pie-svg {
	width: 100%;
	height: 100%;
}

.pie-legend {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 18rpx;
}

.legend-item {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.legend-dot {
	width: 10rpx;
	height: 10rpx;
	border-radius: 50%;
	flex-shrink: 0;
}

.legend-name {
	font-size: 26rpx;
	color: #333;
	flex: 1;
}

.legend-percent {
	font-size: 24rpx;
	color: #4A90E2;
	font-weight: 600;
	width: 60rpx;
	text-align: right;
}

.legend-duration {
	font-size: 24rpx;
	color: #999;
	width: 70rpx;
	text-align: right;
}

/* 习惯分析 */
.habit-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 20rpx;
}

.habit-item {
	text-align: center;
	padding: 30rpx 20rpx;
	background-color: #F8F9FA;
	border-radius: 12rpx;
}

.habit-label {
	font-size: 24rpx;
	color: #999;
	display: block;
	margin-bottom: 12rpx;
}

.habit-value {
	font-size: 32rpx;
	color: #333;
	font-weight: 600;
	display: block;
}

/* 底部提示 */
.footer-tip {
	text-align: center;
	padding: 40rpx 0;
}

.tip-text {
	font-size: 24rpx;
	color: #999;
}
</style>
