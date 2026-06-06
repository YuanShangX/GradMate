package com.gradmate.gradmate_adminsystem_springboot.service;

import java.util.List;
import java.util.Map;

public record TableMeta(
        String key,
        String title,
        String tableName,
        String idColumn,
        List<String> fields,
        List<String> requiredFields,
        List<String> searchFields,
        Map<String, String> filterFields,
        String defaultOrder
) {
}
