package com.cying.lightorm;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class LightORMProcessor extends AbstractProcessor {

    static Filer filer; //文件相关的辅助类
    static Elements elementUtils; //元素相关的辅助类
    static Messager messager; //日志相关的辅助类
    static Types typeUtils;
    private boolean isGenerated = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(Table.class);
        annotations.add(Key.class);
        annotations.add(Column.class);
        return annotations;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        //这个方法可能会进行多次，需要判断是否已经成功生成了代码
        if (isGenerated) {
            return true;
        }
        List<TableClass> tableClassList = findTableClass(roundEnv);
        for (TableClass tableClass : tableClassList) {
            try {
                tableClass.brewJava();
            } catch (Exception e) {
                e.printStackTrace();
                error(tableClass.getEntityElement(), "发生异常: " + e.getMessage());
            }
        }

        try {
            generateDaoCollectionsClass(tableClassList);
        } catch (Exception e) {
            e.printStackTrace();
            error(null, "生成" + LightORM.CLASS_DAO_COLLECTIONS + "类失败, " + e.getMessage());
        }
        isGenerated = true;
        return true;
    }

    private void generateDaoCollectionsClass(List<TableClass> tableClassList) throws IOException {
        if (tableClassList.isEmpty()) {
            return;
        }
        TypeSpec.Builder builder = TypeSpec.classBuilder(LightORM.CLASS_DAO_COLLECTIONS)
                .addModifiers(Modifier.FINAL);

        StringBuilder formatBuilder = new StringBuilder();
        List<Object> params = new ArrayList<>();
        formatBuilder.append("try{ \n");
        for (TableClass tableClass : tableClassList) {
            formatBuilder.append("    $T.forName($S);\n");
            params.add(Class.class);
            params.add(tableClass.getDaoClassCanonicalName());
        }
        formatBuilder.append("\n}catch($T e){}\n");
        params.add(Exception.class);
        builder.addStaticBlock(CodeBlock.of(formatBuilder.toString(), params.toArray()));

        JavaFile javaFile = JavaFile.builder(LightORM.PACKAGE_DAO_COLLECTIONS, builder.build()).build();
        javaFile.writeTo(filer);

    }


    private List<TableClass> findTableClass(RoundEnvironment roundEnv) {
        List<TableClass> tableClassList = new ArrayList<>();
        TableClass tableClass;
        Set<? extends Element> tableClassSet = roundEnv.getElementsAnnotatedWith(Table.class);

        for (Element normalElement : tableClassSet) {
            TypeElement element = (TypeElement) normalElement;
            tableClass = new TableClass(element);
            if (!tableClass.hasPrimaryKey()) {
                error(element, "Table '%s' don't have the primary key", tableClass.getTableName());
                return tableClassList;
            }
            tableClassList.add(tableClass);
        }
        return tableClassList;
    }

    static void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }

    static String getFieldClassNameOf(Element fieldElement) {

        TypeMirror typeMirror = fieldElement.asType();
        String fieldClassName = null;

        if (typeMirror instanceof PrimitiveType) {
            //获得他的包装类型
            fieldClassName = LightORMProcessor.typeUtils.boxedClass((PrimitiveType) typeMirror).getQualifiedName().toString();
        } else if (typeMirror instanceof DeclaredType) {
            TypeElement fieldType = (TypeElement) LightORMProcessor.typeUtils.asElement(typeMirror);
            fieldClassName = fieldType.getQualifiedName().toString();
        } else if (typeMirror instanceof ArrayType && ((ArrayType) typeMirror).getComponentType().getKind() == TypeKind.BYTE) {
            fieldClassName = byte[].class.getCanonicalName();
        } else {
            error(fieldElement, "not support this type which field name is %s", fieldElement.getSimpleName());
        }
        return fieldClassName;
    }


    static void checkKeyWord(Element fieldElement, String columnName, String entityClassName, String fieldName) {
        if (columnName == null || columnName.isEmpty()) return;
        for (String keyword : SqliteKeyword.keywords) {
            if (keyword.equalsIgnoreCase(columnName)) {
                error(fieldElement, "%s.%s :This column name  must not be a sqlite3 keyword  '%s'", entityClassName, fieldName, columnName);
                return;
            }
        }
    }

    static boolean isEnum(Element fieldElement) {
        Element element = typeUtils.asElement(fieldElement.asType());
        return element != null && ElementKind.ENUM.equals(element.getKind());
    }

    static String getPackageNameOf(TypeElement type) {

        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    static boolean isAnnotationPresent(Class<? extends Annotation> annotationClass, Element element) {
        return element.getAnnotation(annotationClass) != null;
    }

    static boolean isFieldInaccessibleViaGeneratedCode(TypeElement typeElement, Element fieldElement) {
        boolean hasError = false;
        Set<Modifier> modifiers = fieldElement.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC) || modifiers.contains(FINAL)) {
            error(fieldElement, "The table entity's fields must not be private , static or final. (%s.%s)", typeElement.getQualifiedName(),
                    fieldElement.getSimpleName());
            hasError = true;
        }

        return hasError;
    }

    static boolean isClassInaccessibleViaGeneratedCode(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        ElementKind enclosingElementKind = enclosingElement.getKind();

        if (ElementKind.PACKAGE.equals(enclosingElementKind)) {
            return false;
        } else if (enclosingElementKind.isClass() || enclosingElementKind.isInterface()) {
            Set<Modifier> selfModifiers = element.getModifiers();
            if (selfModifiers.contains(PRIVATE) || !selfModifiers.contains(STATIC)) {
                error(element, "Class %s must be static and not be private which will cause the nested table entity class to be  inaccessible via generated code.", element.getSimpleName());
                return true;
            }
            return isClassInaccessibleViaGeneratedCode(enclosingElement);
        } else {
            error(element, "unexpected element kind of %s", enclosingElementKind);
            return true;
        }
    }

    static void isNotClassType(Class<? extends Annotation> annotationClass, Element element) {
        if (!ElementKind.CLASS.equals(element.getKind())) {
            error(element, "%s-annotated is not class type", annotationClass.getSimpleName());
        }
    }
}
