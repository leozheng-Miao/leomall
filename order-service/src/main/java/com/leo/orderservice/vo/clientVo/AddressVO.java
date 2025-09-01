package com.leo.orderservice.vo.clientVo;

/**
 * 收货地址VO
 */

public class AddressVO {
    private Long id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String region;
    private String detailAddress;
    private String postCode;
    private Boolean defaultStatus;
    
    public String getFullAddress() {
        return province + city + region + detailAddress;
    }
}