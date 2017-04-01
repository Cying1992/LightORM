package com.cying.lightorm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

import static com.cying.lightorm.Constants.COLUMN_TYPE_BLOB;
import static com.cying.lightorm.Constants.COLUMN_TYPE_INTEGER;
import static com.cying.lightorm.Constants.COLUMN_TYPE_REAL;
import static com.cying.lightorm.Constants.COLUMN_TYPE_TEXT;
import static com.cying.lightorm.Constants.CURSOR_TYPE_BLOB;
import static com.cying.lightorm.Constants.CURSOR_TYPE_DOUBLE;
import static com.cying.lightorm.Constants.CURSOR_TYPE_FLOAT;
import static com.cying.lightorm.Constants.CURSOR_TYPE_INT;
import static com.cying.lightorm.Constants.CURSOR_TYPE_LONG;
import static com.cying.lightorm.Constants.CURSOR_TYPE_SHORT;
import static com.cying.lightorm.Constants.CURSOR_TYPE_STRING;


/**
 * Created by Cying on 17/3/29.

 */
enum ColumnType {

    //Integer数据类型
    BOOLEAN(Boolean.class, BaseDao.FieldType.BOOLEAN, COLUMN_TYPE_INTEGER, CURSOR_TYPE_INT, true, false),
    SHORT(Short.class, BaseDao.FieldType.INTEGER, COLUMN_TYPE_INTEGER, CURSOR_TYPE_SHORT, false, false),
    INTEGER(Integer.class, BaseDao.FieldType.INTEGER, COLUMN_TYPE_INTEGER, CURSOR_TYPE_INT, false, false),
    BYTE(Byte.class, BaseDao.FieldType.INTEGER, COLUMN_TYPE_INTEGER, CURSOR_TYPE_INT, true, false),
    LONG(Long.class, BaseDao.FieldType.INTEGER, COLUMN_TYPE_INTEGER, CURSOR_TYPE_LONG, false, false),
    DATE(Date.class, BaseDao.FieldType.DATE, COLUMN_TYPE_INTEGER, CURSOR_TYPE_LONG, true, true),

    //Real数据类型
    DOUBLE(Double.class, BaseDao.FieldType.DOUBLE, COLUMN_TYPE_REAL, CURSOR_TYPE_DOUBLE, false, false),
    FLOAT(Float.class, BaseDao.FieldType.FLOAT, COLUMN_TYPE_REAL, CURSOR_TYPE_FLOAT, false, false),

    //Text数据类型
    STRING(String.class, BaseDao.FieldType.STRING, COLUMN_TYPE_TEXT, CURSOR_TYPE_STRING, false, false),

    //CursorType.BLOB
    BLOB(byte[].class, BaseDao.FieldType.BINARY, COLUMN_TYPE_BLOB, CURSOR_TYPE_BLOB, false, false);

    private static final Map<String, ColumnType> allFieldTypes = new HashMap<>();

    static {
        for (ColumnType columnType : values()) {
            allFieldTypes.put(columnType.toString(), columnType);
        }
    }

    private final String columnType;
    private final String cursorType;

    private final boolean convertCursor;
    private final boolean convertContentValues;

    private final Class<?> typeClass;
    private final BaseDao.FieldType fieldType;

    ColumnType(Class<?> typeClass, BaseDao.FieldType fieldType, String columnType, String cursorType, boolean convertCursor, boolean convertContentValues) {
        this.typeClass = typeClass;
        this.columnType = columnType;
        this.cursorType = cursorType;
        this.convertCursor = convertCursor;
        this.convertContentValues = convertContentValues;
        this.fieldType = fieldType;
    }

    BaseDao.FieldType getFieldType() {
        return fieldType;
    }

    String getCursorMethodName() {
        if (!convertCursor) {
            return "get" + cursorType;
        } else {
            return "cursor" + cursorType + "To" + typeClass.getSimpleName();
        }
    }

    String getConvertContentValuesMethodName() {
        if (!convertContentValues) {
            return null;
        }

        return "values" + typeClass.getSimpleName() + "To" + cursorType;
    }

    boolean isConvertContentValues() {
        return convertContentValues;
    }

    @Override
    public String toString() {
        return typeClass.getCanonicalName();
    }

    String getColumnType() {
        return columnType;
    }

    static ColumnType getColumnType(Element fieldElement) {
        String type = LightORMProcessor.getFieldClassNameOf(fieldElement);
        return allFieldTypes.get(type);
    }
}
