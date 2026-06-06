package com.gradmate.gradmate_adminsystem_springboot.controller;

import com.gradmate.gradmate_adminsystem_springboot.common.PageResult;
import com.gradmate.gradmate_adminsystem_springboot.common.Result;
import com.gradmate.gradmate_adminsystem_springboot.service.AdminDataService;
import com.gradmate.gradmate_adminsystem_springboot.service.AdminOperationLogService;
import com.gradmate.gradmate_adminsystem_springboot.service.TableMeta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDataController {
    private final AdminDataService dataService;
    private final AdminOperationLogService logService;

    public AdminDataController(AdminDataService dataService, AdminOperationLogService logService) {
        this.dataService = dataService;
        this.logService = logService;
    }

    @GetMapping("/meta")
    public Result<Collection<TableMeta>> meta() {
        return Result.ok(dataService.metas());
    }

    @GetMapping("/schools/options")
    public Result<List<Map<String, Object>>> schoolOptions(@RequestParam(required = false) String keyword) {
        return Result.ok(dataService.schoolOptions(keyword));
    }

    @GetMapping("/{resource}")
    public Result<PageResult> page(@PathVariable String resource,
                                   @RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam Map<String, String> params) {
        return Result.ok(dataService.page(resource, pageNum, pageSize, keyword, params));
    }

    @GetMapping("/{resource}/{id}")
    public Result<Map<String, Object>> detail(@PathVariable String resource, @PathVariable String id) {
        return Result.ok(dataService.detail(resource, id));
    }

    @PostMapping("/{resource}")
    public Result<Void> create(@PathVariable String resource, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return createWithLog(resource, body, request);
    }

    @PutMapping("/{resource}/{id}")
    public Result<Void> update(@PathVariable String resource, @PathVariable String id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return updateWithLog(resource, id, body, request);
    }

    @DeleteMapping("/{resource}/{id}")
    public Result<Void> delete(@PathVariable String resource, @PathVariable String id, HttpServletRequest request) {
        return deleteWithLog(resource, id, request);
    }

    @GetMapping("/infos/{kind}")
    public Result<PageResult> infoPage(@PathVariable String kind,
                                       @RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam Map<String, String> params) {
        return Result.ok(dataService.page(kind, pageNum, pageSize, keyword, params));
    }

    @PostMapping("/infos/{kind}")
    public Result<Void> infoCreate(@PathVariable String kind, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return createWithLog(kind, body, request);
    }

    @PutMapping("/infos/{kind}/{id}")
    public Result<Void> infoUpdate(@PathVariable String kind, @PathVariable String id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return updateWithLog(kind, id, body, request);
    }

    @DeleteMapping("/infos/{kind}/{id}")
    public Result<Void> infoDelete(@PathVariable String kind, @PathVariable String id, HttpServletRequest request) {
        return deleteWithLog(kind, id, request);
    }

    @GetMapping("/study/{kind}")
    public Result<PageResult> studyPage(@PathVariable String kind,
                                        @RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam Map<String, String> params) {
        return Result.ok(dataService.page("study-" + kind, pageNum, pageSize, keyword, params));
    }

    @PostMapping("/study/{kind}")
    public Result<Void> studyCreate(@PathVariable String kind, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return createWithLog("study-" + kind, body, request);
    }

    @PutMapping("/study/{kind}/{id}")
    public Result<Void> studyUpdate(@PathVariable String kind, @PathVariable String id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return updateWithLog("study-" + kind, id, body, request);
    }

    @DeleteMapping("/study/{kind}/{id}")
    public Result<Void> studyDelete(@PathVariable String kind, @PathVariable String id, HttpServletRequest request) {
        return deleteWithLog("study-" + kind, id, request);
    }

    @GetMapping("/ai/{kind}")
    public Result<PageResult> aiPage(@PathVariable String kind,
                                     @RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam Map<String, String> params) {
        return Result.ok(dataService.page("ai-" + kind, pageNum, pageSize, keyword, params));
    }

    @PostMapping("/ai/{kind}")
    public Result<Void> aiCreate(@PathVariable String kind, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return createWithLog("ai-" + kind, body, request);
    }

    @PutMapping("/ai/{kind}/{id}")
    public Result<Void> aiUpdate(@PathVariable String kind, @PathVariable String id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        return updateWithLog("ai-" + kind, id, body, request);
    }

    @DeleteMapping("/ai/{kind}/{id}")
    public Result<Void> aiDelete(@PathVariable String kind, @PathVariable String id, HttpServletRequest request) {
        return deleteWithLog("ai-" + kind, id, request);
    }

    private Result<Void> createWithLog(String resource, Map<String, Object> body, HttpServletRequest request) {
        try {
            Long id = dataService.create(resource, body);
            logService.record(request, resource, "CREATE", id, "SUCCESS", "新增成功");
            return Result.ok();
        } catch (RuntimeException e) {
            logService.record(request, resource, "CREATE", null, "FAIL", e.getMessage());
            throw e;
        }
    }

    private Result<Void> updateWithLog(String resource, String id, Map<String, Object> body, HttpServletRequest request) {
        try {
            dataService.update(resource, id, body);
            logService.record(request, resource, "UPDATE", id, "SUCCESS", "编辑成功");
            return Result.ok();
        } catch (RuntimeException e) {
            logService.record(request, resource, "UPDATE", id, "FAIL", e.getMessage());
            throw e;
        }
    }

    private Result<Void> deleteWithLog(String resource, String id, HttpServletRequest request) {
        try {
            dataService.delete(resource, id);
            logService.record(request, resource, "DELETE", id, "SUCCESS", "删除成功");
            return Result.ok();
        } catch (RuntimeException e) {
            logService.record(request, resource, "DELETE", id, "FAIL", e.getMessage());
            throw e;
        }
    }
}
