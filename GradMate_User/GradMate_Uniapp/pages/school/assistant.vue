<template>
	<view class="page">
		<!-- 顶部导航 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">✕</text>
			</view>
		</view>
		
		<!-- 进度指示器 -->
		<view class="progress-bar">
			<view class="progress-item" :class="{ active: currentStep >= 1 }">
				<view class="progress-dot"></view>
				<text class="progress-text">Part1 基础信息采集</text>
			</view>
			<view class="progress-item" :class="{ active: currentStep >= 2 }">
				<view class="progress-dot"></view>
				<text class="progress-text">Part2 专业倾向测评</text>
			</view>
			<view class="progress-item" :class="{ active: currentStep >= 3 }">
				<view class="progress-dot"></view>
				<text class="progress-text">Part3 学校倾向测评</text>
			</view>
		</view>
		
		<!-- AI助手头像 -->
		<view class="ai-header">
			<view class="ai-avatar">
				<view class="robot-icon"></view>
			</view>
			<view class="ai-info">
				<text class="ai-name">研小伴</text>
				<text class="ai-desc">你的智能择校助手</text>
			</view>
		</view>
		
		<!-- 聊天消息区域 -->
		<scroll-view 
			class="chat-container" 
			scroll-y="true"
			:scroll-into-view="scrollIntoView"
			scroll-with-animation
		>
			<view 
				v-for="(msg, index) in messages" 
				:key="index"
				:id="'msg-' + index"
				class="message-item"
				:class="{ 'user-message': msg.role === 'user', 'fade-in': true }"
			>
				<view v-if="msg.role === 'assistant'" class="ai-message">
					<text class="message-text">{{ msg.content }}</text>
				</view>
				<view v-else class="user-message-bubble">
					<text class="message-text">{{ msg.content }}</text>
				</view>
			</view>
			
			<!-- 加载中提示 -->
			<view v-if="loading" class="loading-message">
				<text class="loading-text">AI正在思考中...</text>
			</view>
		</scroll-view>
		
		<!-- 输入区域 -->
		<view class="input-container">
			<input 
				class="input-box"
				v-model="userInput"
				:placeholder="inputPlaceholder"
				@confirm="sendMessage"
			/>
			<view class="send-btn" @click="sendMessage">
				<text class="send-text">发送</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const currentStep = ref(1)
const messages = ref([])
const userInput = ref('')
const loading = ref(false)
const scrollIntoView = ref('')

// 用户信息收集
const userProfile = ref({
	school: '',
	major: '',
	majorLevel: '',
	englishLevel: '',
	mathLevel: '',
	targetMajor: '',
	targetSchoolType: ''
})

const inputPlaceholder = computed(() => {
	if (currentStep.value === 1) {
		return '点击输入本科/专科院校名称'
	} else if (currentStep.value === 2) {
		return '输入你的专业倾向'
	} else {
		return '输入你的学校倾向'
	}
})

// 初始化欢迎消息
const initChat = () => {
	messages.value = [
		{
			role: 'assistant',
			content: 'Hi，我是你的AI择校小助手~接下来我将和你一起完成择校测评，时长约5分钟，测评结束后即可领取你的专属择校报告啦！\n现在就让我们一起开始吧~'
		},
		{
			role: 'assistant',
			content: 'Part1 基础信息采集\n\n请填写你的本科/专科院校，让小助手更了解你一点~'
		}
	]
}

// 发送消息
const sendMessage = async () => {
	if (!userInput.value.trim() || loading.value) return
	
	const message = userInput.value.trim()
	
	// 添加用户消息
	messages.value.push({
		role: 'user',
		content: message
	})
	
	userInput.value = ''
	scrollToBottom()
	
	// 处理用户输入
	await handleUserInput(message)
}

// 处理用户输入
const handleUserInput = async (input) => {
	loading.value = true
	
	try {
		if (currentStep.value === 1) {
			await handleStep1(input)
		} else if (currentStep.value === 2) {
			await handleStep2(input)
		} else if (currentStep.value === 3) {
			await handleStep3(input)
		}
	} catch (error) {
		console.error('处理失败:', error)
		messages.value.push({
			role: 'assistant',
			content: '抱歉，处理出错了，请重试~'
		})
	} finally {
		loading.value = false
		scrollToBottom()
	}
}

// 处理第一步：基础信息采集
const handleStep1 = async (input) => {
	// 这里调用后端AI接口
	const res = await uni.request({
		url: buildApiUrl('/ai/assistant/step1'),
		method: 'POST',
		data: {
			userInput: input,
			profile: userProfile.value
		},
		header: {
			'Content-Type': 'application/json'
		}
	})
	
	if (res.data.code === 200) {
		const response = res.data.data
		
		// 更新用户信息
		if (response.profile) {
			Object.assign(userProfile.value, response.profile)
		}
		
		// 添加AI回复
		messages.value.push({
			role: 'assistant',
			content: response.message
		})
		
		// 检查是否进入下一步
		if (response.nextStep) {
			currentStep.value = 2
		}
	}
}

// 处理第二步：专业倾向测评
const handleStep2 = async (input) => {
	const res = await uni.request({
		url: buildApiUrl('/ai/assistant/step2'),
		method: 'POST',
		data: {
			userInput: input,
			profile: userProfile.value
		},
		header: {
			'Content-Type': 'application/json'
		}
	})
	
	if (res.data.code === 200) {
		const response = res.data.data
		
		if (response.profile) {
			Object.assign(userProfile.value, response.profile)
		}
		
		messages.value.push({
			role: 'assistant',
			content: response.message
		})
		
		if (response.nextStep) {
			currentStep.value = 3
		}
	}
}

// 处理第三步：学校倾向测评
const handleStep3 = async (input) => {
	const res = await uni.request({
		url: buildApiUrl('/ai/assistant/step3'),
		method: 'POST',
		data: {
			userInput: input,
			profile: userProfile.value
		},
		header: {
			'Content-Type': 'application/json'
		}
	})
	
	if (res.data.code === 200) {
		const response = res.data.data
		
		messages.value.push({
			role: 'assistant',
			content: response.message
		})
		
		// 如果完成，显示报告
		if (response.completed) {
			messages.value.push({
				role: 'assistant',
				content: '🎉 测评完成！\n\n' + response.report
			})
		}
	}
}

// 滚动到底部
const scrollToBottom = () => {
	nextTick(() => {
		const lastIndex = messages.value.length - 1
		scrollIntoView.value = 'msg-' + lastIndex
	})
}

const goBack = () => {
	uni.navigateBack()
}

// 页面加载时初始化
initChat()
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: linear-gradient(180deg, #e8f4ff 0%, #f5f9ff 100%);
	display: flex;
	flex-direction: column;
}

/* 导航栏 */
.navbar {
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
}

.nav-back {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	font-size: 40rpx;
	color: #333;
}

/* 进度条 */
.progress-bar {
	display: flex;
	justify-content: space-around;
	padding: 20rpx 30rpx;
	background: rgba(255, 255, 255, 0.8);
	margin: 0 40rpx 20rpx;
	border-radius: 16rpx;
}

.progress-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 8rpx;
	flex: 1;
}

.progress-dot {
	width: 16rpx;
	height: 16rpx;
	border-radius: 50%;
	background: #ddd;
}

.progress-item.active .progress-dot {
	background: #5b8ff9;
}

.progress-text {
	font-size: 20rpx;
	color: #999;
	text-align: center;
}

.progress-item.active .progress-text {
	color: #5b8ff9;
	font-weight: 500;
}

/* AI头像 */
.ai-header {
	display: flex;
	align-items: center;
	gap: 20rpx;
	padding: 0 30rpx 20rpx;
}

.ai-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background: #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.2);
	position: relative;
}

.robot-icon {
	width: 40rpx;
	height: 40rpx;
	background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
	border-radius: 8rpx;
	position: relative;
}

.robot-icon::before {
	content: '';
	position: absolute;
	width: 12rpx;
	height: 12rpx;
	background: #ffffff;
	border-radius: 50%;
	top: 10rpx;
	left: 8rpx;
}

.robot-icon::after {
	content: '';
	position: absolute;
	width: 12rpx;
	height: 12rpx;
	background: #ffffff;
	border-radius: 50%;
	top: 10rpx;
	right: 8rpx;
}

.ai-info {
	display: flex;
	flex-direction: column;
	gap: 4rpx;
}

.ai-name {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.ai-desc {
	font-size: 24rpx;
	color: #999;
}

/* 聊天容器 */
.chat-container {
	flex: 1;
	padding: 20rpx 30rpx;
	padding-right: 30rpx;
	box-sizing: border-box;
}

.message-item {
	margin-bottom: 24rpx;
	box-sizing: border-box;
	animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translateY(20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.ai-message {
	background: #ffffff;
	border-radius: 16rpx;
	padding: 24rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	box-sizing: border-box;
	max-width: 100%;
	word-wrap: break-word;
	animation: slideInLeft 0.3s ease-out;
}

@keyframes slideInLeft {
	from {
		opacity: 0;
		transform: translateX(-30rpx);
	}
	to {
		opacity: 1;
		transform: translateX(0);
	}
}

.user-message-bubble {
	background: #5b8ff9;
	border-radius: 16rpx;
	padding: 24rpx;
	margin-left: 100rpx;
	box-sizing: border-box;
	max-width: calc(100% - 100rpx);
	word-wrap: break-word;
	animation: slideInRight 0.3s ease-out;
}

@keyframes slideInRight {
	from {
		opacity: 0;
		transform: translateX(30rpx);
	}
	to {
		opacity: 1;
		transform: translateX(0);
	}
}

.user-message-bubble .message-text {
	color: #ffffff;
}

.message-text {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	white-space: pre-wrap;
	animation: typing 0.5s steps(40) forwards;
}

@keyframes typing {
	from {
		opacity: 0;
	}
	to {
		opacity: 1;
	}
}

.loading-message {
	text-align: center;
	padding: 20rpx 0;
}

.loading-text {
	font-size: 26rpx;
	color: #999;
}

/* 输入区域 */
.input-container {
	background: #ffffff;
	padding: 20rpx 30rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	display: flex;
	gap: 16rpx;
	box-shadow: 0 -2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.input-box {
	flex: 1;
	background: #f5f5f5;
	border-radius: 24rpx;
	padding: 16rpx 24rpx;
	font-size: 28rpx;
}

.send-btn {
	background: #5b8ff9;
	border-radius: 24rpx;
	padding: 16rpx 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.send-text {
	font-size: 28rpx;
	color: #ffffff;
	font-weight: 500;
}
</style>
