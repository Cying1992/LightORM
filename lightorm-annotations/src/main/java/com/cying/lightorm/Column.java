package com.cying.lightorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Cying on 17/2/28.
 * email:chengying@souche.com
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Column {

    String value() default "";

    boolean unique() default false;

    boolean notNull() default false;
}
