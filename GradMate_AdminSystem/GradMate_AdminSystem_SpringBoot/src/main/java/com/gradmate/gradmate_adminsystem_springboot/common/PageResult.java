package com.gradmate.gradmate_adminsystem_springboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class PageResult {
    private List<Map<String, Object>> records;
    private long total;
    private int pageNum;
    private int pageSize;
}
