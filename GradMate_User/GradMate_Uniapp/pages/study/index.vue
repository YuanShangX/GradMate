<template>
	<view class="study-container">
		<!-- 倒计时卡片 -->
		<Countdown :userId="userId" size="large" />

		<!-- 今日概览 -->
		<view class="overview-card">
			<view class="card-header">
				<text class="card-title">今日概览</text>
				<text class="today-date">{{ todayDate }}</text>
			</view>
			
			<view class="overview-grid">
				<view class="overview-item">
					<text class="overview-number">{{ stats.todayTasks || '0/0' }}</text>
					<text class="overview-label">今日任务</text>
				</view>
				<view class="overview-item">
					<text class="overview-number">{{ stats.continuousDays || 0 }}</text>
					<text class="overview-label">连续打卡</text>
				</view>
				<view class="overview-item">
					<text class="overview-number">{{ stats.totalHours || '0.0' }}h</text>
					<text class="overview-label">累计学习</text>
				</view>
			</view>
		</view>

		<!-- 功能入口 -->
		<view class="function-grid">
			<view class="function-item" @click="goToTasks">
				<view class="function-icon-wrapper">
					<view class="task-icon"></view>
				</view>
				<text class="function-name">每日任务</text>
				<text class="function-desc">管理学习任务</text>
			</view>
			
			<view class="function-item" @click="goToCalendar">
				<view class="function-icon-wrapper">
					<view class="calendar-icon"></view>
				</view>
				<text class="function-name">打卡日历</text>
				<text class="function-desc">记录学习历程</text>
			</view>
			
			<view class="function-item" @click="goToStats">
				<view class="function-icon-wrapper">
					<view class="stats-icon"></view>
				</view>
				<text class="function-name">统计数据</text>
				<text class="function-desc">查看学习统计</text>
			</view>
			
			<view class="function-item" @click="showCheckInModal" :class="{ checked: stats.todayChecked }">
				<view class="function-icon-wrapper">
					<view class="checkin-icon"></view>
				</view>
				<text class="function-name">今日打卡</text>
				<text class="function-desc">{{ stats.todayChecked ? '已打卡' : '立即打卡' }}</text>
			</view>
		</view>

		<!-- 激励语 -->
		<view class="motivation-card">
			<text class="motivation-text">{{ motivationText }}</text>
		</view>

		<!-- 打卡弹窗 -->
		<view v-if="showCheckIn" class="modal-mask" @click="hideCheckInModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">今日打卡</text>
					<text class="modal-close" @click="hideCheckInModal">✕</text>
				</view>
				
				<view class="form-item">
					<text class="form-label">学习时长（分钟）</text>
					<input class="form-input" v-model.number="checkInForm.duration" type="number" placeholder="请输入学习时长" />
				</view>
				
				<view class="form-item">
					<text class="form-label">今日心情</text>
					<view class="mood-selector">
						<text 
							v-for="i in 5" 
							:key="i" 
							class="mood-star" 
							:class="{ active: i <= checkInForm.mood }"
							@click="checkInForm.mood = i"
						>⭐</text>
					</view>
				</view>
				
				<view class="form-item">
					<text class="form-label">学习备注</text>
					<textarea class="form-textarea" v-model="checkInForm.note" placeholder="今天学了什么？有什么收获？"></textarea>
				</view>
				
				<button class="submit-btn" @click="submitCheckIn">提交打卡</button>
			</view>
		</view>

		<!-- 学习计划弹窗 -->
		<view v-if="showPlan" class="modal-mask" @click="hidePlanModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">学习计划</text>
					<text class="modal-close" @click="hidePlanModal">✕</text>
				</view>
				
				<view class="form-item">
					<text class="form-label">计划标题</text>
					<input class="form-input" v-model="planForm.title" placeholder="例如：2026年考研计划" />
				</view>
				
				<view class="form-item">
					<text class="form-label">目标日期</text>
					<picker mode="date" :value="planForm.targetDate" @change="onDateChange">
						<view class="date-picker">
							{{ planForm.targetDate || '请选择考研日期' }}
						</view>
					</picker>
				</view>
				
				<view class="form-item">
					<text class="form-label">计划内容</text>
					<textarea class="form-textarea" v-model="planForm.content" placeholder="描述你的学习计划和目标"></textarea>
				</view>
				
				<button class="submit-btn" @click="submitPlan">保存计划</button>
			</view>
		</view>
	</view>
</template>

<script>
import { get, post, put } from '@/utils/request.js';
import Countdown from '@/components/Countdown.vue';

export default {
	components: {
		Countdown
	},
	data() {
		return {
			userId: 1, // 从登录状态获取
			stats: {},
			showCheckIn: false,
			showPlan: false,
			checkInForm: {
				duration: null,
				mood: 5,
				note: ''
			},
			planForm: {
				title: '',
				targetDate: '',
				content: ''
			},
			motivationTexts: [
				'每一次努力都是成功的积累！',
				'坚持就是胜利，加油！',
				'今天的努力是明天的收获！',
				'相信自己，你一定可以的！',
				'梦想在前方，努力在脚下！',
				'不积跬步，无以至千里！',
				'成功属于坚持不懈的人！'
			]
		}
	},
	computed: {
		todayDate() {
			const today = new Date();
			const month = today.getMonth() + 1;
			const date = today.getDate();
			const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
			const weekday = weekdays[today.getDay()];
			return `${month}月${date}日 周${weekday}`;
		},
		motivationText() {
			const index = Math.floor(Math.random() * this.motivationTexts.length);
			return this.motivationTexts[index];
		}
	},
	onShow() {
		this.loadStats();
		this.loadPlan();
	},
	methods: {
		// 加载统计数据
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

		// 加载学习计划
		async loadPlan() {
			try {
				const res = await get(`/study/plan/user/${this.userId}`);
				if (res.code === 200 && res.data) {
					this.planForm = {
						id: res.data.id,
						title: res.data.title || '',
						targetDate: res.data.targetDate || '',
						content: res.data.content || ''
					};
				}
			} catch (error) {
				console.error('加载学习计划失败:', error);
			}
		},

		// 跳转到任务页面
		goToTasks() {
			uni.navigateTo({
				url: '/pages/study/task'
			});
		},

		// 跳转到日历页面
		goToCalendar() {
			uni.navigateTo({
				url: '/pages/study/calendar'
			});
		},
		
		// 跳转到统计页面
		goToStats() {
			uni.navigateTo({
				url: '/pages/study/stats'
			});
		},

		// 显示打卡弹窗
		showCheckInModal() {
			this.showCheckIn = true;
			// 重置表单
			this.checkInForm = {
				duration: null,
				mood: 5,
				note: ''
			};
		},

		// 隐藏打卡弹窗
		hideCheckInModal() {
			this.showCheckIn = false;
		},

		// 提交打卡
		async submitCheckIn() {
			if (!this.checkInForm.duration || this.checkInForm.duration <= 0) {
				uni.showToast({
					title: '请输入有效的学习时长',
					icon: 'none'
				});
				return;
			}

			try {
				const res = await post('/study/checkin', {
					userId: this.userId,
					duration: this.checkInForm.duration,
					mood: this.checkInForm.mood,
					note: this.checkInForm.note
				});

				if (res.code === 200) {
					uni.showToast({
						title: '打卡成功！',
						icon: 'success'
					});
					this.hideCheckInModal();
					this.loadStats(); // 刷新统计数据
					
					// 震动反馈
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

		// 显示计划弹窗
		showPlanModal() {
			this.showPlan = true;
		},

		// 隐藏计划弹窗
		hidePlanModal() {
			this.showPlan = false;
		},

		// 日期选择
		onDateChange(e) {
			this.planForm.targetDate = e.detail.value;
		},

		// 提交计划
		async submitPlan() {
			if (!this.planForm.title.trim()) {
				uni.showToast({
					title: '请输入计划标题',
					icon: 'none'
				});
				return;
			}

			try {
				let res;
				if (this.planForm.id) {
					// 更新计划
					res = await put(`/study/plan/${this.planForm.id}`, {
						title: this.planForm.title,
						content: this.planForm.content,
						targetDate: this.planForm.targetDate
					});
				} else {
					// 创建计划
					res = await post('/study/plan', {
						userId: this.userId,
						title: this.planForm.title,
						content: this.planForm.content,
						targetDate: this.planForm.targetDate
					});
				}

				if (res.code === 200) {
					uni.showToast({
						title: '保存成功！',
						icon: 'success'
					});
					this.hidePlanModal();
					this.loadStats(); // 刷新统计数据
				} else {
					uni.showToast({
						title: res.message || '保存失败',
						icon: 'none'
					});
				}
			} catch (error) {
				console.error('保存计划失败:', error);
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				});
			}
		}
	}
}
</script>

<style scoped>
.study-container {
	min-height: 100vh;
	background-color: #F5F5F5;
	padding: 30rpx;
}

/* 今日概览卡片 */
.overview-card {
	background-color: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.card-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.today-date {
	font-size: 24rpx;
	color: #666;
}

.overview-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 30rpx;
}

.overview-item {
	text-align: center;
}

.overview-number {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #4A90E2;
	margin-bottom: 10rpx;
}

.overview-label {
	font-size: 24rpx;
	color: #666;
}

/* 功能网格 */
.function-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
	margin-bottom: 30rpx;
}

.function-item {
	background-color: #FFFFFF;
	border-radius: 20rpx;
	padding: 40rpx 30rpx;
	text-align: center;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	transition: all 0.3s;
}

.function-item.checked {
	background: linear-gradient(135deg, #e8f4ff 0%, #f0f9ff 100%);
}

.function-icon-wrapper {
	width: 100rpx;
	height: 100rpx;
	margin: 0 auto 20rpx;
	background: #f0f3fa;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.function-item.checked .function-icon-wrapper {
	background: #e8f4ff;
}

/* 任务图标 */
.task-icon {
	width: 48rpx;
	height: 48rpx;
	border: 3rpx solid #5b8ff9;
	border-radius: 8rpx;
	position: relative;
	box-sizing: border-box;
}

.task-icon::after {
	content: '';
	position: absolute;
	top: 14rpx;
	left: 8rpx;
	width: 28rpx;
	height: 3rpx;
	background: #5b8ff9;
}

.task-icon::before {
	content: '';
	position: absolute;
	top: 26rpx;
	left: 8rpx;
	width: 28rpx;
	height: 3rpx;
	background: #5b8ff9;
}

/* 日历图标 */
.calendar-icon {
	width: 48rpx;
	height: 48rpx;
	border: 3rpx solid #5b8ff9;
	border-radius: 8rpx;
	position: relative;
	box-sizing: border-box;
}

.calendar-icon::before {
	content: '';
	position: absolute;
	top: -8rpx;
	left: 8rpx;
	width: 8rpx;
	height: 12rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

.calendar-icon::after {
	content: '';
	position: absolute;
	top: -8rpx;
	right: 8rpx;
	width: 8rpx;
	height: 12rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

/* 统计图标 */
.stats-icon {
	width: 48rpx;
	height: 48rpx;
	position: relative;
	display: flex;
	align-items: flex-end;
	justify-content: space-between;
	padding: 0 6rpx;
	box-sizing: border-box;
}

.stats-icon::before {
	content: '';
	width: 8rpx;
	height: 20rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

.stats-icon::after {
	content: '';
	width: 8rpx;
	height: 32rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

/* 打卡图标 */
.checkin-icon {
	width: 48rpx;
	height: 48rpx;
	border: 3rpx solid #52c41a;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.checkin-icon::after {
	content: '';
	position: absolute;
	top: 12rpx;
	left: 10rpx;
	width: 16rpx;
	height: 10rpx;
	border-left: 4rpx solid #52c41a;
	border-bottom: 4rpx solid #52c41a;
	transform: rotate(-45deg);
}

.function-item.checked .checkin-icon {
	border-color: #5b8ff9;
}

.function-item.checked .checkin-icon::after {
	border-left-color: #5b8ff9;
	border-bottom-color: #5b8ff9;
}

.function-name {
	display: block;
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
	margin-bottom: 10rpx;
}

.function-desc {
	font-size: 22rpx;
	color: #666;
}

/* 激励语卡片 */
.motivation-card {
	background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
	border-radius: 20rpx;
	padding: 40rpx;
	text-align: center;
}

.motivation-text {
	font-size: 28rpx;
	color: #FFFFFF;
	font-weight: 500;
}

/* 弹窗样式 */
.modal-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 1000;
}

.modal-content {
	background-color: #FFFFFF;
	border-radius: 20rpx;
	padding: 40rpx;
	margin: 0 40rpx;
	max-width: 600rpx;
	width: 100%;
	max-height: 80vh;
	overflow-y: auto;
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.modal-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.modal-close {
	font-size: 36rpx;
	color: #999;
	padding: 10rpx;
}

.form-item {
	margin-bottom: 30rpx;
}

.form-label {
	display: block;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 15rpx;
}

.form-input {
	width: 100%;
	height: 80rpx;
	border: 2rpx solid #E5E5E5;
	border-radius: 10rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
}

.form-textarea {
	width: 100%;
	min-height: 120rpx;
	border: 2rpx solid #E5E5E5;
	border-radius: 10rpx;
	padding: 20rpx;
	font-size: 28rpx;
}

.mood-selector {
	display: flex;
	gap: 10rpx;
}

.mood-star {
	font-size: 40rpx;
	opacity: 0.3;
	transition: opacity 0.2s;
}

.mood-star.active {
	opacity: 1;
}

.date-picker {
	height: 80rpx;
	line-height: 80rpx;
	border: 2rpx solid #E5E5E5;
	border-radius: 10rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	color: #333;
}

.submit-btn {
	width: 100%;
	height: 80rpx;
	background-color: #4A90E2;
	color: #FFFFFF;
	border: none;
	border-radius: 40rpx;
	font-size: 28rpx;
	margin-top: 20rpx;
}
</style>