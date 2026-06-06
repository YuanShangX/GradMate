<template>
	<view class="page">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 蓝色顶部区域 -->
		<view class="header-section">
			<view class="header-content">
				<view class="back-btn" @click="goBack">
					<text class="back-icon">‹</text>
				</view>
				<text class="page-title">编辑资料</text>
				<view class="placeholder"></view>
			</view>
		</view>
		
		<!-- 白色内容区（带圆角） -->
		<view class="content-section">
			<view class="form-section">
				<!-- 头像（不可更改） -->
				<view class="form-item">
					<text class="label">头像</text>
					<view class="avatar-box">
						<image src="/static/logo.png" class="avatar"></image>
					</view>
				</view>
				
				<!-- 昵称 -->
				<view class="form-item">
					<text class="label">昵称</text>
					<input 
						v-model="formData.nickname" 
						class="input" 
						placeholder="请输入昵称"
						maxlength="20"
					/>
				</view>
				
				<!-- 手机号 -->
				<view class="form-item">
					<text class="label">手机号</text>
					<input 
						v-model="formData.phone" 
						class="input" 
						type="number"
						placeholder="请输入手机号"
						maxlength="11"
					/>
				</view>
				
				<!-- 修改密码 -->
				<view class="form-item">
					<text class="label">新密码</text>
					<input 
						v-model="formData.password" 
						class="input" 
						type="password"
						placeholder="留空则不修改密码"
						maxlength="20"
					/>
				</view>
				
				<!-- 确认密码 -->
				<view class="form-item" v-if="formData.password">
					<text class="label">确认密码</text>
					<input 
						v-model="formData.confirmPassword" 
						class="input" 
						type="password"
						placeholder="请再次输入新密码"
						maxlength="20"
					/>
				</view>
			</view>
			
			<!-- 保存按钮 -->
			<view class="btn-box">
				<button class="save-btn" @click="saveProfile">保存</button>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { buildApiUrl } from '@/utils/request.js'

const statusBarHeight = ref(0)
const formData = ref({
	nickname: '',
	phone: '',
	password: '',
	confirmPassword: ''
})

onMounted(() => {
	const systemInfo = uni.getSystemInfoSync()
	statusBarHeight.value = systemInfo.statusBarHeight || 0
	loadUserInfo()
})

// 返回上一页
const goBack = () => {
	uni.navigateBack()
}

// 加载用户信息
const loadUserInfo = () => {
	const userId = uni.getStorageSync('userId')
	if (!userId) {
		uni.showToast({
			title: '请先登录',
			icon: 'none'
		})
		setTimeout(() => {
			uni.navigateBack()
		}, 1500)
		return
	}
	
	uni.request({
		url: buildApiUrl(`/user/info/${userId}`),
		method: 'GET',
		success: (res) => {
			if (res.data.code === 200) {
				const user = res.data.data
				formData.value = {
					nickname: user.nickname || '',
					phone: user.phone || '',
					password: '',
					confirmPassword: ''
				}
			}
		}
	})
}

// 保存个人信息
const saveProfile = () => {
	const userId = uni.getStorageSync('userId')
	
	// 验证手机号
	if (formData.value.phone && !/^1[3-9]\d{9}$/.test(formData.value.phone)) {
		uni.showToast({
			title: '手机号格式不正确',
			icon: 'none'
		})
		return
	}
	
	// 验证密码
	if (formData.value.password) {
		if (formData.value.password.length < 6) {
			uni.showToast({
				title: '密码至少6位',
				icon: 'none'
			})
			return
		}
		
		if (formData.value.password !== formData.value.confirmPassword) {
			uni.showToast({
				title: '两次密码不一致',
				icon: 'none'
			})
			return
		}
	}
	
	uni.showLoading({ title: '保存中...' })
	
	const updateData = {
		id: userId,
		nickname: formData.value.nickname,
		phone: formData.value.phone
	}
	
	// 如果填写了密码，则包含密码字段
	if (formData.value.password) {
		updateData.password = formData.value.password
	}
	
	uni.request({
		url: buildApiUrl('/user/update'),
		method: 'PUT',
		data: updateData,
		success: (res) => {
			uni.hideLoading()
			if (res.data.code === 200) {
				uni.showToast({
					title: '保存成功',
					icon: 'success'
				})
				
				// 更新本地存储的用户信息
				const userInfo = uni.getStorageSync('userInfo') || {}
				userInfo.nickname = formData.value.nickname
				userInfo.phone = formData.value.phone
				uni.setStorageSync('userInfo', userInfo)
				
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} else {
				uni.showToast({
					title: res.data.message || '保存失败',
					icon: 'none'
				})
			}
		},
		fail: () => {
			uni.hideLoading()
			uni.showToast({
				title: '网络错误',
				icon: 'none'
			})
		}
	})
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
	padding: 20rpx 30rpx 80rpx;
}

.header-content {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.back-btn {
	width: 64rpx;
	height: 64rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 32rpx;
}

.back-icon {
	font-size: 48rpx;
	color: #ffffff;
	font-weight: bold;
	line-height: 1;
}

.page-title {
	flex: 1;
	text-align: center;
	font-size: 36rpx;
	font-weight: 600;
	color: #ffffff;
}

.placeholder {
	width: 64rpx;
}

/* 白色内容区 */
.content-section {
	background: linear-gradient(180deg, #e8f3ff 0%, #f6f7fb 15%, #f6f7fb 100%);
	border-radius: 32rpx 32rpx 0 0;
	margin-top: -32rpx;
	padding: 24rpx 30rpx 40rpx;
	min-height: calc(100vh - 200rpx);
}

.form-section {
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
	margin-bottom: 24rpx;
}

.form-item {
	display: flex;
	align-items: center;
	padding: 32rpx 24rpx;
	border-bottom: 1rpx solid rgba(0, 107, 255, 0.05);
	transition: background 0.2s ease;
}

.form-item:active {
	background: rgba(0, 107, 255, 0.02);
}

.form-item:last-child {
	border-bottom: none;
}

.label {
	width: 160rpx;
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.avatar-box {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: flex-end;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	border: 4rpx solid rgba(0, 107, 255, 0.1);
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.15);
}

.btn-box {
	padding: 16rpx 0;
}

.save-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	border-radius: 44rpx;
	color: #ffffff;
	font-size: 32rpx;
	font-weight: 600;
	border: none;
	box-shadow: 0 8rpx 20rpx rgba(0, 107, 255, 0.3);
}

.save-btn:active {
	opacity: 0.9;
	transform: scale(0.98);
}
</style>
