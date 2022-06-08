package com.work.wushig.domain;

import com.work.wushig.enums.DATA_SOURCE_ENUM;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 12:48
 * @projectName: downloadUtils
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Wither
@Builder
public class LogEntity {

    private Object result;

    private String methodLongName;

    private String methodShortName;

    private long executionTime;

    private String shortException;

    private String args;

    private String prettyInfo;

    private DATA_SOURCE_ENUM data_source_enum;

    private String recordClassInfo;
}
