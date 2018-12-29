package com.ballboycorp.blabs.roomextensionexample;

import androidx.room.PrimaryKey;

import javax.lang.model.element.Element;

/**
 * Created by musooff on 28/12/2018.
 */

class PrimaryKeyProcessor {

    boolean isPrimaryKey(Element column){
        return column.getAnnotation(PrimaryKey.class) != null;
    }

    boolean isAutoIncrement(Element column){
        return column.getAnnotation(PrimaryKey.class).autoGenerate();
    }
}
