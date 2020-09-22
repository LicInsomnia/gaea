package com.tincery.gaea.core.base.plugin.csv;

import java.util.ArrayList;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SplitUtils {


    public static final char DEFAULT_SEPARATOR = 0x07;

    /****
     * 效果等效于JDK的 string.split
     * 但是效率高于JDK  因为JDK是用正则   此方法简单易懂
     **/
    public static String[] split(String str, char sep){
        char[] chars = str.toCharArray();
        ArrayList<String> list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (char aChar : chars) {
            if(aChar == sep){
                list.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }else{
                stringBuilder.append(aChar);
            }
        }
        if(chars[chars.length-1]==sep){
            list.add("");
        }
        return list.toArray(new String[]{});
    }

    public static String[] split(String str){
        return split(str,DEFAULT_SEPARATOR);
    }

}
