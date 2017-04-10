package com.cying.lightorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Cying on 17/2/28.
 * <p>
 * 标记{@link Table}注解的类的主键字段，只能为{@link Long}或{@code long}类型
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Key {

    /**
     * 设置主键的列名，会忽略空字符并转换成小写形式。如果它返回空字符串，则字段名会和属性名的小写形式相同。
     *
     * @return
     */
    String value() default "";
}
