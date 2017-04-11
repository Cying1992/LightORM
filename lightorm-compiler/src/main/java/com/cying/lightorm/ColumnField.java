package com.cying.lightorm;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by Cying on 17/3/29.
 */
class ColumnField {

    private final VariableElement fieldElement;
    private final String fieldName;
    private String columnName;

    private String columnSQL;
    private final boolean columnNotNull;
    private final boolean columnUnique;

    private final ColumnType columnType;

    ColumnField(VariableElement fieldElement, TypeElement entityElement) {
        this.fieldElement = fieldElement;
        this.fieldName = fieldElement.getSimpleName().toString();
        Column column = fieldElement.getAnnotation(Column.class);
        columnName = column.value().trim();
        if (columnName.isEmpty()) {
            columnName = fieldName;
        }

        columnNotNull = column.notNull();
        columnUnique = column.unique();
        columnType = ColumnType.getColumnType(fieldElement);
        if (columnType == null) {
            LightORMProcessor.error(fieldElement, "不支持这个类型");
        } else {
            if (columnType == ColumnType.BOOLEAN && columnUnique) {
                LightORMProcessor.error(fieldElement, "不能在Boolean类型上使用unique约束");
            }
            prepareColumnSQL();
        }
    }

    BaseDao.FieldType getFieldType() {
        if (columnType == null) {
            return null;
        }

        return columnType.getFieldType();
    }

    VariableElement getFieldElement() {
        return fieldElement;
    }

    ColumnType getColumnType() {
        return columnType;
    }

    String getFieldName() {
        return fieldName;
    }

    String getColumnSQL() {
        return columnSQL;
    }

    private void prepareColumnSQL() {
        columnSQL = "[" + columnName + "] " + columnType.getColumnType() + (columnNotNull ? " NOT NULL" : "") + (columnUnique ? " UNIQUE " : "");
    }

    String getColumnName() {
        return columnName;
    }
}
