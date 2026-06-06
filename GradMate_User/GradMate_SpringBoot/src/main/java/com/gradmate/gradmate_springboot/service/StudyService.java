package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gradmate.gradmate_springboot.dto.CheckInRequest;
import com.gradmate.gradmate_springboot.dto.StudyStatsResponse;
import com.gradmate.gradmate_springboot.dto.TaskRequest;
import com.gradmate.gradmate_springboot.entity.DailyTask;
import com.gradmate.gradmate_springboot.entity.StudyPlan;
import com.gradmate.gradmate_springboot.entity.StudyRecord;
import com.gradmate.gradmate_springboot.entity.TaskTag;
import com.gradmate.gradmate_springboot.entity.TaskSubtask;
import com.gradmate.gradmate_springboot.entity.TaskReminder;
import com.gradmate.gradmate_springboot.mapper.DailyTaskMapper;
import com.gradmate.gradmate_springboot.mapper.StudyPlanMapper;
import com.gradmate.gradmate_springboot.mapper.StudyRecordMapper;
import com.gradmate.gradmate_springboot.mapper.TaskTagMapper;
import com.gradmate.gradmate_springboot.mapper.TaskSubtaskMapper;
import com.gradmate.gradmate_springboot.mapper.TaskReminderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 学习服务类
 */
@Service
public class StudyService {
    
    @Autowired
    private StudyPlanMapper studyPlanMapper;
    
    @Autowired
    private StudyRecordMapper studyRecordMapper;
    
    @Autowired
    private DailyTaskMapper dailyTaskMapper;
    
    @Autowired
    private TaskTagMapper taskTagMapper;
    
    @Autowired
    private TaskSubtaskMapper taskSubtaskMapper;
    
    @Autowired
    private TaskReminderMapper taskReminderMapper;
    
    /**
     * 创建学习计划
     */
    public StudyPlan createPlan(Long userId, String title, String content, LocalDate targetDate) {
        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setTitle(title);
        plan.setContent(content);
        plan.setTargetDate(targetDate);
        plan.setStatus(0); // 进行中
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        
        studyPlanMapper.insert(plan);
        return plan;
    }
    
    /**
     * 获取用户的学习计划
     */
    public StudyPlan getPlanByUser(Long userId) {
        QueryWrapper<StudyPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("status", 0) // 进行中的计划
               .orderByDesc("create_time")
               .last("LIMIT 1");
        return studyPlanMapper.selectOne(wrapper);
    }
    
    /**
     * 更新学习计划
     */
    public boolean updatePlan(Long planId, String title, String content, LocalDate targetDate) {
        StudyPlan plan = studyPlanMapper.selectById(planId);
        if (plan == null) {
            return false;
        }
        
        plan.setTitle(title);
        plan.setContent(content);
        plan.setTargetDate(targetDate);
        plan.setUpdateTime(LocalDateTime.now());
        
        return studyPlanMapper.updateById(plan) > 0;
    }
    
    /**
     * 删除学习计划
     */
    public boolean deletePlan(Long planId) {
        return studyPlanMapper.deleteById(planId) > 0;
    }
    
    /**
     * 计算距离考研剩余天数
     */
    public Long calculateDaysLeft(Long userId) {
        StudyPlan plan = getPlanByUser(userId);
        if (plan == null || plan.getTargetDate() == null) {
            // 默认2026年考研日期（12月第三个周六，假设是12月21日）
            LocalDate defaultExamDate = LocalDate.of(2026, 12, 21);
            return ChronoUnit.DAYS.between(LocalDate.now(), defaultExamDate);
        }
        
        return ChronoUnit.DAYS.between(LocalDate.now(), plan.getTargetDate());
    }
    
    /**
     * 今日打卡
     */
    public StudyRecord checkIn(CheckInRequest request) {
        LocalDate today = LocalDate.now();
        
        // 检查今日是否已打卡
        QueryWrapper<StudyRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", request.getUserId())
               .eq("check_date", today);
        StudyRecord existingRecord = studyRecordMapper.selectOne(wrapper);
        
        if (existingRecord != null) {
            // 更新已有记录
            existingRecord.setDuration(request.getDuration());
            existingRecord.setNote(request.getNote());
            existingRecord.setMood(request.getMood());
            existingRecord.setStudyType(request.getStudyType() != null ? request.getStudyType() : "other");
            studyRecordMapper.updateById(existingRecord);
            return existingRecord;
        } else {
            // 创建新记录
            StudyRecord record = new StudyRecord();
            record.setUserId(request.getUserId());
            record.setCheckDate(today);
            record.setDuration(request.getDuration());
            record.setNote(request.getNote());
            record.setMood(request.getMood());
            record.setStudyType(request.getStudyType() != null ? request.getStudyType() : "other");
            record.setCreateTime(LocalDateTime.now());
            
            studyRecordMapper.insert(record);
            return record;
        }
    }
    
    /**
     * 获取某日打卡记录
     */
    public StudyRecord getRecordByDate(Long userId, LocalDate date) {
        QueryWrapper<StudyRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("check_date", date);
        return studyRecordMapper.selectOne(wrapper);
    }
    
    /**
     * 获取某月打卡记录
     */
    public List<StudyRecord> getRecordsByMonth(Long userId, int year, int month) {
        QueryWrapper<StudyRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .apply("YEAR(check_date) = {0}", year)
               .apply("MONTH(check_date) = {0}", month)
               .orderByAsc("check_date");
        return studyRecordMapper.selectList(wrapper);
    }

    /**
     * 获取日期范围内的打卡记录
     */
    public List<StudyRecord> getRecordsByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<StudyRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .ge("check_date", startDate)
               .le("check_date", endDate)
               .orderByAsc("check_date");
        return studyRecordMapper.selectList(wrapper);
    }

    
    /**
     * 计算连续打卡天数
     */
    public Integer getContinuousDays(Long userId) {
        QueryWrapper<StudyRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("check_date");
        List<StudyRecord> records = studyRecordMapper.selectList(wrapper);
        
        if (records.isEmpty()) {
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        // 检查今天或昨天是否有打卡记录
        LocalDate latestDate = records.get(0).getCheckDate();
        if (!latestDate.equals(today) && !latestDate.equals(yesterday)) {
            return 0;
        }
        
        // 计算连续天数
        int continuousDays = 0;
        LocalDate expectedDate = latestDate;
        
        for (StudyRecord record : records) {
            if (record.getCheckDate().equals(expectedDate)) {
                continuousDays++;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }
        
        return continuousDays;
    }
    
    /**
     * 计算总学习时长
     */
    public Integer getTotalDuration(Long userId) {
        QueryWrapper<StudyRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<StudyRecord> records = studyRecordMapper.selectList(wrapper);
        
        return records.stream()
                .mapToInt(record -> record.getDuration() != null ? record.getDuration() : 0)
                .sum();
    }
    
    /**
     * 获取学习统计数据
     */
    public StudyStatsResponse getStudyStats(Long userId) {
        StudyStatsResponse stats = new StudyStatsResponse();
        
        // 连续打卡天数
        stats.setContinuousDays(getContinuousDays(userId));
        
        // 本月打卡天数
        LocalDate now = LocalDate.now();
        List<StudyRecord> monthlyRecords = getRecordsByMonth(userId, now.getYear(), now.getMonthValue());
        stats.setMonthlyDays(monthlyRecords.size());
        
        // 总学习时长
        Integer totalMinutes = getTotalDuration(userId);
        stats.setTotalDuration(totalMinutes);
        stats.setTotalHours(String.format("%.1f", totalMinutes / 60.0));
        
        // 今日是否已打卡
        StudyRecord todayRecord = getRecordByDate(userId, LocalDate.now());
        stats.setTodayChecked(todayRecord != null);
        
        // 今日任务完成情况
        List<DailyTask> todayTasks = getTasksByDate(userId, LocalDate.now());
        long completedTasks = todayTasks.stream().filter(task -> task.getIsCompleted() == 1).count();
        stats.setTodayTasks(completedTasks + "/" + todayTasks.size());
        
        // 距离考研剩余天数
        stats.setDaysLeft(calculateDaysLeft(userId));
        
        return stats;
    }
    
    /**
     * 创建任务
     */
    public DailyTask createTask(TaskRequest request) {
            DailyTask task = new DailyTask();
            task.setUserId(request.getUserId());
            task.setTaskContent(request.getTaskContent());
            task.setTaskDate(request.getTaskDate());
            task.setPriority(request.getPriority());
            task.setIsCompleted(0);

            // 设置新字段
            task.setEstimatedDuration(request.getEstimatedDuration());
            task.setTags(request.getTags());
            task.setDescription(request.getDescription());
            task.setRepeatType(request.getRepeatType() != null ? request.getRepeatType() : "none");
            task.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
            task.setStatus("todo"); // 默认状态
            task.setActualDuration(0); // 初始实际时长为0

            task.setCreateTime(LocalDateTime.now());
            task.setUpdateTime(LocalDateTime.now());

            dailyTaskMapper.insert(task);
            return task;
        }

    
    /**
     * 更新任务
     */
    public boolean updateTask(Long taskId, String taskContent, Integer priority, Integer estimatedDuration) {
        DailyTask task = dailyTaskMapper.selectById(taskId);
        if (task == null) {
            return false;
        }
        
        task.setTaskContent(taskContent);
        task.setPriority(priority);
        if (estimatedDuration != null) {
            task.setEstimatedDuration(estimatedDuration);
        }
        task.setUpdateTime(LocalDateTime.now());
        
        return dailyTaskMapper.updateById(task) > 0;
    }
    
    /**
     * 删除任务
     */
    public boolean deleteTask(Long taskId) {
        return dailyTaskMapper.deleteById(taskId) > 0;
    }
    
    /**
     * 完成任务
     */
    public boolean completeTask(Long taskId) {
        DailyTask task = dailyTaskMapper.selectById(taskId);
        if (task == null) {
            return false;
        }
        
        // 切换完成状态
        task.setIsCompleted(task.getIsCompleted() == 1 ? 0 : 1);
        // 同步更新status字段
        task.setStatus(task.getIsCompleted() == 1 ? "done" : "todo");
        task.setCompleteTime(task.getIsCompleted() == 1 ? LocalDateTime.now() : null);
        task.setUpdateTime(LocalDateTime.now());
        
        return dailyTaskMapper.updateById(task) > 0;
    }
    
    /**
     * 获取今日任务
     */
    public List<DailyTask> getTodayTasks(Long userId) {
        return getTasksByDate(userId, LocalDate.now());
    }
    
    /**
     * 获取指定日期的任务
     */
    public List<DailyTask> getTasksByDate(Long userId, LocalDate date) {
        QueryWrapper<DailyTask> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("task_date", date)
               .orderByAsc("priority")
               .orderByAsc("create_time");
        return dailyTaskMapper.selectList(wrapper);
    }

    // ==================== 标签管理 ====================
    
    /**
     * 获取用户标签列表
     */
    public List<TaskTag> getUserTags(Long userId) {
        QueryWrapper<TaskTag> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByAsc("sort_order", "create_time");
        return taskTagMapper.selectList(wrapper);
    }
    
    /**
     * 创建标签
     */
    public TaskTag createTag(Long userId, String tagName, String tagColor, String icon) {
        TaskTag tag = new TaskTag();
        tag.setUserId(userId);
        tag.setTagName(tagName);
        tag.setTagColor(tagColor != null ? tagColor : "#1890ff");
        tag.setIcon(icon);
        tag.setSortOrder(0);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        
        taskTagMapper.insert(tag);
        return tag;
    }
    
    /**
     * 更新标签
     */
    public boolean updateTag(Long tagId, String tagName, String tagColor, String icon) {
        TaskTag tag = taskTagMapper.selectById(tagId);
        if (tag == null) {
            return false;
        }
        
        if (tagName != null) tag.setTagName(tagName);
        if (tagColor != null) tag.setTagColor(tagColor);
        if (icon != null) tag.setIcon(icon);
        tag.setUpdateTime(LocalDateTime.now());
        
        return taskTagMapper.updateById(tag) > 0;
    }
    
    /**
     * 删除标签
     */
    public boolean deleteTag(Long tagId) {
        return taskTagMapper.deleteById(tagId) > 0;
    }
    
    // ==================== 子任务管理 ====================
    
    /**
     * 获取任务的子任务列表
     */
    public List<TaskSubtask> getSubtasks(Long parentTaskId) {
        QueryWrapper<TaskSubtask> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_task_id", parentTaskId);
        wrapper.orderByAsc("sort_order", "create_time");
        return taskSubtaskMapper.selectList(wrapper);
    }
    
    /**
     * 创建子任务
     */
    public TaskSubtask createSubtask(Long parentTaskId, String content, Integer sortOrder) {
        TaskSubtask subtask = new TaskSubtask();
        subtask.setParentTaskId(parentTaskId);
        subtask.setSubtaskContent(content);
        subtask.setIsCompleted(0);
        subtask.setSortOrder(sortOrder != null ? sortOrder : 0);
        subtask.setCreateTime(LocalDateTime.now());
        subtask.setUpdateTime(LocalDateTime.now());
        
        taskSubtaskMapper.insert(subtask);
        return subtask;
    }
    
    /**
     * 完成子任务
     */
    public boolean completeSubtask(Long subtaskId) {
        TaskSubtask subtask = taskSubtaskMapper.selectById(subtaskId);
        if (subtask == null) {
            return false;
        }
        
        subtask.setIsCompleted(subtask.getIsCompleted() == 1 ? 0 : 1);
        subtask.setCompleteTime(subtask.getIsCompleted() == 1 ? LocalDateTime.now() : null);
        subtask.setUpdateTime(LocalDateTime.now());
        
        return taskSubtaskMapper.updateById(subtask) > 0;
    }
    
    /**
     * 删除子任务
     */
    public boolean deleteSubtask(Long subtaskId) {
        return taskSubtaskMapper.deleteById(subtaskId) > 0;
    }
    
    // ==================== 任务搜索和筛选 ====================
    
    /**
     * 搜索任务
     */
    public List<DailyTask> searchTasks(Long userId, String keyword) {
        QueryWrapper<DailyTask> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.and(w -> w.like("task_content", keyword)
                         .or()
                         .like("description", keyword)
                         .or()
                         .like("tags", keyword));
        wrapper.orderByDesc("create_time");
        return dailyTaskMapper.selectList(wrapper);
    }
    
    /**
     * 按标签筛选
     */
    public List<DailyTask> getTasksByTag(Long userId, String tag) {
        QueryWrapper<DailyTask> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.like("tags", tag);
        wrapper.orderByDesc("task_date", "priority");
        return dailyTaskMapper.selectList(wrapper);
    }
    
    /**
     * 按状态筛选
     */
    public List<DailyTask> getTasksByStatus(Long userId, String status) {
        QueryWrapper<DailyTask> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", status);
        wrapper.orderByDesc("task_date", "priority");
        return dailyTaskMapper.selectList(wrapper);
    }
    
    /**
     * 按优先级筛选
     */
    public List<DailyTask> getTasksByPriority(Long userId, Integer priority) {
        QueryWrapper<DailyTask> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("priority", priority);
        wrapper.orderByDesc("task_date");
        return dailyTaskMapper.selectList(wrapper);
    }
    
    /**
     * 综合筛选任务
     */
    public List<DailyTask> filterTasks(Long userId, String tag, String status, Integer priority) {
        QueryWrapper<DailyTask> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        if (tag != null && !tag.isEmpty()) {
            wrapper.like("tags", tag);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (priority != null) {
            wrapper.eq("priority", priority);
        }
        
        wrapper.orderByDesc("task_date", "priority");
        return dailyTaskMapper.selectList(wrapper);
    }
    
    // ==================== 任务提醒 ====================
    
    /**
     * 创建提醒
     */
    public TaskReminder createReminder(Long taskId, LocalDateTime reminderTime, String type) {
        TaskReminder reminder = new TaskReminder();
        reminder.setTaskId(taskId);
        reminder.setReminderTime(reminderTime);
        reminder.setReminderType(type != null ? type : "once");
        reminder.setIsSent(0);
        reminder.setCreateTime(LocalDateTime.now());
        
        taskReminderMapper.insert(reminder);
        return reminder;
    }
    
    /**
     * 获取待发送提醒
     */
    public List<TaskReminder> getPendingReminders() {
        QueryWrapper<TaskReminder> wrapper = new QueryWrapper<>();
        wrapper.eq("is_sent", 0);
        wrapper.le("reminder_time", LocalDateTime.now());
        wrapper.orderByAsc("reminder_time");
        return taskReminderMapper.selectList(wrapper);
    }
    
    /**
     * 标记提醒已发送
     */
    public boolean markReminderSent(Long reminderId) {
        TaskReminder reminder = taskReminderMapper.selectById(reminderId);
        if (reminder == null) {
            return false;
        }
        
        reminder.setIsSent(1);
        return taskReminderMapper.updateById(reminder) > 0;
    }
}
