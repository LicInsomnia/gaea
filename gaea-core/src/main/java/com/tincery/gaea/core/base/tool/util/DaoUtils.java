package com.tincery.gaea.core.base.tool.util;

import com.tincery.starter.base.model.SimpleBaseDO;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DaoUtils {

    /***
     * 通过对象获取对象字段的update对象 不忽略null的内容 覆盖
     * @author gxz
     * @date 2019/11/6
     * @param t
     * @return org.springframework.data.mongodb.core.query.Update
     **/
    public static Update beanToUpdateCovered(Object t) {
        Class<?> beanClass = t.getClass();
        List<Field> fieldList = new ArrayList<>();

        while (beanClass != SimpleBaseDO.class){
            fieldList.addAll(Arrays.asList(beanClass.getDeclaredFields()));
            beanClass = beanClass.getSuperclass();

        }
        Update update = new Update();
        for (Field declaredField : fieldList) {
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }
            String fieldName = declaredField.getName();
            if (Objects.equals(fieldName, "_id")) {
                continue;
            }
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            Object fieldValue = null;
            try {
                fieldValue = declaredField.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Object updateValue = type.cast(fieldValue);
            update.set(fieldName, updateValue);
        }
        return update;
    }

}
