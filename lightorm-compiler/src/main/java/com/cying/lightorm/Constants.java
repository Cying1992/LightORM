package com.cying.lightorm;

/**
 * Created by Cying on 17/3/29.

 */
final class Constants {


    static final String METHOD_GET_METADATA = "getMetaData";
    static final String METHOD_GET_IDENTITY = "getIdentity";
    static final String METHOD_SET_IDENTITY = "setIdentity";
    static final String METHOD_CURSOR_TO_ENTITY = "cursorToEntity";
    static final String METHOD_ENTITY_TO_VALUES = "entityToValues";
    static final String METHOD_COLLECT_FIELD_TYPES = "collectFieldTypes";

    static final String FIELD_TABLE = "TABLE_NAME";
    static final String FIELD_DATABASE = "DATABASE_NAME";
    static final String FIELD_PRIMARY_KEY = "PRIMARY_KEY_NAME";
    static final String FIELD_SQL_CREATE = "SQL_CREATE";
    static final String FIELD_META_DATA = "META_DATA";
    static final String FIELD_DAO = "DAO";

    static final String PARAM_NAME_ENTITY = "entity";
    static final String PARAM_NAME_CURSOR = "cursor";
    static final String PARAM_NAME_VALUE = "value";
    static final String PARAM_NAME_CONTENT_VALUES = "contentValues";


    static final String COLUMN_TYPE_INTEGER = "INTEGER";
    static final String COLUMN_TYPE_REAL = "REAL";
    static final String COLUMN_TYPE_TEXT = "TEXT";
    static final String COLUMN_TYPE_BLOB = "BLOB";

    static final String CURSOR_TYPE_INT = "Int";
    static final String CURSOR_TYPE_STRING = "String";
    static final String CURSOR_TYPE_LONG = "Long";
    static final String CURSOR_TYPE_DOUBLE = "Double";
    static final String CURSOR_TYPE_FLOAT = "Float";
    static final String CURSOR_TYPE_SHORT = "Short";
    static final String CURSOR_TYPE_BLOB = "Blob";

    private Constants() {
    }
}
