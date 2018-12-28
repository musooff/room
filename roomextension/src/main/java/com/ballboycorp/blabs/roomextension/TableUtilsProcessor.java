package com.ballboycorp.blabs.roomextension;

import androidx.room.Entity;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@AutoService(Processor.class)
public class TableUtilsProcessor extends AbstractProcessor {

    private Filer filer;
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Entity.class)){
            TypeSpec binder = createClass(element.getSimpleName().toString());
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
    }

    private String getPackage(String s){
        return s.substring(0, s.lastIndexOf('.'));
    }

    private TypeSpec createClass(String className){
        return TypeSpec.classBuilder(className + "SqlUtils")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build();
    }
}