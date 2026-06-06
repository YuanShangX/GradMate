# 400错误排查指南

## 错误信息
```
GET http://localhost:9090/api/major/disciplines-v2?categoryCode=02&degreeType=academic 400 (Bad Request)
```

## 可能原因

### 1. Spring Boot未重启
新增的MajorCategory实体类和Mapper需要重启服务才能生效。

**解决方案：**
- 停止Spring Boot应用
- 重新启动应用
- 等待启动完成（看到"Started GradMateSpringBootApplication"）

### 2. Mapper未被扫描
MajorCategoryMapper可能没有被Spring扫描到。

**解决方案：**
已在主类添加 `@MapperScan("com.gradmate.gradmate_springboot.mapper")` 注解

### 3. 数据库表不存在
major_category表可能未创建。

**检查命令：**
```bash
mysql -uroot -pA123456 -D gradmate -e "SHOW TABLES LIKE 'major_category';"
```

**解决方案：**
```bash
mysql -uroot -pA123456 -D gradmate -e "CREATE TABLE IF NOT EXISTS major_category (id INT NOT NULL AUTO_INCREMENT, category_code VARCHAR(2) NOT NULL, category_name VARCHAR(50) NOT NULL, discipline_code VARCHAR(4) NOT NULL, discipline_name VARCHAR(100) NOT NULL, major_code VARCHAR(10) NOT NULL, major_name VARCHAR(100) NOT NULL, degree_type VARCHAR(20) NOT NULL, sort_order INT DEFAULT 0, create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id), INDEX idx_category (category_code), INDEX idx_discipline (discipline_code), INDEX idx_major (major_code), INDEX idx_degree_type (degree_type)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
```

### 4. 数据库连接问题
数据库配置可能有误。

**检查文件：**
`src/main/resources/application.properties` 或 `application.yml`

**检查内容：**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gradmate?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=A123456
```

### 5. 依赖问题
MyBatis-Plus依赖可能未正确配置。

**检查文件：**
`pom.xml`

**确认依赖：**
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3</version>
</dependency>
```

## 排查步骤

### 步骤1：检查后端日志
查看Spring Boot控制台输出，寻找错误信息：
- 启动错误
- SQL错误
- Bean创建错误
- Mapper扫描错误

### 步骤2：检查数据库
```bash
# 检查表是否存在
mysql -uroot -pA123456 -D gradmate -e "SHOW TABLES LIKE 'major_category';"

# 检查数据是否存在
mysql -uroot -pA123456 -D gradmate -e "SELECT COUNT(*) FROM major_category;"

# 检查表结构
mysql -uroot -pA123456 -D gradmate -e "DESC major_category;"
```

### 步骤3：测试简单接口
先测试不需要major_category表的接口：
```
http://localhost:9090/api/major/disciplines?categoryCode=02
```

如果这个接口正常，说明基础配置没问题，只是新表的问题。

### 步骤4：查看详细错误
在浏览器开发者工具的Network标签中：
1. 点击失败的请求
2. 查看Response标签
3. 查看详细的错误信息

### 步骤5：检查CORS配置
确认CorsConfig是否正确配置：
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

## 快速修复方案

### 方案1：完全重启
1. 停止Spring Boot应用
2. 清理编译缓存：`mvn clean`
3. 重新编译：`mvn compile`
4. 启动应用：`mvn spring-boot:run`

### 方案2：使用旧接口
如果新接口一直有问题，可以暂时使用旧接口：

**前端修改：**
```javascript
// 将 disciplines-v2 改回 disciplines
url: 'http://localhost:9090/api/major/disciplines'
// 注意：旧接口不支持degreeType参数
```

### 方案3：手动测试SQL
直接在数据库中测试查询：
```sql
SELECT * FROM major_category 
WHERE category_code = '02' 
AND degree_type = 'academic'
ORDER BY discipline_code, sort_order;
```

如果SQL能正常执行，说明数据库层面没问题。

## 成功标志

当以下条件都满足时，说明问题已解决：

1. ✅ Spring Boot启动无错误
2. ✅ 浏览器访问接口返回200状态码
3. ✅ 返回的JSON数据格式正确
4. ✅ 前端页面能正常显示专业列表
5. ✅ 学硕/专硕切换功能正常

## 联系支持

如果以上方法都无法解决问题，请提供：
1. Spring Boot完整启动日志
2. 浏览器Network标签的完整错误信息
3. 数据库表结构和数据量
4. application.properties配置文件内容
