package com.ballboycorp.blabs.roomextension

import androidx.room.Database
import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

import javax.annotation.processing.*
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import java.io.IOException
import java.util.Collections
import java.util.LinkedHashSet

@AutoService(Processor::class)
class DatabaseUtilsProcessor : AbstractProcessor() {

    private var filer: Filer? = null
    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        for (element in roundEnvironment.getElementsAnnotatedWith(Database::class.java)) {
            val binder = createClass(element.simpleName.toString())
            val typeElement = element as TypeElement
            val javaFile = JavaFile.builder(getPackage(typeElement.qualifiedName.toString()), binder).build()
            try {
                javaFile.writeTo(filer!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return false
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val annotations = LinkedHashSet<String>()
        for (annotation in setOf(Database::class.java)) {
            annotations.add(annotation.canonicalName)
        }
        return annotations
    }

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        this.filer = processingEnvironment.filer
    }

    private fun getPackage(s: String): String {
        return s.substring(0, s.lastIndexOf('.'))
    }

    private fun createClass(className: String): TypeSpec {
        return TypeSpec.classBuilder(className + "Builder")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .build()
    }
}