# Mybatis-Table-Modify

本项目Fork自 [A.C.Table](https://gitee.com/sunchenbin/mybatis-enhance)，因为该开源项目长期未更新，并且我在其他项目中也用到了A.C.Table，
不过是以源码的方式进行的引入，并对功能进行了改造，所以打算把这些功能给开源出来，同时继续将这个项目完善下去。

## 路线图
- 1.5.1 主要目标：调整项目结构，去除除表维护相关的其他功能，代码暂时只适配Spring Boot项目
- 1.5.2 主要目标：拆分为Maven多模块项目，支持非Spring Boot的项目接入
- 1.5.x 目标：
  - 支持SQL Server数据库的表维护
  - 支持Oracle数据库的表维护
  - 支持达梦数据库的表维护
  - 提供接入其他类型数据库的插件机制
- 1.6.x 目标：
  - 支持非Spring中间件的项目接入

## 使用说明
### 添加pom依赖
```xml
    <dependency>
        <groupId>cn.bootxe</groupId>
        <artifactId>mybatis-table-modify</artifactId>
        <version>1.5.1.RELEASE</version>
    </dependency>
```
### 配置要建表的路径
```yaml
mybatis-table:
  # 数据库类型
  database-type: mysql
  # 更新类型
  update-type: create
  # 扫描包路径, 可以用 ,和 ; 分隔
  scan-package: cn.bootx.**.entity
```
### 在类上配置

## 支持完善度
### 数据库类型
- [x] MySQL
- [ ] Oracle

### 表设计功能
- 表信息
  - [x] 名称 
  - [x] 备注
  - [x] 引擎
  - [x] 字符集
- 字段
  - [x] 名称
  - [x] 长度
  - [x] 默认值
  - [x] 自增
  - [x] 主键/多主键
  - 
