# Mybatis-Table-Modify(表结构管理工具)

<p>
 <img src='https://gitee.com/bootx/mybatis-table-modify/badge/star.svg?theme=dark' alt='star'/>
 <img src="https://img.shields.io/badge/mybatis table modify-1.5.3-success.svg" alt="Build Status"/>
<img src="https://img.shields.io/badge/Author-Bootx-orange.svg" alt="Build Status"/>
 <img src="https://img.shields.io/badge/license-Apache%20License%202.0-green.svg"/>
</p>

## 🍈项目介绍
本项目Fork自 [A.C.Table](https://gitee.com/sunchenbin/mybatis-enhance)，之前在其他项目中用到了A.C.Table，
并对功能进行了改造。因为该A.C.Table项目近期发生了停更，所以打算把这些功能给开源出来，同时继续将这个项目完善下去。

## 🍎 路线图
- 1.5.2 主要目标：调整项目结构，去除除表维护相关的其他功能，代码暂时只适配Spring Boot项目
- 1.5.3 主要目标
  - [x] 拆分为Maven多模块项目，支持非Spring Boot的项目接入
  - [x] 支持MP的字段忽略配置
  - [x] 支持简单索引方式（MySql）,与标准索引无法一起使用
  - [x] 新增追加模式，处理
  - [x] 空对象不进行创建
- 1.5.x 目标：
  - 支持SQL Server数据库的表维护
  - 支持Oracle数据库的表维护
  - 支持达梦数据库的表维护
  - 提供接入其他类型数据库的插件机制
- 1.6.x 目标：
  - 支持非Spring中间件的项目接入

## 📚 使用文档
- [快速开始.md](_doc/教程/快速开始.md)
- [核心注解.md](_doc/教程/核心注解.md)
- [MySQL专用注解.md](_doc/教程/MySQL专用注解.md)

## 🥂 使用说明
> 推荐只在开发时使用，生产环境停用自动更新。如何使用可以参考示例项目示例项目: [mybatis-table-modify-example](https://gitee.com/bootx/mybatis-table-modify-example)
> ，以及项目使用文档

### 添加pom依赖
```xml
<dependency>
    <groupId>cn.bootx</groupId>
    <artifactId>mybatis-table-modify-mysql-boot-starter</artifactId>
    <version>${mybatis-table-modify.version}</version>
</dependency>
```
[最新版本](https://mvnrepository.com/artifact/cn.bootx/mybatis-table-modify)
### 配置文件配置
```yaml
mybatis-table:
  # 更新类型
  update-type: create
  # 扫描包路径, 可以用 ,和 ; 分隔
  scan-package: cn.bootx.**.entity
  # 是否开启快速失败模式, 出现错误项目直接中止启动
  fail-fast: false
```
### 其他配置
> 无论是使用MyBatis还是MyBatis Plus，需要保证项目中的`mapper`被扫描到，否则会报错无法启动，
>
> 项目中`mapper`文件所在的路径为 `classpath*:cn/bootx/mybatis/table/modify/impl/*/mapper/*TableModifyMapper.xml`

#### MyBatis配置扫描
```yaml
mybatis:
  mapper-locations: classpath*:mapper/**/*Mapper.xml
```
#### MyBatis Plus配置扫描
```yaml
# mp配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*Mapper.xml
```

##  🍁 Bootx 项目合集
- Bootx-Platform：单体版脚手架 [Gitee地址](https://gitee.com/bootx/bootx-platform)
- Bootx-Cloud：微服务版脚手架 [Gitee地址](https://gitee.com/bootx/bootx-cloud)
- dax-pay：开源支付系统 [Gitee地址](https://gitee.com/bootx/dax-pay)
- bpm-plus：协同办公系统 [Gitee地址](https://gitee.com/bootx/bpm-plus)
- mybatis-table-modify：数据表结构管理 [Gitee地址](https://gitee.com/bootx/mybatis-table-modify)

##  🥪 关于我们

QQ扫码加入QQ交流群
<p>

<img src="https://oscimg.oschina.net/oscnet/up-ac1a8f8221203de2b5cbc6a461a26199b95.jpg" width = "330" height = "500"/>
</p>

## 🍻 鸣谢
感谢 JetBrains 提供的免费开源 License：

[![JetBrains](https://oscimg.oschina.net/oscnet/up-4aab9fa8bc769295b48c888d93e71320d93.png)](https://www.jetbrains.com/?from=bootx)

## 🍷License

Apache License Version 2.0
