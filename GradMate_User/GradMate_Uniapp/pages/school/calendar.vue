<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<text class="nav-title">考研日历</text>
		</view>
		
		<!-- 标签页 -->
		<view class="tabs">
			<view 
				v-for="(tab, index) in tabs" 
				:key="index"
				class="tab-item"
				:class="{ active: currentTab === index }"
				@click="switchTab(index)"
			>
				<text class="tab-text">{{ tab }}</text>
				<view v-if="currentTab === index" class="tab-line"></view>
			</view>
		</view>
		
		<!-- 内容区 -->
		<scroll-view class="content" scroll-y="true">
			<!-- 时间线 -->
			<view v-if="currentTab === 0" class="timeline-content">
				<view v-for="(year, yearIndex) in timelineData" :key="yearIndex" class="year-section">
					<view class="year-title">
						<view class="year-dot"></view>
						<text class="year-text">{{ year.year }}</text>
					</view>
					
					<view v-for="(item, itemIndex) in year.items" :key="itemIndex" class="timeline-item">
						<view class="item-left">
							<view class="item-dot"></view>
							<view v-if="itemIndex < year.items.length - 1" class="item-line"></view>
						</view>
						
						<view class="item-content">
							<view class="item-header">
								<text class="item-title">{{ item.title }}</text>
								<text v-if="item.link" class="item-link-text">{{ item.link }}</text>
							</view>
							<text class="item-desc">{{ item.desc }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 考试科目 -->
			<view v-if="currentTab === 1" class="subject-content">
				<view class="year-title">
					<view class="year-dot"></view>
					<text class="year-text">2026届全国硕士研究生招生考试</text>
				</view>
				
				<view v-for="(subject, index) in subjects" :key="index" class="subject-item">
					<view class="subject-left">
						<view class="subject-dot"></view>
					</view>
					<view class="subject-info">
						<text class="subject-name">{{ subject.name }}</text>
						<text class="subject-date">{{ subject.date }}</text>
					</view>
					<view class="subject-right">
						<text class="subject-time">{{ subject.time }}</text>
						<text class="subject-label">考试时间</text>
					</view>
				</view>
			</view>
			
			<!-- 调剂时间线 -->
			<view v-if="currentTab === 2" class="adjust-content">
				<view class="adjust-notice">
					<text class="notice-icon">⏰</text>
					<text class="notice-text">具体时间以学校公布为准，以下为往年通用安排</text>
				</view>
				
				<view class="year-title">
					<view class="year-dot"></view>
					<text class="year-text">2025届考研调剂</text>
				</view>
				
				<view v-for="(item, index) in adjustData" :key="index" class="timeline-item">
					<view class="item-left">
						<view class="item-dot"></view>
						<view v-if="index < adjustData.length - 1" class="item-line"></view>
					</view>
					
					<view class="item-content">
						<view class="item-header">
							<text class="item-title">{{ item.title }}</text>
							<text v-if="item.link" class="item-link-text">{{ item.link }}</text>
						</view>
						<text class="item-desc">{{ item.desc }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'

const currentTab = ref(0)
const tabs = ['时间线', '考试科目', '调剂时间线']

const timelineData = ref([
	{
		year: '2025年',
		items: [
			{ title: '2026届考研初试择校', desc: '建议7月之前完成', link: '智能择校' },
			{ title: '2026届考研大纲发布', desc: '9月中旬', link: '大纲解读' },
			{ title: '2026届院校招生简章发布', desc: '9-10月' },
			{ title: '2026届考研预报名', desc: '9月24-27日' },
			{ title: '2026届考研正式报名', desc: '10月5-25日' },
			{ title: '2026届考研网上确认', desc: '11月初' },
			{ title: '2026届考研准考证打印', desc: '12月中旬' },
			{ title: '2026届考研初试', desc: '12月20-21日' }
		]
	},
	{
		year: '2026年',
		items: [
			{ title: '2026届考研初试成绩查询', desc: '2月中旬' },
			{ title: '2026届国家线公布', desc: '3月中旬' },
			{ title: '2026届考研复试', desc: '3-4月' },
			{ title: '2026届考研调剂', desc: '3-4月', link: '调剂常识' },
			{ title: '2026届拟录取公示', desc: '5月' },
			{ title: '2026届录取通知书发放', desc: '6-7月' }
		]
	}
])

const subjects = ref([
	{ name: '政治/管综', date: '12月20日', time: '8:30—11:30' },
	{ name: '外国语', date: '12月20日', time: '14:00—17:00' },
	{ name: '业务课一/专业基础综合', date: '12月21日', time: '8:30—11:30' },
	{ name: '业务课二', date: '12月21日', time: '14:00—17:00' }
])

const adjustData = ref([
	{ title: '2025届考研初试估分', desc: '初试结束后即可进行' },
	{ title: '2025届考研初试成绩查询', desc: '2月24日' },
	{ title: '2025届国家线公布', desc: '2月24日' },
	{ title: '2025届部分院校预调剂', desc: '预计3月开始，请关注院校招生网站', link: '调剂常识' },
	{ title: '2025届调剂意向采集系统开放', desc: '3月28日', link: '历年调剂信息' },
	{ title: '2025届正式调剂系统开放', desc: '4月8号' }
])

const switchTab = (index) => {
	currentTab.value = index
}

const goBack = () => {
	uni.navigateBack()
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f6f7fb;
}

/* 导航栏 */
.navbar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	display: flex;
	align-items: center;
	position: relative;
}

.nav-back {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	font-size: 48rpx;
	color: #333;
	font-weight: bold;
}

.nav-title {
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
	font-size: 32rpx;
	font-weight: 600;
	color: #006bff;
}

/* 标签页 */
.tabs {
	background: #ffffff;
	display: flex;
	padding: 0 30rpx;
}

.tab-item {
	flex: 1;
	padding: 24rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	position: relative;
}

.tab-text {
	font-size: 28rpx;
	color: #666;
}

.tab-item.active .tab-text {
	color: #006bff;
	font-weight: 600;
}

.tab-line {
	position: absolute;
	bottom: 0;
	width: 60rpx;
	height: 4rpx;
	background: #006bff;
	border-radius: 2rpx;
}

/* 内容区 */
.content {
	height: calc(100vh - 160rpx);
	padding: 24rpx 30rpx;
	box-sizing: border-box;
}

/* 时间线内容 */
.timeline-content {
	padding-top: 20rpx;
}

/* 年份标题 */
.year-section {
	margin-bottom: 40rpx;
}

.year-title {
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 24rpx;
}

.year-dot {
	width: 16rpx;
	height: 16rpx;
	background: #ff9500;
	border-radius: 50%;
}

.year-text {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

/* 时间线项 */
.timeline-item {
	display: flex;
	margin-bottom: 32rpx;
	gap: 12rpx;
}

.item-left {
	width: 40rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	flex-shrink: 0;
}

.item-dot {
	width: 16rpx;
	height: 16rpx;
	background: #e5e5e5;
	border-radius: 50%;
	margin-top: 8rpx;
}

.item-line {
	width: 2rpx;
	flex: 1;
	background: #e5e5e5;
	margin-top: 8rpx;
}

.item-content {
	flex: 1;
	background: #ffffff;
	border-radius: 12rpx;
	padding: 20rpx;
	min-width: 0;
	box-sizing: border-box;
}

.item-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 8rpx;
}

.item-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.item-link-text {
	font-size: 24rpx;
	color: #999;
}

.item-desc {
	font-size: 24rpx;
	color: #999;
}

/* 考试科目 */
.subject-content {
	padding-top: 20rpx;
}

.subject-item {
	display: flex;
	align-items: center;
	background: #ffffff;
	border-radius: 12rpx;
	padding: 24rpx 20rpx;
	margin-bottom: 16rpx;
}

.subject-left {
	width: 40rpx;
	display: flex;
	justify-content: center;
}

.subject-dot {
	width: 12rpx;
	height: 12rpx;
	background: #e5e5e5;
	border-radius: 50%;
}

.subject-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.subject-name {
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
}

.subject-date {
	font-size: 24rpx;
	color: #999;
}

.subject-right {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	gap: 8rpx;
}

.subject-time {
	font-size: 26rpx;
	color: #333;
}

.subject-label {
	font-size: 22rpx;
	color: #999;
}

/* 调剂内容 */
.adjust-content {
	padding-top: 20rpx;
}

.adjust-notice {
	background: #fff5e6;
	border-radius: 12rpx;
	padding: 16rpx 20rpx;
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 24rpx;
}

.notice-icon {
	font-size: 32rpx;
}

.notice-text {
	flex: 1;
	font-size: 24rpx;
	color: #ff9500;
}
</style>
