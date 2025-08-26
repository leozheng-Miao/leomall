# 商城系统项目完整档案（AI传承终极版）

## 一、项目全景图

### 1.1 项目基本信息
```yaml
项目名称: Mall System（大型B2C电商平台）
开发时间: 2025年1月底 - 2月初
当前进度: 70%
技术栈: Spring Cloud Alibaba + Spring Boot 3.x + MySQL + Redis + ES + RocketMQ
架构模式: 微服务架构（DDD领域驱动设计）
服务数量: 7个微服务 + 1个网关
代码规模: 约15000行核心代码
```

### 1.2 系统架构图
```
┌─────────────────────────────────────────────────────────┐
│                     前端应用层                           │
├─────────────────────────────────────────────────────────┤
│                  Gateway网关(8080)                       │
│         路由转发 | 认证过滤 | 限流熔断 | 日志追踪        │
├─────────────────────────────────────────────────────────┤
│                     业务服务层                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐│
│  │User(8001)│  │Prod(8002)│  │Inv(8004) │  │Cart(8005)││
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘│
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │Ord(8006) │  │Pay(8007) │  │Msg(8008) │              │
│  └──────────┘  └──────────┘  └──────────┘              │
├─────────────────────────────────────────────────────────┤
│                     中间件层                             │
│   MySQL | Redis | Elasticsearch | RocketMQ | Nacos      │
└─────────────────────────────────────────────────────────┘
```

## 二、已完成模块详细清单

### 2.1 Common公共模块（3个子模块）

#### common-core 核心工具包
```java
// 响应体
R<T>                        - 统一响应格式 {code,message,data}
BusinessException           - 业务异常类（运行时异常）
GlobalExceptionHandler      - 全局异常处理器

// 分页
PageQuery                   - 分页查询参数(pageNum,pageSize)
PageResult<T>               - 分页结果封装

// 常量
SecurityConstants           - 安全常量(USER_ID="userId",ROLES="roles"等)
PermissionConstants         - 权限编码(USER_VIEW="user:view"等)

// 工具类
DateUtil                    - 日期工具
JsonUtil                    - JSON工具
```

#### common-security 安全模块
```java
// JWT相关
JwtUtil                     - JWT工具类(基于JJWT 0.12.6)
  ├── createAccessToken()   - 创建访问令牌(15分钟)
  ├── createRefreshToken()  - 创建刷新令牌(7天)
  ├── parseToken()          - 解析令牌
  └── validateToken()       - 验证令牌

// 认证相关
SecurityUser                - 当前登录用户信息载体
@RequireLogin              - 需要登录注解
@RequirePermission         - 需要权限注解(value="user:view")
AuthenticationInterceptor   - 认证拦截器
LoginUserArgumentResolver  - 登录用户参数解析器
```

#### common-mybatis 数据库模块
```java
MyBatisPlusConfig          - MyBatis Plus配置
  ├── 分页插件
  ├── 字段自动填充(create_time,update_time)
  └── 逻辑删除配置
BaseEntity                 - 基础实体类(id,createTime,updateTime)
```

### 2.2 Gateway网关服务

#### 核心功能清单
- **路由管理**: 7条路由规则，动态路由配置
- **认证过滤**: Token验证，用户信息Header传递
- **请求追踪**: RequestId生成，全链路追踪
- **限流熔断**: 基于IP/用户/接口的限流
- **异常处理**: 统一错误响应格式

#### 核心类说明
```java
filters/global/
├── RequestLogFilter       // 请求日志(生成RequestId,记录耗时)
├── AuthFilter            // 认证过滤(Token验证,解析用户信息)
└── RequestIdFilter       // 请求ID过滤器

config/
├── GatewayConfig         // 网关配置(白名单:/api/user/auth/*)
├── CorsConfig           // 跨域配置(允许localhost:3000)
├── RateLimitConfig      // 限流配置(IP/用户/接口三种策略)
└── RouteConfig          // 路由配置

handler/
├── GlobalExceptionHandler // 全局异常(统一错误格式)
└── FallbackHandler       // 服务降级(503响应)

constant/
└── GatewayConstants      // 常量定义(HEADER_USER_ID等)
```

### 2.3 User用户服务（含认证）

#### 数据库表（6张）
```sql
ums_user              -- 用户表(id,username,password,phone,email等)
ums_role              -- 角色表(id,name,code,description)
ums_permission        -- 权限表(id,name,code,type,parent_id)
ums_user_role         -- 用户角色关联表
ums_role_permission   -- 角色权限关联表
ums_login_log         -- 登录日志表
```

#### 核心接口
```
POST /api/user/auth/register     - 用户注册
POST /api/user/auth/login        - 用户登录(返回双Token)
POST /api/user/auth/logout       - 用户登出(Token加入黑名单)
POST /api/user/auth/refresh      - 刷新Token
GET  /api/user/info              - 获取用户信息
PUT  /api/user/info              - 更新用户信息
POST /api/user/password          - 修改密码
```

### 2.4 Product商品服务

#### 数据库设计（13张表）
```sql
// 分类品牌
pms_category              -- 分类表(三级树形结构)
pms_brand                -- 品牌表
pms_brand_category       -- 品牌分类关联表

// SPU相关(Standard Product Unit)
pms_spu_info            -- SPU信息表
pms_spu_images          -- SPU图片表
pms_spu_info_desc       -- SPU详情表
pms_product_attr_value  -- SPU属性值表

// SKU相关(Stock Keeping Unit)
pms_sku_info            -- SKU信息表
pms_sku_images          -- SKU图片表
pms_sku_sale_attr_value -- SKU销售属性表

// 属性管理
pms_attr_group          -- 属性分组表
pms_attr                -- 属性表
pms_attr_attrgroup_relation -- 属性分组关联表
```

#### Elasticsearch集成
```java
ProductEsModel          // ES商品模型
├── skuId              // SKU ID
├── skuTitle           // 标题(ik_max_word分词)
├── price              // 价格
├── hasStock           // 库存状态
├── brandId/brandName  // 品牌
├── categoryId/Name    // 分类
└── attrs[]            // 嵌套属性

SearchService           // 搜索服务
├── productUp()        // 商品上架到ES
├── productDown()      // 商品下架
└── search()           // 多维度搜索
    ├── 关键字搜索
    ├── 分类/品牌/价格筛选
    ├── 属性筛选
    └── 排序(综合/销量/价格/新品/热度)
```

### 2.5 Inventory库存服务

#### 数据库设计（7张表）
```sql
wms_ware_sku            -- 库存主表(stock,stock_locked,version乐观锁)
wms_ware_info           -- 仓库信息表
wms_ware_order_task     -- 库存工作单(锁定任务)
wms_ware_order_task_detail -- 工作单详情
wms_ware_log            -- 库存流水记录
wms_purchase            -- 采购单表
wms_purchase_detail     -- 采购需求表
```

#### 核心技术特性
```java
// 三层库存模型
stock         - 实际库存
stock_locked  - 锁定库存  
available     - 可用库存(stock - stock_locked)

// 防超卖机制
1. Redisson分布式锁
2. Version乐观锁
3. 数据库层检查: WHERE stock - stock_locked >= quantity

// 库存流程
下单 → lockStock() → 支付成功 → deductStock()
                   ↘ 支付失败/超时 → unlockStock()

// 自动释放
RocketMQ延迟消息(30分钟) + 定时任务扫描
```

### 2.6 Cart购物车服务（最新完成）

#### 技术方案
```java
// 存储方案
纯Redis存储: Hash结构
Key: cart:userId 或 cart:userKey
Field: skuId
Value: CartItem的JSON

// 身份识别
登录用户: userId
未登录用户: Cookie中的userKey(30天有效)
登录时自动合并

// 核心功能
├── 添加商品(重复商品累加数量)
├── 修改数量
├── 删除商品
├── 选中/取消选中
├── 清空购物车
├── 价格同步(结算前刷新)
└── 购物车合并(登录时)
```

## 三、关键技术决策记录

### 3.1 为什么不单独做Auth服务？
- 认证本质是用户领域的一部分
- 避免循环依赖和过度拆分
- 简化服务调用链路

### 3.2 为什么用JWT而不是Session？
- 无状态，支持水平扩展
- 减少Redis查询压力
- 适合前后端分离架构

### 3.3 为什么库存用DB而不是Redis？
- 数据准确性要求高
- Redis宕机风险
- DB+乐观锁性能足够

### 3.4 为什么购物车用Redis而不是DB？
- 购物车数据允许丢失
- 访问频率高，性能要求高
- 结构简单，适合KV存储

## 四、服务调用关系

```
用户下单流程:
Cart → Product(获取商品信息)
     ↓
Order → Inventory(锁定库存)
      → Cart(清空购物车)
      ↓
Payment → Order(更新订单状态)
        → Inventory(扣减库存)
```

## 五、核心配置汇总

### 5.1 端口分配
```
8080 - Gateway网关
8001 - User用户服务
8002 - Product商品服务
8004 - Inventory库存服务
8005 - Cart购物车服务
8006 - Order订单服务[待开发]
8007 - Payment支付服务[待开发]
8008 - Message消息服务[待开发]
```

### 5.2 Redis数据库分配
```
db0 - 通用缓存
db1 - 购物车数据
db2 - Token黑名单
db3 - 分布式锁
```

### 5.3 关键配置项
```yaml
JWT:
  access-token-expire: 900    # 15分钟
  refresh-token-expire: 604800 # 7天
  
库存:
  lock-expire-time: 30        # 锁定30分钟
  
购物车:
  max-items: 200              # 最大200件商品
  expire-days: 30             # 30天过期
```

## 六、待开发模块规划

### 6.1 Order订单服务（Day 10-12）
```
核心功能:
- 订单确认页(获取购物车选中商品)
- 创建订单(调用库存锁定)
- 订单支付(更新状态)
- 订单列表/详情
- 订单取消(释放库存)
- 订单超时自动取消
- 订单状态机(待支付→已支付→已发货→已完成)
```

### 6.2 Payment支付服务（Day 13）
```
核心功能:
- 创建支付单
- 支付宝/微信支付(模拟)
- 支付回调处理
- 支付状态同步
- 退款处理
```

### 6.3 Message消息服务（Day 14）
```
核心功能:
- 订单消息通知
- 库存预警通知
- 营销活动通知
- 站内信管理
```

## 七、项目亮点总结

### 7.1 架构设计
- **DDD领域驱动**: 清晰的服务边界
- **防腐层设计**: DTO/VO/Entity分离
- **统一规范**: 响应格式/异常处理/日志格式

### 7.2 技术实现
- **高并发库存**: 三层防护防止超卖
- **分布式事务**: 最终一致性方案
- **性能优化**: 多级缓存/批量操作/异步处理

### 7.3 工程实践
- **代码规范**: Lombok/统一注释/RESTful
- **测试覆盖**: 单元测试/接口测试
- **运维友好**: 健康检查/日志追踪/监控指标

## 八、常见问题与解决方案

### Q1: 如何处理分布式事务？
使用最终一致性：可靠消息+补偿机制

### Q2: 如何防止重复下单？
订单号幂等性校验 + 分布式锁

### Q3: 如何处理高并发？
限流+熔断+降级+缓存+异步

### Q4: 如何保证数据一致性？
主从延迟：强制读主库
缓存不一致：延迟双删

## 九、部署运维

### 9.1 启动顺序
1. Nacos → 2. Redis → 3. MySQL → 4. ES → 5. RocketMQ
6. Gateway → 7. User → 8. Product → 9. Inventory → 10. Cart

### 9.2 健康检查
所有服务暴露 /actuator/health 端点

### 9.3 配置中心
使用Nacos Config，支持热更新

## 十、性能指标

- 网关QPS: 10000+
- 商品查询: < 50ms
- 购物车操作: < 20ms  
- 库存锁定: < 100ms
- 订单创建: < 500ms

## 十一、代码统计

```
总代码行数: ~15000行
测试代码: ~3000行
配置文件: ~2000行
SQL脚本: ~1000行
文档: ~5000行
```

## 十二、技术栈版本

```xml
Spring Boot: 3.2.0
Spring Cloud: 2023.0.0
Spring Cloud Alibaba: 2022.0.0.0
MySQL: 8.0
Redis: 7.0
Elasticsearch: 8.11
RocketMQ: 5.0
JDK: 17
```

---
**此文档为项目完整档案，包含所有已开发内容的详细信息，可作为新会话的完整参考。**