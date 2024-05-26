package com.x12q.randomizer.ir_plugin.transformers

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * An IR transformer that transforms any function that is named "someFunction".
 * It will add the following to the beginning of the function
 * val myLib = MyLib()
 * println(myLib.makeStr())
 */
class SomeFunctionTransformer(
    val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

    // factory for generating declaration such as class, properties etc
    val irFactory: IrFactory = pluginContext.irFactory

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val f = declaration
        val body = f.body
        if (isTargetFunction(f, body)) {
            val newBody = transformTargetFunction(pluginContext, f)
            if (newBody != null) {
                declaration.body = newBody
            }
        }
        return super.visitFunctionNew(declaration)
    }

    /**
     * Check if a function is the target for transformation or not
     */
    private fun isTargetFunction(function: IrFunction, body: IrBody?): Boolean {
        val f = this::transformTargetFunction
        val rt = function.name == Name.identifier("someFunction") && body != null
        return rt
    }

    /**
     * FUN name:someFunction visibility:public modality:FINAL <> () returnType:kotlin.Unit
     *   BLOCK_BODY
     *     VAR name:l type:com.k7.MyLib [val]
     *       CONSTRUCTOR_CALL 'public constructor <init> () [primary] declared in com.k7.MyLib' type=com.k7.MyLib origin=null
     *     CALL 'public final fun println (message: kotlin.Any?): kotlin.Unit [inline] declared in kotlin.io.ConsoleKt' type=kotlin.Unit origin=null
     *       message: CALL 'public final fun makeStr (): kotlin.String declared in com.k7.MyLib' type=kotlin.String origin=null
     *         $this: GET_VAR 'val l: com.k7.MyLib [val] declared in <root>.someFunction' type=com.k7.MyLib origin=null
     *
     */
    private fun transformTargetFunction(
        pluginContext: IrPluginContext,
        targetFunction: IrFunction,
    ): IrBlockBody? {
        val body: IrBody? = targetFunction.body
        if (body != null) {

            val declarationBuilder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = targetFunction.symbol
            )

            val newbody = declarationBuilder.irBlockBody {
                val newValStatement = buildVarIrStatement(pluginContext, targetFunction, this)
                if (newValStatement != null) {
                    +newValStatement
                    val q = buildPrintStatement(
                        pluginContext, this,
                        newValStatement
                    )

                    if (q != null) {
                        +q
                    }
                }

                body.statements.forEach {
                    +it
                }
            }

            return newbody
        } else {
            return null
        }
    }

    val myLibClassId = ClassId(FqName("com.k7"),Name.identifier("MyLib"))
    val printlnCallId = CallableId(FqName("kotlin.io"),Name.identifier("println"))

    /**
     *  CONSTRUCTOR_CALL 'public constructor <init> () [primary] declared in com.k7.MyLib' type=com.k7.MyLib origin=null
     */
    private fun buildConstructor(
        pluginContext: IrPluginContext,
        irBuilder: IrBuilderWithScope
    ): IrConstructorCall? {
        val constructorSymbol = pluginContext.referenceConstructors(myLibClassId).firstOrNull {
            it.owner.valueParameters.isEmpty()
        }
        val call = constructorSymbol?.let { irBuilder.irCallConstructor(it, emptyList()) }
        return call
    }

    /**
     * Build this statement: val myNewVariable = strValue
     *    VAR name:l type:com.k7.MyLib [val]
     *      CONSTRUCTOR_CALL 'public constructor <init> () [primary] declared in com.k7.MyLib' type=com.k7.MyLib origin=null
     */
    private fun buildVarIrStatement(
        pluginContext: IrPluginContext,
        targetFunction: IrFunction,
        irBuilder: IrBuilderWithScope,
    ): IrVariable? {
        val irConstructorCall: IrConstructorCall? = buildConstructor(pluginContext, irBuilder)
        val valType = pluginContext.referenceClass(myLibClassId)
        if (valType != null && irConstructorCall != null) {
            val myLibVal = buildVariable(
                parent = targetFunction,
                startOffset = targetFunction.startOffset,
                endOffset = targetFunction.endOffset,
                origin = IrDeclarationOrigin.DEFINED,
                name = Name.identifier("myLib"),
                type = valType.defaultType
            ).apply {
                this.initializer = irConstructorCall
            }
            return myLibVal
        } else {
            return null
        }
    }

    /**
     * CALL 'public final fun println (message: kotlin.Any?): kotlin.Unit [inline] declared in kotlin.io.ConsoleKt' type=kotlin.Unit origin=null
     *    message: CALL 'public final fun makeStr (): kotlin.String declared in com.k7.MyLib' type=kotlin.String origin=null
     *       $this: GET_VAR 'val l: com.k7.MyLib [val] declared in <root>.someFunction' type=com.k7.MyLib origin=null
     */
    private fun buildPrintStatement(
        pluginContext: IrPluginContext,
        irBuilder: IrBuilderWithScope,
        myLibObjectVariable: IrVariable
    ): IrCall? {

        val functionName = CallableId(myLibClassId,Name.identifier("makeStr"))
        val makeStrFunctionSymbol = pluginContext.referenceFunctions(functionName).first()

        val makeStrCall = irBuilder.irCall(makeStrFunctionSymbol)

        makeStrCall.dispatchReceiver = irBuilder.irGet(myLibObjectVariable)


        val typeAnyOrNull = pluginContext.irBuiltIns.anyNType


        val strConcat = irBuilder.irConcat().apply {
            this.addArgument(irBuilder.irString("makeStr function: "))
            this.addArgument(makeStrCall)
        }

        val printlnFunction = pluginContext.referenceFunctions(printlnCallId).firstOrNull {
            it.owner.valueParameters.let {
                it.size == 1 && it[0].type == typeAnyOrNull
            }
        }

        val printlnCall = printlnFunction?.let {
            irBuilder.irCall(it).apply {
                this.putValueArgument(0, strConcat)
            }
        }

        return printlnCall
    }
}
