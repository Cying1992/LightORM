package com.cying.lightorm;

import android.support.annotation.NonNull;

/**
 * Created by Cying on 17/4/1.
 * <p>
 * 对实体进行处理的接口
 */
public interface EntityProcessor<T> {

    @NonNull
    void process(@NonNull T entity);
}
