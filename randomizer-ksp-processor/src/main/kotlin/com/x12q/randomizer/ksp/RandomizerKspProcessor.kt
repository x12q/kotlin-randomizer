package com.x12q.com.x12q.randomizer.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.x12q.randomizer.ksp.RdMetaData
import java.io.OutputStream
import java.io.PrintWriter
import kotlin.sequences.forEach

class RandomizerKspProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(RdMetaData.randomizableAnnotationFullName)
            .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) {
            return emptyList()
        }


        val fileOutStream: OutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "com.x12q.generated_files",
            fileName = "GeneratedFunctions"
        )
        val writer = PrintWriter(fileOutStream)
        writer.write("package com.x12q\n")

        symbols.forEach { it.accept(RandomizerKspVisitor(writer), Unit) }

        writer.close()
        val unableToProcess = symbols.filterNot { it.validate() }.toList()
        return unableToProcess
    }
}



