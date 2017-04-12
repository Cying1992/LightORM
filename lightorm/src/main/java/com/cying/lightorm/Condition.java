package com.cying.lightorm;

import android.support.annotation.NonNull;

/**
 * Created by Cying on 17/4/12.
 */
public interface Condition<T> {

    void where(@NonNull Query<T> query);
}
