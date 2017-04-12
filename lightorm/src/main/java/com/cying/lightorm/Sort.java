package com.cying.lightorm;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Cying on 17/4/1.
 * <p>
 * 用来对结果集进行处理，比如排序或者分页
 */
public class Sort {

    private static final String ORDER_BY_DESC = " DESC";
    private static final String ORDER_BY_ASC = " ASC";

    public static Sort create() {
        return new Sort();
    }

    private String limit;
    private String having;
    private Map<String, Boolean> orderByMap;
    private Set<String> groupBySet;
    private boolean distinct;

    Sort() {

    }

    <T> TableQuery<T> into(TableQuery<T> query) {
        query.having = this.having;
        query.limit = this.limit;
        query.distinct = this.distinct;
        query.groupBy = generateGroupBy(query);
        query.orderBy = generateOrderBy(query);
        return query;
    }


    private String generateGroupBy(TableQuery query) {
        if (groupBySet == null || groupBySet.isEmpty()) {
            return null;
        }
        for (String columnName : groupBySet) {
            query.checkColumn(columnName);
        }
        return TextUtils.join(",", groupBySet);
    }

    private String generateOrderBy(TableQuery query) {
        if (orderByMap == null || orderByMap.isEmpty()) {
            return null;
        }
        List<String> orderByList = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : orderByMap.entrySet()) {
            String columnName = entry.getKey();
            query.checkColumn(columnName);
            boolean isDesc = entry.getValue();
            orderByList.add(columnName + (isDesc ? ORDER_BY_DESC : ORDER_BY_ASC));
        }
        return TextUtils.join(",", orderByList);
    }

    /**
     * 清除所有条件
     *
     * @return
     */
    public Sort reset() {
        this.limit = this.having = null;
        this.orderByMap = null;
        this.groupBySet = null;
        this.distinct = false;
        return this;
    }

    public Sort distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public Sort limit(int count, int offset) {
        limit = count + " OFFSET " + offset;
        return this;
    }

    public Sort limit(int count) {
        limit = "" + count;
        return this;
    }

    public Sort having(@NonNull String having) {
        this.having = having;
        return this;
    }

    public Sort groupBy(@NonNull String columnName, String... otherColumns) {
        if (groupBySet == null) {
            groupBySet = new LinkedHashSet<>();
        } else if (groupBySet.contains(columnName)) {
            throw new IllegalArgumentException("已经存在列" + columnName + "的group by");
        }
        groupBySet.add(columnName);
        if (otherColumns != null) {
            for (String otherColumn : otherColumns) {
                if (groupBySet.contains(otherColumn)) {
                    throw new IllegalArgumentException("已经存在列" + otherColumn + "的group by");
                }
                groupBySet.add(otherColumn);
            }
        }
        return this;
    }

    public Sort orderBy(boolean isDesc, @NonNull String columnName, String... otherColumns) {
        if (orderByMap == null) {
            orderByMap = new LinkedHashMap<>();
        } else if (orderByMap.containsKey(columnName)) {
            throw new IllegalArgumentException("已经存在列" + columnName + "的order by");
        }
        orderByMap.put(columnName, isDesc);
        if (otherColumns != null) {
            for (String otherColumn : otherColumns) {
                if (orderByMap.containsKey(otherColumn)) {
                    throw new IllegalArgumentException("已经存在列" + otherColumn + "的order by");
                }
                orderByMap.put(otherColumn, isDesc);
            }
        }
        return this;
    }

}
