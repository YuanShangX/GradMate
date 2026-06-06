package com.gradmate.gradmate_springboot.controller;

import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.dto.CheckInRequest;
import com.gradmate.gradmate_springboot.dto.StudyStatsResponse;
import com.gradmate.gradmate_springboot.dto.TaskRequest;
import com.gradmate.gradmate_springboot.dto.TaskTagRequest;
import com.gradmate.gradmate_springboot.dto.SubtaskRequest;
import com.gradmate.gradmate_springboot.entity.DailyTask;
import com.gradmate.gradmate_springboot.entity.StudyPlan;
import com.gradmate.gradmate_springboot.entity.StudyRecord;
import com.gradmate.gradmate_springboot.entity.TaskTag;
import com.gradmate.gradmate_springboot.entity.TaskSubtask;
import com.gradmate.gradmate_springboot.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习控制器
 */
@RestController
@RequestMapping("/api/study")
public class StudyController {
    
    @Autowired
    private StudyService studyService;
    
    /**
     * 创建学习计划
     */
    @PostMapping("/plan")
    public Result<StudyPlan> createPlan(@RequestBody StudyPlan request) {
        StudyPlan plan = studyService.createPlan(
            request.getUserId(), 
            request.getTitle(), 
            request.getContent(), 
            request.getTargetDate()
        );
        return Result.success(plan);
    }
    
    /**
     * 获取用户学习计划
     */
    @GetMapping("/plan/user/{userId}")
    public Result<StudyPlan> getPlanByUser(@PathVariable Long userId) {
        StudyPlan plan = studyService.getPlanByUser(userId);
        return Result.success(plan);
    }
    
    /**
     * 更新学习计划
     */
    @PutMapping("/plan/{id}")
    public Result<Boolean> updatePlan(@PathVariable Long id, @RequestBody StudyPlan request) {
        boolean success = studyService.updatePlan(
            id, 
            request.getTitle(), 
            request.getContent(), 
            request.getTargetDate()
        );
        return success ? Result.success(true) : Result.error("更新失败");
    }
    
    /**
     * 删除学习计划
     */
    @DeleteMapping("/plan/{id}")
    public Result<Boolean> deletePlan(@PathVariable Long id) {
        boolean success = studyService.deletePlan(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
    
    /**
     * 获取倒计时天数
     */
    @GetMapping("/countdown")
    public Result<Long> getCountdown(@RequestParam Long userId) {
        Long daysLeft = studyService.calculateDaysLeft(userId);
        return Result.success(daysLeft);
    }
    
    /**
     * 今日打卡
     */
    @PostMapping("/checkin")
    public Result<StudyRecord> checkIn(@RequestBody CheckInRequest request) {
        StudyRecord record = studyService.checkIn(request);
        return Result.success(record);
    }
    
    /**
     * 获取某日打卡记录
     */
    @GetMapping("/record/date")
    public Result<StudyRecord> getRecordByDate(
            @RequestParam Long userId,
            @RequestParam LocalDate date) {
        
        StudyRecord record = studyService.getRecordByDate(userId, date);
        return Result.success(record);
    }
    
    /**
     * 获取某月打卡记录
     */
    @GetMapping("/record/month")
    public Result<List<StudyRecord>> getRecordsByMonth(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        
        List<StudyRecord> records = studyService.getRecordsByMonth(userId, year, month);
        return Result.success(records);
    }

    /**
     * 获取日期范围内的打卡记录
     */
    @GetMapping("/record/range")
    public Result<List<StudyRecord>> getRecordsByRange(
            @RequestParam Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<StudyRecord> records = studyService.getRecordsByRange(userId, startDate, endDate);
        return Result.success(records);
    }

    
    /**
     * 获取学习统计数据
     */
    @GetMapping("/stats")
    public Result<StudyStatsResponse> getStudyStats(@RequestParam Long userId) {
        StudyStatsResponse stats = studyService.getStudyStats(userId);
        return Result.success(stats);
    }
    
    /**
     * 创建任务
     */
    @PostMapping("/task")
    public Result<DailyTask> createTask(@RequestBody TaskRequest request) {
        DailyTask task = studyService.createTask(request);
        return Result.success(task);
    }
    
    /**
     * 更新任务
     */
    @PutMapping("/task/{id}")
    public Result<Boolean> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        boolean success = studyService.updateTask(id, request.getTaskContent(), request.getPriority(), request.getEstimatedDuration());
        return success ? Result.success(true) : Result.error("更新失败");
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/task/{id}")
    public Result<Boolean> deleteTask(@PathVariable Long id) {
        boolean success = studyService.deleteTask(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
    
    /**
     * 完成/取消完成任务
     */
    @PutMapping("/task/{id}/complete")
    public Result<Boolean> completeTask(@PathVariable Long id) {
        boolean success = studyService.completeTask(id);
        return success ? Result.success(true) : Result.error("操作失败");
    }
    
    /**
     * 获取今日任务列表
     */
    @GetMapping("/task/today")
    public Result<List<DailyTask>> getTodayTasks(@RequestParam Long userId) {
        List<DailyTask> tasks = studyService.getTodayTasks(userId);
        return Result.success(tasks);
    }
    
    /**
     * 获取指定日期任务列表
     */
    @GetMapping("/task/date/{date}")
    public Result<List<DailyTask>> getTasksByDate(
            @PathVariable LocalDate date,
            @RequestParam Long userId) {
        
        List<DailyTask> tasks = studyService.getTasksByDate(userId, date);
        return Result.success(tasks);
    }

    // ==================== 标签管理接口 ====================
    
    /**
     * 获取用户标签列表
     */
    @GetMapping("/tags")
    public Result<List<TaskTag>> getUserTags(@RequestParam Long userId) {
        List<TaskTag> tags = studyService.getUserTags(userId);
        return Result.success(tags);
    }
    
    /**
     * 创建标签
     */
    @PostMapping("/tags")
    public Result<TaskTag> createTag(@RequestBody TaskTagRequest request) {
        TaskTag tag = studyService.createTag(
            request.getUserId(),
            request.getTagName(),
            request.getTagColor(),
            request.getIcon()
        );
        return Result.success(tag);
    }
    
    /**
     * 更新标签
     */
    @PutMapping("/tags/{id}")
    public Result<Boolean> updateTag(@PathVariable Long id, @RequestBody TaskTagRequest request) {
        boolean success = studyService.updateTag(
            id,
            request.getTagName(),
            request.getTagColor(),
            request.getIcon()
        );
        return success ? Result.success(true) : Result.error("更新失败");
    }
    
    /**
     * 删除标签
     */
    @DeleteMapping("/tags/{id}")
    public Result<Boolean> deleteTag(@PathVariable Long id) {
        boolean success = studyService.deleteTag(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
    
    // ==================== 子任务管理接口 ====================
    
    /**
     * 获取任务的子任务列表
     */
    @GetMapping("/task/{taskId}/subtasks")
    public Result<List<TaskSubtask>> getSubtasks(@PathVariable Long taskId) {
        List<TaskSubtask> subtasks = studyService.getSubtasks(taskId);
        return Result.success(subtasks);
    }
    
    /**
     * 创建子任务
     */
    @PostMapping("/task/{taskId}/subtasks")
    public Result<TaskSubtask> createSubtask(
            @PathVariable Long taskId,
            @RequestBody SubtaskRequest request) {
        
        TaskSubtask subtask = studyService.createSubtask(
            taskId,
            request.getSubtaskContent(),
            request.getSortOrder()
        );
        return Result.success(subtask);
    }
    
    /**
     * 完成/取消完成子任务
     */
    @PutMapping("/subtask/{id}/complete")
    public Result<Boolean> completeSubtask(@PathVariable Long id) {
        boolean success = studyService.completeSubtask(id);
        return success ? Result.success(true) : Result.error("操作失败");
    }
    
    /**
     * 删除子任务
     */
    @DeleteMapping("/subtask/{id}")
    public Result<Boolean> deleteSubtask(@PathVariable Long id) {
        boolean success = studyService.deleteSubtask(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
    
    // ==================== 任务搜索和筛选接口 ====================
    
    /**
     * 搜索任务
     */
    @GetMapping("/task/search")
    public Result<List<DailyTask>> searchTasks(
            @RequestParam Long userId,
            @RequestParam String keyword) {
        
        List<DailyTask> tasks = studyService.searchTasks(userId, keyword);
        return Result.success(tasks);
    }
    
    /**
     * 综合筛选任务
     */
    @GetMapping("/task/filter")
    public Result<List<DailyTask>> filterTasks(
            @RequestParam Long userId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer priority) {
        
        List<DailyTask> tasks = studyService.filterTasks(userId, tag, status, priority);
        return Result.success(tasks);
    }
}
