package com.ballboycorp.blabs.roomextensionexample;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import javax.lang.model.element.Element;

/**
 * Created by musooff on 28/12/2018.
 */

class ColumnInfoProcessor {

    String getColumnName(Element column){
        String columnName = column.getSimpleName().toString();
        ColumnInfo columnInfo = column.getAnnotation(ColumnInfo.class);
        if (columnInfo != null){
            if (!columnInfo.name().equals(ColumnInfo.INHERIT_FIELD_NAME)){
                columnName = columnInfo.name();
            }
        }

        return "`" + columnName + "`";
    }

    String getColumnQuery(Element column){
        String columnName = getColumnName(column);
        String type = getSqlType(column);
        String nullable = getNullable(column);

        return columnName + type + nullable;
    }

    private String getSqlType(Element column){
        switch (column.asType().toString()){
            case "int":
            case "java.lang.Integer":
            case "long":
                return " INTEGER";
            case "java.lang.String":
                return " TEXT";
            case "double":
            case "float":
                return " REAL";
            case "boolean":
                return " INTEGER";
            default:
                return " UNKNOWN";
        }
    }

    private String getNullable(Element column){
        if (column.getAnnotation(org.jetbrains.annotations.Nullable.class) == null){
            return " NOT NULL";
        }
        else if (column.getAnnotation(NonNull.class) != null){
            return " NOT NULL";
        }
        return "";
    }

}
