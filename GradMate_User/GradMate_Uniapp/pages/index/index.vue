<template>
	<view class="container">
		<view class="header">
			<text class="title">GradMate 考研助手</text>
			<text class="subtitle">您的考研路上的智能伙伴</text>
		</view>

		<!-- 快捷入口 -->
		<view class="quick-access">
			<view class="access-item" @click="goToSchools">
				<text class="access-icon">🏫</text>
				<text class="access-text">院校库</text>
			</view>
			<view class="access-item" @click="goToMajors">
				<text class="access-icon">📚</text>
				<text class="access-text">专业库</text>
			</view>
			<view class="access-item" @click="goToInfo">
				<text class="access-icon">📰</text>
				<text class="access-text">考研资讯</text>
			</view>
			<view class="access-item" @click="goToStudy">
				<text class="access-icon">📝</text>
				<text class="access-text">学习伴侣</text>
			</view>
		</view>
		
		<!-- AI智囊卡片 -->
		<view class="ai-card" @click="goToAi">
			<view class="ai-icon">🤖</view>
			<view class="ai-info">
				<text class="ai-title">AI智囊</text>
				<text class="ai-desc">考研问答 · 课业辅导 · 情绪疏导</text>
			</view>
			<text class="ai-arrow">→</text>
		</view>

		<!-- 资讯快览 -->
		<view class="info-preview">
			<view class="section-header">
				<text class="section-title">最新资讯</text>
				<text class="more-link" @click="goToInfo">查看更多 ></text>
			</view>
			
			<view v-if="latestInfo.length > 0" class="info-list">
				<view 
					v-for="item in latestInfo" 
					:key="item.id" 
					class="info-item"
					@click="openInfoLink(item)"
				>
					<view class="info-badge" :class="'badge-' + item.type">
						{{ getTypeName(item.type) }}
					</view>
					<text class="info-title">{{ item.title }}</text>
					<text class="info-date">{{ formatDate(item.publishDate) }}</text>
				</view>
			</view>
			
			<view v-else class="no-data">
				<text>暂无资讯</text>
			</view>
		</view>

		<!-- 功能介绍 -->
		<view class="features">
			<view class="section-title">核心功能</view>
			<view class="feature-list">
				<view class="feature-item">
					<text class="feature-icon">🔍</text>
					<text class="feature-name">智能检索</text>
					<text class="feature-desc">32万+专业数据，875所院校信息</text>
				</view>
				<view class="feature-item">
					<text class="feature-icon">📊</text>
					<text class="feature-name">数据分析</text>
					<text class="feature-desc">招生简章、调剂信息一网打尽</text>
				</view>
				<view class="feature-item">
					<text class="feature-icon">💡</text>
					<text class="feature-name">个性推荐</text>
					<text class="feature-desc">基于目标院校的智能推荐</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { get } from '@/utils/request.js';

export default {
	data() {
		return {
			latestInfo: []
		}
	},
	onShow() {
		this.loadLatestInfo();
	},
	methods: {
		// 加载最新资讯（混合三种类型）
		async loadLatestInfo() {
			try {
				// 并发请求三种资讯，各取2条
				const [brochureRes, adjustRes, infoRes] = await Promise.all([
					get('/info/brochure/list?pageNum=1&pageSize=2'),
					get('/info/adjust/list?pageNum=1&pageSize=2'),
					get('/info/information/list?pageNum=1&pageSize=2')
				]);
				
				const allInfo = [];
				
				if (brochureRes.code === 200) {
					allInfo.push(...brochureRes.data.records);
				}
				if (adjustRes.code === 200) {
					allInfo.push(...adjustRes.data.records);
				}
				if (infoRes.code === 200) {
					allInfo.push(...infoRes.data.records);
				}
				
				// 按发布日期排序，取前5条
				this.latestInfo = allInfo
					.sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate))
					.slice(0, 5);
					
			} catch (error) {
				console.error('加载资讯失败:', error);
			}
		},
		
		// 跳转到院校库
		goToSchools() {
			uni.switchTab({
				url: '/pages/school/list'
			});
		},
		
		// 跳转到专业库
		goToMajors() {
			uni.switchTab({
				url: '/pages/major/search'
			});
		},
		
		// 跳转到资讯页
		goToInfo() {
			uni.navigateTo({
				url: '/pages/info/index'
			});
		},
		
		// 跳转到学习伴侣
		goToStudy() {
			uni.navigateTo({
				url: '/pages/study/tasklist'
			});
		},
		
		// 跳转到AI智囊
		goToAi() {
			uni.navigateTo({
				url: '/pages/ai/chat'
			});
		},
		
		// 跳转到个人中心
		goToProfile() {
			uni.switchTab({
				url: '/pages/profile/profile'
			});
		},
		
		// 打开资讯链接
		openInfoLink(item) {
			if (item.url) {
				// #ifdef H5
				window.open(item.url, '_blank');
				// #endif
				
				// #ifndef H5
				plus.runtime.openURL(item.url);
				// #endif
			}
		},
		
		// 获取类型名称
		getTypeName(type) {
			const typeMap = {
				'brochure': '简章',
				'adjust': '调剂',
				'information': '通知'
			};
			return typeMap[type] || '资讯';
		},
		
		// 格式化日期
		formatDate(dateStr) {
			if (!dateStr) return '';
			
			const date = new Date(dateStr);
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			
			return `${month}-${day}`;
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #4A90E2 0%, #F5F5F5 30%);
	padding: 40rpx 30rpx;
}

.header {
	text-align: center;
	margin-bottom: 60rpx;
	padding-top: 40rpx;
}

.title {
	display: block;
	font-size: 52rpx;
	font-weight: bold;
	color: #FFFFFF;
	margin-bottom: 15rpx;
}

.subtitle {
	display: block;
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.9);
}

/* 快捷入口 */
.quick-access {
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	gap: 20rpx;
	margin-bottom: 40rpx;
}

.access-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background-color: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.access-icon {
	font-size: 48rpx;
	margin-bottom: 15rpx;
}

.access-text {
	font-size: 24rpx;
	color: #333;
}

/* AI智囊卡片 */
.ai-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 20rpx;
	padding: 40rpx;
	margin-bottom: 30rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);
}

.ai-icon {
	font-size: 60rpx;
	margin-right: 30rpx;
}

.ai-info {
	flex: 1;
}

.ai-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #fff;
	display: block;
	margin-bottom: 10rpx;
}

.ai-desc {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

.ai-arrow {
	font-size: 40rpx;
	color: #fff;
}

/* 资讯快览 */
.info-preview {
	background-color: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 25rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.more-link {
	font-size: 24rpx;
	color: #4A90E2;
}

.info-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.info-item {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background-color: #F8F9FA;
	border-radius: 15rpx;
	gap: 15rpx;
}

.info-badge {
	padding: 6rpx 15rpx;
	border-radius: 20rpx;
	font-size: 20rpx;
	color: #FFFFFF;
	flex-shrink: 0;
}

.badge-brochure {
	background-color: #4A90E2;
}

.badge-adjust {
	background-color: #FF6B6B;
}

.badge-information {
	background-color: #4ECDC4;
}

.info-title {
	flex: 1;
	font-size: 26rpx;
	color: #333;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.info-date {
	font-size: 22rpx;
	color: #999;
	flex-shrink: 0;
}

.no-data {
	text-align: center;
	padding: 60rpx;
	color: #999;
	font-size: 26rpx;
}

/* 功能介绍 */
.features {
	background-color: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.feature-list {
	display: flex;
	flex-direction: column;
	gap: 25rpx;
	margin-top: 25rpx;
}

.feature-item {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.feature-icon {
	font-size: 40rpx;
	width: 80rpx;
	text-align: center;
}

.feature-name {
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
	width: 120rpx;
}

.feature-desc {
	flex: 1;
	font-size: 24rpx;
	color: #666;
	line-height: 1.5;
}
</style>
