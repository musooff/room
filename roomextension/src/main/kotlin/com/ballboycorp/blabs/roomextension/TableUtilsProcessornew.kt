package com.ballboycorp.blabs.roomextension

import androidx.room.*
import com.google.auto.service.AutoService
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import java.io.IOException
import java.util.*
import javax.lang.model.type.TypeKind

@AutoService(Processor::class)
class TableUtilsProcessornew : AbstractProcessor() {
    private var messager: Messager? = null
    private var filer: Filer? = null
    private var typesUtil: Types? = null
    private var elementsUtil: Elements? = null

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        this.messager = processingEnvironment.messager
        this.filer = processingEnvironment.filer
        typesUtil = processingEnv.typeUtils
        elementsUtil = processingEnv.elementUtils
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {

        for (el in roundEnvironment.getElementsAnnotatedWith(Entity::class.java)) {
            val binder = createClass(el)
            val typeElement = el as TypeElement
            val javaFile = JavaFile.builder(getPackage(typeElement.qualifiedName.toString()), binder).build()
            try {
                javaFile.writeTo(filer!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return false
    }

    private fun getPackage(s: String): String {
        return s.substring(0, s.lastIndexOf(CHAR_DOT))
    }

    private fun createClass(element: Element): TypeSpec {
        var hasAutoIncrement = false

        val createQuery = StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS ")

        val className = element.simpleName.toString()
        var tableName = element.getAnnotation(Entity::class.java).tableName
        if (tableName == "") {
            tableName = className
        }

        val primaryKeys = ArrayList(getPrimaryKeys(element))

        createQuery.append(tableName)

        createQuery.append(" (")
        for (el in getFields(element)) {
            if (isIgnored(el)) {
                continue
            }
            createQuery.append(getColumnQuery(el))
            if (isPrimaryKey(el)) {
                if (isAutoIncrement(el)) {
                    hasAutoIncrement = true
                    createQuery.append(" PRIMARY KEY AUTOINCREMENT")
                } else {
                    primaryKeys.add(getColumnName(el))
                }
            }
            createQuery.append(",")

        }

        if (!(hasAutoIncrement)) {
            createQuery.append(" PRIMARY KEY (")
            for (columnName in primaryKeys) {
                createQuery.append("`").append(columnName).append("`")
                createQuery.append(",")
            }
            createQuery.setLength(Math.max(createQuery.length - 1, 0))
            createQuery.append("))")
        } else {
            createQuery.setLength(Math.max(createQuery.length - 1, 0))
            createQuery.append(")")
        }


        val foreignKeys = getForeignKeys(element)


        val builder = TypeSpec.classBuilder(className + TABLE_UTILS_SUFFIX)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        builder.addField(createSqlQuery(createQuery.toString()))
        builder.addField(dropSqlQuery(tableName))

        return builder.build()
    }

    private fun isIgnored(element: Element): Boolean {
        return element.getAnnotation(Ignore::class.java) != null
    }

    private fun getForeignKeys(element: Element): Array<ForeignKey> {
        return element.getAnnotation(Entity::class.java).foreignKeys
    }

    private fun getForeignKeyQuery(foreignKey: ForeignKey): String {
        val foreignQuery = StringBuilder()
            .append("FOREIGN KEY")
        val entity = foreignKey.entity
        val parentColumns = foreignKey.parentColumns
        val childColumns = foreignKey.childColumns
        val onDelete = foreignKey.onDelete
        val onUpdate = foreignKey.onUpdate

        foreignQuery.append("(")
        for (columnName in childColumns) {
            foreignQuery.append("`").append(columnName).append("`")
                .append(",")
        }

        return ""

    }

    private fun getPrimaryKeys(element: Element): List<String> {
        return Arrays.asList(*element.getAnnotation(Entity::class.java).primaryKeys)
    }

    private fun getFields(element: Element): List<VariableElement> {
        return ElementFilter.fieldsIn(element.enclosedElements)
    }

    private fun getColumnName(el: Element): String {
        var columnName = el.simpleName.toString()
        val columnInfo = el.getAnnotation(ColumnInfo::class.java)
        if (columnInfo != null) {
            if (columnInfo.name != ColumnInfo.INHERIT_FIELD_NAME) {
                columnName = columnInfo.name
            }
        }

        return columnName
    }

    private fun getColumnQuery(el: Element): String {
        val columnName = getColumnName(el)
        val type = getSqlType(el)
        val nullable = getNullable(el)

        return columnName + type + nullable
    }

    private fun isPrimaryKey(el: Element): Boolean {
        return el.getAnnotation(PrimaryKey::class.java) != null
    }

    private fun isAutoIncrement(el: Element): Boolean {
        return el.getAnnotation(PrimaryKey::class.java).autoGenerate
    }

    private fun getSqlType(el: Element): String {
        return when (el.asType().kind) {
            TypeKind.INT -> " INTEGER"
            TypeKind.DECLARED -> " TEXT"
            TypeKind.LONG, TypeKind.DOUBLE, TypeKind.FLOAT -> " REAL"
            TypeKind.BOOLEAN -> " INTEGER"
            else -> " UNKNOWN"
        }
    }

    private fun getNullable(el: Element): String {
        return if (el.getAnnotation(org.jetbrains.annotations.Nullable::class.java) == null) {
            " NOT NULL"
        } else ""
    }

    private fun createSqlQuery(query: String): FieldSpec {
        return FieldSpec
            .builder(
                String::class.java,
                "createTable", Modifier.PUBLIC
            )
            .initializer("\"" + query + "\"")
            .build()
    }

    private fun dropSqlQuery(tableName: String): FieldSpec {
        return FieldSpec
            .builder(String::class.java, "dropTable", Modifier.PUBLIC)
            .initializer(String.format("\"DROP TABLE %s\"", tableName))
            .build()
    }


    override fun getSupportedAnnotationTypes(): Set<String> {
        val annotations = LinkedHashSet<String>()
        for (annotation in listOf(Entity::class.java)) {
            annotations.add(annotation.canonicalName)
        }
        return annotations
    }


    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    companion object {

        private const val TABLE_UTILS_SUFFIX = "SqlUtils"
        private const val CHAR_DOT = '.'
    }
}
