package com.work.wushig.uuid;

import java.util.UUID;

public class WUUIDUtils {

    /**
     * 返回一个不带中划线的UUID
     * @return
     */
    public static String getUUIDWithoutSymbol(){
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 返回一个UUID
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
