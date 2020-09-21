package com.tincery.gaea.core.base.tool.util;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ObjectUtils {

    private ObjectUtils() {

    }


    /****
     * 参数中有一个为null 就返回false  所有参数全不为null 就返回true
     * 也可以传一个boolean 表达式  表达式为true 视为此项为null
     * @author gxz
     **/
    public static boolean allNotNull(Object... objects){
        for (Object object : objects) {
            if(object == null){
                return false;
            }
            if(object instanceof Boolean && (Boolean) object){
                return false;
            }
        }
        return true;
    }
    /****
     * 参数中存在不为null的值
     **/
    public static boolean haveNotNull(Object... objects){
        for (Object object : objects) {
            if(object !=null){
                return true;
            }
        }
        return false;
    }

}
