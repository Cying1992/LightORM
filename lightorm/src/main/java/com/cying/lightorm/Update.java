package com.cying.lightorm;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cying on 17/4/12.
 * 根据条件更新数据表
 */
public class Update<T> {

    private final BaseDao<T> dao;

    private final Map<String, Object> columnValueMap;

    private final BaseDao.MetaData metaData;

    private TableQuery<T> tableQuery;

    Update(BaseDao<T> dao) {
        this.dao = dao;
        this.columnValueMap = new LinkedHashMap<>();
        this.metaData = dao.getMetaData();
    }

    private void putColumnValue(String columnName, Object value) {
        if (columnValueMap.containsKey(columnName)) {
            throw new IllegalStateException("已经set了列" + columnName + "的值");
        }
        columnValueMap.put(columnName, value);
    }

    private Update<T> putColumnLongValue(String columnName, Number value) {
        dao.checkColumn(columnName, BaseDao.FieldType.INTEGER);
        putColumnValue(columnName, value);
        return this;
    }

    public Update<T> set(@NonNull String columnName, Boolean value) {
        dao.checkColumn(columnName, BaseDao.FieldType.BOOLEAN);
        putColumnValue(columnName, value == null ? null : (value ? 1 : 0));
        return this;
    }

    public Update<T> set(@NonNull String columnName, Long value) {
        return putColumnLongValue(columnName, value);
    }

    public Update<T> set(@NonNull String columnName, Integer value) {
        return putColumnLongValue(columnName, value);
    }

    public Update<T> set(@NonNull String columnName, Short value) {
        return putColumnLongValue(columnName, value);
    }

    public Update<T> set(@NonNull String columnName, Byte value) {
        return putColumnLongValue(columnName, value);
    }

    public Update<T> set(@NonNull String columnName, Date value) {
        dao.checkColumn(columnName, BaseDao.FieldType.DATE);
        putColumnValue(columnName, value == null ? null : value.getTime());
        return this;
    }

    public Update<T> set(@NonNull String columnName, String value) {
        dao.checkColumn(columnName, BaseDao.FieldType.STRING);
        putColumnValue(columnName, value);
        return this;
    }

    public Update<T> set(@NonNull String columnName, Double value) {
        dao.checkColumn(columnName, BaseDao.FieldType.DOUBLE);
        putColumnValue(columnName, value);
        return this;
    }

    public Update<T> set(@NonNull String columnName, Float value) {
        dao.checkColumn(columnName, BaseDao.FieldType.DOUBLE);
        putColumnValue(columnName, value);
        return this;
    }

    /**
     * 设置查询条件
     *
     * @param query 查询条件
     * @return
     */
    public Update<T> where(@NonNull Query<T> query) {
        if (this.tableQuery != null) {
            throw new IllegalArgumentException("只能调用一次where");
        }
        query.checkValid();
        TableQuery<T> tableQuery = query.query;
        this.tableQuery = tableQuery;
        return this;
    }

    /**
     * 设置查询条件
     *
     * @param condition
     * @return
     */
    public Update<T> where(@NonNull Condition<T> condition) {
        Query<T> query = new Query<>(dao);
        this.tableQuery = query.query;
        condition.where(query);
        query.checkValid();
        return this;
    }

    private String buildSql() {
        StringBuilder query = new StringBuilder(120);
        query.append("UPDATE ").append(metaData.getTableName())
                .append(" SET ");

        List<String> setList = new ArrayList<>();
        for (String column : columnValueMap.keySet()) {
            setList.add(column + " = ?");
        }
        query.append(TextUtils.join(",", setList));

        String selection = tableQuery == null ? null : tableQuery.getSelection();
        if (!TextUtils.isEmpty(selection)) {
            query.append(" WHERE ").append(selection);
        }
        return query.toString();
    }

    /**
     * 重置更新条件
     */
    public void reset() {
        this.columnValueMap.clear();
        if (this.tableQuery != null) {
            //清除所有查询条件
            this.tableQuery.clearSql();
            this.tableQuery = null;
        }
    }

    /**
     * 执行更新操作
     */
    public void execute() {
        if (columnValueMap.isEmpty()) {
            throw new IllegalArgumentException("更新时须至少set一列的值");
        }
        String sql = buildSql();
        LightORM.debug("生成的更新SQL语句为：" + sql);

        List<Object> bindArgs = new ArrayList<>();
        bindArgs.addAll(columnValueMap.values());
        if (this.tableQuery != null) {
            String[] args = this.tableQuery.getSelectionArgs();
            if (args != null) {
                Collections.addAll(bindArgs, args);
            }
        }
        dao.openDatabase().execSQL(sql, bindArgs.toArray());
        dao.closeDatabase();
        reset();
    }
}
