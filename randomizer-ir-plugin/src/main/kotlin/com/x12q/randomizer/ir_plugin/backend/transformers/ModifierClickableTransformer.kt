package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.ir_plugin.backend.utils.UtilIRBuilderImp
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Modify the Modifier.clickable{} call so that it include these two lines:
 *  val myLib = MyLib()
 *  println(myLib.makeStr())
 */
class ModifierClickableTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

    override fun visitCall(expression: IrCall): IrExpression {
        modifyClickable(expression)
        modifyButtonOnClick(expression)
        return super.visitCall(expression)
    }

    private fun modifyClickable(irCall: IrCall) {
        if (isClickableModifier(irCall)) {
            val onClickLambdaArg: IrExpression? = irCall.getValueArgument(3)
            val onClickLambda = (onClickLambdaArg as? IrFunctionExpression)?.function

            if(onClickLambda != null){
                val body = onClickLambda.body
                val declarationIrBuilder = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = onClickLambda.symbol
                )

                val newBody = declarationIrBuilder.irBlockBody {
                    // add a println
                    val newValStatement = buildVarIrStatement(pluginContext, onClickLambda, this)
                    if (newValStatement != null) {
                        +newValStatement
                        val printStm = buildPrintStatement(
                            pluginContext, this,
                            newValStatement
                        )

                        if (printStm != null) {
                            +printStm
                        }
                    }

                    body?.statements?.forEach {stm->
                        +stm
                    }
                }
                onClickLambda?.body = newBody
            }
        }
    }

    /**
     * A primitive predicate to check if an [IrCall] is named "clickable" or not.
     * This is used for identifying the Modifier.clickable{} function call.
     * This will be dangerous in production, but for the purpose of demonstration, I prefer keeping it as simple as possible.
     */
    private fun isClickableModifier(irCall: IrCall): Boolean {
        val nameIsClickable = irCall.symbol.owner.name == Name.identifier("clickable")
        return nameIsClickable
    }

    /**
     * Class id of MyLib class
     */
    private val myLibClassId = ClassId(FqName("com.k7"), Name.identifier("MyLib"))

    /**
     * Build an [IrVariable] that stores an instance of MyLib class
     */
    private fun buildVarIrStatement(
        pluginContext: IrPluginContext,
        targetFunction: IrFunction,
        irBuilder: IrBuilderWithScope,
    ): IrVariable? {
        val irConstructorCall: IrConstructorCall? = buildConstructorCall(pluginContext, irBuilder)
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
     * Check if the [irCall] is a:
     * `@Composable Button(onClick = {...}){...}`
     */
    private fun modifyButtonOnClick(irCall: IrCall) {
        if (isButtonOnClick(irCall)) {
            val onClickLambdaArg: IrExpression? = irCall.getValueArgument(0)!!
            val onClickLambda = (onClickLambdaArg as IrFunctionExpression).function
            val body = onClickLambda.body!!
            val declarationIrBuilder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = onClickLambda.symbol,
            )
            val newBody = declarationIrBuilder.irBlockBody {
                // add a println
                val newValStatement = buildVarIrStatement(pluginContext, onClickLambda, this)
                if (newValStatement != null) {
                    +newValStatement
                    val printStm = buildPrintStatement(
                        pluginContext, this,
                        newValStatement
                    )

                    if (printStm != null) {
                        +printStm
                    }
                }

                for (stm in body.statements) {
                    +stm
                }
            }
            onClickLambda.body = newBody
        }
    }

    private fun isButtonOnClick(irCall: IrCall): Boolean {
        val nameIsButton = irCall.symbol.owner.name == Name.identifier("Button")
        val composableAnnotation = pluginContext.referenceClass(
            ClassId(
                FqName("androidx.compose.runtime"), Name.identifier("Composable")
            )
        )!!

        val isAnnotatedWithComposable = irCall.symbol.owner.annotations.any {
            it.symbol.owner.parentAsClass.symbol == composableAnnotation
        }

        val dname = FqName("androidx.compose.material.ButtonKt")
        val decl = irCall.attributeOwnerId.extractRelatedDeclaration()

        return nameIsButton && isAnnotatedWithComposable
    }

    /**
     * Build a [IrConstructorCall] to create an instance of MyLib class by calling its constructor
     */
    private fun buildConstructorCall(
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
     * Build an [IrCall] to call println(mylib.makeStr())
     */
    private fun buildPrintStatement(
        pluginContext: IrPluginContext,
        irBuilder: IrBuilderWithScope,
        myLibObjectVariable: IrVariable
    ): IrCall? {
        val uBuilder = UtilIRBuilderImp(pluginContext, irBuilder)

        val functionName = CallableId(myLibClassId, Name.identifier("makeStr"))
        val makeStrFunctionSymbol = pluginContext.referenceFunctions(functionName).first()
        val makeStrCall = irBuilder.irCall(makeStrFunctionSymbol)
        makeStrCall.dispatchReceiver = irBuilder.irGet(myLibObjectVariable)

        val strConcat = irBuilder.irConcat().apply {
            this.addArgument(irBuilder.irString("makeStr function: "))
            this.addArgument(makeStrCall)
        }
        val printlnFunction = uBuilder.printlnIr(strConcat)
        return printlnFunction
    }

}
