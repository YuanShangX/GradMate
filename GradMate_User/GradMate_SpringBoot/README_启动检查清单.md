# Spring Boot 启动检查清单

## 启动前检查

### 1. 数据库检查
```bash
# 检查MySQL服务是否运行
mysql -uroot -pA123456 -e "SELECT 1;"

# 检查gradmate数据库是否存在
mysql -uroot -pA123456 -e "SHOW DATABASES LIKE 'gradmate';"

# 检查major_category表是否存在
mysql -uroot -pA123456 -D gradmate -e "SHOW TABLES LIKE 'major_category';"

# 检查major_category表数据量
mysql -uroot -pA123456 -D gradmate -e "SELECT COUNT(*) FROM major_category;"
```

预期结果：
- MySQL服务正常运行
- gradmate数据库存在
- major_category表存在
- major_category表有2545条数据

### 2. 配置文件检查
检查 `src/main/resources/application.properties` 或 `application.yml`

必须包含：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gradmate?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=A123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis-Plus配置
mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml
mybatis-plus.type-aliases-package=com.gradmate.gradmate_springboot.entity
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# 服务器端口
server.port=9090
```

### 3. 依赖检查
检查 `pom.xml` 是否包含必要依赖：
- spring-boot-starter-web
- mybatis-plus-boot-starter
- mysql-connector-java
- lombok

## 启动步骤

### 方法1：使用IDE启动
1. 在IDE中打开项目
2. 找到 `GradMateSpringBootApplication.java`
3. 右键 → Run 'GradMateSpringBootApplication'
4. 等待启动完成

### 方法2：使用Maven命令启动
```bash
cd GradMate_SpringBoot
mvn clean
mvn spring-boot:run
```

### 方法3：打包后启动
```bash
cd GradMate_SpringBoot
mvn clean package
java -jar target/gradmate_springboot-0.0.1-SNAPSHOT.jar
```

## 启动成功标志

### 控制台输出
应该看到类似以下输出：
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.x.x)

...
Started GradMateSpringBootApplication in X.XXX seconds (JVM running for X.XXX)
```

### 端口检查
```bash
# Windows PowerShell
netstat -ano | findstr :9090

# 应该看到类似输出：
# TCP    0.0.0.0:9090           0.0.0.0:0              LISTENING       12345
```

### 健康检查
在浏览器中访问：
```
http://localhost:9090/api/major/disciplines-v2?categoryCode=02
```

应该返回JSON数据，code为200。

## 常见启动问题

### 问题1：端口被占用
**错误信息：**
```
Web server failed to start. Port 9090 was already in use.
```

**解决方案：**
```bash
# 查找占用端口的进程
netstat -ano | findstr :9090

# 结束进程（替换12345为实际PID）
taskkill /PID 12345 /F

# 或者修改application.properties中的端口
server.port=9091
```

### 问题2：数据库连接失败
**错误信息：**
```
Could not create connection to database server
```

**解决方案：**
1. 检查MySQL服务是否启动
2. 检查数据库用户名密码是否正确
3. 检查数据库名称是否正确
4. 检查防火墙设置

### 问题3：Mapper扫描失败
**错误信息：**
```
Invalid bound statement (not found)
```

**解决方案：**
确认主类有 `@MapperScan` 注解：
```java
@SpringBootApplication
@MapperScan("com.gradmate.gradmate_springboot.mapper")
public class GradMateSpringBootApplication {
    // ...
}
```

### 问题4：Bean创建失败
**错误信息：**
```
Error creating bean with name 'majorService'
```

**解决方案：**
1. 检查所有Mapper接口是否有 `@Mapper` 注解
2. 检查Service类是否有 `@Service` 注解
3. 检查Controller类是否有 `@RestController` 注解
4. 清理并重新编译：`mvn clean compile`

### 问题5：表不存在
**错误信息：**
```
Table 'gradmate.major_category' doesn't exist
```

**解决方案：**
运行创建表的SQL脚本：
```bash
mysql -uroot -pA123456 -D gradmate -e "CREATE TABLE IF NOT EXISTS major_category (id INT NOT NULL AUTO_INCREMENT, category_code VARCHAR(2) NOT NULL, category_name VARCHAR(50) NOT NULL, discipline_code VARCHAR(4) NOT NULL, discipline_name VARCHAR(100) NOT NULL, major_code VARCHAR(10) NOT NULL, major_name VARCHAR(100) NOT NULL, degree_type VARCHAR(20) NOT NULL, sort_order INT DEFAULT 0, create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id), INDEX idx_category (category_code), INDEX idx_discipline (discipline_code), INDEX idx_major (major_code), INDEX idx_degree_type (degree_type)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
```

## 启动后测试

### 自动化测试
运行测试脚本：
```bash
cd GradMate_SpringBoot
.\test_api.ps1
```

### 手动测试
在浏览器中依次访问：

1. 查询经济学门类学硕专业
```
http://localhost:9090/api/major/disciplines-v2?categoryCode=02&degreeType=academic
```

2. 查询经济学门类专硕专业
```
http://localhost:9090/api/major/disciplines-v2?categoryCode=02&degreeType=professional
```

3. 查询金融专业的学校列表
```
http://localhost:9090/api/major/schools?majorCode=025100
```

4. 查询上海地区的金融专业学校
```
http://localhost:9090/api/major/schools?majorCode=025100&location=上海
```

### 预期结果
所有接口都应该返回：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [...]
}
```

## 性能检查

### 响应时间
- disciplines-v2接口：< 200ms
- schools接口：< 500ms

### 内存使用
- 启动后：约200-300MB
- 运行中：约300-500MB

### CPU使用
- 空闲时：< 5%
- 请求时：< 20%

## 日志检查

### 正常日志
```
Mapped "{[/api/major/disciplines-v2]}" onto ...
Mapped "{[/api/major/schools]}" onto ...
```

### 错误日志
如果看到ERROR级别的日志，需要检查：
1. 数据库连接
2. SQL语句
3. 实体类映射
4. 业务逻辑

## 停止服务

### IDE中停止
点击IDE的停止按钮

### 命令行停止
```bash
# 找到进程ID
netstat -ano | findstr :9090

# 结束进程
taskkill /PID <PID> /F
```

## 重启建议

遇到以下情况需要重启：
1. 修改了Java代码
2. 修改了配置文件
3. 添加了新的依赖
4. 数据库结构发生变化
5. 出现内存泄漏

## 开发模式

### 热重载
使用Spring Boot DevTools实现热重载：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### 调试模式
在IDE中使用Debug模式启动，可以设置断点调试。

## 生产环境部署

### 打包
```bash
mvn clean package -DskipTests
```

### 运行
```bash
java -jar target/gradmate_springboot-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 后台运行
```bash
nohup java -jar target/gradmate_springboot-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

## 监控建议

1. 使用Spring Boot Actuator监控应用状态
2. 配置日志文件，便于问题排查
3. 定期检查数据库连接池状态
4. 监控API响应时间
5. 设置异常告警机制
