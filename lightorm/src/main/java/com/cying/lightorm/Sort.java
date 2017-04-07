package com.cying.lightorm;

import android.support.annotation.NonNull;

/**
 * Created by Cying on 17/4/1.
 * <p>
 * 用来排序或者分页
 */
public class Sort {

    private static final String ORDER_BY_DESC = " DESC ";
    private static final String ORDER_BY_ASC = " ASC ";

    public static Sort create() {
        return new Sort();
    }

    private String limit;
    private String orderBy, groupBy, having;

    Sort() {

    }

    <T> TableQuery<T> into(TableQuery<T> query) {
        query.having = this.having;
        query.groupBy = this.groupBy;
        query.orderBy = this.orderBy;
        query.limit = this.limit;
        return query;
    }

    public Sort limit(int count, int offset) {
        limit = count + " OFFSET " + offset;
        return this;
    }

    public Sort limit(int count) {
        limit = "" + count;
        return this;
    }

    public Sort groupBy(String groupBy, String having) {
        this.groupBy = groupBy;
        this.having = having;
        return this;
    }

    public Sort groupBy(String groupBy) {
        return this.groupBy(groupBy, null);
    }

    public Sort orderBy(boolean isDesc, @NonNull String columnName, String... columnNames) {
        StringBuilder builder = new StringBuilder(columnName);
        if (columnNames != null) {
            for (String name : columnNames) {
                builder.append(",").append(name);
            }
        }
        this.orderBy = builder + (isDesc ? ORDER_BY_DESC : ORDER_BY_ASC);
        return this;
    }


}
