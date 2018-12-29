package com.ballboycorp.blabs.roomextensionexample;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by musooff on 28/12/2018.
 */

@AutoService(Processor.class)
public class TableUtilsProcessor extends AbstractProcessor {


    private static final String TABLE_UTILS_SUFFIX = "SqlUtils";
    private static final Character CHAR_DOT = '.';

    private ColumnInfoProcessor columnInfoProcessor = new ColumnInfoProcessor();
    private EntityProcessor entityProcessor = new EntityProcessor();
    private ForeignKeyProcessor foreignKeyProcessor = new ForeignKeyProcessor();
    private IgnoreProcessor ignoreProcessor = new IgnoreProcessor();
    private PrimaryKeyProcessor primaryKeyProcessor = new PrimaryKeyProcessor();

    private Filer filer;
    private Types typeUtils;
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Entity.class)){
            TypeSpec binder = createClass(element);
            TypeElement typeElement = (TypeElement) element;
            JavaFile javaFile = JavaFile.builder(getPackage(typeElement.getQualifiedName().toString()), binder).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : Collections.singleton(Entity.class)) {
            annotations.add(annotation.getCanonicalName());
        }
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.filer = processingEnvironment.getFiler();
        this.typeUtils = processingEnvironment.getTypeUtils();
    }

    private String getPackage(String s){
        return s.substring(0, s.lastIndexOf(CHAR_DOT));
    }

    private TypeSpec createClass(Element entity){
        boolean hasAutoIncrement = false;
        StringBuilder createQuery = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS ");


        String className = entity.getSimpleName().toString();
        String tableName = entityProcessor.getTableName(entity);

        List<String> primaryKeys = entityProcessor.getPrimaryKeys(entity);
        ForeignKey[] foreignKeys  = entityProcessor.getForeignKeys(entity);

        createQuery.append(tableName);

        createQuery.append(" (");

        for (Element field : entityProcessor.getFields(entity)){
            if (ignoreProcessor.isIgnored(field)){
                continue;
            }
            createQuery.append(columnInfoProcessor.getColumnQuery(field));


            if (primaryKeyProcessor.isPrimaryKey(field)){
                if (primaryKeyProcessor.isAutoIncrement(field)){
                    hasAutoIncrement = true;
                    createQuery.append(" PRIMARY KEY AUTOINCREMENT");
                }
                else {
                    primaryKeys.add(columnInfoProcessor.getColumnName(field));
                }
            }
            createQuery.append(", ");
        }

        if (!hasAutoIncrement){
            createQuery.append("PRIMARY KEY(");
            for (String columnName : primaryKeys) {
                createQuery.append(columnName);
                createQuery.append(", ");
            }
            createQuery.setLength(Math.max(createQuery.length() - 2, 0));
            createQuery.append(")");
        }
        else {
            createQuery.setLength(Math.max(createQuery.length() - 2, 0));
        }


        if (foreignKeys.length != 0){
            createQuery.append(", ");
            for (ForeignKey foreignKey: foreignKeys){
                createQuery.append(foreignKeyProcessor.getForeignKeyQuery(typeUtils, foreignKey));
                createQuery.append(", ");
            }
            createQuery.setLength(Math.max(createQuery.length() - 2, 0));
            createQuery.append(")");
        }
        else {
            createQuery.append(")");
        }

        TypeSpec.Builder builder =  TypeSpec.classBuilder(className + TABLE_UTILS_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        builder.addField(createSqlQuery(createQuery.toString()));
        builder.addField(dropSqlQuery(tableName));

        return builder.build();

    }

    private FieldSpec createSqlQuery(String query) {
        return FieldSpec.builder(String.class, "createTable", Modifier.PUBLIC)
                .initializer("\"" + query + "\"")
                .build();
    }

    private FieldSpec dropSqlQuery(String tableName){
        return FieldSpec.builder(String.class, "dropTable", Modifier.PUBLIC)
                .initializer(String.format("\"DROP TABLE %s\"", tableName))
                .build();
    }



}