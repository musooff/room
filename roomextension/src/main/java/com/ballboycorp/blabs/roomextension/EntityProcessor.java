package com.ballboycorp.blabs.roomextension;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by musooff on 28/12/2018.
 */

class EntityProcessor {

    List<String> getPrimaryKeys(Element entity){
        String[] primaryKeys = entity.getAnnotation(Entity.class).primaryKeys();
        List<String> result = new ArrayList<>();
        for (String columnName: primaryKeys){
            result.add("`" + columnName + "`");
        }
        return result;
    }

    ForeignKey[] getForeignKeys(Element entity){
        return entity.getAnnotation(Entity.class).foreignKeys();
    }

    List<VariableElement> getFields(Element entity){
        return ElementFilter.fieldsIn(entity.getEnclosedElements());
    }


}
