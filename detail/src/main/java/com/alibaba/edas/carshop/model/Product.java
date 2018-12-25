package com.alibaba.edas.carshop.model;

import com.perfect.third.integration.api.dto.response.ProDeliveryProductDto;

import java.io.Serializable;
import java.util.Map;

/**
 * 产品扩展类
 * @author 亮亮
 */
public class Product extends ProDeliveryProductDto  implements Serializable {

    private Map<String,Object> map;


    public Product() {}
    public Product(ProDeliveryProductDto productDto) {
        this.setFormat(productDto.getFormat());
        this.setNum(productDto.getNum());
        this.setProductCode(productDto.getProductCode());
        this.setUnit(productDto.getUnit());
        this.setProductName(productDto.getProductName());
    }
    public Map<String, Object> getMap() {
        return map;
    }
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
