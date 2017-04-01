package com.cying.lightorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Cying on 17/2/28.
 * <p>
 * 标记{@link Table}注解的类的数据列字段
 * <p>
 * 支持{@code int},{@code short},{@code byte},{@code long},{@code float},{@code double},{@code boolean}
 * 这几种基本类型及它们的包装类型，还支持{@code String},{@code byte[]},{@link java.util.Date}三种类型
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Column {

    /**
     * 设置列的列名，会忽略空字符并转换成小写形式。如果它返回空字符串，则字段名会和属性名的小写形式相同。
     *
     * @return
     */
    String value() default "";

    /**
     * 是否
     * @return
     */
    boolean unique() default false;

    boolean notNull() default false;

}
