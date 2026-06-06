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
				<text class="page-title">设置目标</text>
				<view class="placeholder"></view>
			</view>
		</view>
		
		<!-- 白色内容区（带圆角） -->
		<view class="content-section">
			<!-- 步骤指示 -->
			<view class="steps">
				<view class="step" :class="{ active: step >= 1, completed: step > 1 }">
					<view class="step-number">1</view>
					<text class="step-text">选择院校</text>
				</view>
				<view class="step-line" :class="{ active: step > 1 }"></view>
				<view class="step" :class="{ active: step >= 2, completed: step > 2 }">
					<view class="step-number">2</view>
					<text class="step-text">选择专业</text>
				</view>
				<view class="step-line" :class="{ active: step > 2 }"></view>
				<view class="step" :class="{ active: step >= 3 }">
					<view class="step-number">3</view>
					<text class="step-text">确认保存</text>
				</view>
			</view>

			<!-- 步骤1：选择院校 -->
			<view v-if="step === 1" class="step-content">
				<view class="search-box">
					<view class="search-icon"></view>
					<input 
						class="search-input" 
						v-model="schoolKeyword" 
						placeholder="搜索院校名称"
						@input="searchSchool"
					/>
				</view>
				
				<scroll-view class="school-list" scroll-y>
					<view 
						v-for="school in schoolList" 
						:key="school.id"
						class="school-item"
						@click="selectSchool(school)"
					>
						<view class="item-content">
							<text class="school-name">{{ school.schoolName }}</text>
							<text class="school-location">{{ school.location }}</text>
						</view>
						<text class="arrow-icon">›</text>
					</view>
					
					<view v-if="schoolList.length === 0" class="empty">
						<text class="empty-text">暂无搜索结果</text>
					</view>
				</scroll-view>
			</view>

			<!-- 步骤2：选择专业 -->
			<view v-if="step === 2" class="step-content">
				<view class="selected-info">
					<view class="info-icon school-info-icon"></view>
					<view class="info-content">
						<text class="info-label">已选院校</text>
						<text class="info-value">{{ selectedSchool.schoolName }}</text>
					</view>
				</view>
				
				<view class="search-box">
					<view class="search-icon"></view>
					<input 
						class="search-input" 
						v-model="majorKeyword" 
						placeholder="搜索专业名称"
						@input="searchMajor"
					/>
				</view>
				
				<scroll-view class="major-list" scroll-y>
					<view 
						v-for="major in majorList" 
						:key="major.id"
						class="major-item"
						@click="selectMajor(major)"
					>
						<view class="item-content">
							<text class="major-name">{{ major.majorName }}</text>
							<text class="major-code">{{ major.majorCode }}</text>
						</view>
						<text class="arrow-icon">›</text>
					</view>
					
					<view v-if="majorList.length === 0" class="empty">
						<text class="empty-text">该院校暂无专业数据</text>
					</view>
				</scroll-view>
				
				<button class="back-btn-secondary" @click="step = 1">返回重选院校</button>
			</view>

			<!-- 步骤3：确认保存 -->
			<view v-if="step === 3" class="step-content">
				<view class="confirm-card">
					<view class="confirm-item">
						<view class="confirm-icon school-confirm-icon"></view>
						<view class="confirm-content">
							<text class="confirm-label">目标院校</text>
							<text class="confirm-value">{{ selectedSchool.schoolName }}</text>
						</view>
					</view>
					<view class="confirm-item">
						<view class="confirm-icon major-confirm-icon"></view>
						<view class="confirm-content">
							<text class="confirm-label">目标专业</text>
							<text class="confirm-value">{{ selectedMajor.majorName }}</text>
						</view>
					</view>
					<view class="confirm-item">
						<view class="confirm-icon code-confirm-icon"></view>
						<view class="confirm-content">
							<text class="confirm-label">专业代码</text>
							<text class="confirm-value">{{ selectedMajor.majorCode }}</text>
						</view>
					</view>
				</view>
				
				<button class="save-btn" @click="saveTarget">确认保存</button>
				<button class="back-btn-secondary" @click="step = 2">返回重选专业</button>
			</view>
		</view>
	</view>
</template>

<script>
import { get, put } from '@/utils/request.js';

export default {
	data() {
		return {
			statusBarHeight: 0,
			step: 1,
			userId: null,
			schoolKeyword: '',
			majorKeyword: '',
			schoolList: [],
			majorList: [],
			selectedSchool: {},
			selectedMajor: {}
		}
	},
	onLoad() {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight || 0
		
		this.userId = uni.getStorageSync('userId');
		if (!this.userId) {
			uni.showToast({
				title: '请先登录',
				icon: 'none'
			});
			setTimeout(() => {
				uni.navigateBack();
			}, 1500);
		}
	},
	methods: {
		// 返回上一页
		goBack() {
			uni.navigateBack()
		},
		
		// 搜索院校
		async searchSchool() {
			if (!this.schoolKeyword.trim()) {
				this.schoolList = [];
				return;
			}
			
			try {
				const res = await get('/school/list', {
					schoolName: this.schoolKeyword,
					pageNum: 1,
					pageSize: 20
				});
				
				if (res.code === 200) {
					this.schoolList = res.data.records || [];
				}
			} catch (error) {
				console.error('搜索院校失败:', error);
			}
		},
		
		// 选择院校
		selectSchool(school) {
			this.selectedSchool = school;
			this.step = 2;
			this.loadMajors();
		},
		
		// 加载专业列表
		async loadMajors() {
			try {
				const res = await get('/major/school/' + this.selectedSchool.schoolName, {
					pageNum: 1,
					pageSize: 100
				});
				
				if (res.code === 200) {
					this.majorList = res.data.records || [];
				}
			} catch (error) {
				console.error('加载专业失败:', error);
			}
		},
		
		// 搜索专业
		searchMajor() {
			if (!this.majorKeyword.trim()) {
				this.loadMajors();
				return;
			}
			
			// 前端过滤
			this.majorList = this.majorList.filter(major => 
				major.majorName.includes(this.majorKeyword)
			);
		},
		
		// 选择专业
		selectMajor(major) {
			this.selectedMajor = major;
			this.step = 3;
		},
		
		// 保存目标
		async saveTarget() {
			try {
				const res = await put('/user/target', {
					userId: this.userId,
					targetSchoolId: this.selectedSchool.id,
					targetSchool: this.selectedSchool.schoolName,
					targetMajorId: this.selectedMajor.id,
					targetMajor: this.selectedMajor.majorName
				});
				
				if (res.code === 200) {
					uni.showToast({
						title: '设置成功',
						icon: 'success'
					});
					
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				}
			} catch (error) {
				console.error('保存失败:', error);
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

/* 步骤指示器 */
.steps {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 32rpx 24rpx;
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	margin-bottom: 24rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

.step {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 8rpx;
}

.step-number {
	width: 56rpx;
	height: 56rpx;
	border-radius: 50%;
	background: #e8e8e8;
	color: #999;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	font-weight: bold;
	transition: all 0.3s ease;
}

.step.active .step-number {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	color: #ffffff;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.3);
}

.step.completed .step-number {
	background: linear-gradient(135deg, #52c990 0%, #00a870 100%);
	color: #ffffff;
}

.step-text {
	font-size: 22rpx;
	color: #999;
}

.step.active .step-text {
	color: #333;
	font-weight: 600;
}

.step-line {
	flex: 1;
	height: 3rpx;
	background: #e8e8e8;
	margin: 0 16rpx;
	transition: all 0.3s ease;
}

.step-line.active {
	background: linear-gradient(90deg, #378cff 0%, #1b7dff 100%);
}

/* 步骤内容 */
.step-content {
	background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
	border-radius: 16rpx;
	padding: 24rpx;
	min-height: 600rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 107, 255, 0.08);
	border: 1rpx solid rgba(0, 107, 255, 0.05);
}

/* 搜索框 */
.search-box {
	display: flex;
	align-items: center;
	background: linear-gradient(135deg, #f0f8ff 0%, #e8f3ff 100%);
	border-radius: 50rpx;
	padding: 0 24rpx;
	margin-bottom: 24rpx;
	border: 1rpx solid rgba(0, 107, 255, 0.1);
}

.search-icon {
	width: 28rpx;
	height: 28rpx;
	border: 2rpx solid #006cff;
	border-radius: 50%;
	position: relative;
	flex-shrink: 0;
	margin-right: 12rpx;
}

.search-icon::after {
	content: '';
	position: absolute;
	width: 2rpx;
	height: 10rpx;
	background: #006cff;
	transform: rotate(-45deg);
	bottom: -6rpx;
	right: -3rpx;
}

.search-input {
	flex: 1;
	height: 80rpx;
	font-size: 28rpx;
	color: #333;
}

/* 列表 */
.school-list, .major-list {
	height: 480rpx;
	margin-bottom: 24rpx;
}

.school-item, .major-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 28rpx 20rpx;
	border-bottom: 1rpx solid rgba(0, 107, 255, 0.05);
	transition: background 0.2s ease;
}

.school-item:active, .major-item:active {
	background: rgba(0, 107, 255, 0.03);
}

.item-content {
	flex: 1;
}

.school-name, .major-name {
	display: block;
	font-size: 30rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 8rpx;
}

.school-location, .major-code {
	font-size: 24rpx;
	color: #999;
}

.arrow-icon {
	font-size: 40rpx;
	color: #ccc;
	font-weight: bold;
}

/* 已选信息 */
.selected-info {
	display: flex;
	align-items: center;
	padding: 24rpx;
	background: linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%);
	border-radius: 12rpx;
	margin-bottom: 24rpx;
	border: 1rpx solid rgba(0, 107, 255, 0.15);
	gap: 16rpx;
}

.info-icon {
	width: 48rpx;
	height: 48rpx;
	border-radius: 12rpx;
	flex-shrink: 0;
	position: relative;
}

.school-info-icon {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}

.school-info-icon::before {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 18rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 10rpx;
	left: 12rpx;
	border-radius: 3rpx;
}

.school-info-icon::after {
	content: '';
	position: absolute;
	width: 30rpx;
	height: 3rpx;
	background: rgba(255, 255, 255, 0.9);
	bottom: 8rpx;
	left: 9rpx;
	border-radius: 2rpx;
}

.info-content {
	flex: 1;
}

.info-label {
	display: block;
	font-size: 22rpx;
	color: #666;
	margin-bottom: 6rpx;
}

.info-value {
	display: block;
	font-size: 28rpx;
	color: #006cff;
	font-weight: 600;
}

/* 确认卡片 */
.confirm-card {
	padding: 24rpx;
	background: linear-gradient(135deg, #f0f8ff 0%, #e8f3ff 100%);
	border-radius: 16rpx;
	margin-bottom: 32rpx;
	border: 1rpx solid rgba(0, 107, 255, 0.1);
}

.confirm-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid rgba(0, 107, 255, 0.05);
	gap: 16rpx;
}

.confirm-item:last-child {
	border-bottom: none;
}

.confirm-icon {
	width: 48rpx;
	height: 48rpx;
	border-radius: 12rpx;
	flex-shrink: 0;
	position: relative;
}

.school-confirm-icon {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}

.school-confirm-icon::before {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 18rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 10rpx;
	left: 12rpx;
	border-radius: 3rpx;
}

.school-confirm-icon::after {
	content: '';
	position: absolute;
	width: 30rpx;
	height: 3rpx;
	background: rgba(255, 255, 255, 0.9);
	bottom: 8rpx;
	left: 9rpx;
	border-radius: 2rpx;
}

.major-confirm-icon {
	background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}

.major-confirm-icon::before {
	content: '';
	position: absolute;
	width: 26rpx;
	height: 20rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 12rpx;
	left: 11rpx;
	border-radius: 3rpx 3rpx 4rpx 4rpx;
}

.major-confirm-icon::after {
	content: '';
	position: absolute;
	width: 12rpx;
	height: 6rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 6rpx;
	left: 18rpx;
	border-radius: 3rpx 3rpx 0 0;
}

.code-confirm-icon {
	background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
}

.code-confirm-icon::before {
	content: '';
	position: absolute;
	width: 24rpx;
	height: 24rpx;
	border: 3rpx solid rgba(255, 255, 255, 0.9);
	border-radius: 6rpx;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.code-confirm-icon::after {
	content: '';
	position: absolute;
	width: 12rpx;
	height: 2rpx;
	background: rgba(255, 255, 255, 0.9);
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.confirm-content {
	flex: 1;
}

.confirm-label {
	display: block;
	font-size: 22rpx;
	color: #666;
	margin-bottom: 8rpx;
}

.confirm-value {
	display: block;
	font-size: 30rpx;
	color: #333;
	font-weight: 600;
}

/* 按钮 */
.save-btn, .back-btn-secondary {
	width: 100%;
	height: 88rpx;
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: 600;
	border: none;
	margin-bottom: 16rpx;
}

.save-btn {
	background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
	color: #ffffff;
	box-shadow: 0 8rpx 20rpx rgba(0, 107, 255, 0.3);
}

.save-btn:active {
	opacity: 0.9;
	transform: scale(0.98);
}

.back-btn-secondary {
	background: linear-gradient(135deg, #f0f8ff 0%, #e8f3ff 100%);
	color: #006cff;
	border: 1rpx solid rgba(0, 107, 255, 0.2);
}

.back-btn-secondary:active {
	background: rgba(0, 107, 255, 0.05);
}

/* 空状态 */
.empty {
	text-align: center;
	padding: 100rpx 0;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>
