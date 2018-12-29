package com.ballboycorp.blabs.roomextension;

import androidx.room.Ignore;

import javax.lang.model.element.Element;

/**
 * Created by musooff on 28/12/2018.
 */

class IgnoreProcessor {
    boolean isIgnored(Element element){
        return element.getAnnotation(Ignore.class) != null;
    }
}
