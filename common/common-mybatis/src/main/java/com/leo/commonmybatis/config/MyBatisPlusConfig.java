package com.leo.commonmybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.leo.commonmybatis.handler.TenantContextHolder;
import com.leo.commonsecurity.context.AuthenticationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class MyBatisPlusConfig {

    /**
     * MyBatis Plus插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(100L); // 单页最大限制
        paginationInnerInterceptor.setOverflow(true); // 溢出总页数后是否进行处理
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        // 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());


        //多租户插件
//        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor());
        
        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                log.debug("开始插入填充...");
                LocalDateTime now = LocalDateTime.now();
                
                // 创建时间
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                // 更新时间
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                
                // 创建人
                String username = AuthenticationContext.getUsername();
                if (username != null) {
                    this.strictInsertFill(metaObject, "createBy", String.class, username);
                    this.strictInsertFill(metaObject, "updateBy", String.class, username);
                }
                
                // 删除标记
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
                // 版本号
                this.strictInsertFill(metaObject, "version", Integer.class, 1);
                this.strictInsertFill(metaObject, "tenantId", Long.class, TenantContextHolder.getTenantId());

            }

            @Override
            public void updateFill(MetaObject metaObject) {
                log.debug("开始更新填充...");
                // 更新时间
//                this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
//                this.strictUpdateFill(metaObject, "updateTime",LocalDateTime.class, LocalDateTime.now());
//                this.strictUpdateFill(metaObject, "deleted", Integer.class, 1);
                setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
                
                // 更新人
                String username = AuthenticationContext.getUsername();
                if (username != null) {
//                    this.strictUpdateFill(metaObject, "updateBy", String.class, username);
                    setFieldValByName("updateBy", username, metaObject);
                }
            }
        };
    }
}