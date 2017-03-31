package com.cying.lightorm;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
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
 * email:chengying@souche.com
 */
enum ColumnType {

    //Integer数据类型
    BOOLEAN(Boolean.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_INT, true, false),

    SHORT(Short.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_SHORT, false, false),

    INTEGER(Integer.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_INT, false, false),

    BYTE(Byte.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_INT, true, false),

    CHARACTER(Character.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_INT, true, true),

    LONG(Long.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_LONG, false, false),

    CALENDAR(Calendar.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_LONG, true, true),

    DATE(Date.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_LONG, true, true),

    TIMESTAMP(Timestamp.class, COLUMN_TYPE_INTEGER, CURSOR_TYPE_LONG, true, true),

    //Real数据类型
    DOUBLE(Double.class, COLUMN_TYPE_REAL, CURSOR_TYPE_DOUBLE, false, false),

    FLOAT(Float.class, COLUMN_TYPE_REAL, CURSOR_TYPE_FLOAT, false, false),

    //Text数据类型
    ENUM(Enum.class, COLUMN_TYPE_TEXT, CURSOR_TYPE_STRING, true, true),
    STRING(String.class, COLUMN_TYPE_TEXT, CURSOR_TYPE_STRING, false, false),
    BIG_DECIMAL(BigDecimal.class, COLUMN_TYPE_TEXT, CURSOR_TYPE_STRING, true, true),

    //CursorType.BLOB
    BLOB(byte[].class, COLUMN_TYPE_BLOB, CURSOR_TYPE_BLOB, false, false);


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

    ColumnType(Class<?> typeClass, String columnType, String cursorType, boolean convertCursor, boolean convertContentValues) {
        this.typeClass = typeClass;
        this.columnType = columnType;
        this.cursorType = cursorType;
        this.convertCursor = convertCursor;
        this.convertContentValues = convertContentValues;
    }

    public String getCursorMethodName() {
        if (!convertCursor) {
            return "get" + cursorType;
        } else {
            return "cursor" + cursorType + "To" + typeClass.getSimpleName();
        }
    }


    public String getConvertContentValuesMethodName() {
        if (!convertContentValues) {
            return null;
        }

        return "values" + typeClass.getSimpleName() + "To" + cursorType;
    }

    public boolean isConvertContentValues() {
        return convertContentValues;
    }

    public boolean isEnum() {
        return Enum.class.equals(typeClass);
    }

    @Override
    public String toString() {
        return typeClass.getCanonicalName();
    }

    public String getColumnType() {
        return columnType;
    }

    static ColumnType getFiledType(Element fieldElement) {
        String type = LightORMProcessor.getFieldClassNameOf(fieldElement);
        return allFieldTypes.get(type);
    }
}
