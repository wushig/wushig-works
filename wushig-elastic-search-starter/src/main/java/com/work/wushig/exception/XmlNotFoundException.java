package com.work.wushig.exception;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-12 13:22
 * @projectName: downloadUtils
 * @Description:
 */
public class XmlNotFoundException extends RuntimeException{

    public XmlNotFoundException() {
        super();
    }

    public XmlNotFoundException(String message) {
        super(message);
    }

    public XmlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlNotFoundException(Throwable cause) {
        super(cause);
    }

    protected XmlNotFoundException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
