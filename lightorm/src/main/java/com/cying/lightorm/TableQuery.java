package com.cying.lightorm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cying on 17/3/31.
 */
class TableQuery<T> {

    private static final String SECTION_DELIMITER_AND = "AND ";
    private static final String SECTION_DELIMITER_OR = "OR ";

    private static final String PRAGMA_ENABLE_CASE_SENSITIVE_LIKE = "PRAGMA case_sensitive_like=1";
    private static final String PRAGMA_DISABLE_CASE_SENSITIVE_LIKE = "PRAGMA case_sensitive_like=0";

    private static final String ALL_COLUMNS = "*";
    private static final String FUNCTION_COUNT = "COUNT";
    static final String FUNCTION_SUM = "SUM";
    static final String FUNCTION_MAX = "MAX";
    static final String FUNCTION_MIN = "MIN";
    static final String FUNCTION_AVG = "AVG";
    private static final String ZERO = "0";

    private final BaseDao<T> dao;
    String limit;
    String orderBy, groupBy, having;
    boolean distinct;

    private BaseDao.MetaData metaData;
    private StringBuilder selection;
    private String selectWhat = ALL_COLUMNS;

    private boolean isOr = false;
    private boolean inBeginGroup = false;

    private final List<String> selectionArgs = new ArrayList<>();

    private boolean caseSensitive = false;
    private int beginGroupCount = 0;

    TableQuery(BaseDao<T> dao) {
        this.dao = dao;
        this.metaData = dao.getMetaData();
    }

    void checkColumn(String columnName, BaseDao.FieldType... types) {
        if (!dao.isColumnValid(columnName, types)) {
            throw new IllegalArgumentException("数据库'" + metaData.getRealDatabaseName() + "'的表'" + metaData.getTableName() + "'不存在列'" + columnName + "'");
        }
    }

    TableQuery<T> addDelimiter() {

        if (selection == null) {
            selection = new StringBuilder();
        } else {
            if (!inBeginGroup) {
                selection.append(isOr ? SECTION_DELIMITER_OR : SECTION_DELIMITER_AND);
            }
        }
        inBeginGroup = false;
        isOr = false;
        return this;
    }

    TableQuery<T> beginGroup() {
        if (inBeginGroup) {
            throw new IllegalStateException("不能连续调用beginGroup");
        }

        addDelimiter();

        inBeginGroup = true;
        beginGroupCount++;
        selection.append("(");
        return this;
    }

    TableQuery<T> endGroup() {
        if (isOr) {
            throw new IllegalStateException("or后必须有条件");
        }
        if (inBeginGroup) {
            throw new IllegalStateException("beginGroup和endGroup之间必须有条件");
        }
        selection.append(")");
        inBeginGroup = false;
        beginGroupCount--;
        return this;
    }


    TableQuery<T> or() {
        if (inBeginGroup) {
            throw new IllegalStateException("不能在beginGroup之后调用or");
        }
        isOr = true;
        return this;
    }

    TableQuery<T> isNull(String columnName) {
        selection.append(columnName).append(" IS NULL ");
        return this;
    }

    TableQuery<T> isNotNull(String columnName) {
        selection.append(columnName).append(" IS NOT NULL ");
        return this;
    }

    TableQuery<T> equalTo(String columnName, Object value) {
        if (value == null) {
            isNull(columnName);
        } else {
            selection.append(columnName).append(" IS ? ");
            selectionArgs.add(String.valueOf(value));
        }
        return this;
    }

    TableQuery<T> notEqualTo(String columnName, Object value) {
        if (value == null) {
            isNotNull(columnName);
        } else {
            selection.append(columnName).append(" IS NOT ? ");
            selectionArgs.add(String.valueOf(value));
        }
        return this;
    }

    TableQuery<T> notEqualTo(String columnName, Boolean value) {
        if (value == null) {
            isNotNull(columnName);
        } else {
            if (value) {
                equalTo(columnName, ZERO);
            } else {
                notEqualTo(columnName, ZERO);
            }
        }
        return this;
    }

    TableQuery<T> notEqualTo(String columnName, Date value) {
        if (value == null) {
            isNotNull(columnName);
        } else {
            notEqualTo(columnName, value.getTime());
        }
        return this;
    }


    TableQuery<T> equalTo(String columnName, Boolean value) {
        if (value == null) {
            isNull(columnName);
        } else {
            if (value) {
                notEqualTo(columnName, ZERO);
            } else {
                equalTo(columnName, ZERO);
            }
        }
        return this;
    }

    TableQuery<T> equalTo(String columnName, Date value) {
        if (value == null) {
            isNull(columnName);
        } else {
            equalTo(columnName, value.getTime());
        }
        return this;
    }

    TableQuery<T> greaterThan(String columnName, Number value) {
        selection.append(columnName).append(" >? ");
        selectionArgs.add(value + "");
        return this;
    }

    TableQuery<T> greaterThan(String columnName, Date value) {
        return greaterThan(columnName, value.getTime());
    }

    TableQuery<T> greaterOrEqual(String columnName, Number value) {
        selection.append(columnName).append(" >=? ");
        selectionArgs.add(value + "");
        return this;
    }

    TableQuery<T> greaterOrEqual(String columnName, Date value) {
        return greaterOrEqual(columnName, value.getTime());
    }

    TableQuery<T> lessThan(String columnName, Number value) {
        selection.append(columnName).append(" <? ");
        selectionArgs.add(value + "");
        return this;
    }

    TableQuery<T> lessThan(String columnName, Date value) {
        return lessThan(columnName, value.getTime());
    }

    TableQuery<T> lessOrEqual(String columnName, Number value) {
        selection.append(columnName).append(" <=? ");
        selectionArgs.add(value + "");
        return this;
    }

    TableQuery<T> lessOrEqual(String columnName, Date value) {
        return lessOrEqual(columnName, value.getTime());
    }


    TableQuery<T> between(String columnName, Number value1, Number value2) {
        selection.append(columnName).append(" BETWEEN ? AND ? ");
        selectionArgs.add(value1 + "");
        selectionArgs.add(value2 + "");
        return this;
    }

    TableQuery<T> between(String columnName, Date value1, Date value2) {
        return between(columnName, value1.getTime(), value2.getTime());
    }

    TableQuery<T> like(String columnName, String value, boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        selection.append(columnName).append(" LIKE ? ");
        selectionArgs.add(value);
        return this;
    }

    TableQuery<T> contains(String columnName, String value, boolean caseSensitive) {
        return like(columnName, "%" + value + "%", caseSensitive);
    }


    TableQuery<T> isEmpty(String columnName) {
        //beginGroup().addDelimiter().isNull(columnName).or();
        selection.append("(").append(columnName).append(" IS NULL OR ").append("LENGTH(").append(columnName).append(")=0) ");
        return this;
    }

    TableQuery<T> isNotEmpty(String columnName) {
        //beginGroup().addDelimiter().isNotNull(columnName).addDelimiter();
        selection.append("(").append(columnName).append(" IS NOT NULL AND ").append("LENGTH(").append(columnName).append(")!=0) ");
        return this;
    }

    boolean exists() {
        Cursor cursor = Sort.create().limit(1).into(this).query();
        boolean exists = cursor.getCount() == 1;
        cursor.close();
        closeDatabase();
        return exists;
    }

    long count() {
        return funQueryInt(FUNCTION_COUNT, ALL_COLUMNS);
    }

    private SQLiteDatabase openDatabase() {
        SQLiteDatabase database = dao.openDatabase();
        if (caseSensitive) {
            database.execSQL(PRAGMA_ENABLE_CASE_SENSITIVE_LIKE);
            LightORM.debug("启用大小写敏感的模糊查询：" + PRAGMA_ENABLE_CASE_SENSITIVE_LIKE);
        }
        return database;
    }

    private void closeDatabase() {
        if (caseSensitive) {
            SQLiteDatabase openedDatabase = dao.getOpenedDatabase();
            if (openedDatabase != null) {
                openedDatabase.execSQL(PRAGMA_DISABLE_CASE_SENSITIVE_LIKE);
                caseSensitive = false;
                LightORM.debug("禁用大小写敏感的模糊查询：" + PRAGMA_DISABLE_CASE_SENSITIVE_LIKE);
            }
        }
        dao.closeDatabase();
        clearSql();
    }

    void checkEndGroup() {
        if (beginGroupCount != 0) {
            throw new IllegalStateException("beginGroup和endGroup方法必须成对出现");
        }
    }

    private Cursor query() {
        checkEndGroup();
        Cursor cursor = openDatabase().rawQuery(buildSql(), getSelectionArgs());
        return cursor;
    }

    private Number queryNumber(String sqliteFunctionName, String columnName, Class<? extends Number> numberClass) {
        checkEndGroup();
        Number result = null;
        selectWhat = sqliteFunctionName + "(" + columnName + ")";
        Cursor cursor = query();
        if (cursor.moveToNext()) {
            if (numberClass == Long.class) {
                result = cursor.getLong(0);
            } else if (numberClass == Double.class) {
                result = cursor.getDouble(0);
            }
        }
        cursor.close();
        closeDatabase();
        return result;
    }

    double funQueryDouble(String sqliteFunctionName, String columnName) {
        Number result = queryNumber(sqliteFunctionName, columnName, Double.class);
        return result == null ? -1 : (Double) result;
    }

    long funQueryInt(String sqliteFunctionName, String columnName) {
        Number result = queryNumber(sqliteFunctionName, columnName, Long.class);
        return result == null ? -1 : (Long) result;
    }

    Date funQueryDate(String sqliteFunctionName, String columnName) {
        Number result = queryNumber(sqliteFunctionName, columnName, Long.class);
        return result == null ? null : new Date((Long) result);
    }

    List<T> findAll() {
        Cursor cursor = query();
        List<T> list = dao.cursorToEntityList(cursor);
        closeDatabase();
        return list;
    }

    List<T> findAll(Sort sort) {
        return sort.into(this).findAll();
    }

    List<T> findAll(String sql, String... selectionArgs) {
        Cursor cursor = openDatabase().rawQuery(sql, selectionArgs);
        List<T> list = dao.cursorToEntityList(cursor);
        closeDatabase();
        return list;
    }


    T findOne() {
        List<T> list = findAll();
        return list.isEmpty() ? null : list.get(0);
    }

    T findFirst() {
        return Sort.create().limit(1).orderBy(false, metaData.getPrimaryKey()).into(this).findOne();
    }

    T findLast() {
        return Sort.create().limit(1).orderBy(true, metaData.getPrimaryKey()).into(this).findOne();
    }

    void clearSql() {
        this.selection = null;
        this.limit = this.orderBy = this.having = this.groupBy = null;
        this.isOr = false;
        this.inBeginGroup = false;
        this.selectWhat = ALL_COLUMNS;
        this.selectionArgs.clear();
        this.caseSensitive = false;
        this.beginGroupCount = 0;
        this.distinct = false;
    }

    String getSelection() {
        return selection == null ? null : selection.toString();
    }

    String[] getSelectionArgs() {
        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }

    private String buildSql() {

        if (TextUtils.isEmpty(groupBy) && !TextUtils.isEmpty(having)) {
            throw new IllegalArgumentException(
                    "HAVING clauses are only permitted when using a groupBy clause");
        }

        StringBuilder query = new StringBuilder(120);
        query.append("SELECT ");
        if (distinct) {
            query.append("DISTINCT ");
        }
        query.append(selectWhat);
        query.append(" FROM ");
        query.append(metaData.getTableName());
        appendClause(query, " WHERE ", getSelection());
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        appendClause(query, " LIMIT ", limit);
        String result = query.toString();
        LightORM.debug("生成sql语句为" + result);
        return result;
    }

    private static void appendClause(StringBuilder s, String name, String clause) {
        if (!TextUtils.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

}
