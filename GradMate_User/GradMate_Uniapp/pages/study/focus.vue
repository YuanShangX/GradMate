<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="confirmExit">
				<text class="back-icon">‹</text>
			</view>
			<view class="nav-title-wrapper">
				<text class="nav-title">专注计时</text>
			</view>
			<view class="nav-placeholder"></view>
		</view>
		
		<!-- 激励语 -->
		<view class="quote-section">
			<text class="quote-text">{{ currentQuote }}</text>
		</view>

		<!-- 倒计时显示 -->
		<view class="timer-section">
			<view class="timer-circle" :class="{ paused: isPaused }">
				<svg class="progress-ring" viewBox="0 0 200 200">
					<circle 
						class="progress-ring-bg" 
						cx="100" 
						cy="100" 
						r="90"
					/>
					<circle 
						class="progress-ring-circle" 
						cx="100" 
						cy="100" 
						r="90"
						:style="{ strokeDashoffset: progressOffset }"
					/>
				</svg>
				<view class="timer-inner">
					<text class="timer-text">{{ formattedTime }}</text>
					<text class="timer-label">{{ statusText }}</text>
				</view>
			</view>
		</view>

		<!-- 任务信息 -->
		<view class="task-card">
			<view class="task-icon-wrapper">
				<view class="task-icon"></view>
			</view>
			<view class="task-info">
				<text class="task-name">{{ taskName }}</text>
				<text class="task-duration">目标时长 {{ duration }} 分钟</text>
			</view>
		</view>

		<!-- 统计信息 -->
		<view class="stats-row">
			<view class="stat-item">
				<view class="stat-icon-wrapper">
					<view class="focus-icon"></view>
				</view>
				<view class="stat-content">
					<text class="stat-value">{{ focusCount }}</text>
					<text class="stat-label">专注次数</text>
				</view>
			</view>
			<view class="stat-item">
				<view class="stat-icon-wrapper">
					<view class="pause-count-icon"></view>
				</view>
				<view class="stat-content">
					<text class="stat-value">{{ pauseCount }}</text>
					<text class="stat-label">暂停次数</text>
				</view>
			</view>
		</view>

		<!-- 控制按钮 -->
		<view class="controls">
			<view class="control-btn secondary-btn" @click="togglePause">
				<view v-if="isPaused" class="play-icon"></view>
				<view v-else class="pause-icon"></view>
				<text class="btn-label">{{ isPaused ? '继续' : '暂停' }}</text>
			</view>
			<view class="control-btn stop-btn" @click="stopFocus">
				<view class="stop-icon"></view>
				<text class="btn-label">结束</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { post } from '@/utils/request.js'

const taskId = ref(null)
const taskName = ref('')
const duration = ref(25)
const remainingSeconds = ref(0)
const timer = ref(null)
const isPaused = ref(false)
const startTime = ref(null)
const pauseTime = ref(null)
const totalPauseDuration = ref(0)
const focusCount = ref(1)
const pauseCount = ref(0)

const quotes = [
	'微尘莫若都可让我动容，却无事可使我动摇',
	'你的努力，时间看得见',
	'每一次专注，都是在靠近梦想',
	'坚持下去，未来可期',
	'今天的努力，是明天的底气',
	'专注当下，成就未来',
	'不积跬步，无以至千里',
	'天道酬勤，功不唐捐'
]
const currentQuote = ref('')

const formattedTime = computed(() => {
	const minutes = Math.floor(remainingSeconds.value / 60)
	const seconds = remainingSeconds.value % 60
	return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

const statusText = computed(() => {
	if (isPaused.value) {
		return '已暂停'
	}
	return '进行中'
})

const progressOffset = computed(() => {
	const totalSeconds = duration.value * 60
	const progress = remainingSeconds.value / totalSeconds
	const circumference = 2 * Math.PI * 90
	return circumference * (1 - progress)
})

onMounted(() => {
	const pages = getCurrentPages()
	const currentPage = pages[pages.length - 1]
	const options = currentPage.options
	
	taskId.value = options.taskId
	taskName.value = options.taskName || '学习任务'
	duration.value = parseInt(options.duration) || 25
	remainingSeconds.value = duration.value * 60
	
	// 随机选择激励语
	currentQuote.value = quotes[Math.floor(Math.random() * quotes.length)]
	
	// 开始计时
	startFocus()
})

onUnmounted(() => {
	clearTimer()
})

const startFocus = () => {
	startTime.value = Date.now()
	isPaused.value = false
	startTimer()
	
	// 震动反馈
	uni.vibrateShort()
}

const startTimer = () => {
	clearTimer()
	timer.value = setInterval(() => {
		if (remainingSeconds.value > 0) {
			remainingSeconds.value--
		} else {
			onComplete()
		}
	}, 1000)
}

const clearTimer = () => {
	if (timer.value) {
		clearInterval(timer.value)
		timer.value = null
	}
}

const togglePause = () => {
	if (isPaused.value) {
		// 继续
		totalPauseDuration.value += Date.now() - pauseTime.value
		isPaused.value = false
		startTimer()
		uni.vibrateShort()
	} else {
		// 暂停
		pauseTime.value = Date.now()
		isPaused.value = true
		pauseCount.value++
		clearTimer()
		uni.vibrateShort()
	}
}

const stopFocus = () => {
	uni.showModal({
		title: '确认停止',
		content: '确定要停止专注吗？学习时长将被记录。',
		success: async (res) => {
			if (res.confirm) {
				await saveFocusRecord()
				uni.navigateBack()
			}
		}
	})
}

const confirmExit = () => {
	uni.showModal({
		title: '确认退出',
		content: '确定要退出专注吗？当前进度将不会保存。',
		success: (res) => {
			if (res.confirm) {
				clearTimer()
				uni.navigateBack()
			}
		}
	})
}

const onComplete = async () => {
	clearTimer()
	
	// 长震动
	uni.vibrateLong()
	
	// 显示完成提示
	uni.showToast({
		title: '专注完成！',
		icon: 'success',
		duration: 2000
	})
	
	// 保存记录
	await saveFocusRecord()
	
	// 延迟返回
	setTimeout(() => {
		uni.navigateBack()
	}, 2000)
}

const saveFocusRecord = async () => {
	try {
		const userId = uni.getStorageSync('userId') || 1
		const actualDuration = Math.floor((duration.value * 60 - remainingSeconds.value) / 60)
		
		if (actualDuration < 1) {
			return // 少于1分钟不记录
		}
		
		// 记录打卡
		await post('/study/checkin', {
			userId: userId,
			studyDuration: actualDuration,
			focusCount: focusCount.value,
			pauseCount: pauseCount.value,
			notes: `完成任务：${taskName.value}`
		})
		
		// 如果有任务ID，标记任务完成
		if (taskId.value) {
			await post(`/study/task/${taskId.value}/complete`)
		}
	} catch (error) {
		console.error('保存专注记录失败:', error)
	}
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
	justify-content: space-between;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
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

/* 激励语 */
.quote-section {
	padding: 50rpx 60rpx 30rpx;
	text-align: center;
}

.quote-text {
	font-size: 26rpx;
	line-height: 1.8;
	color: #666;
	font-style: italic;
}

/* 倒计时 */
.timer-section {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20rpx 0;
}

.timer-circle {
	width: 480rpx;
	height: 480rpx;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
}

.progress-ring {
	position: absolute;
	width: 100%;
	height: 100%;
	transform: rotate(-90deg);
}

.progress-ring-bg {
	fill: none;
	stroke: #e8f4ff;
	stroke-width: 12;
}

.progress-ring-circle {
	fill: none;
	stroke: #5b8ff9;
	stroke-width: 12;
	stroke-linecap: round;
	stroke-dasharray: 565.48;
	transition: stroke-dashoffset 1s linear;
}

.timer-circle.paused .progress-ring-circle {
	stroke: #faad14;
}

.timer-inner {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 16rpx;
	z-index: 1;
}

.timer-text {
	font-size: 100rpx;
	font-weight: 300;
	color: #333;
	letter-spacing: 6rpx;
	font-family: 'Helvetica Neue', Arial, sans-serif;
}

.timer-label {
	font-size: 26rpx;
	color: #999;
	font-weight: 500;
}

/* 任务卡片 */
.task-card {
	background: #ffffff;
	border-radius: 16rpx;
	padding: 32rpx;
	margin: 0 30rpx 20rpx;
	display: flex;
	align-items: center;
	gap: 24rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.task-icon-wrapper {
	width: 80rpx;
	height: 80rpx;
	background: #e8f4ff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.task-icon {
	width: 36rpx;
	height: 36rpx;
	border: 3rpx solid #5b8ff9;
	border-radius: 8rpx;
	position: relative;
	box-sizing: border-box;
}

.task-icon::after {
	content: '';
	position: absolute;
	top: 10rpx;
	left: 6rpx;
	width: 20rpx;
	height: 2rpx;
	background: #5b8ff9;
}

.task-icon::before {
	content: '';
	position: absolute;
	top: 18rpx;
	left: 6rpx;
	width: 20rpx;
	height: 2rpx;
	background: #5b8ff9;
}

.task-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.task-name {
	font-size: 30rpx;
	font-weight: 500;
	color: #333;
}

.task-duration {
	font-size: 24rpx;
	color: #999;
}

/* 统计信息 */
.stats-row {
	display: flex;
	gap: 16rpx;
	padding: 0 30rpx 20rpx;
}

.stat-item {
	flex: 1;
	background: #ffffff;
	border-radius: 16rpx;
	padding: 24rpx;
	display: flex;
	align-items: center;
	gap: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.stat-icon-wrapper {
	width: 60rpx;
	height: 60rpx;
	background: #fff7e6;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

/* 专注图标 */
.focus-icon {
	width: 28rpx;
	height: 28rpx;
	border: 3rpx solid #fa8c16;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.focus-icon::after {
	content: '';
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 12rpx;
	height: 12rpx;
	background: #fa8c16;
	border-radius: 50%;
}

/* 暂停计数图标 */
.pause-count-icon {
	width: 24rpx;
	height: 28rpx;
	position: relative;
}

.pause-count-icon::before {
	content: '';
	position: absolute;
	left: 0;
	width: 8rpx;
	height: 28rpx;
	background: #fa8c16;
	border-radius: 2rpx;
}

.pause-count-icon::after {
	content: '';
	position: absolute;
	right: 0;
	width: 8rpx;
	height: 28rpx;
	background: #fa8c16;
	border-radius: 2rpx;
}

.stat-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 4rpx;
}

.stat-value {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.stat-label {
	font-size: 22rpx;
	color: #999;
}

/* 控制按钮 */
.controls {
	display: flex;
	justify-content: center;
	gap: 30rpx;
	padding: 20rpx 30rpx 60rpx;
}

.control-btn {
	flex: 1;
	max-width: 280rpx;
	height: 100rpx;
	border-radius: 50rpx;
	background: #ffffff;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 12rpx;
	transition: all 0.3s;
}

.secondary-btn {
	background: #e8f4ff;
}

.stop-btn {
	background: #fff1f0;
}

.btn-label {
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
}

.secondary-btn .btn-label {
	color: #5b8ff9;
}

.stop-btn .btn-label {
	color: #ff4d4f;
}

/* 播放图标 */
.play-icon {
	width: 0;
	height: 0;
	border-left: 24rpx solid #5b8ff9;
	border-top: 14rpx solid transparent;
	border-bottom: 14rpx solid transparent;
	margin-left: 4rpx;
}

/* 暂停图标 */
.pause-icon {
	width: 24rpx;
	height: 28rpx;
	position: relative;
}

.pause-icon::before {
	content: '';
	position: absolute;
	left: 0;
	width: 8rpx;
	height: 28rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

.pause-icon::after {
	content: '';
	position: absolute;
	right: 0;
	width: 8rpx;
	height: 28rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

/* 停止图标 */
.stop-icon {
	width: 24rpx;
	height: 24rpx;
	background: #ff4d4f;
	border-radius: 4rpx;
}
</style>
