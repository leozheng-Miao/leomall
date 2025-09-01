package com.leo.orderservice.dto;

import com.leo.commoncore.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * 订单查询DTO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单查询请求")
public class OrderQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    private String orderSn;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭")
    private Integer status;

    @Schema(description = "订单类型：0->正常订单；1->秒杀订单")
    private Integer orderType;

    @Schema(description = "订单来源：0->PC订单；1->app订单")
    private Integer sourceType;

    @Schema(description = "收货人姓名/电话")
    private String receiverKeyword;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
