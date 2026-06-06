<template>
	<view class="page">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 蓝色顶部区域 -->
		<view class="header-section">
			<view class="header-content">
				<view class="menu-btn" @click="showSidebar = !showSidebar">
					<view class="menu-icon"></view>
				</view>
				<text class="page-title">{{ currentSessionTitle }}</text>
				<view class="new-chat-btn" @click="createNewSession">
					<view class="plus-icon"></view>
				</view>
			</view>
		</view>
		
		<!-- 白色内容区（带圆角） -->
		<view class="content-section">
			<!-- 消息列表 -->
			<scroll-view 
				class="message-list" 
				scroll-y 
				:scroll-into-view="scrollIntoView"
				scroll-with-animation
			>
				<view v-if="messages.length === 0" class="empty">
					<image class="empty-icon" src="/static/AI_Image.png" mode="aspectFit"></image>
					<text class="empty-text">你好！我是研小伴</text>
					<text class="empty-desc">我可以帮你解答考研问题、辅导课业、疏导情绪</text>
					
					<!-- 快捷问题 -->
					<view class="quick-questions">
						<view 
							v-for="(q, index) in quickQuestions" 
							:key="index"
							class="quick-item"
							@click="sendQuickQuestion(q)"
						>
							{{ q }}
						</view>
					</view>
				</view>

				<view 
					v-for="(msg, index) in messages" 
					:key="index"
					:id="'msg-' + index"
					class="message-item"
					:class="msg.role"
				>
					<!-- 消息卡片 -->
					<view class="message-card" :class="msg.role">
						<MarkdownText v-if="msg.role === 'ai'" :content="msg.content" />
						<text v-else class="message-text">{{ msg.content }}</text>
					</view>
				</view>
				
				<!-- 加载动画 -->
				<view v-if="loading" class="message-item ai">
					<view class="message-card ai loading">
						<text class="loading-text">思考中</text>
						<view class="loading-dots">
							<view class="dot"></view>
							<view class="dot"></view>
							<view class="dot"></view>
						</view>
					</view>
				</view>
			</scroll-view>
			
			<!-- 输入栏 -->
			<view class="input-bar">
				<input 
					class="input" 
					v-model="inputText" 
					placeholder="输入你的问题..."
					:disabled="loading"
					@confirm.stop.prevent="sendMessage"
				/>
				<view 
					class="send-btn" 
					:class="{ disabled: !inputText.trim() || loading }"
					@click.stop="sendMessage"
				>
					<view class="send-icon"></view>
				</view>
			</view>
		</view>

		<!-- 会话列表侧边栏 -->
		<view class="sidebar" :class="{ show: showSidebar }">
			<view class="sidebar-header">
				<text class="sidebar-title">对话列表</text>
				<view class="close-btn" @click="showSidebar = false">
					<view class="close-icon"></view>
				</view>
			</view>
			
			<scroll-view class="session-list" scroll-y>
				<view 
					v-for="session in sessions" 
					:key="session.sessionId"
					class="session-item"
					:class="{ active: currentSessionId === session.sessionId }"
					@click="switchSession(session.sessionId)"
				>
					<view class="session-main">
						<text class="session-title">{{ session.title }}</text>
						<text class="session-count">{{ session.messageCount }}条消息</text>
					</view>
					<view class="session-actions">
						<view class="action-btn edit-btn" @click.stop="showRenameDialog(session)">
							<view class="edit-icon"></view>
						</view>
						<view class="action-btn delete-btn" @click.stop="deleteSessionConfirm(session.sessionId)">
							<view class="delete-icon"></view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
		
		<!-- 遮罩层 -->
		<view v-if="showSidebar" class="mask" @click="showSidebar = false"></view>

		<!-- 重命名对话框 -->
		<view v-if="showRename" class="dialog-mask" @click="showRename = false">
			<view class="dialog" @click.stop>
				<view class="dialog-title">重命名对话</view>
				<input 
					class="dialog-input" 
					v-model="renameTitle" 
					placeholder="请输入新标题"
					maxlength="50"
				/>
				<view class="dialog-actions">
					<view class="dialog-btn cancel" @click="showRename = false">
						<text class="btn-text">取消</text>
					</view>
					<view class="dialog-btn confirm" @click="confirmRename">
						<text class="btn-text">确定</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { post, get, del, put } from '@/utils/request.js';
import MarkdownText from '@/components/MarkdownText.vue';

export default {
	components: {
		MarkdownText
	},
	data() {
		return {
			statusBarHeight: 0,
			userId: 1,
			sessions: [],
			currentSessionId: null,
			currentSessionTitle: '研小伴',
			messages: [],
			inputText: '',
			loading: false,
			scrollIntoView: '',
			showSidebar: false,
			showRename: false,
			renameTitle: '',
			renameSessionId: null,
			quickQuestions: [
				'如何选择考研院校?',
				'考研英语怎么复习?',
				'高数极限怎么求?',
				'我感觉压力好大...'
			],
			typingTimer: null,
			typingSpeed: 30 // 打字速度（毫秒/字）
		};
	},
	
	onLoad() {
		const systemInfo = uni.getSystemInfoSync();
		this.statusBarHeight = systemInfo.statusBarHeight || 0;
		this.userId = uni.getStorageSync('userId') || 1;
		this.loadSessions();
	},
	
	onUnload() {
		// 清理定时器
		if (this.typingTimer) {
			clearInterval(this.typingTimer);
			this.typingTimer = null;
		}
	},
	
	methods: {
		async loadSessions() {
			try {
				const res = await get(`/ai/session/list?userId=${this.userId}`, {}, false);
				if (res.code === 200) {
					this.sessions = res.data;
					// 只在初始化时加载消息，不要在每次刷新会话列表时清空消息
					if (this.sessions.length > 0 && !this.currentSessionId) {
						this.currentSessionId = this.sessions[0].sessionId;
						this.currentSessionTitle = this.sessions[0].title;
						await this.loadMessages();
					}
				}
			} catch (error) {
				console.error('加载会话列表失败:', error);
			}
		},
		
		async loadMessages() {
			if (!this.currentSessionId) {
				this.messages = [];
				return;
			}
			
			try {
				const res = await get(`/ai/session/${this.currentSessionId}/messages`, {}, false);
				if (res.code === 200 && res.data) {
					// 确保role值标准化为"ai"或"user"
					this.messages = res.data.map(msg => {
						let role = msg.role;
						// 将assistant转换为ai
						if (role === 'assistant') {
							role = 'ai';
						}
						return {
							role: role,
							content: msg.content
						};
					});
					
					// 调试日志：查看加载的消息
					console.log('加载的历史消息:', this.messages);
					this.messages.forEach((msg, index) => {
						console.log(`消息${index}: role="${msg.role}", content长度=${msg.content.length}`);
					});
					
					this.scrollToBottom();
				}
			} catch (error) {
				console.error('加载历史消息失败:', error);
				this.messages = [];
			}
		},
		
		async createNewSession() {
			try {
				const res = await post('/ai/session/create', {
					userId: this.userId
				});
				
				if (res.code === 200) {
					const newSession = {
						sessionId: res.data.sessionId,
						title: res.data.title,
						messageCount: 0,
						createTime: res.data.createTime,
						updateTime: res.data.createTime
					};
					
					this.sessions.unshift(newSession);
					this.currentSessionId = newSession.sessionId;
					this.currentSessionTitle = newSession.title;
					this.messages = [];
					this.showSidebar = false;
					
					uni.showToast({
						title: '新对话已创建',
						icon: 'success'
					});
				}
			} catch (error) {
				console.error('创建会话失败:', error);
				uni.showToast({
					title: '创建失败',
					icon: 'none'
				});
			}
		},
		
		async switchSession(sessionId) {
			if (this.currentSessionId === sessionId) {
				this.showSidebar = false;
				return;
			}
			
			this.currentSessionId = sessionId;
			const session = this.sessions.find(s => s.sessionId === sessionId);
			if (session) {
				this.currentSessionTitle = session.title;
			}
			
			this.messages = [];
			this.showSidebar = false;
			await this.loadMessages();
		},
		
		showRenameDialog(session) {
			this.renameSessionId = session.sessionId;
			this.renameTitle = session.title;
			this.showRename = true;
		},
		
		async confirmRename() {
			if (!this.renameTitle.trim()) {
				uni.showToast({
					title: '标题不能为空',
					icon: 'none'
				});
				return;
			}
			
			try {
				const res = await put(`/ai/session/${this.renameSessionId}/title`, {
					title: this.renameTitle
				});
				
				if (res.code === 200) {
					const session = this.sessions.find(s => s.sessionId === this.renameSessionId);
					if (session) {
						session.title = this.renameTitle;
					}
					
					if (this.currentSessionId === this.renameSessionId) {
						this.currentSessionTitle = this.renameTitle;
					}
					
					this.showRename = false;
					uni.showToast({
						title: '重命名成功',
						icon: 'success'
					});
				}
			} catch (error) {
				console.error('重命名失败:', error);
				uni.showToast({
					title: '重命名失败',
					icon: 'none'
				});
			}
		},
		
		deleteSessionConfirm(sessionId) {
			uni.showModal({
				title: '提示',
				content: '确定要删除这个对话吗?',
				success: async (res) => {
					if (res.confirm) {
						await this.deleteSession(sessionId);
					}
				}
			});
		},
		
		async deleteSession(sessionId) {
			try {
				const res = await del(`/ai/session/${sessionId}`);
				
				if (res.code === 200) {
					this.sessions = this.sessions.filter(s => s.sessionId !== sessionId);
					
					if (this.currentSessionId === sessionId) {
						if (this.sessions.length > 0) {
							this.currentSessionId = this.sessions[0].sessionId;
							this.currentSessionTitle = this.sessions[0].title;
							await this.loadMessages();
						} else {
							await this.createNewSession();
						}
					}
					
					uni.showToast({
						title: '已删除',
						icon: 'success'
					});
				}
			} catch (error) {
				console.error('删除会话失败:', error);
				uni.showToast({
					title: '删除失败',
					icon: 'none'
				});
			}
		},
		
		async sendMessage(e) {
			// 阻止默认行为（防止表单提交导致页面刷新）
			if (e && e.preventDefault) {
				e.preventDefault();
			}
			
			if (!this.inputText.trim() || this.loading) return;
			
			if (!this.currentSessionId) {
				await this.createNewSession();
			}
			
			const userMessage = this.inputText.trim();
			this.inputText = '';
			
			this.messages.push({
				role: 'user',
				content: userMessage
			});
			
			this.scrollToBottom();
			this.loading = true;
			
			try {
				console.log('发送消息:', {
					userId: this.userId,
					sessionId: this.currentSessionId,
					message: userMessage
				});
				
				const res = await post('/ai/chat', {
					userId: this.userId,
					sessionId: this.currentSessionId,
					message: userMessage
				}, false);
				
				console.log('收到响应:', res);
				
				if (res.code === 200) {
					console.log('AI回复内容:', res.data);
					// 添加一个空的AI消息，然后逐字显示
					const aiMessageIndex = this.messages.length;
					this.messages.push({
						role: 'ai',
						content: ''
					});
					
					// 逐字显示AI回复
					this.typewriterEffect(res.data, aiMessageIndex);
					
					// 只更新会话列表，不重新加载消息
					this.loadSessions();
				} else {
					console.error('业务错误:', res.message);
					this.messages.push({
						role: 'ai',
						content: res.message || '抱歉,我现在有点累了,请稍后再试~'
					});
				}
			} catch (error) {
				console.error('发送消息失败:', error);
				this.messages.push({
					role: 'ai',
					content: '网络连接失败,请检查网络后重试。'
				});
				this.loading = false;
			}
			
			// 返回false阻止默认行为
			return false;
		},
		
		sendQuickQuestion(question) {
			this.inputText = question;
			this.sendMessage();
		},
		
		scrollToBottom() {
			this.$nextTick(() => {
				this.scrollIntoView = 'msg-' + (this.messages.length - 1);
			});
		},
		
		// 打字机效果
		typewriterEffect(fullText, messageIndex) {
			if (this.typingTimer) {
				clearInterval(this.typingTimer);
			}
			
			let currentIndex = 0;
			const textLength = fullText.length;
			
			this.typingTimer = setInterval(() => {
				if (currentIndex < textLength) {
					// 每次添加一个字符
					this.messages[messageIndex].content = fullText.substring(0, currentIndex + 1);
					currentIndex++;
					
					// 自动滚动到底部
					this.scrollToBottom();
				} else {
					// 完成打字效果
					clearInterval(this.typingTimer);
					this.typingTimer = null;
					this.loading = false;
				}
			}, this.typingSpeed);
		}
	}
};
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #006cff;
	display: flex;
	flex-direction: column;
}

.status-bar {
	background: #006cff;
}

/* 蓝色顶部区域 */
.header-section {
	background: #006cff;
	padding: 20rpx 30rpx 80rpx;
}

.header-content {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.menu-btn {
	width: 56rpx;
	height: 56rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.menu-icon {
	width: 32rpx;
	height: 24rpx;
	position: relative;
}

.menu-icon::before,
.menu-icon::after {
	content: '';
	position: absolute;
	left: 0;
	width: 100%;
	height: 3rpx;
	background: #ffffff;
	border-radius: 2rpx;
}

.menu-icon::before {
	top: 0;
	box-shadow: 0 10rpx 0 #ffffff;
}

.menu-icon::after {
	bottom: 0;
}

.page-title {
	flex: 1;
	font-size: 36rpx;
	font-weight: 600;
	color: #ffffff;
	text-align: center;
}

.new-chat-btn {
	width: 56rpx;
	height: 56rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.plus-icon {
	width: 24rpx;
	height: 24rpx;
	position: relative;
}

.plus-icon::before,
.plus-icon::after {
	content: '';
	position: absolute;
	background: #ffffff;
	border-radius: 2rpx;
}

.plus-icon::before {
	width: 24rpx;
	height: 3rpx;
	top: 50%;
	left: 0;
	transform: translateY(-50%);
}

.plus-icon::after {
	width: 3rpx;
	height: 24rpx;
	left: 50%;
	top: 0;
	transform: translateX(-50%);
}

/* 白色内容区 */
.content-section {
	flex: 1;
	background: #ffffff;
	border-radius: 32rpx 32rpx 0 0;
	margin-top: -32rpx;
	display: flex;
	flex-direction: column;
	position: relative;
}

.message-list {
	flex: 1;
	padding: 24rpx 30rpx;
	padding-bottom: 240rpx;
}

.empty {
	text-align: center;
	padding: 120rpx 60rpx 60rpx;
}

.empty-icon {
	width: 120rpx;
	height: 120rpx;
	margin: 0 auto 30rpx;
	border-radius: 50%;
	display: block;
}

.empty-text {
	font-size: 32rpx;
	color: #333;
	display: block;
	margin-bottom: 16rpx;
	font-weight: 600;
}

.empty-desc {
	font-size: 26rpx;
	color: #999;
	line-height: 1.6;
	display: block;
	margin-bottom: 40rpx;
}

.quick-questions {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
	justify-content: center;
	margin-top: 20rpx;
}

.quick-item {
	padding: 20rpx 28rpx;
	background: #ffffff;
	border-radius: 40rpx;
	font-size: 26rpx;
	color: #5b8ff9;
	border: 1rpx solid #d4e8ff;
	box-shadow: 0 2rpx 8rpx rgba(91, 143, 249, 0.1);
}

/* 消息卡片 */
.message-item {
	display: flex;
	margin-bottom: 20rpx;
	padding: 0 30rpx;
}

.message-item.ai {
	justify-content: flex-start;
}

.message-item.user {
	justify-content: flex-end;
}

.message-card {
	max-width: 600rpx;
	min-width: 200rpx;
	padding: 24rpx 28rpx 24rpx 28rpx;
	border-radius: 24rpx;
	word-wrap: break-word;
	box-sizing: border-box;
}

.message-card.ai {
	background: #f5f5f5;
	border-bottom-left-radius: 6rpx;
}

.message-card.user {
	background: #0068ff;
	border-bottom-right-radius: 6rpx;
	margin-right: 60rpx;
}

.message-text {
	font-size: 32rpx;
	line-height: 1.6;
	word-wrap: break-word;
}

.message-card.ai .message-text {
	color: #333;
}

.message-card.user .message-text {
	color: #ffffff;
	text-decoration: underline;
}

.message-card.loading {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.loading-text {
	font-size: 28rpx;
	color: #999;
}

.loading-dots {
	display: flex;
	gap: 6rpx;
}

.dot {
	width: 8rpx;
	height: 8rpx;
	background: #ccc;
	border-radius: 50%;
	animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) {
	animation-delay: -0.32s;
}

.dot:nth-child(2) {
	animation-delay: -0.16s;
}

@keyframes bounce {
	0%, 80%, 100% {
		transform: scale(0);
	}
	40% {
		transform: scale(1);
	}
}

/* 输入栏 */
.input-bar {
	position: fixed;
	bottom: 100rpx;
	left: 0;
	right: 0;
	background: #ffffff;
	padding: 20rpx 30rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	display: flex;
	gap: 16rpx;
	border-top: 1rpx solid #f0f0f0;
	box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.04);
	z-index: 100;
}

.input {
	flex: 1;
	height: 72rpx;
	background: #f5f7fa;
	border-radius: 36rpx;
	padding: 0 28rpx;
	font-size: 28rpx;
	color: #333;
}

.send-btn {
	width: 72rpx;
	height: 72rpx;
	background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.3);
}

.send-btn.disabled {
	background: #e0e0e0;
	box-shadow: none;
}

.send-icon {
	width: 28rpx;
	height: 28rpx;
	border: 3rpx solid #ffffff;
	border-left: none;
	border-bottom: none;
	transform: rotate(45deg);
	margin-left: 4rpx;
}

/* 侧边栏 */
.sidebar {
	position: fixed;
	left: -650rpx;
	top: 0;
	bottom: 0;
	width: 600rpx;
	background: #ffffff;
	box-shadow: 2rpx 0 16rpx rgba(0, 0, 0, 0.1);
	transition: left 0.3s ease;
	z-index: 1000;
	display: flex;
	flex-direction: column;
}

.sidebar.show {
	left: 0;
}

.sidebar-header {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	display: flex;
	justify-content: space-between;
	align-items: center;
	background: linear-gradient(135deg, #e8f3ff 0%, #ffffff 100%);
}

.sidebar-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.close-btn {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.close-icon {
	width: 28rpx;
	height: 28rpx;
	position: relative;
}

.close-icon::before,
.close-icon::after {
	content: '';
	position: absolute;
	width: 28rpx;
	height: 3rpx;
	background: #999;
	border-radius: 2rpx;
	top: 50%;
	left: 0;
}

.close-icon::before {
	transform: translateY(-50%) rotate(45deg);
}

.close-icon::after {
	transform: translateY(-50%) rotate(-45deg);
}

.session-list {
	flex: 1;
	overflow-y: auto;
}

.session-item {
	padding: 28rpx 30rpx;
	border-bottom: 1rpx solid #f8f8f8;
	display: flex;
	justify-content: space-between;
	align-items: center;
	transition: background 0.2s;
}

.session-item.active {
	background: linear-gradient(135deg, #e8f3ff 0%, #f0f8ff 100%);
	border-left: 4rpx solid #5b8ff9;
}

.session-main {
	flex: 1;
	min-width: 0;
	margin-right: 16rpx;
}

.session-title {
	font-size: 28rpx;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	font-weight: 500;
}

.session-count {
	font-size: 24rpx;
	color: #999;
}

.session-actions {
	display: flex;
	gap: 8rpx;
	flex-shrink: 0;
}

.action-btn {
	width: 48rpx;
	height: 48rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f5f7fa;
}

.edit-btn {
	background: #e8f3ff;
}

.delete-btn {
	background: #fff0f0;
}

.edit-icon {
	width: 20rpx;
	height: 20rpx;
	border: 2rpx solid #5b8ff9;
	border-radius: 3rpx;
	position: relative;
}

.edit-icon::before {
	content: '';
	position: absolute;
	width: 12rpx;
	height: 2rpx;
	background: #5b8ff9;
	top: -6rpx;
	right: -6rpx;
	transform: rotate(45deg);
}

.delete-icon {
	width: 20rpx;
	height: 24rpx;
	border: 2rpx solid #ff4d4f;
	border-top: none;
	border-radius: 0 0 3rpx 3rpx;
	position: relative;
}

.delete-icon::before {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 2rpx;
	background: #ff4d4f;
	top: -4rpx;
	left: -4rpx;
}

.delete-icon::after {
	content: '';
	position: absolute;
	width: 8rpx;
	height: 2rpx;
	background: #ff4d4f;
	top: -8rpx;
	left: 4rpx;
}

.mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	z-index: 999;
}

/* 重命名对话框 */
.dialog-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 2000;
}

.dialog {
	width: 600rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 40rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
}

.dialog-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 30rpx;
	text-align: center;
}

.dialog-input {
	width: 100%;
	height: 80rpx;
	border: 1rpx solid #e0e0e0;
	border-radius: 12rpx;
	padding: 0 24rpx;
	font-size: 28rpx;
	margin-bottom: 30rpx;
	background: #f8f9fa;
	box-sizing: border-box;
}

.dialog-actions {
	display: flex;
	gap: 20rpx;
}

.dialog-btn {
	flex: 1;
	height: 80rpx;
	border-radius: 40rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.dialog-btn.cancel {
	background: #f5f7fa;
}

.dialog-btn.confirm {
	background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
	box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.3);
}

.btn-text {
	font-size: 30rpx;
	font-weight: 500;
}

.dialog-btn.cancel .btn-text {
	color: #666;
}

.dialog-btn.confirm .btn-text {
	color: #ffffff;
}
</style>
