package com.cying.lightorm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * Created by Cying on 17/3/30.
 *
 * @see LightORM#where(Class)
 */
public class Query<T> {


    final TableQuery<T> query;

    final BaseDao<T> dao;
    final BaseDao.MetaData metaData;

    Query(BaseDao<T> dao) {
        this.dao = dao;
        this.query = new TableQuery<>(dao);
        metaData = dao.getMetaData();
    }

    public Query<T> beginGroup() {
        query.beginGroup();
        return this;
    }

    public Query<T> endGroup() {
        query.endGroup();
        return this;
    }

    public Query<T> or() {
        query.or();
        return this;
    }


    private void checkColumn(String columnName, BaseDao.FieldType... types) {
        if (!dao.isColumnValid(columnName, types)) {
            throw new IllegalArgumentException("数据库'" + metaData.getRealDatabaseName() + "'的表'" + metaData.getTableName() + "'不存在列'" + columnName + "'或该列数据类型不匹配");
        }
    }

    private void checkAndAddDelimiter(String columnName, BaseDao.FieldType... types) {
        checkColumn(columnName, types);
        query.addDelimiter();
    }

    public Query<T> isNull(@NonNull String columnName) {
        checkAndAddDelimiter(columnName);
        query.isNull(columnName);
        return this;
    }

    public Query<T> isNotNull(@NonNull String columnName) {
        checkAndAddDelimiter(columnName);
        query.isNotNull(columnName);
        return this;
    }


    public Query<T> equalTo(@NonNull String columnName, Boolean value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.BOOLEAN);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, Long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, Integer value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, Short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, Byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, Double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, Float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, Date date) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.equalTo(columnName, date);
        return this;
    }

    public Query<T> equalTo(@NonNull String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Boolean value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.BOOLEAN);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Integer value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, Date date) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.notEqualTo(columnName, date);
        return this;
    }

    public Query<T> notEqualTo(@NonNull String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> greaterThan(@NonNull String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(@NonNull String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(@NonNull String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(@NonNull String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(@NonNull String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(@NonNull String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(@NonNull String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.greaterThan(columnName, value);
        return this;
    }


    public Query<T> lessThan(@NonNull String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(@NonNull String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(@NonNull String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(@NonNull String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(@NonNull String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(@NonNull String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(@NonNull String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.lessThan(columnName, value);
        return this;
    }


    public Query<T> lessOrEqual(@NonNull String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(@NonNull String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(@NonNull String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(@NonNull String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(@NonNull String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(@NonNull String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(@NonNull String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.lessOrEqual(columnName, value);
        return this;
    }


    public Query<T> greaterOrEqual(@NonNull String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(@NonNull String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(@NonNull String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(@NonNull String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(@NonNull String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(@NonNull String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(@NonNull String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> between(@NonNull String columnName, int value1, int value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(@NonNull String columnName, long value1, long value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(@NonNull String columnName, short value1, short value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(@NonNull String columnName, byte value1, byte value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(@NonNull String columnName, float value1, float value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(@NonNull String columnName, double value1, double value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(@NonNull String columnName, Date value1, Date value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> like(@NonNull String columnName, String value, boolean caseSensitive) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.like(columnName, value, caseSensitive);
        return this;
    }

    public Query<T> like(@NonNull String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.like(columnName, value, true);
        return this;
    }

    public Query<T> contains(@NonNull String columnName, String value, boolean caseSensitive) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.contains(columnName, value, caseSensitive);
        return this;
    }

    public Query<T> contains(@NonNull String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.contains(columnName, value, true);
        return this;
    }

    public Query<T> isEmpty(@NonNull String columnName) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.isEmpty(columnName);
        return this;
    }


    public Query<T> isNotEmpty(@NonNull String columnName) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.isNotEmpty(columnName);
        return this;
    }

    /**
     * 清除所有查询条件
     */
    public Query<T> reset() {
        query.clearSql();
        return this;
    }

    public boolean exists() {
        return query.exists();
    }

    public long count() {
        return query.count();
    }

    @Nullable
    public Date minDate(@NonNull String columnName) {
        checkColumn(columnName, BaseDao.FieldType.DATE);
        return query.funQueryDate(TableQuery.FUNCTION_MIN, columnName);
    }

    @NonNull
    public Number min(@NonNull String columnName) {
        BaseDao.FieldType fieldType = dao.getFieldType(columnName);
        switch (fieldType) {
            case INTEGER:
                return query.funQueryInt(TableQuery.FUNCTION_MIN, columnName);

            case FLOAT:
            case DOUBLE:
                return query.funQueryDouble(TableQuery.FUNCTION_MIN, columnName);

            default: {
                throw new IllegalArgumentException("列" + columnName + "不是数值类型");
            }
        }
    }

    @Nullable
    public Date maxDate(@NonNull String columnName) {
        checkColumn(columnName, BaseDao.FieldType.DATE);
        return query.funQueryDate(TableQuery.FUNCTION_MAX, columnName);
    }

    @NonNull
    public Number max(@NonNull String columnName) {
        BaseDao.FieldType fieldType = dao.getFieldType(columnName);
        switch (fieldType) {
            case INTEGER:
                return query.funQueryInt(TableQuery.FUNCTION_MAX, columnName);

            case FLOAT:
            case DOUBLE:
                return query.funQueryDouble(TableQuery.FUNCTION_MAX, columnName);

            default: {
                throw new IllegalArgumentException("列" + columnName + "不是数值类型");
            }
        }
    }

    @NonNull
    public Number average(@NonNull String columnName) {
        BaseDao.FieldType fieldType = dao.getFieldType(columnName);
        switch (fieldType) {
            case INTEGER:
                return query.funQueryInt(TableQuery.FUNCTION_AVG, columnName);

            case FLOAT:
            case DOUBLE:
                return query.funQueryDouble(TableQuery.FUNCTION_AVG, columnName);

            default: {
                throw new IllegalArgumentException("列" + columnName + "不是数值类型");
            }
        }
    }

    @NonNull
    public Number sum(@NonNull String columnName) {
        BaseDao.FieldType fieldType = dao.getFieldType(columnName);
        switch (fieldType) {
            case INTEGER:
                return query.funQueryInt(TableQuery.FUNCTION_SUM, columnName);

            case FLOAT:
            case DOUBLE:
                return query.funQueryDouble(TableQuery.FUNCTION_SUM, columnName);

            default: {
                throw new IllegalArgumentException("列" + columnName + "不是数值类型");
            }
        }
    }

    @NonNull
    public List<T> findAll() {
        return query.findAll();
    }

    @NonNull
    public List<T> findAll(@NonNull Sort sort) {
        return query.findAll(sort);
    }

    @NonNull
    public List<T> findAll(String sql, String... selectionArgs) {
        return query.findAll(sql, selectionArgs);
    }

    @Nullable
    public T findFirst() {
        return query.findFirst();
    }

    @Nullable
    public T findLast() {
        return query.findLast();
    }

}
