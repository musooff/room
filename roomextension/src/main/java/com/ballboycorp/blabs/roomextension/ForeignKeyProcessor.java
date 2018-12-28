package com.ballboycorp.blabs.roomextension;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Created by musooff on 28/12/2018.
 */

class ForeignKeyProcessor {

    String getForeignKeyQuery(Types typeUtils, ForeignKey foreignKey){
        StringBuilder foreignKeyQuery = new StringBuilder()
                .append("FOREIGN KEY");

        foreignKeyQuery.append("(");
        for (String columnName : foreignKey.childColumns()){
            foreignKeyQuery.append("`").append(columnName).append("`")
                    .append(", ");
        }
        foreignKeyQuery.setLength(Math.max(foreignKeyQuery.length() - 2, 0));

        foreignKeyQuery.append(")");

        foreignKeyQuery.append(" REFERENCES ");

        try {
            foreignKeyQuery.append(getTableName(foreignKey.entity()));
        }
        catch (MirroredTypeException ex){
            TypeMirror typeMirror = ex.getTypeMirror();
            foreignKeyQuery.append(getTableName(typeUtils, typeMirror));
        }

        foreignKeyQuery.append("(");
        for (String columnName : foreignKey.parentColumns()){
            foreignKeyQuery.append("`").append(columnName).append("`")
                    .append(", ");
        }

        foreignKeyQuery.setLength(Math.max(foreignKeyQuery.length() - 2, 0));

        foreignKeyQuery.append(")");

        foreignKeyQuery.append(" ON UPDATE ").append(getAction(foreignKey.onUpdate()));
        foreignKeyQuery.append(" ON DELETE ").append(getAction(foreignKey.onDelete()));

        foreignKeyQuery.append(" ");

        return foreignKeyQuery.toString();
    }

    private String getTableName(Class entity){
        String classname = entity.getSimpleName();
        String tableName = ((Entity) entity.getAnnotation(Entity.class)).tableName();
        if (tableName.equals("")){
            tableName = classname;
        }
        return "`" + tableName + "`";
    }

    private String getTableName(Types typeUtils, TypeMirror typeMirror){
        Element element = typeUtils.asElement(typeMirror);
        String classname = element.getSimpleName().toString();
        String tableName = element.getAnnotation(Entity.class).tableName();
        if (tableName.equals("")){
            tableName = classname;
        }
        return "`" + tableName + "`";
    }

    private String getAction(int value){
        switch (value){
            case ForeignKey.CASCADE: return "CASCADE";
            case ForeignKey.NO_ACTION: return "NO ACTION";
            case ForeignKey.RESTRICT: return "RESTRICT";
            case ForeignKey.SET_DEFAULT: return "SET DEFAULT";
            case ForeignKey.SET_NULL: return "SET NULL";
            default: return "UNKNOWN";
        }
    }

}
