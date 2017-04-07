package com.cying.lightorm;

import android.support.annotation.NonNull;

/**
 * Created by Cying on 17/4/1.
 * <p>
 * 在查询到数据并转换成实体后可实体进行处理的接口
 */
public interface EntityInterceptor<T> {

    @NonNull
    T process(@NonNull T entity);
}
