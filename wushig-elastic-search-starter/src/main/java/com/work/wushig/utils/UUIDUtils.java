package com.work.wushig.utils;

import java.util.UUID;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 21:10
 * @projectName: downloadUtils
 * @Description:
 */
public class UUIDUtils {

    public static String generateUUIDForIDs(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
