<template>
	<view class="countdown-container" :class="{ 'large': size === 'large' }">
		<view class="countdown-content">
			<text class="countdown-label">距离2026年考研还有</text>
			<view class="countdown-number">
				<text class="days">{{ daysLeft }}</text>
				<text class="unit">天</text>
			</view>
			<text v-if="targetDate" class="target-date">目标日期：{{ formatDate(targetDate) }}</text>
		</view>
	</view>
</template>

<script>
import { get } from '@/utils/request.js';

export default {
	name: 'Countdown',
	props: {
		userId: {
			type: Number,
			default: 1
		},
		size: {
			type: String,
			default: 'normal' // normal, large
		}
	},
	data() {
		return {
			daysLeft: 0,
			targetDate: null
		}
	},
	mounted() {
		this.loadCountdown();
	},
	methods: {
		async loadCountdown() {
			try {
				const res = await get(`/study/countdown?userId=${this.userId}`);
				if (res.code === 200) {
					this.daysLeft = res.data;
				}
				
				// 获取用户学习计划中的目标日期
				const planRes = await get(`/study/plan/user/${this.userId}`);
				if (planRes.code === 200 && planRes.data) {
					this.targetDate = planRes.data.targetDate;
				}
			} catch (error) {
				console.error('加载倒计时失败:', error);
				// 使用默认倒计时
				const defaultExamDate = new Date('2026-12-21');
				const today = new Date();
				const timeDiff = defaultExamDate.getTime() - today.getTime();
				this.daysLeft = Math.ceil(timeDiff / (1000 * 3600 * 24));
			}
		},
		
		formatDate(dateStr) {
			if (!dateStr) return '';
			const date = new Date(dateStr);
			return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
		}
	}
}
</script>

<style scoped>
.countdown-container {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 20rpx;
	padding: 30rpx;
	margin: 20rpx 0;
}

.countdown-container.large {
	padding: 50rpx 30rpx;
	margin: 30rpx 0;
}

.countdown-content {
	text-align: center;
	color: #FFFFFF;
}

.countdown-label {
	display: block;
	font-size: 26rpx;
	opacity: 0.9;
	margin-bottom: 20rpx;
}

.large .countdown-label {
	font-size: 30rpx;
	margin-bottom: 30rpx;
}

.countdown-number {
	display: flex;
	align-items: baseline;
	justify-content: center;
	margin-bottom: 15rpx;
}

.days {
	font-size: 80rpx;
	font-weight: bold;
	line-height: 1;
}

.large .days {
	font-size: 120rpx;
}

.unit {
	font-size: 36rpx;
	margin-left: 10rpx;
	opacity: 0.9;
}

.large .unit {
	font-size: 48rpx;
}

.target-date {
	display: block;
	font-size: 22rpx;
	opacity: 0.8;
}

.large .target-date {
	font-size: 26rpx;
}
</style>