<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<view class="nav-title-wrapper">
				<text class="nav-title">打卡日历</text>
			</view>
			<view class="nav-placeholder"></view>
		</view>
		
		<!-- 统计卡片 -->
		<view class="stats-section">
			<view class="stat-card">
				<view class="stat-icon-wrapper stat-icon-blue">
					<view class="fire-icon"></view>
				</view>
				<view class="stat-content">
					<text class="stat-number">{{ stats.continuousDays || 0 }}</text>
					<text class="stat-label">连续打卡</text>
				</view>
			</view>
			<view class="stat-card">
				<view class="stat-icon-wrapper stat-icon-green">
					<view class="check-icon"></view>
				</view>
				<view class="stat-content">
					<text class="stat-number">{{ stats.monthlyDays || 0 }}</text>
					<text class="stat-label">本月打卡</text>
				</view>
			</view>
			<view class="stat-card">
				<view class="stat-icon-wrapper stat-icon-orange">
					<view class="clock-icon"></view>
				</view>
				<view class="stat-content">
					<text class="stat-number">{{ formatHours(stats.totalDuration) }}</text>
					<text class="stat-label">累计学习</text>
				</view>
			</view>
		</view>

		<!-- 日历卡片 -->
		<view class="calendar-card">
			<view class="calendar-header">
				<text class="calendar-title">{{ currentYear }}年{{ currentMonth }}月</text>
				<view class="month-nav">
					<view class="nav-btn" @click="prevMonth">
						<text class="nav-arrow">‹</text>
					</view>
					<view class="nav-btn" @click="nextMonth">
						<text class="nav-arrow">›</text>
					</view>
				</view>
			</view>

			<view class="calendar-grid">
				<view class="weekday-header">
					<text v-for="day in weekdays" :key="day" class="weekday">{{ day }}</text>
				</view>

				<view class="date-grid">
					<view 
						v-for="date in calendarDates" 
						:key="date.key"
						class="date-cell"
						:class="{ 
							'other-month': !date.isCurrentMonth,
							'today': date.isToday,
							'checked': date.hasRecord
						}"
						:style="{ backgroundColor: getDateColor(date) }"
						@click="selectDate(date)"
					>
						<text class="date-number" :class="{ 'white-text': date.hasRecord }">{{ date.day }}</text>
						<text v-if="date.duration" class="date-duration">{{ date.duration }}h</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 今日打卡按钮 -->
		<view v-if="!stats.todayChecked" class="checkin-btn-wrapper">
			<view class="checkin-btn" @click="showCheckInModal">
				<view class="btn-icon-wrapper">
					<view class="check-icon-btn"></view>
				</view>
				<text class="btn-text">今日打卡</text>
			</view>
		</view>

		<!-- 打卡详情弹窗 -->
		<view v-if="showDetail" class="picker-mask" @click="hideDetailModal">
			<view class="picker-content detail-modal" @click.stop>
				<view class="picker-header">
					<text class="picker-title">{{ selectedDate }}打卡详情</text>
					<text class="picker-close" @click="hideDetailModal">×</text>
				</view>
				
				<view v-if="selectedRecord" class="record-detail">
					<view class="detail-card">
						<view class="detail-icon-wrapper">
							<view class="clock-icon-detail"></view>
						</view>
						<view class="detail-info">
							<text class="detail-label">学习时长</text>
							<text class="detail-value">{{ selectedRecord.duration }}分钟</text>
						</view>
					</view>
					
					<view class="detail-card">
						<view class="detail-icon-wrapper">
							<text class="mood-stars">{{ getMoodEmoji(selectedRecord.mood) }}</text>
						</view>
						<view class="detail-info">
							<text class="detail-label">今日心情</text>
							<text class="detail-value">{{ getMoodText(selectedRecord.mood) }}</text>
						</view>
					</view>
					
					<view v-if="selectedRecord.studyType" class="detail-card">
						<view class="detail-icon-wrapper">
							<view class="book-icon"></view>
						</view>
						<view class="detail-info">
							<text class="detail-label">学习类型</text>
							<text class="detail-value">{{ getTypeName(selectedRecord.studyType) }}</text>
						</view>
					</view>
					
					<view v-if="selectedRecord.note" class="detail-note">
						<text class="note-label">学习备注</text>
						<text class="note-content">{{ selectedRecord.note }}</text>
					</view>
				</view>
				
				<view v-else class="no-record">
					<view class="no-record-icon-wrapper">
						<view class="empty-icon"></view>
					</view>
					<text class="no-record-text">该日期没有打卡记录</text>
				</view>
			</view>
		</view>

		<!-- 打卡弹窗 -->
		<view v-if="showCheckIn" class="picker-mask" @click="hideCheckInModal">
			<view class="picker-content checkin-modal" @click.stop>
				<view class="picker-header">
					<text class="picker-title">今日打卡</text>
					<text class="picker-close" @click="hideCheckInModal">×</text>
				</view>
				
				<!-- 快捷时长选择 -->
				<view class="form-section">
					<text class="section-title">学习时长</text>
					<view class="duration-grid">
						<view 
							v-for="duration in quickDurations" 
							:key="duration"
							class="duration-btn"
							:class="{ active: checkInForm.duration === duration }"
							@click="selectDuration(duration)"
						>
							<text class="duration-text">{{ duration }}分钟</text>
						</view>
					</view>
					<input 
						v-if="showCustomDuration"
						class="custom-input" 
						v-model.number="checkInForm.duration" 
						type="number" 
						placeholder="自定义时长"
					/>
				</view>
				
				<!-- 学习类型选择 -->
				<view class="form-section">
					<text class="section-title">学习类型</text>
					<scroll-view class="type-scroll" scroll-x>
						<view 
							v-for="type in studyTypes" 
							:key="type.value"
							class="type-btn"
							:class="{ active: checkInForm.studyType === type.value }"
							@click="selectType(type.value)"
						>
							<text class="type-name">{{ type.label }}</text>
						</view>
					</scroll-view>
				</view>
				
				<!-- 心情选择 -->
				<view class="form-section">
					<text class="section-title">今日心情</text>
					<view class="mood-selector">
						<text 
							v-for="i in 5" 
							:key="i" 
							class="mood-star" 
							:class="{ active: i <= checkInForm.mood }"
							@click="checkInForm.mood = i"
						>{{ i <= checkInForm.mood ? '⭐' : '☆' }}</text>
					</view>
				</view>
				
				<!-- 学习备注 -->
				<view class="form-section">
					<text class="section-title">学习备注</text>
					<textarea 
						class="form-textarea" 
						v-model="checkInForm.note" 
						placeholder="今天学了什么？有什么收获？"
						maxlength="200"
					/>
				</view>
				
				<button class="submit-btn" @click="submitCheckIn">提交打卡</button>
			</view>
		</view>
	</view>
</template>

<script>
import { get, post } from '@/utils/request.js';

export default {
	data() {
		return {
			userId: 1,
			stats: {},
			currentYear: new Date().getFullYear(),
			currentMonth: new Date().getMonth() + 1,
			monthlyRecords: [],
			showDetail: false,
			showCheckIn: false,
			showCustomDuration: false,
			selectedDate: '',
			selectedRecord: null,
			checkInForm: {
				duration: null,
				studyType: '',
				mood: 5,
				note: ''
			},
			weekdays: ['日', '一', '二', '三', '四', '五', '六'],
			quickDurations: [15, 25, 30, 45, 60],
			studyTypes: [
				{ value: 'major', label: '专业课' },
				{ value: 'english', label: '英语' },
				{ value: 'math', label: '数学' },
				{ value: 'coding', label: '编程' },
				{ value: 'practice', label: '刷题' },
				{ value: 'reading', label: '阅读' },
				{ value: 'other', label: '其他' }
			]
		}
	},
	computed: {
		calendarDates() {
			const year = this.currentYear;
			const month = this.currentMonth;
			
			const firstDay = new Date(year, month - 1, 1);
			const lastDay = new Date(year, month, 0);
			
			const startDate = new Date(firstDay);
			startDate.setDate(startDate.getDate() - firstDay.getDay());
			
			const endDate = new Date(lastDay);
			endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()));
			
			const dates = [];
			const current = new Date(startDate);
			const today = new Date();
			
			while (current <= endDate) {
				const dateStr = this.formatDate(current);
				const record = this.monthlyRecords.find(r => 
					this.formatDate(new Date(r.checkDate)) === dateStr
				);
				
				dates.push({
					key: dateStr,
					day: current.getDate(),
					date: new Date(current),
					dateStr: dateStr,
					isCurrentMonth: current.getMonth() === month - 1,
					isToday: this.isSameDate(current, today),
					hasRecord: !!record,
					duration: record ? (record.duration / 60).toFixed(1) : null,
					mood: record ? record.mood : null
				});
				
				current.setDate(current.getDate() + 1);
			}
			
			return dates;
		}
	},
	onShow() {
		this.userId = uni.getStorageSync('userId') || 1;
		this.loadStats();
		this.loadMonthlyRecords();
	},
	methods: {
		goBack() {
			uni.navigateBack();
		},
		
		async loadStats() {
			try {
				const res = await get(`/study/stats?userId=${this.userId}`);
				if (res.code === 200) {
					this.stats = res.data;
				}
			} catch (error) {
				console.error('加载统计数据失败:', error);
			}
		},

		async loadMonthlyRecords() {
			try {
				const res = await get(`/study/record/month?userId=${this.userId}&year=${this.currentYear}&month=${this.currentMonth}`);
				if (res.code === 200) {
					this.monthlyRecords = res.data;
				}
			} catch (error) {
				console.error('加载月度记录失败:', error);
			}
		},

		prevMonth() {
			if (this.currentMonth === 1) {
				this.currentYear--;
				this.currentMonth = 12;
			} else {
				this.currentMonth--;
			}
			this.loadMonthlyRecords();
		},

		nextMonth() {
			if (this.currentMonth === 12) {
				this.currentYear++;
				this.currentMonth = 1;
			} else {
				this.currentMonth++;
			}
			this.loadMonthlyRecords();
		},

		async selectDate(date) {
			if (!date.isCurrentMonth) return;
			
			this.selectedDate = this.formatDateChinese(date.date);
			this.selectedRecord = this.monthlyRecords.find(record => 
				this.formatDate(new Date(record.checkDate)) === date.dateStr
			);
			
			this.showDetail = true;
		},

		hideDetailModal() {
			this.showDetail = false;
		},

		showCheckInModal() {
			this.showCheckIn = true;
			this.showCustomDuration = false;
			this.checkInForm = {
				duration: null,
				studyType: '',
				mood: 5,
				note: ''
			};
		},

		hideCheckInModal() {
			this.showCheckIn = false;
		},

		selectDuration(duration) {
			if (duration === 'custom') {
				this.showCustomDuration = true;
				this.checkInForm.duration = null;
			} else {
				this.showCustomDuration = false;
				this.checkInForm.duration = duration;
			}
		},

		selectType(type) {
			this.checkInForm.studyType = type;
		},

		async submitCheckIn() {
			if (!this.checkInForm.duration || this.checkInForm.duration <= 0) {
				uni.showToast({
					title: '请选择学习时长',
					icon: 'none'
				});
				return;
			}

			try {
				const res = await post('/study/checkin', {
					userId: this.userId,
					duration: this.checkInForm.duration,
					studyType: this.checkInForm.studyType,
					mood: this.checkInForm.mood,
					note: this.checkInForm.note
				});

				if (res.code === 200) {
					uni.showToast({
						title: '打卡成功！',
						icon: 'success'
					});
					this.hideCheckInModal();
					this.loadStats();
					this.loadMonthlyRecords();
					uni.vibrateShort();
				} else {
					uni.showToast({
						title: res.message || '打卡失败',
						icon: 'none'
					});
				}
			} catch (error) {
				console.error('打卡失败:', error);
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				});
			}
		},

		getDateColor(date) {
			if (!date.hasRecord) return 'transparent';
			const duration = parseFloat(date.duration);
			if (duration >= 4) return '#4A90E2';
			if (duration >= 2) return '#7BA9E8';
			if (duration >= 1) return '#A5C5ED';
			return '#D0E3F7';
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

		formatDateChinese(date) {
			const month = date.getMonth() + 1;
			const day = date.getDate();
			return `${month}月${day}日`;
		},

		isSameDate(date1, date2) {
			return this.formatDate(date1) === this.formatDate(date2);
		},

		getMoodEmoji(mood) {
			const moods = ['', '★', '★★', '★★★', '★★★★', '★★★★★'];
			return moods[mood] || '';
		},

		getMoodText(mood) {
			const texts = ['', '很差', '一般', '不错', '很好', '超棒'];
			return texts[mood] || '不错';
		},

		getTypeIcon(type) {
			const typeObj = this.studyTypes.find(t => t.value === type);
			return typeObj ? typeObj.label : '其他';
		},

		getTypeName(type) {
			const typeObj = this.studyTypes.find(t => t.value === type);
			return typeObj ? typeObj.label : '其他';
		}
	}
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
	justify-content: space-between;
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

.nav-title-wrapper {
	flex: 1;
	display: flex;
	justify-content: center;
}

.nav-title {
	font-size: 34rpx;
	font-weight: 600;
	color: #333;
}

.nav-placeholder {
	width: 48rpx;
	flex-shrink: 0;
}

/* 统计卡片 */
.stats-section {
	padding: 20rpx 30rpx;
	display: flex;
	gap: 16rpx;
}

.stat-card {
	flex: 1;
	background: #ffffff;
	border-radius: 16rpx;
	padding: 24rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.stat-icon-wrapper {
	width: 56rpx;
	height: 56rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.stat-icon-blue {
	background: #e8f4ff;
}

.stat-icon-green {
	background: #e6f7ed;
}

.stat-icon-orange {
	background: #fff7e6;
}

/* 火焰图标 */
.fire-icon {
	width: 24rpx;
	height: 32rpx;
	background: #5b8ff9;
	border-radius: 12rpx 12rpx 0 0;
	position: relative;
}

.fire-icon::before {
	content: '';
	position: absolute;
	bottom: 0;
	left: 50%;
	transform: translateX(-50%);
	width: 0;
	height: 0;
	border-left: 8rpx solid transparent;
	border-right: 8rpx solid transparent;
	border-bottom: 12rpx solid #5b8ff9;
}

/* 打勾图标 */
.check-icon {
	width: 28rpx;
	height: 28rpx;
	border: 3rpx solid #52c41a;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.check-icon::after {
	content: '';
	position: absolute;
	top: 6rpx;
	left: 4rpx;
	width: 10rpx;
	height: 6rpx;
	border-left: 3rpx solid #52c41a;
	border-bottom: 3rpx solid #52c41a;
	transform: rotate(-45deg);
}

/* 时钟图标 */
.clock-icon {
	width: 28rpx;
	height: 28rpx;
	border: 3rpx solid #fa8c16;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.clock-icon::after {
	content: '';
	position: absolute;
	top: 4rpx;
	left: 50%;
	transform: translateX(-50%);
	width: 2rpx;
	height: 8rpx;
	background: #fa8c16;
}

.clock-icon::before {
	content: '';
	position: absolute;
	top: 10rpx;
	left: 10rpx;
	width: 6rpx;
	height: 2rpx;
	background: #fa8c16;
}

.stat-content {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 6rpx;
}

.stat-number {
	font-size: 36rpx;
	font-weight: 600;
	color: #333;
}

.stat-label {
	font-size: 22rpx;
	color: #999;
}

/* 日历卡片 */
.calendar-card {
	background: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin: 0 30rpx 30rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.calendar-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.calendar-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.month-nav {
	display: flex;
	gap: 12rpx;
}

.nav-btn {
	width: 56rpx;
	height: 56rpx;
	background: #f8f9fa;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.nav-arrow {
	font-size: 40rpx;
	color: #666;
	font-weight: 300;
	line-height: 1;
}

.weekday-header {
	display: grid;
	grid-template-columns: repeat(7, 1fr);
	gap: 8rpx;
	margin-bottom: 16rpx;
}

.weekday {
	text-align: center;
	font-size: 24rpx;
	color: #999;
	padding: 12rpx 0;
	font-weight: 500;
}

.date-grid {
	display: grid;
	grid-template-columns: repeat(7, 1fr);
	gap: 8rpx;
}

.date-cell {
	aspect-ratio: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	border-radius: 12rpx;
	position: relative;
	background: #f8f9fa;
}

.date-cell.other-month {
	opacity: 0.3;
}

.date-cell.today {
	background: #fff7e6;
	border: 2rpx solid #fa8c16;
}

.date-cell.checked {
	color: #ffffff;
}

.date-number {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 2rpx;
}

.date-number.white-text {
	color: #ffffff;
}

.date-duration {
	font-size: 20rpx;
	color: rgba(255, 255, 255, 0.9);
	font-weight: 500;
}

/* 打卡按钮 */
.checkin-btn-wrapper {
	position: fixed;
	bottom: 120rpx;
	left: 50%;
	transform: translateX(-50%);
	z-index: 100;
}

.checkin-btn {
	padding: 24rpx 60rpx;
	background: #5b8ff9;
	border-radius: 50rpx;
	display: flex;
	align-items: center;
	gap: 12rpx;
	box-shadow: 0 8rpx 24rpx rgba(91, 143, 249, 0.4);
}

.btn-icon-wrapper {
	width: 32rpx;
	height: 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.check-icon-btn {
	width: 24rpx;
	height: 24rpx;
	border: 3rpx solid #ffffff;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.check-icon-btn::after {
	content: '';
	position: absolute;
	top: 4rpx;
	left: 2rpx;
	width: 8rpx;
	height: 4rpx;
	border-left: 2rpx solid #ffffff;
	border-bottom: 2rpx solid #ffffff;
	transform: rotate(-45deg);
}

.btn-text {
	font-size: 28rpx;
	color: #ffffff;
	font-weight: 500;
}

/* 选择器弹窗 */
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
	max-height: 85vh;
	background: #ffffff;
	border-radius: 24rpx 24rpx 0 0;
	display: flex;
	flex-direction: column;
	overflow-y: auto;
}

.detail-modal {
	max-height: 70vh;
}

.checkin-modal {
	max-height: 85vh;
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

/* 打卡详情 */
.record-detail {
	padding: 30rpx;
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.detail-card {
	display: flex;
	align-items: center;
	gap: 20rpx;
	padding: 24rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
}

.detail-icon-wrapper {
	width: 80rpx;
	height: 80rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #ffffff;
	border-radius: 50%;
	flex-shrink: 0;
}

/* 时钟图标（详情页） */
.clock-icon-detail {
	width: 36rpx;
	height: 36rpx;
	border: 3rpx solid #fa8c16;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.clock-icon-detail::after {
	content: '';
	position: absolute;
	top: 6rpx;
	left: 50%;
	transform: translateX(-50%);
	width: 3rpx;
	height: 12rpx;
	background: #fa8c16;
}

.clock-icon-detail::before {
	content: '';
	position: absolute;
	top: 14rpx;
	left: 14rpx;
	width: 10rpx;
	height: 3rpx;
	background: #fa8c16;
}

/* 心情星星 */
.mood-stars {
	font-size: 32rpx;
	color: #faad14;
	font-weight: bold;
}

/* 书本图标 */
.book-icon {
	width: 36rpx;
	height: 32rpx;
	border: 3rpx solid #5b8ff9;
	border-radius: 4rpx;
	position: relative;
	box-sizing: border-box;
}

.book-icon::before {
	content: '';
	position: absolute;
	top: 8rpx;
	left: 6rpx;
	width: 20rpx;
	height: 2rpx;
	background: #5b8ff9;
}

.book-icon::after {
	content: '';
	position: absolute;
	top: 16rpx;
	left: 6rpx;
	width: 20rpx;
	height: 2rpx;
	background: #5b8ff9;
}

.detail-info {
	flex: 1;
}

.detail-label {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-bottom: 8rpx;
}

.detail-value {
	display: block;
	font-size: 30rpx;
	color: #333;
	font-weight: 600;
}

.detail-note {
	padding: 24rpx;
	background: #fff7e6;
	border-radius: 12rpx;
	border-left: 4rpx solid #fa8c16;
}

.note-label {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-bottom: 10rpx;
}

.note-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
}

.no-record {
	text-align: center;
	padding: 80rpx 30rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 20rpx;
}

.no-record-icon-wrapper {
	width: 120rpx;
	height: 120rpx;
	background: #f8f9fa;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 空状态图标 */
.empty-icon {
	width: 48rpx;
	height: 48rpx;
	border: 3rpx solid #d9d9d9;
	border-radius: 8rpx;
	position: relative;
	box-sizing: border-box;
}

.empty-icon::before {
	content: '';
	position: absolute;
	top: 12rpx;
	left: 8rpx;
	width: 28rpx;
	height: 2rpx;
	background: #d9d9d9;
}

.empty-icon::after {
	content: '';
	position: absolute;
	top: 22rpx;
	left: 8rpx;
	width: 28rpx;
	height: 2rpx;
	background: #d9d9d9;
}

.no-record-text {
	font-size: 28rpx;
	color: #999;
}

/* 打卡表单 */
.form-section {
	padding: 0 30rpx;
	margin-bottom: 30rpx;
}

.section-title {
	display: block;
	font-size: 28rpx;
	color: #333;
	font-weight: 600;
	margin-bottom: 20rpx;
}

.duration-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 12rpx;
	margin-bottom: 12rpx;
}

.duration-btn {
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	text-align: center;
}

.duration-btn.active {
	background: #5b8ff9;
}

.duration-text {
	font-size: 26rpx;
	color: #666;
}

.duration-btn.active .duration-text {
	color: #ffffff;
	font-weight: 600;
}

.custom-input {
	width: 100%;
	height: 80rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 12rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}

.type-scroll {
	white-space: nowrap;
}

.type-btn {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	padding: 20rpx 32rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	margin-right: 12rpx;
	min-width: 120rpx;
}

.type-btn.active {
	background: #5b8ff9;
}

.type-name {
	font-size: 26rpx;
	color: #666;
	font-weight: 500;
}

.type-btn.active .type-name {
	color: #ffffff;
	font-weight: 600;
}

.mood-selector {
	display: flex;
	gap: 15rpx;
	justify-content: center;
	padding: 20rpx 0;
}

.mood-star {
	font-size: 48rpx;
	opacity: 0.3;
}

.mood-star.active {
	opacity: 1;
}

.form-textarea {
	width: 100%;
	min-height: 150rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 12rpx;
	padding: 20rpx;
	font-size: 28rpx;
	line-height: 1.6;
	box-sizing: border-box;
}

.submit-btn {
	width: calc(100% - 60rpx);
	height: 88rpx;
	background: #5b8ff9;
	color: #ffffff;
	border: none;
	border-radius: 44rpx;
	font-size: 30rpx;
	font-weight: 600;
	margin: 20rpx 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.3);
}
</style>
