# 老年大学管理系统

## 项目简介

老年大学管理系统是一个基于Spring Boot的后端服务系统，用于管理老年教育机构的日常运营，包括服务站点管理、课程管理、教师管理、学员报名等功能。

## 技术栈

- Java 17
- Spring Boot 3.3.4
- PostgreSQL
- MyBatis-Plus
- Druid
- EasyExcel
- Docker

## 主要功能

- 服务站点管理
  - 站点信息维护
  - 站点图片上传
  - 站点审核
  - 站点统计报表

- 课程管理
  - 课程信息维护
  - 课程复制
  - 课程导入导出
  - 课程签到管理

- 教师管理
  - 教师信息维护
  - 教师课程关联

- 学员管理
  - 学员报名
  - 签到记录
  - 考勤统计

## 项目结构
```bash
old-school/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/npc/old_school/
│ │ │ ├── controller/ # 控制器层
│ │ │ ├── service/ # 服务层
│ │ │ ├── entity/ # 实体类
│ │ │ ├── dto/ # 数据传输对象
│ │ │ ├── mapper/ # MyBatis映射接口
│ │ │ ├── util/ # 工具类
│ │ │ └── exception/ # 异常处理
│ │ └── resources/
│ │ ├── mapper/ # MyBatis映射文件
│ │ ├── application.yml # 应用配置文件
│ │ └── schema.sql # 数据库初始化脚本
│ └── test/ # 测试代码
├── docker/ # Docker相关配置
└── logs/ # 日志文件
```
##

## 快速开始

1. 环境要求
   - JDK 17+
   - PostgreSQL 12+
   - Docker & Docker Compose

2. 数据库配置
```yaml
  datasource:
    druid:
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://192.168.23.112:5432/old_school?useSSL=false
      username: postgres
      password: 123456
```

3. 启动服务
```bash
# 使用Docker Compose启动
docker-compose up -d

# 或直接使用Java启动
./mvnw spring-boot:run
```

## 文件上传配置

系统支持图片上传功能，配置如下：

```yaml
upload:
  path: D:\Code\Java\old-school\logs\upload
  max-size: 10485760 # 10MB
```

## 接口文档

主要接口包括：

- 服务站点管理接口
  - POST /api/sites - 创建服务站点
  - PUT /api/sites/{id} - 更新服务站点
  - GET /api/sites/{id} - 获取站点详情
  - POST /api/sites/audit - 审核站点

- 课程管理接口
  - POST /api/courses - 创建课程
  - GET /api/courses/{id} - 获取课程详情
  - POST /api/courses/{id}/copy - 复制课程
  - GET /api/courses/export - 导出课程信息

## 异常处理

系统统一异常处理：

```java
public enum ExceptionEnum implements BaseErrorInfomation {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未经授权的访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");
}
```

## 日志配置

系统使用logback进行日志管理，配置文件位于 `src/main/resources/logback-spring.xml`。

## 部署说明

项目支持Docker部署，使用docker-compose进行容器编排，包含以下服务：

- old-school: Spring Boot应用服务
- postgres: PostgreSQL数据库服务
- nginx: 反向代理服务器

详细配置请参考项目根目录下的 `docker-compose.yml` 文件。

## 许可证

本项目采用 Apache License 2.0 许可证。