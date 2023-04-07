# Mybatis-Table-Modify

本项目Fork自 [A.C.Table](https://gitee.com/sunchenbin/mybatis-enhance)，因为该开源项目长期未有更新，而且目前项目中也用到了这个项目并进行了改造，
所以打算继续根据原有的功能进行继续完善下去。

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
