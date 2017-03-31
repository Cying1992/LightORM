package com.cying.lightorm;

import android.content.ContentValues;
import android.database.Cursor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import static com.cying.lightorm.Constants.FIELD_DAO;
import static com.cying.lightorm.Constants.FIELD_DATABASE;
import static com.cying.lightorm.Constants.FIELD_META_DATA;
import static com.cying.lightorm.Constants.FIELD_PRIMARY_KEY;
import static com.cying.lightorm.Constants.FIELD_SQL_CREATE;
import static com.cying.lightorm.Constants.FIELD_TABLE;
import static com.cying.lightorm.Constants.METHOD_CURSOR_TO_ENTITY;
import static com.cying.lightorm.Constants.METHOD_ENTITY_TO_VALUES;
import static com.cying.lightorm.Constants.METHOD_GET_IDENTITY;
import static com.cying.lightorm.Constants.METHOD_GET_METADATA;
import static com.cying.lightorm.Constants.METHOD_SET_IDENTITY;
import static com.cying.lightorm.Constants.PARAM_NAME_CONTENT_VALUES;
import static com.cying.lightorm.Constants.PARAM_NAME_CURSOR;
import static com.cying.lightorm.Constants.PARAM_NAME_ENTITY;
import static com.cying.lightorm.Constants.PARAM_NAME_VALUE;
import static com.cying.lightorm.LightORMProcessor.error;

/**
 * Created by Cying on 17/3/29.
 * email:chengying@souche.com
 */
class TableClass {

    private final TypeElement entityElement;
    private final String packageName;
    private final String entityClassName;
    private final String databaseName;
    private final String tableName;
    private final String daoClassName;

    private String primaryKeyColumnName;
    private String primaryKeyFieldName;
    private boolean hasPrimaryKey;
    private final Map<String, ColumnField> columnFieldMap = new HashMap<>();

    private String createTableSQL;

    private TypeName entityTypeName;

    TableClass(TypeElement entityElement) {
        checkEntityElementValid(entityElement);
        this.entityElement = entityElement;
        this.packageName = LightORMProcessor.getPackageNameOf(entityElement);
        this.entityClassName = findEntityClassName(entityElement, packageName);
        this.daoClassName = entityClassName.replace(".", "$") + LightORM.DAO_SUFFIX;
        this.tableName = findTableName(entityElement);
        this.databaseName = findDatabaseName(entityElement);
        this.entityTypeName = TypeName.get(entityElement.asType());
        init();

        for (String keyword : SqliteKeyword.keywords) {
            if (keyword.equalsIgnoreCase(tableName)) {
                error(entityElement, "表名不能为sqlite关键字");
                break;
            }
        }
    }

    TypeElement getEntityElement() {
        return entityElement;
    }

    String getTableName() {
        return tableName;
    }

    String getDaoClassCanonicalName() {
        return packageName + "." + daoClassName;
    }

    boolean hasPrimaryKey() {
        return hasPrimaryKey;
    }

    private void init() {
        processColumns();
        generateCreateSQL();
    }

    private String findEntityClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen);
    }

    private String findTableName(Element type) {
        String tableName = type.getAnnotation(Table.class).value().trim();
        if (tableName.isEmpty()) {
            tableName = type.getSimpleName().toString();
        }

        return tableName;
    }

    private String findDatabaseName(Element type) {
        return type.getAnnotation(Table.class).database().trim();
    }

    private void checkEntityElementValid(TypeElement entityElement) {
        LightORMProcessor.isNotClassType(Table.class, entityElement);
        LightORMProcessor.isClassInaccessibleViaGeneratedCode(entityElement);
    }

    private void checkFieldElementValid(VariableElement variableElement) {
        LightORMProcessor.isFieldInaccessibleViaGeneratedCode(entityElement, variableElement);
    }

    private void processPrimaryKey(VariableElement fieldElement) {
        if (hasPrimaryKey) {
            error(fieldElement, "@Class (%s) :@Field (%s) :table '%s' already has the primary key '%s'",
                    entityClassName, fieldElement.getSimpleName(),
                    tableName, primaryKeyColumnName);
        } else {
            TypeMirror typeMirror = fieldElement.asType();
            primaryKeyFieldName = fieldElement.getSimpleName().toString();
            primaryKeyColumnName = fieldElement.getAnnotation(Key.class).value().trim().toLowerCase();
            if (primaryKeyColumnName.isEmpty()) {
                primaryKeyColumnName = primaryKeyFieldName.toLowerCase();
            }

            hasPrimaryKey = true;
            String fieldClassName = LightORMProcessor.getFieldClassNameOf(fieldElement);
            if (typeMirror.getKind() != TypeKind.LONG && !fieldClassName.equals(Long.class.getCanonicalName())) {
                error(fieldElement, "@Class (%s) :@Field (%s) :the primary key must be long or Long",
                        entityClassName, primaryKeyFieldName);
            }
            LightORMProcessor.checkKeyWord(fieldElement, primaryKeyColumnName, entityClassName, primaryKeyFieldName);

            if (columnFieldMap.containsKey(primaryKeyColumnName)) {
                error(fieldElement, "column '%s' is already exists ", primaryKeyColumnName);
            }

        }
    }

    private void generateCreateSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE [");
        builder.append(tableName);
        builder.append("] ([");
        builder.append(primaryKeyColumnName);
        builder.append("] INTEGER PRIMARY KEY AUTOINCREMENT");

        for (ColumnField columnField : columnFieldMap.values()) {
            builder.append(",");
            builder.append(columnField.getColumnSQL());
        }

        builder.append(");");
        createTableSQL = builder.toString();
    }

    private void processNormalColumn(VariableElement fieldElement) {
        ColumnField columnField = new ColumnField(fieldElement, entityElement);
        String columnName = columnField.getColumnName();
        if (columnName.equalsIgnoreCase(primaryKeyColumnName) || columnFieldMap.containsKey(columnName)) {
            error(fieldElement, "column '%s' is already exists ", columnName);
        }
        columnFieldMap.put(columnName, columnField);
        LightORMProcessor.checkKeyWord(fieldElement, columnField.getColumnName(), entityClassName, columnField.getFieldName());

    }

    private void processColumns() {
        for (VariableElement variableElement : ElementFilter.fieldsIn(entityElement.getEnclosedElements())) {
            if (LightORMProcessor.isAnnotationPresent(Key.class, variableElement)) {
                checkFieldElementValid(variableElement);
                processPrimaryKey(variableElement);
            } else if (LightORMProcessor.isAnnotationPresent(Column.class, variableElement)) {
                checkFieldElementValid(variableElement);
                processNormalColumn(variableElement);
            }
        }
    }


    void brewJava() throws IOException {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(ClassName.get(packageName, daoClassName))
                .superclass(ParameterizedTypeName.get(ClassName.get(BaseDao.class), entityTypeName));

        //添加静态变量
        addStaticStringField(typeBuilder, FIELD_TABLE, tableName);
        addStaticStringField(typeBuilder, FIELD_DATABASE, databaseName);
        addStaticStringField(typeBuilder, FIELD_PRIMARY_KEY, primaryKeyColumnName);
        addStaticStringField(typeBuilder, FIELD_SQL_CREATE, createTableSQL);
        addStaticMetaDataField(typeBuilder);
        addStaticDaoField(typeBuilder);
        addStaticBlock(typeBuilder);

        //添加方法
        addGetMetaDataMethod(typeBuilder);
        addGetIdentityMethod(typeBuilder);
        addSetIdentityMethod(typeBuilder);
        addCursorToEntityMethod(typeBuilder);
        addEntityToValuesMethod(typeBuilder);

        JavaFile javaFile = JavaFile.builder(packageName, typeBuilder.build())
                .build();
        javaFile.writeTo(LightORMProcessor.filer);
    }


    private void addStaticStringField(TypeSpec.Builder builder, String filedName, String filedValue) {
        builder.addField(FieldSpec.builder(String.class, filedName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$S", filedValue).build());
    }

    private void addStaticMetaDataField(TypeSpec.Builder builder) {
        builder.addField(
                FieldSpec.builder(BaseDao.MetaData.class, FIELD_META_DATA, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T($L,$L,$L,$L)", BaseDao.MetaData.class, FIELD_SQL_CREATE, FIELD_DATABASE, FIELD_TABLE, FIELD_PRIMARY_KEY)
                        .build()
        );
    }

    private void addStaticDaoField(TypeSpec.Builder builder) {
        builder.addField(
                FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(BaseDao.class), entityTypeName), FIELD_DAO, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", ClassName.get(packageName, daoClassName))
                        .build()
        );
    }

    private void addStaticBlock(TypeSpec.Builder builder) {
        builder.addStaticBlock(CodeBlock.of("saveDao($T.class,$L);\n", entityTypeName, FIELD_DAO));
    }

    private void addGetMetaDataMethod(TypeSpec.Builder builder) {
        MethodSpec.Builder createMetaDataMethod = MethodSpec
                .methodBuilder(METHOD_GET_METADATA)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(BaseDao.MetaData.class)
                .addStatement("return $L", FIELD_META_DATA);

        builder.addMethod(createMetaDataMethod.build());
    }

    private void addGetIdentityMethod(TypeSpec.Builder builder) {
        MethodSpec.Builder getIdentityMethod = MethodSpec
                .methodBuilder(METHOD_GET_IDENTITY)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(Long.class)
                .addParameter(entityTypeName, PARAM_NAME_ENTITY)
                .addStatement("return $L.$L", PARAM_NAME_ENTITY, primaryKeyFieldName);

        builder.addMethod(getIdentityMethod.build());

    }

    private void addSetIdentityMethod(TypeSpec.Builder builder) {
        MethodSpec.Builder setIdentityMethod = MethodSpec
                .methodBuilder(METHOD_SET_IDENTITY)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(entityTypeName, PARAM_NAME_ENTITY)
                .addParameter(Long.class, PARAM_NAME_VALUE)
                .addStatement("$L.$L = $L", PARAM_NAME_ENTITY, primaryKeyFieldName, PARAM_NAME_VALUE);
        builder.addMethod(setIdentityMethod.build());
    }

    private void addCursorToEntityMethod(TypeSpec.Builder builder) {
        MethodSpec.Builder cursorToEntityMethod = MethodSpec.methodBuilder(METHOD_CURSOR_TO_ENTITY)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(entityTypeName)
                .addParameter(Cursor.class, PARAM_NAME_CURSOR)
                .addStatement("$T $L = new $T()", entityElement, PARAM_NAME_ENTITY, entityElement)
                .addStatement("$L.$L = getLong($L,$S)", PARAM_NAME_ENTITY, primaryKeyFieldName, PARAM_NAME_CURSOR, primaryKeyColumnName);


        List<Object> params = new ArrayList<>();
        for (ColumnField columnField : columnFieldMap.values()) {
            ColumnType columnType = columnField.getColumnType();
            StringBuilder formatBuilder = new StringBuilder();
            formatBuilder.append("$L.$L = ");
            params.add(PARAM_NAME_ENTITY);
            params.add(columnField.getFieldName());


            formatBuilder.append(columnType.getCursorMethodName())
                    .append("(");

            //enum需要特殊处理
            if (columnType.isEnum()) {
                formatBuilder.append("$T.class, ");
                params.add(columnField.getFieldElement().asType());
            }

            formatBuilder.append("$L,$S)");

            params.add(PARAM_NAME_CURSOR);
            params.add(columnField.getColumnName());

            cursorToEntityMethod.addStatement(formatBuilder.toString(), params.toArray());
            params.clear();
        }

        cursorToEntityMethod.addStatement("return $L", PARAM_NAME_ENTITY);

        builder.addMethod(cursorToEntityMethod.build());
    }

    private void addEntityToValuesMethod(TypeSpec.Builder builder) {

        MethodSpec.Builder entityToValuesMethod = MethodSpec.methodBuilder(METHOD_ENTITY_TO_VALUES)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(ContentValues.class)
                .addParameter(entityTypeName, PARAM_NAME_ENTITY)
                .addStatement("$T $L = new $T()", ContentValues.class, PARAM_NAME_CONTENT_VALUES, ContentValues.class)
                .addStatement("$L.put($S,$L.$L)", PARAM_NAME_CONTENT_VALUES, primaryKeyColumnName, PARAM_NAME_ENTITY, primaryKeyFieldName);
        for (ColumnField columnField : columnFieldMap.values()) {

            ColumnType columnType = columnField.getColumnType();
            StringBuilder formatBuilder = new StringBuilder();
            formatBuilder.append("$L.put($S,");


            if (columnType.isConvertContentValues()) {
                formatBuilder.append(columnType.getConvertContentValuesMethodName()).append("($L.$L))");
            } else {
                formatBuilder.append("$L.$L)");
            }

            entityToValuesMethod.addStatement(formatBuilder.toString(), PARAM_NAME_CONTENT_VALUES, columnField.getColumnName(), PARAM_NAME_ENTITY, columnField.getFieldName());

        }
        entityToValuesMethod.addStatement("return $L", PARAM_NAME_CONTENT_VALUES);
        builder.addMethod(entityToValuesMethod.build());

    }
}
