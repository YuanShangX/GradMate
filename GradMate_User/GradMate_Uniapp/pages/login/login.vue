<template>
	<view class="page">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 蓝色顶部区域 -->
		<view class="header-section">
			<view class="header-content">
				<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
				<text class="title">GradMate 考研助手</text>
				<text class="subtitle">您的考研路上的智能伙伴</text>
			</view>
		</view>
		
		<!-- 白色内容区（带圆角） -->
		<view class="content-section">
			<!-- Tab切换 -->
			<view class="tab-bar">
				<view 
					class="tab-item" 
					:class="{ active: currentTab === 0 }"
					@click="currentTab = 0"
				>
					<text class="tab-text">登录</text>
				</view>
				<view 
					class="tab-item" 
					:class="{ active: currentTab === 1 }"
					@click="currentTab = 1"
				>
					<text class="tab-text">注册</text>
				</view>
			</view>

			<!-- 登录表单 -->
			<view v-if="currentTab === 0" class="form-container">
				<view class="form-card">
					<view class="form-item">
						<view class="input-wrapper">
							<view class="input-icon user-icon"></view>
							<input 
								class="input" 
								v-model="loginForm.username" 
								placeholder="请输入账号"
								maxlength="50"
							/>
						</view>
					</view>
					
					<view class="form-item">
						<view class="input-wrapper">
							<view class="input-icon lock-icon"></view>
							<input 
								class="input" 
								v-model="loginForm.password" 
								placeholder="请输入密码"
								type="password"
								maxlength="50"
							/>
						</view>
					</view>
				</view>
				
				<button class="submit-btn" @click="handleLogin" :disabled="!canLogin">
					{{ loginLoading ? '登录中...' : '立即登录' }}
				</button>
			</view>

			<!-- 注册表单 -->
			<view v-else class="form-container">
				<view class="form-card">
					<view class="form-item">
						<view class="input-wrapper">
							<view class="input-icon user-icon"></view>
							<input 
								class="input" 
								v-model="registerForm.username" 
								placeholder="请输入账号（字母或数字）"
								maxlength="50"
							/>
						</view>
					</view>
					
					<view class="form-item">
						<view class="input-wrapper">
							<view class="input-icon lock-icon"></view>
							<input 
								class="input" 
								v-model="registerForm.password" 
								placeholder="请输入密码（6位以上）"
								type="password"
								maxlength="50"
							/>
						</view>
					</view>
					
					<view class="form-item">
						<view class="input-wrapper">
							<view class="input-icon lock-icon"></view>
							<input 
								class="input" 
								v-model="registerForm.confirmPassword" 
								placeholder="请再次输入密码"
								type="password"
								maxlength="50"
							/>
						</view>
					</view>
					
					<view class="form-item">
						<view class="input-wrapper">
							<view class="input-icon nickname-icon"></view>
							<input 
								class="input" 
								v-model="registerForm.nickname" 
								placeholder="请输入昵称"
								maxlength="20"
							/>
						</view>
					</view>
				</view>
				
				<button class="submit-btn" @click="handleRegister" :disabled="!canRegister">
					{{ registerLoading ? '注册中...' : '立即注册' }}
				</button>
			</view>
		</view>
	</view>
</template>

<script>
import { post } from '@/utils/request.js';

export default {
	data() {
		return {
			statusBarHeight: 0,
			currentTab: 0, // 0-登录 1-注册
			loginForm: {
				username: '',
				password: ''
			},
			registerForm: {
				username: '',
				password: '',
				confirmPassword: '',
				nickname: ''
			},
			loginLoading: false,
			registerLoading: false
		}
	},
	onLoad() {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight || 0
		
		// 检查是否已登录
		this.checkAutoLogin()
	},
	computed: {
		canLogin() {
			return this.loginForm.username.trim().length > 0 
				&& this.loginForm.password.trim().length > 0 
				&& !this.loginLoading;
		},
		canRegister() {
			return this.registerForm.username.trim().length > 0 
				&& this.registerForm.password.trim().length >= 6
				&& this.registerForm.confirmPassword.trim().length > 0
				&& !this.registerLoading;
		}
	},
	methods: {
		// 检查自动登录
		checkAutoLogin() {
			const isLogin = uni.getStorageSync('isLogin')
			const userInfo = uni.getStorageSync('userInfo')
			
			if (isLogin && userInfo && userInfo.id) {
				// 已登录，自动跳转到首页
				uni.showToast({
					title: '自动登录中...',
					icon: 'loading',
					duration: 1000
				})
				
				setTimeout(() => {
					uni.switchTab({
						url: '/pages/school/index'
					})
				}, 1000)
			}
		},
		
		// 登录
		async handleLogin() {
			if (!this.canLogin) return;
			
			this.loginLoading = true;
			
			try {
				const res = await post('/user/login', {
					username: this.loginForm.username,
					password: this.loginForm.password
				});
				
				if (res.code === 200) {
					// 保存用户信息到本地
					uni.setStorageSync('userInfo', res.data);
					uni.setStorageSync('userId', res.data.id);
					uni.setStorageSync('isLogin', true);
					
					uni.showToast({
						title: '登录成功',
						icon: 'success'
					});
					
					// 延迟跳转
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/school/index'
						});
					}, 1500);
				}
			} catch (error) {
				console.error('登录失败:', error);
			} finally {
				this.loginLoading = false;
			}
		},
		
		// 注册
		async handleRegister() {
			if (!this.canRegister) return;
			
			// 验证密码
			if (this.registerForm.password !== this.registerForm.confirmPassword) {
				uni.showToast({
					title: '两次密码不一致',
					icon: 'none'
				});
				return;
			}
			
			if (this.registerForm.password.length < 6) {
				uni.showToast({
					title: '密码至少6位',
					icon: 'none'
				});
				return;
			}
			
			this.registerLoading = true;
			
			try {
				const res = await post('/user/register', {
					username: this.registerForm.username,
					password: this.registerForm.password,
					nickname: this.registerForm.nickname || this.registerForm.username
				});
				
				if (res.code === 200) {
					uni.showToast({
						title: '注册成功，请登录',
						icon: 'success'
					});
					
					// 切换到登录Tab，并填充账号
					setTimeout(() => {
						this.currentTab = 0;
						this.loginForm.username = this.registerForm.username;
						this.loginForm.password = this.registerForm.password;
					}, 1500);
				}
			} catch (error) {
				console.error('注册失败:', error);
			} finally {
				this.registerLoading = false;
			}
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #006cff;
}

.status-bar {
	background: #006cff;
}

/* 蓝色顶部区域 */
.header-section {
	background: #006cff;
	padding: 60rpx 30rpx 100rpx;
}

.header-content {
	text-align: center;
}

.logo {
	width: 140rpx;
	height: 140rpx;
	margin-bottom: 32rpx;
	border-radius: 70rpx;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
}

.title {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 16rpx;
}

.subtitle {
	display: block;
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.85);
}

/* 白色内容区 */
.content-section {
	background: linear-gradient(180deg, #e8f3ff 0%, #f6f7fb 15%, #f6f7fb 100%);
	border-radius: 32rpx 32rpx 0 0;
	margin-top: -32rpx;
	padding: 32rpx 30rpx 60rpx;
	min-height: calc(100vh - 400rpx);
}

/* Tab切换 */
.tab-bar {
	display: flex;
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 50rpx;
	padding: 8rpx;
	margin-bottom: 32rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 20rpx 0;
	border-radius: 44rpx;
	transition: all 0.3s ease;
}

.tab-item.active {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.25);
}

.tab-text {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
}

.tab-item.active .tab-text {
	color: #ffffff;
	font-weight: 600;
}

/* 表单容器 */
.form-container {
	padding-top: 8rpx;
}

.form-card {
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	padding: 16rpx 24rpx;
	margin-bottom: 32rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

.form-item {
	margin-bottom: 24rpx;
}

.form-item:last-child {
	margin-bottom: 0;
}

.input-wrapper {
	display: flex;
	align-items: center;
	background: linear-gradient(135deg, #f0f8ff 0%, #e8f3ff 100%);
	border-radius: 50rpx;
	padding: 0 24rpx;
	border: 2rpx solid rgba(0, 107, 255, 0.1);
	transition: all 0.3s ease;
}

.input-wrapper:focus-within {
	border-color: #006cff;
	background: #ffffff;
	box-shadow: 0 2rpx 8rpx rgba(0, 107, 255, 0.15);
}

.input-icon {
	width: 36rpx;
	height: 36rpx;
	margin-right: 16rpx;
	flex-shrink: 0;
	position: relative;
}

.user-icon {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	border-radius: 50%;
}

.user-icon::before {
	content: '';
	position: absolute;
	width: 14rpx;
	height: 14rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	top: 8rpx;
	left: 11rpx;
}

.user-icon::after {
	content: '';
	position: absolute;
	width: 20rpx;
	height: 10rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 0 0 10rpx 10rpx;
	bottom: 6rpx;
	left: 8rpx;
}

.lock-icon {
	background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
	border-radius: 8rpx;
}

.lock-icon::before {
	content: '';
	position: absolute;
	width: 18rpx;
	height: 14rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-bottom: none;
	border-radius: 9rpx 9rpx 0 0;
	top: 6rpx;
	left: 9rpx;
}

.lock-icon::after {
	content: '';
	position: absolute;
	width: 20rpx;
	height: 12rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 3rpx;
	bottom: 8rpx;
	left: 8rpx;
}

.nickname-icon {
	background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
	border-radius: 50%;
}

.nickname-icon::before {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-left: 8rpx solid transparent;
	border-right: 8rpx solid transparent;
	border-bottom: 12rpx solid rgba(255, 255, 255, 0.9);
	top: 8rpx;
	left: 10rpx;
}

.nickname-icon::after {
	content: '';
	position: absolute;
	width: 0;
	height: 0;
	border-left: 8rpx solid transparent;
	border-right: 8rpx solid transparent;
	border-top: 12rpx solid rgba(255, 255, 255, 0.9);
	bottom: 8rpx;
	left: 10rpx;
}

.input {
	flex: 1;
	height: 80rpx;
	font-size: 28rpx;
	color: #333;
}

.input::placeholder {
	color: #999;
}

/* 提交按钮 */
.submit-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	color: #ffffff;
	border: none;
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: 600;
	box-shadow: 0 8rpx 20rpx rgba(0, 107, 255, 0.3);
	transition: all 0.3s ease;
}

.submit-btn:active {
	opacity: 0.9;
	transform: scale(0.98);
}

.submit-btn:disabled {
	background: linear-gradient(135deg, #e0e0e0 0%, #d0d0d0 100%);
	color: #999;
	box-shadow: none;
}
</style>
