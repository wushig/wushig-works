package com.work.wushig.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 21:18
 * @projectName: downloadUtils
 * @Description:
 */
@Data
public class Product extends ESEntity{

    private Date createDate;
    private String description;
    private Boolean onSale;
    private Double price;
    private String title;
    private Integer type;

}
