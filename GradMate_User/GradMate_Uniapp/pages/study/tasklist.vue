<template>
	<view class="page">
		<!-- 导航栏 -->
		<view class="navbar">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">‹</text>
			</view>
			<view class="search-container">
				<view class="search-icon"></view>
				<input 
					class="search-input" 
					v-model="searchKeyword" 
					placeholder="搜索任务内容"
					placeholder-style="color: #999; font-size: 28rpx;"
					@input="onSearch"
				/>
			</view>
		</view>
		
		<!-- 快捷操作栏 -->
		<view class="quick-actions">
			<view class="action-card" @click="goToCalendar">
				<view class="action-icon-wrapper">
					<view class="calendar-icon"></view>
				</view>
				<text class="action-label">打卡日历</text>
			</view>
			<view class="action-card" @click="goToStats">
				<view class="action-icon-wrapper">
					<view class="stats-icon"></view>
				</view>
				<text class="action-label">学习统计</text>
			</view>
			<view class="action-card" @click="goToFocus">
				<view class="action-icon-wrapper">
					<view class="focus-icon"></view>
				</view>
				<text class="action-label">专注计时</text>
			</view>
		</view>
		
		<!-- 筛选栏 -->
		<view class="filter-bar">
			<view class="filter-item" @click="showStatusPicker = true">
				<text class="filter-text">{{ selectedStatusLabel || '任务状态' }}</text>
				<text class="filter-arrow">▼</text>
			</view>
			<view class="filter-item" @click="showPriorityPicker = true">
				<text class="filter-text">{{ selectedPriorityLabel || '优先级' }}</text>
				<text class="filter-arrow">▼</text>
			</view>
			<view class="filter-item" @click="showTagPicker = true">
				<text class="filter-text">{{ selectedTagLabel || '标签' }}</text>
				<text class="filter-arrow">▼</text>
			</view>
		</view>
		
		<!-- 任务列表 -->
		<scroll-view class="task-list" scroll-y="true" @scrolltolower="loadMore">
			<view 
				v-for="task in filteredTasks" 
				:key="task.id"
				class="task-card"
			>
				<view class="task-header">
					<view class="task-checkbox" @click="toggleComplete(task)">
						<view v-if="task.isCompleted === 1" class="checkbox-checked"></view>
					</view>
					<view class="task-info">
						<view class="task-title-row">
							<text class="task-title" :class="{ completed: task.isCompleted === 1 }">
								{{ task.taskContent }}
							</text>
						</view>
						<text v-if="task.description" class="task-desc">{{ task.description }}</text>
						
						<!-- 标签 -->
						<view v-if="task.tags" class="task-tags">
							<view 
								v-for="(tag, index) in task.tags.split(',')" 
								:key="index"
								class="tag"
							>
								{{ tag }}
							</view>
						</view>
					</view>
				</view>
				
				<view class="divider"></view>
				
				<view class="task-footer">
					<view class="task-meta">
						<view class="meta-item">
							<view class="time-icon"></view>
							<text class="meta-text">{{ task.estimatedDuration || 25 }}分钟</text>
						</view>
						<text class="meta-dot">·</text>
						<view class="meta-item">
							<text class="priority-label" :class="'priority-' + task.priority">
								{{ getPriorityText(task.priority) }}
							</text>
						</view>
					</view>
					<view class="task-actions">
						<view class="action-btn" @click="editTask(task)">
							<text class="action-text">编辑</text>
						</view>
						<view class="action-btn action-btn-primary" @click="startTask(task)">
							<text class="action-text">{{ task.isCompleted === 1 ? '已完成' : '开始' }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view v-if="filteredTasks.length === 0" class="empty-text">暂无任务数据</view>
			<view v-if="!loading && filteredTasks.length > 0" class="nomore-text">没有更多了</view>
		</scroll-view>
		
		<!-- 添加任务按钮 -->
		<view class="add-btn" @click="showAddModal">
			<text class="add-icon">+</text>
		</view>
		
		<!-- 状态选择器 -->
		<view v-if="showStatusPicker" class="picker-mask" @click="showStatusPicker = false">
			<view class="picker-content" @click.stop>
				<view class="picker-header">
					<text class="picker-title">任务状态</text>
					<text class="picker-close" @click="showStatusPicker = false">×</text>
				</view>
				
				<scroll-view class="picker-list" scroll-y="true">
					<view class="picker-grid">
						<view 
							v-for="(item, index) in statusList" 
							:key="index" 
							class="picker-item"
							@click="selectStatus(item.value)"
						>
							<text class="picker-text">{{ item.label }}</text>
						</view>
					</view>
				</scroll-view>
				
				<view class="picker-footer">
					<view class="footer-btn footer-btn-reset" @click="selectStatus('')">
						<text class="footer-btn-text">重置</text>
					</view>
					<view class="footer-btn footer-btn-confirm" @click="showStatusPicker = false">
						<text class="footer-btn-text">确定</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 优先级选择器 -->
		<view v-if="showPriorityPicker" class="picker-mask" @click="showPriorityPicker = false">
			<view class="picker-content" @click.stop>
				<view class="picker-header">
					<text class="picker-title">优先级</text>
					<text class="picker-close" @click="showPriorityPicker = false">×</text>
				</view>
				
				<scroll-view class="picker-list" scroll-y="true">
					<view class="picker-grid">
						<view 
							v-for="(item, index) in priorityList" 
							:key="index" 
							class="picker-item"
							@click="selectPriority(item.value)"
						>
							<text class="picker-text">{{ item.label }}</text>
						</view>
					</view>
				</scroll-view>
				
				<view class="picker-footer">
					<view class="footer-btn footer-btn-reset" @click="selectPriority('')">
						<text class="footer-btn-text">重置</text>
					</view>
					<view class="footer-btn footer-btn-confirm" @click="showPriorityPicker = false">
						<text class="footer-btn-text">确定</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 标签选择器 -->
		<view v-if="showTagPicker" class="picker-mask" @click="showTagPicker = false">
			<view class="picker-content" @click.stop>
				<view class="picker-header">
					<text class="picker-title">标签</text>
					<text class="picker-close" @click="showTagPicker = false">×</text>
				</view>
				
				<scroll-view class="picker-list" scroll-y="true">
					<view class="picker-grid">
						<view 
							v-for="tag in userTags" 
							:key="tag.id"
							class="picker-item"
							@click="selectTag(tag.tagName)"
						>
							<text class="picker-text">{{ tag.tagName }}</text>
						</view>
					</view>
				</scroll-view>
				
				<view class="picker-footer">
					<view class="footer-btn footer-btn-reset" @click="selectTag('')">
						<text class="footer-btn-text">重置</text>
					</view>
					<view class="footer-btn footer-btn-confirm" @click="showTagPicker = false">
						<text class="footer-btn-text">确定</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 添加/编辑任务弹窗 -->
		<view v-if="showAdd" class="modal" catchtouchmove="true">
			<view class="modal-mask" @tap="hideAddModal"></view>
			<view class="modal-box">
				<view class="modal-header">
					<text class="modal-title">{{ editingTask ? '编辑任务' : '新建任务' }}</text>
					<text class="modal-close" @tap="hideAddModal">×</text>
				</view>
				
				<view class="modal-body">
					<input 
						class="task-input-field" 
						v-model="newTask.taskContent" 
						placeholder="请输入任务内容"
						placeholder-style="color: #999"
						maxlength="100"
						confirm-type="done"
					/>
					
					<textarea 
						class="task-textarea-field" 
						v-model="newTask.description" 
						placeholder="请输入任务描述（选填）"
						placeholder-style="color: #999"
						maxlength="200"
						auto-height
					/>
					
					<view class="modal-row">
						<text class="modal-label">预计时长</text>
						<picker mode="selector" :range="durations" @change="onDurationChange">
							<view class="picker-trigger">
								<text class="modal-value">{{ newTask.estimatedDuration }}分钟</text>
								<text class="picker-arrow">›</text>
							</view>
						</picker>
					</view>
					
					<view class="modal-row">
						<text class="modal-label">优先级</text>
						<picker mode="selector" :range="['低', '中', '高']" @change="onPriorityChange">
							<view class="picker-trigger">
								<text class="modal-value">{{ getPriorityText(newTask.priority) }}</text>
								<text class="picker-arrow">›</text>
							</view>
						</picker>
					</view>
					
					<button class="modal-submit" @tap="saveTask">确定</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { get, post, put, del } from '@/utils/request.js'

const userId = ref(1)
const tasks = ref([])
const userTags = ref([])
const filteredTasks = ref([])
const searchKeyword = ref('')
const selectedStatus = ref('')
const selectedPriority = ref('')
const selectedTag = ref('')

const showStatusPicker = ref(false)
const showPriorityPicker = ref(false)
const showTagPicker = ref(false)
const showAdd = ref(false)
const loading = ref(false)

const editingTask = ref(null)
const newTask = ref({
	taskContent: '',
	description: '',
	estimatedDuration: 25,
	priority: 2,
	tags: ''
})

const statusList = ref([
	{ label: '全部', value: '' },
	{ label: '待完成', value: 0 },
	{ label: '已完成', value: 1 }
])

const priorityList = ref([
	{ label: '全部', value: '' },
	{ label: '低', value: 1 },
	{ label: '中', value: 2 },
	{ label: '高', value: 3 }
])

const durations = [10, 15, 25, 30, 45, 60, 90, 120]

// 计算选中标签的显示文本
const selectedStatusLabel = computed(() => {
	if (selectedStatus.value === '') return ''
	const status = statusList.value.find(item => item.value === selectedStatus.value)
	return status ? status.label : ''
})

const selectedPriorityLabel = computed(() => {
	if (selectedPriority.value === '') return ''
	const priority = priorityList.value.find(item => item.value === selectedPriority.value)
	return priority ? priority.label : ''
})

const selectedTagLabel = computed(() => {
	return selectedTag.value || ''
})

onMounted(() => {
	userId.value = uni.getStorageSync('userId') || 1
	loadTasks()
	loadTags()
})

const loadTasks = async () => {
	loading.value = true
	try {
		const res = await get(`/study/task/today?userId=${userId.value}`)
		if (res.code === 200) {
			tasks.value = res.data || []
			applyFilters()
		}
	} catch (error) {
		console.error('加载任务失败:', error)
	} finally {
		loading.value = false
	}
}

const loadTags = async () => {
	try {
		const res = await get(`/study/tags?userId=${userId.value}`)
		if (res.code === 200) {
			userTags.value = res.data || []
		}
	} catch (error) {
		console.error('加载标签失败:', error)
	}
}

const applyFilters = () => {
	let result = [...tasks.value]
	
	// 搜索过滤
	if (searchKeyword.value) {
		const keyword = searchKeyword.value.toLowerCase()
		result = result.filter(task => 
			task.taskContent.toLowerCase().includes(keyword) ||
			(task.description && task.description.toLowerCase().includes(keyword))
		)
	}
	
	// 状态过滤
	if (selectedStatus.value !== '') {
		result = result.filter(task => task.isCompleted === selectedStatus.value)
	}
	
	// 优先级过滤
	if (selectedPriority.value !== '') {
		result = result.filter(task => task.priority === selectedPriority.value)
	}
	
	// 标签过滤
	if (selectedTag.value) {
		result = result.filter(task => 
			task.tags && task.tags.includes(selectedTag.value)
		)
	}
	
	filteredTasks.value = result
}

const onSearch = () => {
	applyFilters()
}

const selectStatus = (status) => {
	selectedStatus.value = status
	showStatusPicker.value = false
	applyFilters()
}

const selectPriority = (priority) => {
	selectedPriority.value = priority
	showPriorityPicker.value = false
	applyFilters()
}

const selectTag = (tag) => {
	selectedTag.value = tag
	showTagPicker.value = false
	applyFilters()
}

const toggleComplete = async (task) => {
	try {
		const res = await put(`/study/task/${task.id}/complete`)
		if (res.code === 200) {
			task.isCompleted = task.isCompleted === 1 ? 0 : 1
			applyFilters()
		}
	} catch (error) {
		console.error('切换任务状态失败:', error)
	}
}

const startTask = (task) => {
	if (task.isCompleted === 1) return
	
	uni.navigateTo({
		url: `/pages/study/focus?taskId=${task.id}&taskName=${task.taskContent}&duration=${task.estimatedDuration || 25}`
	})
}

const showAddModal = () => {
	editingTask.value = null
	newTask.value = {
		taskContent: '',
		description: '',
		estimatedDuration: 25,
		priority: 2,
		tags: ''
	}
	showAdd.value = true
}

const hideAddModal = () => {
	showAdd.value = false
}

const editTask = (task) => {
	editingTask.value = task
	newTask.value = {
		taskContent: task.taskContent,
		description: task.description || '',
		estimatedDuration: task.estimatedDuration || 25,
		priority: task.priority || 2,
		tags: task.tags || ''
	}
	showAdd.value = true
}

const deleteTask = async (task) => {
	uni.showModal({
		title: '确认删除',
		content: '确定要删除这个任务吗？',
		success: async (res) => {
			if (res.confirm) {
				try {
					const result = await del(`/study/task/${task.id}`)
					if (result.code === 200) {
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						})
						loadTasks()
					}
				} catch (error) {
					console.error('删除任务失败:', error)
				}
			}
		}
	})
}

const saveTask = async () => {
	if (!newTask.value.taskContent.trim()) {
		uni.showToast({
			title: '请输入任务内容',
			icon: 'none'
		})
		return
	}
	
	try {
		const date = new Date()
		const taskDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
		
		const taskData = {
			userId: userId.value,
			taskContent: newTask.value.taskContent,
			description: newTask.value.description,
			taskDate: taskDate,
			priority: newTask.value.priority,
			estimatedDuration: newTask.value.estimatedDuration,
			tags: newTask.value.tags
		}
		
		if (editingTask.value) {
			const res = await put(`/study/task/${editingTask.value.id}`, taskData)
			if (res.code === 200) {
				uni.showToast({
					title: '更新成功',
					icon: 'success'
				})
			}
		} else {
			const res = await post('/study/task', taskData)
			if (res.code === 200) {
				uni.showToast({
					title: '添加成功',
					icon: 'success'
				})
			}
		}
		
		hideAddModal()
		loadTasks()
	} catch (error) {
		console.error('保存任务失败:', error)
	}
}

const onDurationChange = (e) => {
	newTask.value.estimatedDuration = durations[e.detail.value]
}

const onPriorityChange = (e) => {
	newTask.value.priority = [1, 2, 3][e.detail.value]
}

const getPriorityText = (priority) => {
	const map = { 1: '低', 2: '中', 3: '高' }
	return map[priority] || '中'
}

const loadMore = () => {
	// 可以实现分页加载
}

const goBack = () => {
	uni.navigateBack()
}

const goToCalendar = () => {
	uni.navigateTo({
		url: '/pages/study/calendar'
	})
}

const goToStats = () => {
	uni.navigateTo({
		url: '/pages/study/stats'
	})
}

const goToFocus = () => {
	uni.navigateTo({
		url: '/pages/study/focus'
	})
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f0f3fa;
}

/* 导航栏 */
.navbar {
	background: #ffffff;
	padding: 20rpx 30rpx;
	padding-top: calc(20rpx + env(safe-area-inset-top));
	display: flex;
	align-items: center;
	gap: 20rpx;
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

.search-container {
	flex: 1;
	display: flex;
	align-items: center;
	background: #f5f5f5;
	border-radius: 50rpx;
	padding: 14rpx 28rpx;
	gap: 12rpx;
}

.search-icon {
	width: 28rpx;
	height: 28rpx;
	border: 2rpx solid #999;
	border-radius: 50%;
	position: relative;
	flex-shrink: 0;
}

.search-icon::after {
	content: '';
	position: absolute;
	width: 2rpx;
	height: 10rpx;
	background: #999;
	transform: rotate(-45deg);
	bottom: -6rpx;
	right: -3rpx;
}

.search-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

/* 快捷操作栏 */
.quick-actions {
	background: #ffffff;
	padding: 24rpx 30rpx;
	display: flex;
	gap: 20rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.action-card {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12rpx;
	padding: 20rpx 0;
	background: #f8f9fa;
	border-radius: 12rpx;
}

.action-icon-wrapper {
	width: 56rpx;
	height: 56rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 日历图标 */
.calendar-icon {
	width: 48rpx;
	height: 48rpx;
	border: 2rpx solid #5b8ff9;
	border-radius: 8rpx;
	position: relative;
	box-sizing: border-box;
}

.calendar-icon::before {
	content: '';
	position: absolute;
	top: -6rpx;
	left: 8rpx;
	width: 4rpx;
	height: 10rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

.calendar-icon::after {
	content: '';
	position: absolute;
	top: -6rpx;
	right: 8rpx;
	width: 4rpx;
	height: 10rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

/* 统计图标 */
.stats-icon {
	width: 48rpx;
	height: 48rpx;
	position: relative;
}

.stats-icon::before {
	content: '';
	position: absolute;
	bottom: 0;
	left: 4rpx;
	width: 10rpx;
	height: 24rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

.stats-icon::after {
	content: '';
	position: absolute;
	bottom: 0;
	right: 4rpx;
	width: 10rpx;
	height: 40rpx;
	background: #5b8ff9;
	border-radius: 2rpx;
}

/* 专注图标 */
.focus-icon {
	width: 48rpx;
	height: 48rpx;
	border: 3rpx solid #5b8ff9;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.focus-icon::before {
	content: '';
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 0;
	height: 0;
	border-left: 12rpx solid #5b8ff9;
	border-top: 8rpx solid transparent;
	border-bottom: 8rpx solid transparent;
	margin-left: 2rpx;
}

.action-label {
	font-size: 24rpx;
	color: #666;
}

/* 筛选栏 */
.filter-bar {
	background: #ffffff;
	padding: 24rpx 30rpx;
	display: flex;
	gap: 16rpx;
}

.filter-item {
	display: flex;
	align-items: center;
	gap: 6rpx;
	padding: 10rpx 20rpx;
	background: #f8f9fa;
	border-radius: 8rpx;
}

.filter-text {
	font-size: 26rpx;
	color: #666;
}

.filter-arrow {
	font-size: 18rpx;
	color: #999;
}

/* 任务列表 */
.task-list {
	height: calc(100vh - 340rpx);
	padding: 20rpx 30rpx;
	padding-bottom: 40rpx;
	box-sizing: border-box;
}

.task-card {
	background: #ffffff;
	border-radius: 16rpx;
	padding: 32rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
	box-sizing: border-box;
}

.task-header {
	display: flex;
	align-items: flex-start;
	gap: 24rpx;
	margin-bottom: 24rpx;
}

.task-checkbox {
	width: 40rpx;
	height: 40rpx;
	border: 3rpx solid #5b8ff9;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	box-sizing: border-box;
}

.checkbox-checked {
	width: 20rpx;
	height: 20rpx;
	background: #5b8ff9;
	border-radius: 50%;
}

.task-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.task-title-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.task-title {
	font-size: 32rpx;
	font-weight: 500;
	color: #333;
	line-height: 1.4;
}

.task-title.completed {
	text-decoration: line-through;
	color: #999;
}

.task-desc {
	font-size: 26rpx;
	color: #999;
	line-height: 1.5;
}

.task-tags {
	display: flex;
	gap: 12rpx;
	flex-wrap: wrap;
}

.tag {
	padding: 6rpx 16rpx;
	border-radius: 6rpx;
	font-size: 22rpx;
	color: #5b8ff9;
	background: #e8f4ff;
	border: 1rpx solid #d4e8ff;
}

.divider {
	height: 1rpx;
	background: #f0f0f0;
	margin: 24rpx 0;
}

.task-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.task-meta {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.meta-item {
	display: flex;
	align-items: center;
	gap: 6rpx;
}

.time-icon {
	width: 24rpx;
	height: 24rpx;
	border: 2rpx solid #c0c4cc;
	border-radius: 50%;
	position: relative;
	box-sizing: border-box;
}

.time-icon::after {
	content: '';
	position: absolute;
	width: 2rpx;
	height: 8rpx;
	background: #c0c4cc;
	top: 2rpx;
	left: 50%;
	transform: translateX(-50%);
}

.time-icon::before {
	content: '';
	position: absolute;
	width: 6rpx;
	height: 2rpx;
	background: #c0c4cc;
	top: 8rpx;
	left: 8rpx;
}

.meta-text {
	font-size: 24rpx;
	color: #c0c4cc;
}

.meta-dot {
	font-size: 24rpx;
	color: #e0e0e0;
}

.priority-label {
	padding: 4rpx 12rpx;
	border-radius: 6rpx;
	font-size: 22rpx;
}

.priority-1 {
	color: #52c41a;
	background: #f6ffed;
	border: 1rpx solid #b7eb8f;
}

.priority-2 {
	color: #fa8c16;
	background: #fff7e6;
	border: 1rpx solid #ffd591;
}

.priority-3 {
	color: #f5222d;
	background: #fff1f0;
	border: 1rpx solid #ffa39e;
}

.task-actions {
	display: flex;
	gap: 12rpx;
}

.action-btn {
	padding: 10rpx 24rpx;
	border-radius: 8rpx;
	background: #f8f9fa;
	border: 1rpx solid #e8e8e8;
}

.action-btn-primary {
	background: #5b8ff9;
	border-color: #5b8ff9;
}

.action-btn-primary .action-text {
	color: #ffffff;
}

.action-text {
	font-size: 24rpx;
	color: #666;
}

/* 空状态 */
.empty-text {
	text-align: center;
	padding: 60rpx 0;
	font-size: 26rpx;
	color: #999;
}

.nomore-text {
	text-align: center;
	padding: 32rpx 0;
	font-size: 24rpx;
	color: #ccc;
}

/* 添加按钮 */
.add-btn {
	position: fixed;
	right: 30rpx;
	bottom: 120rpx;
	width: 100rpx;
	height: 100rpx;
	background: #5b8ff9;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.3);
	z-index: 100;
}

.add-icon {
	font-size: 60rpx;
	color: #ffffff;
	font-weight: 300;
	line-height: 1;
}

/* 选择器 */
.picker-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	z-index: 999;
	display: flex;
	align-items: flex-end;
}

.picker-content {
	width: 100%;
	max-height: 70vh;
	background: #ffffff;
	border-radius: 24rpx 24rpx 0 0;
	display: flex;
	flex-direction: column;
}

.picker-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	flex-shrink: 0;
}

.picker-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.picker-close {
	font-size: 48rpx;
	color: #999;
	line-height: 1;
}

.picker-list {
	flex: 1;
	padding: 30rpx 20rpx;
	overflow-y: auto;
	box-sizing: border-box;
}

.picker-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 16rpx;
}

.picker-item {
	background: #f8f9fa;
	border-radius: 12rpx;
	padding: 28rpx 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
}

.picker-text {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.picker-footer {
	display: flex;
	gap: 20rpx;
	padding: 20rpx 30rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	border-top: 1rpx solid #f0f0f0;
	flex-shrink: 0;
}

.footer-btn {
	flex: 1;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.footer-btn-reset {
	background: #e8f1ff;
}

.footer-btn-confirm {
	background: #5b8ff9;
}

.footer-btn-text {
	font-size: 30rpx;
	font-weight: 500;
}

.footer-btn-reset .footer-btn-text {
	color: #5b8ff9;
}

.footer-btn-confirm .footer-btn-text {
	color: #ffffff;
}

/* 添加/编辑任务弹窗 */
.modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 999;
	display: flex;
	align-items: center;
	justify-content: center;
}

.modal-mask {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	z-index: 1;
}

.modal-box {
	position: relative;
	width: 650rpx;
	max-height: 80vh;
	background: #fff;
	border-radius: 20rpx;
	z-index: 2;
	display: flex;
	flex-direction: column;
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 40rpx 40rpx 20rpx;
	flex-shrink: 0;
}

.modal-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.modal-close {
	font-size: 50rpx;
	color: #999;
	line-height: 1;
}

.modal-body {
	padding: 0 40rpx 40rpx;
	overflow-y: auto;
	flex: 1;
}

.task-input-field {
	width: 100%;
	height: 88rpx;
	line-height: 88rpx;
	padding: 0 24rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 12rpx;
	font-size: 28rpx;
	margin-bottom: 24rpx;
	box-sizing: border-box;
}

.task-textarea-field {
	width: 100%;
	min-height: 160rpx;
	padding: 20rpx 24rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 12rpx;
	font-size: 28rpx;
	line-height: 1.6;
	margin-bottom: 24rpx;
	box-sizing: border-box;
}

.modal-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 25rpx 0;
	border-bottom: 1rpx solid #eee;
	margin-bottom: 20rpx;
}

.modal-label {
	font-size: 28rpx;
	color: #333;
}

.picker-trigger {
	display: flex;
	align-items: center;
	gap: 10rpx;
}

.modal-value {
	font-size: 28rpx;
	color: #5b8ff9;
}

.picker-arrow {
	font-size: 32rpx;
	color: #999;
	line-height: 1;
}

.modal-link {
	font-size: 28rpx;
	color: #5b8ff9;
}

.selected-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 10rpx;
	margin-bottom: 20rpx;
}

.selected-tag {
	background: #e8f4ff;
	color: #5b8ff9;
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
}

.modal-submit {
	width: 100%;
	background: #5b8ff9;
	color: #fff;
	border: none;
	border-radius: 40rpx;
	padding: 25rpx;
	font-size: 30rpx;
	margin-top: 20rpx;
}
</style>
