# 库存服务 (Inventory Service)

## 📋 服务概述

库存服务是商城系统的核心服务之一，负责管理商品库存的查询、锁定、解锁、扣减等操作。采用三层库存模型（实际库存、锁定库存、可用库存），通过分布式锁和数据库乐观锁双重机制防止超卖。

- **端口**: 8004
- **服务名**: inventory-service
- **数据库**: mall_wms

## 🏗️ 项目结构

```
inventory-service/
├── src/main/java/com/leo/inventoryservice/
│   ├── constants/           # 常量定义
│   │   └── InventoryConstants.java
│   ├── controller/          # 控制器层
│   │   └── InventoryController.java
│   ├── dto/                 # 数据传输对象
│   │   ├── StockLockDTO.java
│   │   ├── StockQueryDTO.java
│   │   └── StockUpdateDTO.java
│   ├── entity/              # 实体类
│   │   ├── WareSku.java           # 库存主表
│   │   ├── WareInfo.java          # 仓库信息
│   │   ├── WareOrderTask.java     # 工作单
│   │   ├── WareOrderTaskDetail.java # 工作单详情
│   │   └── WareLog.java           # 库存流水
│   ├── feign/               # Feign客户端
│   │   └── InventoryFeignClient.java
│   ├── mapper/              # MyBatis Mapper
│   │   ├── WareSkuMapper.java
│   │   ├── WareInfoMapper.java
│   │   ├── WareOrderTaskMapper.java
│   │   ├── WareOrderTaskDetailMapper.java
│   │   └── WareLogMapper.java
│   ├── service/             # 服务层
│   │   ├── InventoryService.java
│   │   └── impl/
│   │       └── InventoryServiceImpl.java
│   ├── task/                # 定时任务
│   │   └── InventoryScheduledTask.java
│   ├── vo/                  # 视图对象
│   │   ├── StockVO.java
│   │   └── StockLockResultVO.java
│   └── InventoryServiceApplication.java  # 启动类
│
├── src/main/resources/
│   ├── application.yml      # 配置文件
│   ├── mapper/              # MyBatis XML映射文件
│   └── sql/
│       └── inventory.sql    # 数据库脚本
└── pom.xml
```

## 🚀 核心功能

### 1. 库存查询
- 批量查询SKU库存信息
- 判断SKU是否有库存
- 分页查询库存列表
- 库存预警查询

### 2. 库存锁定
- **分布式锁**: 使用Redisson防止并发锁定
- **乐观锁**: 数据库version字段防止超卖
- **三层检查**: WHERE stock - stock_locked >= quantity
- **自动解锁**: 30分钟未支付自动释放

### 3. 库存操作
- **锁定库存**: 下单时锁定商品库存
- **解锁库存**: 订单取消或支付超时
- **扣减库存**: 支付成功后实际扣减
- **增加库存**: 采购入库、退货入库

### 4. 库存管理
- 库存调拨
- 库存盘点
- 库存预警
- 库存统计

## 💾 数据库设计

### 核心表结构

1. **wms_ware_sku** - 库存主表
   - stock: 实际库存
   - stock_locked: 锁定库存
   - available: 可用库存（计算值）

2. **wms_ware_order_task** - 库存工作单
   - 跟踪每个订单的库存锁定状态

3. **wms_ware_order_task_detail** - 工作单详情
   - 记录每个SKU的锁定详情

4. **wms_ware_log** - 库存流水
   - 记录所有库存变动历史

## 🔧 技术特性

### 防超卖机制
```java
// 1. Redisson分布式锁
RLock lock = redissonClient.getLock(lockKey);

// 2. 数据库乐观锁
UPDATE wms_ware_sku SET version = version + 1 WHERE version = ?

// 3. SQL层面检查
WHERE stock - stock_locked >= quantity
```

### 库存流程
```
下单 → lockStock() → 支付成功 → deductStock()
                   ↘ 支付失败/超时 → unlockStock()
```

### 自动释放机制
- RocketMQ延迟消息（30分钟）
- 定时任务扫描（每5分钟）

## 📡 接口列表

### 对外接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询库存 | GET | /api/inventory/stock/list | 批量查询SKU库存 |
| 是否有库存 | POST | /api/inventory/stock/has-stock | 批量判断库存 |
| 锁定库存 | POST | /api/inventory/stock/lock | 锁定订单库存 |
| 解锁库存 | POST | /api/inventory/stock/unlock/{orderSn} | 解锁订单库存 |
| 扣减库存 | POST | /api/inventory/stock/deduct/{orderSn} | 扣减订单库存 |

### 管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 更新库存 | POST | /api/inventory/stock/update | 更新库存（入库） |
| 批量更新 | POST | /api/inventory/stock/batch-update | 批量更新库存 |
| 分页查询 | GET | /api/inventory/stock/page | 分页查询库存 |
| 库存预警 | GET | /api/inventory/stock/warning | 获取预警列表 |

### 内部接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 自动解锁 | POST | /api/inventory/stock/auto-unlock | 定时任务调用 |
| 同步ES | POST | /api/inventory/stock/sync-es | 同步库存到ES |

## ⚙️ 配置说明

### 核心配置
```yaml
inventory:
  lock-timeout: 30        # 库存锁定超时时间（分钟）
  warning-threshold: 10   # 库存预警阈值
  batch-max-size: 1000   # 批量操作最大数量
  cache-expire: 300      # 缓存过期时间（秒）
```

### 定时任务
- **自动解锁**: 每5分钟执行，解锁超时未支付的订单
- **库存预警**: 每天凌晨2点执行，检查低库存商品
- **库存统计**: 每天凌晨3点执行，生成统计报表

## 🔌 服务依赖

- **User Service**: 获取用户信息（可选）
- **Product Service**: 获取商品信息
- **Order Service**: 订单状态同步
- **Message Service**: 发送库存预警通知

## 📊 监控指标

- 库存锁定成功率
- 库存解锁次数
- 超卖预警次数
- 库存周转率
- API响应时间

## 🚨 异常处理

1. **库存不足**: 返回具体的缺货SKU信息
2. **锁定超时**: 自动回滚已锁定的库存
3. **并发冲突**: 重试机制（最多3次）
4. **数据不一致**: 记录异常日志，人工介入

## 📝 开发规范

1. **实体类**: 所有实体继承BaseEntity
2. **响应格式**: 统一使用R<T>包装
3. **异常处理**: 抛出BizException
4. **日志规范**: 关键操作记录详细日志
5. **事务管理**: 使用@Transactional注解

## 🔄 后续优化

1. **性能优化**
   - 增加Redis缓存层
   - 批量操作优化
   - 读写分离

2. **功能增强**
   - 多仓库智能分配
   - 库存预测算法
   - 自动补货机制

3. **监控完善**
   - 增加Prometheus指标
   - 完善链路追踪
   - 异常告警机制

## 👥 开发团队

- 作者: Miao Zheng
- 创建时间: 2025-02-03
- 版本: 1.0.0

## 📄 License

Copyright © 2025 Leo Mall System. All rights reserved.