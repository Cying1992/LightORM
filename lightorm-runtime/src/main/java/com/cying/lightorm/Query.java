package com.cying.lightorm;

import java.util.Date;
import java.util.List;

/**
 * Created by Cying on 17/3/30.

 */
public class Query<T> {


    private final TableQuery<T> query;

    private final BaseDao<T> dao;

    Query(BaseDao<T> dao) {
        this.dao = dao;
        this.query = new TableQuery<>(dao);
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
            throw new IllegalArgumentException("列名不存在或列类型不正确");
        }
    }

    private void checkAndAddDelimiter(String columnName, BaseDao.FieldType... types) {
        checkColumn(columnName, types);
        query.addDelimiter();
    }

    public Query<T> isNull(String columnName) {
        checkAndAddDelimiter(columnName);
        query.isNull(columnName);
        return this;
    }

    public Query<T> isNotNull(String columnName) {
        checkAndAddDelimiter(columnName);
        query.isNotNull(columnName);
        return this;
    }


    public Query<T> equalTo(String columnName, Boolean value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.BOOLEAN);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(String columnName, Long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(String columnName, Integer value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(String columnName, Short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(String columnName, Byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(String columnName, Double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(String columnName, Float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> equalTo(String columnName, Date date) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.equalTo(columnName, date);
        return this;
    }

    public Query<T> equalTo(String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.equalTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Boolean value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.BOOLEAN);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Integer value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> notEqualTo(String columnName, Date date) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.notEqualTo(columnName, date);
        return this;
    }

    public Query<T> notEqualTo(String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.notEqualTo(columnName, value);
        return this;
    }

    public Query<T> greaterThan(String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.greaterThan(columnName, value);
        return this;
    }

    public Query<T> greaterThan(String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.greaterThan(columnName, value);
        return this;
    }


    public Query<T> lessThan(String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.lessThan(columnName, value);
        return this;
    }

    public Query<T> lessThan(String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.lessThan(columnName, value);
        return this;
    }


    public Query<T> lessOrEqual(String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.lessOrEqual(columnName, value);
        return this;
    }

    public Query<T> lessOrEqual(String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.lessOrEqual(columnName, value);
        return this;
    }


    public Query<T> greaterOrEqual(String columnName, int value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(String columnName, long value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(String columnName, short value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(String columnName, byte value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(String columnName, float value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(String columnName, double value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> greaterOrEqual(String columnName, Date value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.greaterOrEqual(columnName, value);
        return this;
    }

    public Query<T> between(String columnName, int value1, int value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(String columnName, long value1, long value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(String columnName, short value1, short value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(String columnName, byte value1, byte value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.INTEGER);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(String columnName, float value1, float value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.FLOAT);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(String columnName, double value1, double value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DOUBLE);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> between(String columnName, Date value1, Date value2) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.DATE);
        query.between(columnName, value1, value2);
        return this;
    }

    public Query<T> like(String columnName, String value, boolean caseSensitive) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.like(columnName, value, caseSensitive);
        return this;
    }

    public Query<T> like(String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.like(columnName, value, true);
        return this;
    }

    public Query<T> contains(String columnName, String value, boolean caseSensitive) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.contains(columnName, value, caseSensitive);
        return this;
    }

    public Query<T> contains(String columnName, String value) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.contains(columnName, value, true);
        return this;
    }

    public Query<T> isEmpty(String columnName) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.isEmpty(columnName);
        return this;
    }


    public Query<T> isNotEmpty(String columnName) {
        checkAndAddDelimiter(columnName, BaseDao.FieldType.STRING);
        query.isNotEmpty(columnName);
        return this;
    }

    /**
     * 清除所有查询条件
     */
    public void reset() {
        query.clearSql();
    }

    public boolean exists() {
        return query.exists();
    }

    public long count() {
        return query.count();
    }

    public Date minDate(String columnName) {
        checkColumn(columnName, BaseDao.FieldType.DATE);
        return query.funQueryDate(TableQuery.FUNCTION_MIN, columnName);
    }

    public Number min(String columnName) {
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

    public Date maxDate(String columnName) {
        checkColumn(columnName, BaseDao.FieldType.DATE);
        return query.funQueryDate(TableQuery.FUNCTION_MAX, columnName);
    }

    public Number max(String columnName) {
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

    public Number average(String columnName) {
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

    public Number sum(String columnName) {
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

    public List<T> findAll() {
        return query.findAll();
    }

    public List<T> findAll(Sort sort) {
        return query.findAll(sort);
    }


    public T findFirst() {
        return query.findFirst();
    }

    public T findLast() {
        return query.findLast();
    }

}
