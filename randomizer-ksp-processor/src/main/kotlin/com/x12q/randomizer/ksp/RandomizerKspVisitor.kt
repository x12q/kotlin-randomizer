package com.x12q.com.x12q.randomizer.ksp

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.validate
import java.io.PrintWriter

class RandomizerKspVisitor(private val fileWriter: PrintWriter) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind != ClassKind.INTERFACE) {
            return
        }

        val annotation: KSAnnotation = classDeclaration.annotations.first {
            it.shortName.asString() == "Randomizable"
        }

        // Getting the value of the 'name' argument.
        val functionName = "randomize_${classDeclaration.simpleName.asString()}"

        val properties: Sequence<KSPropertyDeclaration> = classDeclaration.getAllProperties()
            .filter { it.validate() }

        fileWriter.write("\n")
        if (properties.iterator().hasNext()) {
            fileWriter.write("fun $functionName(\n")

            properties.forEach { prop ->
                visitPropertyDeclaration(prop, Unit)
            }
            fileWriter.write(") {\n")

        } else {
            fileWriter.write ("fun $functionName() {\n")
        }

        fileWriter.write("    println(\"Hello from $functionName\")\n")
        fileWriter.write("}\n")
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val argumentName = property.simpleName.asString()
        fileWriter.write("    $argumentName: ")

        val resolvedType: KSType = property.type.resolve()
        fileWriter.write(resolvedType.declaration.qualifiedName?.asString() ?: run {
            return
        })

        val genericArguments: List<KSTypeArgument> = property.type.element?.typeArguments ?: emptyList()
        visitTypeArguments(genericArguments)

        fileWriter.write(if (resolvedType.nullability == Nullability.NULLABLE) "?" else "")

        fileWriter.write(",\n")
    }
    private fun visitTypeArguments(typeArguments: List<KSTypeArgument>) {
        if (typeArguments.isNotEmpty()) {
            fileWriter.write("<")
            typeArguments.forEachIndexed { i, arg ->
                visitTypeArgument(arg, data = Unit)
                if (i < typeArguments.lastIndex) fileWriter.write(", ")
            }
            fileWriter.write(">")
        }
    }
    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
        when (val variance: Variance = typeArgument.variance) {
            Variance.STAR -> {
                fileWriter.write("*")
                return
            }
            Variance.COVARIANT, Variance.CONTRAVARIANT -> {
                fileWriter.write(variance.label) // 'out' or 'in'
                fileWriter.write(" ")
            }
            Variance.INVARIANT -> {
                // do nothing
            }
        }
        val resolvedType: KSType? = typeArgument.type?.resolve()
        fileWriter.write(resolvedType?.declaration?.qualifiedName?.asString() ?: run {

            return
        })

        val genericArguments: List<KSTypeArgument> = typeArgument.type?.element?.typeArguments ?: emptyList()
        visitTypeArguments(genericArguments)

        fileWriter.write(if (resolvedType?.nullability == Nullability.NULLABLE) "?" else "")
    }
}
