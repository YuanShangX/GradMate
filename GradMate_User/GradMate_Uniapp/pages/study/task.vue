<template>
	<view class="container">
		<!-- 顶部倒计时 -->
		<Countdown size="normal" />
		
		<!-- 日期信息 -->
		<view class="date-info">
			<text class="date-text">{{ currentDate }}</text>
			<text class="weekday-text">{{ currentWeekday }}</text>
		</view>
		
		<!-- 任务进度 -->
		<view class="progress-card">
			<text class="progress-text">今日任务完成情况</text>
			<view class="progress-bar">
				<view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
			</view>
			<text class="progress-label">{{ completedCount }}/{{ totalCount }} 已完成</text>
		</view>
		
		<!-- 任务列表 -->
		<view class="task-list">
			<view 
				v-for="(task, index) in tasks" 
				:key="task.id"
				class="task-card"
				:style="{ background: getGradient(index) }"
				@longpress="showTaskMenu(task)"
			>
				<view class="task-content">
					<view class="task-header">
						<text class="task-title">{{ task.taskContent }}</text>
						<view class="priority-tag" :class="'priority-' + task.priority">
							{{ getPriorityText(task.priority) }}
						</view>
					</view>
					<text class="task-duration">{{ task.estimatedDuration || 25 }}分钟</text>
				</view>
				<view class="task-action">
					<button 
						class="start-btn" 
						@click="startFocus(task)"
						:disabled="task.isCompleted === 1"
					>
						{{ task.isCompleted === 1 ? '已完成' : '开始' }}
					</button>
				</view>
			</view>
		</view>
		
		<!-- 添加任务按钮 -->
		<view class="add-btn" @click="showAddModal">
			<text class="add-icon">+</text>
		</view>
		
		<!-- 任务菜单 -->
		<view class="action-sheet" v-if="showMenu" @click="hideTaskMenu">
			<view class="action-sheet-content" @click.stop>
				<view class="action-item" @click="editTask">编辑</view>
				<view class="action-item delete" @click="deleteTask">删除</view>
				<view class="action-item cancel" @click="hideTaskMenu">取消</view>
			</view>
		</view>
	</view>
</template>

<script>
import Countdown from '@/components/Countdown.vue';
import { get, post, del, put } from '@/utils/request.js';

export default {
	components: {
		Countdown
	},
	
	data() {
		return {
			userId: 1,
			tasks: [],
			showMenu: false,
			selectedTask: null,
			
			// 渐变色方案
			gradients: [
				'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
				'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
				'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
				'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
				'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
				'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
				'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
				'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)'
			]
		};
	},
	
	computed: {
		currentDate() {
			const date = new Date();
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
		},
		
		currentWeekday() {
			const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
			return weekdays[new Date().getDay()];
		},
		
		completedCount() {
			return this.tasks.filter(t => t.isCompleted === 1).length;
		},
		
		totalCount() {
			return this.tasks.length;
		},
		
		progressPercent() {
			if (this.totalCount === 0) return 0;
			return Math.round((this.completedCount / this.totalCount) * 100);
		}
	},
	
	onShow() {
		this.userId = uni.getStorageSync('userId') || 1;
		this.loadTasks();
	},
	
	methods: {
		async loadTasks() {
			try {
				const res = await get(`/study/task/today?userId=${this.userId}`);
				if (res.code === 200) {
					this.tasks = res.data || [];
				}
			} catch (error) {
				console.error('加载任务失败:', error);
			}
		},
		
		getGradient(index) {
			return this.gradients[index % this.gradients.length];
		},
		
		getPriorityText(priority) {
			const map = { 1: '低', 2: '中', 3: '高' };
			return map[priority] || '中';
		},
		
		startFocus(task) {
			if (task.isCompleted === 1) {
				return;
			}
			
			uni.navigateTo({
				url: `/pages/study/focus?taskId=${task.id}&taskName=${task.taskContent}&duration=${task.estimatedDuration || 25}`
			});
		},
		
		showAddModal() {
			// TODO: 实现新建任务弹窗
		},
		
		showTaskMenu(task) {
			this.selectedTask = task;
			this.showMenu = true;
		},
		
		hideTaskMenu() {
			this.showMenu = false;
			this.selectedTask = null;
		},
		
		editTask() {
			// TODO: 实现编辑任务弹窗
			this.hideTaskMenu();
		},
		
		async deleteTask() {
			if (!this.selectedTask) return;
			
			try {
				const res = await del(`/study/task/${this.selectedTask.id}`);
				if (res.code === 200) {
					uni.showToast({
						title: '删除成功',
						icon: 'success'
					});
					this.hideTaskMenu();
					this.loadTasks();
				}
			} catch (error) {
				console.error('删除任务失败:', error);
			}
		}
	}
};
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #f5f7fa;
	padding: 20rpx 30rpx 120rpx;
}

.date-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx 0;
}

.date-text {
	font-size: 32rpx;
	font-weight: 500;
	color: #333;
}

.weekday-text {
	font-size: 28rpx;
	color: #666;
}

.progress-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.progress-text {
	font-size: 28rpx;
	color: #666;
	display: block;
	margin-bottom: 20rpx;
}

.progress-bar {
	height: 12rpx;
	background: #e5e5e5;
	border-radius: 6rpx;
	overflow: hidden;
	margin-bottom: 15rpx;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
	transition: width 0.3s;
}

.progress-label {
	font-size: 24rpx;
	color: #999;
}

.task-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.task-card {
	border-radius: 20rpx;
	padding: 40rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
	color: #fff;
}

.task-content {
	flex: 1;
}

.task-header {
	display: flex;
	align-items: center;
	gap: 15rpx;
	margin-bottom: 15rpx;
}

.task-title {
	font-size: 32rpx;
	font-weight: 500;
}

.priority-tag {
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
	font-size: 22rpx;
	background: rgba(255, 255, 255, 0.3);
}

.task-duration {
	font-size: 26rpx;
	opacity: 0.8;
}

.task-action {
	margin-left: 20rpx;
}

.start-btn {
	background: rgba(255, 255, 255, 0.9);
	color: #333;
	border: none;
	border-radius: 50rpx;
	padding: 15rpx 40rpx;
	font-size: 28rpx;
	font-weight: 500;
}

.start-btn[disabled] {
	opacity: 0.5;
}

.add-btn {
	position: fixed;
	right: 40rpx;
	bottom: 150rpx;
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.4);
}

.add-icon {
	font-size: 60rpx;
	color: #fff;
	line-height: 1;
}

/* 操作菜单 */
.action-sheet {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: flex-end;
	z-index: 1000;
}

.action-sheet-content {
	width: 100%;
	background: #fff;
	border-radius: 20rpx 20rpx 0 0;
}

.action-item {
	padding: 35rpx;
	text-align: center;
	font-size: 32rpx;
	border-bottom: 1rpx solid #eee;
}

.action-item.delete {
	color: #ff3b30;
}

.action-item.cancel {
	border-top: 10rpx solid #f5f5f5;
	border-bottom: none;
	color: #999;
}
</style>
