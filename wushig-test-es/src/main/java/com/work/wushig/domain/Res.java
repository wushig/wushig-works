package com.work.wushig.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-12 17:25
 * @projectName: downloadUtils
 * @Description:
 */
@Data
public class Res {
    private Date stdExpiredDate;
    private String stdContent;
    private Integer stdType;
    private String stdName;
    private Date stdPublishDate;
    private Integer isCatalog;
    private String stdChapter;
    private Integer sdcNo;
    private String stdNo;
    private Integer stdId;
    private Date tsLabel;
    private Integer sort;
    private Integer stdStatus;
    private Date stdDoDate;
}
