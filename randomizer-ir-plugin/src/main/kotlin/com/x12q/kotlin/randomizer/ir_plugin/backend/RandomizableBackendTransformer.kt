package com.x12q.kotlin.randomizer.ir_plugin.backend

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib.RandomConfigAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib.RandomContextAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib.UnableToMakeRandomExceptionAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.std_lib.RandomAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.std_lib.collections.ArrayAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.std_lib.collections.ListAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.std_lib.collections.MapAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.std_lib.collections.SetAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.reporting.ListReportData
import com.x12q.kotlin.randomizer.ir_plugin.backend.reporting.MapKeyReportData
import com.x12q.kotlin.randomizer.ir_plugin.backend.reporting.MapValueReportData
import com.x12q.kotlin.randomizer.ir_plugin.backend.reporting.ParamReportData
import com.x12q.kotlin.randomizer.ir_plugin.backend.reporting.ReportData
import com.x12q.kotlin.randomizer.ir_plugin.backend.support.InitMetaData
import com.x12q.kotlin.randomizer.ir_plugin.backend.support.TypeMap
import com.x12q.kotlin.randomizer.ir_plugin.backend.support.TypeParamOrArg
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.dotCall
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.extensionDotCall
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.getArgAtParam
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.getMakeRandomParam
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.getTypeParamFromGenericParam
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isAbstract
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isAnnotatedWithRandomizable
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isAny2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isArrayList
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isBoolean2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isByte2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isChar2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isDouble2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isFinalOrOpenConcrete
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isFloat2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isGeneric
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isHashMap
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isHashSet
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isInt2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isLinkedHashMap
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isLinkedHashSet
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isListAssignable
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isLong2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isMap
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isNothing2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isNumber2
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.*
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib.*
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isRandomFunctions
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isSealed
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isSet
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isShort2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isString2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isUByte2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isUInt2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isULong2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isUShort2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.isUnit2
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.makeTypeMap
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.withTypeArgs
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.withValueArgs
import com.x12q.kotlin.randomizer.ir_plugin.backend.reporting.*
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.*
import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.ir_plugin.util.stopAtFirstNotNull
import com.x12q.kotlin.randomizer.lib.*
import com.x12q.kotlin.randomizer.lib.annotations.Randomizable
import com.x12q.kotlin.randomizer.lib.util.developerErrorMsg
import com.x12q.kotlin.randomizer.lib.util.impossibleErr
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.fir.generateTemporaryVariable
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.IrFunctionBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addTypeParameter
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrThrowImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrWhenImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import javax.inject.Inject


/**
 * Perform the main IR transformation for the whole plugin
 */
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
    private val randomAccessor: RandomAccessor,
    private val randomConfigAccessor: RandomConfigAccessor,
    private val randomContextAccessor: RandomContextAccessor,
    private val unableToMakeRandomExceptionAccessor: UnableToMakeRandomExceptionAccessor,
    private val listAccessor: ListAccessor,
    private val mapAccessor: MapAccessor,
    private val setAccessor: SetAccessor,
    private val arrayAccessor: ArrayAccessor,
    private val randomizableAccessor: RandomizableAccessor,
) : RDBackendTransformer() {

    override fun visitCall(expression: IrCall): IrExpression {
        completeRandomFunctionCall(expression)
        return super.visitCall(expression)
    }

    private fun extractClassReferencesFromRandomizableAnnotation(targetClass: IrClass): List<IrClassReference> {
        val randomizableAnnotation = targetClass.getAnnotation(Randomizable::class)
        val classListParamArgPair: Pair<IrValueParameter, IrExpression?>? =
            randomizableAnnotation?.getAllArgumentsWithIr()
                ?.firstOrNull { (param, _) ->
                    param.name == randomizableAccessor.classesParamName
                }
        if (classListParamArgPair != null) {
            val arg = classListParamArgPair.second
            if (arg == null) {
                return emptyList()
            }

            val classReferences = (arg as? IrVararg)?.elements?.mapNotNull { it as? IrClassReference }
                ?: emptyList()

            validateClassInRandomizableAnnotationOrCrash(classReferences)

            return classReferences
        } else {
            return emptyList()
        }
    }

    private fun extractIrClassesFromRandomizableAnnotation(targetClass: IrClass): List<IrClass> {
        return extractClassReferencesFromRandomizableAnnotation(targetClass).mapNotNull { it.classType.classOrNull?.owner }
    }

    /**
     * Check if all class ref in [classRefList] is legal or not. Crash on illegal ones.
     */
    private fun validateClassInRandomizableAnnotationOrCrash(classRefList: List<IrClassReference>) {
        for (classRef in classRefList) {
            val clzz = classRef.classType.classOrNull?.owner
                .crashOnNull { impossibleErr("ClassRef $classRef passed to ${randomizableAccessor.classId} is from a null class.") }
            if (clzz.isInterface || clzz.modality == ABSTRACT) {
                throw IllegalArgumentException("${clzz} must not be abstract")
            }
        }
    }

    /**
     * This involves:
     * - completing "makeRandom" lambda
     */
    private fun completeRandomFunctionCall(irCall: IrCall) {
        val function = irCall.symbol.owner
        if (!isRandomFunctions(function)) {
            return
        }

        val randomFunction = function

        completeMakeRandomLambda(
            randomFunctionCall = irCall,
            randomFunction = randomFunction,
        )
    }

    private fun completeMakeRandomLambda(randomFunctionCall: IrCall, randomFunction: IrSimpleFunction) {

        val makeRandomLambdaParam = randomFunction.getMakeRandomParam()
            .crashOnNull { developerErrorMsg("makeRandom param does not exist") }

        val providedMakeRandomArg = randomFunctionCall.getArgAtParam(makeRandomLambdaParam)

        if (providedMakeRandomArg == null) {

            /**
             * makeRandom arg is not provided => generate a default one.
             * Signature (randomContext: RandomContext)->T, with T being the type arg passed to the call
             */

            val makeRandomReturnType = randomFunctionCall.typeArguments.firstOrNull()
                .crashOnNull { developerErrorMsg("Type argument of makeRandom cannot be null") }

            val makeRandomLambda = generateMakeRandomLambda(
                returnType = makeRandomReturnType,
                declarationParent = currentDeclarationParent,
            )

            val makeRandomLambdaArg = makeIrFunctionExpr(
                lambda = makeRandomLambda,
                functionType = pluginContext.irBuiltIns.functionN(1)
                    .typeWith(
                        randomContextAccessor.irType,
                        makeRandomReturnType
                    )
            )
            randomFunctionCall.putValueArgument(makeRandomLambdaParam.index, makeRandomLambdaArg)
        }

    }

    private fun generateMakeRandomLambda(
        returnType: IrType,
        declarationParent: IrDeclarationParent?,
    ): IrSimpleFunction {
        val newMakeRandomLambda: IrSimpleFunction = makeLocalLambdaWithoutBody_forMakeRandom(
            returnType = returnType,
            visibility = DescriptorVisibilities.LOCAL,
        )

        if (declarationParent != null) {
            newMakeRandomLambda.parent = declarationParent
        }

        newMakeRandomLambda.addValueParameter(
            name = Name.identifier("randomContextzzzz"),
            type = randomContextAccessor.irType,
            origin = BaseObjects.declarationOrigin
        )

        newMakeRandomLambda.addTypeParameter(
            name = "T",
            upperBound = randomContextAccessor.irType
        )

        newMakeRandomLambda.addTypeParameter(
            name = "E",
            upperBound = returnType
        )

        val body = generateMakeRandomBody(
            makeRandomFunction = newMakeRandomLambda,
            initMetadata = InitMetaData(
                targetClass = returnType.classOrNull?.owner
                    .crashOnNull { developerErrorMsg("class passed to random() function must not be null") },
                initTypeMap = returnType.makeTypeMap()
            ),
        )

        newMakeRandomLambda.body = body
        return newMakeRandomLambda
    }

    /**
     * Construct random() function body, used in both the 1st and 2nd random() function.
     */
    private fun generateMakeRandomBody(
        makeRandomFunction: IrFunction,
        initMetadata: InitMetaData,
    ): IrBlockBody {
        val builder = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = makeRandomFunction.symbol,
        )
        return builder.irBlockBody {

            val target: IrClass = initMetadata.targetClass

            val getRandomContextExpr = builder.irGet(makeRandomFunction.valueParameters[0])

            val randomConfigTempVar = irTemporary(
                value = getRandomContextExpr.dotCall(randomContextAccessor.randomConfig(builder)),
                irType = randomConfigAccessor.irType,
                nameHint = "randomConfigInMakeRandom"
            )

            val makeRandomPrimaryClass = generateRandomPrimaryClass(
                irClass = target,
                builder = builder,
                declarationParent = makeRandomFunction,
                getRandomContextExpr = getRandomContextExpr,
                getRandomConfigExpr = builder.irGet(randomConfigTempVar),
                randomFunctionMetaData = initMetadata,
            )
            if (makeRandomPrimaryClass != null) {
                +builder.irReturn(makeRandomPrimaryClass)
            } else {
                throw IllegalArgumentException("unable generate random for primary target [$target].")
            }
        }
    }

    /**
     * Generate a random instance of the primary class returned by random() function.
     */
    private fun generateRandomPrimaryClass(
        irClass: IrClass,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
        declarationParent: IrDeclarationParent?,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
    ): IrExpression? {
        return generateRandomClass(
            declarationParent = declarationParent,
            // this is the starting point, so there's no received type arguments.
            receivedTypeArguments = emptyList(),
            irType = null,
            irClass = irClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            param = null,
            enclosingClass = null,
            initMetadata = randomFunctionMetaData,
            prevTypeMap = randomFunctionMetaData.initTypeMap
        )
    }


    /**
     * Generate an [IrExpression] that can return a random instance of [irClass]
     */
    private fun generateRandomClass(
        irClass: IrClass,
        irType: IrType?,
        /**
         * Declaration parent for generated lambda downstream
         */
        declarationParent: IrDeclarationParent?,
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * Param that hold the instance returned by this function
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        initMetadata: InitMetaData,
        prevTypeMap: TypeMap,
    ): IrExpression? {
        if (irClass.isInner) {
            throw IllegalArgumentException("Inner class is not supported for now.")
        }

        val rt = stopAtFirstNotNull(
            { generateRandomObj(irClass, builder) },
            { generateRandomEnum(irClass, getRandomContextExpr, builder) },
            {
                generateRandomConcreteClass(
                    irClass = irClass,
                    irType = irType,
                    builder = builder,
                    declarationParent = declarationParent,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    randomFunctionMetaData = initMetadata,
                )
            },
            {
                generateStdCollection(
                    collectionClass = irClass,
                    irType = irType,
                    param = param,
                    builder = builder,
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    enclosingClass = enclosingClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    randomFunctionMetaData = initMetadata,
                )
            },
            {
                generateRandomSealClass(
                    irClass = irClass,
                    builder = builder,
                    getRandomContextExpr = getRandomContextExpr,
                    randomFunctionMetaData = initMetadata
                )
            },
            {
                generateRandomAbstractClassAndInterface(
                    irClass = irClass,
                    irType = irType.crashOnNull {
                        developerErrorMsg("type of interface cannot be null")
                    },
                    builder = builder,
                    declarationParent = declarationParent,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    initMetadata = initMetadata,
                )
            })
        return rt
    }

    private fun generateStdCollection(
        declarationParent: IrDeclarationParent?,
        receivedTypeArguments: List<IrTypeArgument>?,
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irType: IrType?,
        collectionClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        val rt = stopAtFirstNotNull(
            {
                generateArray(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    arrayIrClass = collectionClass,
                    irListType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateArrayList(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    listIrClass = collectionClass,
                    irListType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateHashMap(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    mapIrClass = collectionClass,
                    irMapType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateLinkedHashMap(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    mapIrClass = collectionClass,
                    irMapType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateMap(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    mapIrClass = collectionClass,
                    irMapType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateList(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    listIrClass = collectionClass,
                    irListType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateHashSet(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    setIrClass = collectionClass,
                    irListType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateLinkedHashSet(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    setIrClass = collectionClass,
                    irListType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            },
            {
                generateSet(
                    declarationParent = declarationParent,
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    enclosingClass = enclosingClass,
                    setIrClass = collectionClass,
                    irListType = irType,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    randomFunctionMetaData = randomFunctionMetaData,
                )
            }

        )
        return rt
    }

    /**
     * Generate an expression that will invoke List{} function to create a list of random size, holding random elements.
     */
    private fun generateArrayList(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        listIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        return templateGenerateListDerivative(
            classCheck = { listIrClass.isArrayList() },
            factoryFunctionCall = listAccessor.makeArrayList(builder),
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irListType = irListType,
            listIrClass = listIrClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
    }

    private fun generateArray(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        arrayIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        return templateGenerateListDerivative(
            classCheck = { arrayAccessor.isArray(arrayIrClass) },
            factoryFunctionCall = arrayAccessor.makeArray(builder),
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irListType = irListType,
            listIrClass = arrayIrClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
    }

    /**
     * A template to generate collection derived from [List]
     */
    private fun templateGenerateListDerivative(
        classCheck: () -> Boolean,
        factoryFunctionCall: IrCall,
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        listIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        if (!classCheck()) {
            return null
        }

        val elementTypes = extractTypeArgument(
            receivedTypeArgument = receivedTypeArguments, irType = irListType
        ).firstOrNull()

        if (elementTypes != null) {
            val listExpr = generateList(
                declarationParent = declarationParent,
                receivedTypeArguments = receivedTypeArguments,
                param = param,
                enclosingClass = enclosingClass,
                irListType = irListType,
                listIrClass = listAccessor.clzz.owner,
                getRandomContextExpr = getRandomContextExpr,
                getRandomConfigExpr = getRandomConfigExpr,
                builder = builder,
                randomFunctionMetaData = randomFunctionMetaData,
            )
            if (listExpr != null) {
                val tt = elementTypes.typeOrNull.crashOnNull {
                    val paramNamePrefix = param?.let { "${param.name}:" } ?: ""
                    "$paramNamePrefix Set's element type must be specified. It is null here."
                }
                return factoryFunctionCall
                    .withTypeArgs(tt)
                    .withValueArgs(listExpr)
            } else {
                return null
            }
        } else {
            return null
        }
    }

    /**
     * Generate an expression that will invoke List{} function to create a list of random size, holding random elements.
     */
    private fun generateList(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        listIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {


        if (!listIrClass.isListAssignable()) {
            return null
        }

        val elementTypes = extractTypeArgument(
            receivedTypeArgument = receivedTypeArguments, irType = irListType
        ).firstOrNull()

        if (elementTypes != null) {
            val type = elementTypes.typeOrNull.crashOnNull {
                val paramNamePrefix = param?.let { "${param.name}:" } ?: ""
                "$paramNamePrefix List's element type must be specified. It is null here."
            }

            val randomElementLambdaExpr = run {

                /**
                 * This block generates the lambda that will be passed to makeList() function
                 */

                val lambdaDeclaration = makeLocalLambdaWithoutBody(type, {
                    name = Name.special("<generateList_withinRandomFunction>")
                }).apply {
                    val lambdaFunction = this

                    declarationParent?.also { parent = it }

                    addValueParameter("index", pluginContext.irBuiltIns.intType)

                    val lambdaBuilder = DeclarationIrBuilder(
                        generatorContext = pluginContext,
                        symbol = this.symbol,
                    )
                    val randomElementExpr = generateRandomType(
                        declarationParent = lambdaFunction,
                        constructorParam = null,
                        enclosingClass = null,
                        receivedType = type,
                        targetType = type,
                        builder = builder,
                        getRandomContextExpr = getRandomContextExpr,
                        getRandomConfigExpr = getRandomConfigExpr,
                        initMetaData = randomFunctionMetaData,
                        optionalParamMetaDataForReporting = ListReportData(
                            valueType = type.dumpKotlinLike(),
                            paramName = param?.name?.asString(),
                            enclosingClassName = enclosingClass?.name?.asString(),
                        ),
                        typeMap = TypeMap.Companion.emptyTODO,
                        tempVarName = null
                    )
                    body = lambdaBuilder.irBlockBody {
                        +irReturn(randomElementExpr)
                    }
                }

                makeIrFunctionExpr(
                    lambda = lambdaDeclaration,
                    functionType = pluginContext.irBuiltIns.functionN(1)
                        .typeWith(pluginContext.irBuiltIns.intType, type)
                )
            }

            val sizeExpr = getRandomConfigExpr.dotCall(
                randomConfigAccessor.randomCollectionSize(builder)
            )

            val rt = listAccessor.makeList(builder)
                .withValueArgs(sizeExpr, randomElementLambdaExpr)
                .withTypeArgs(type)

            return rt

        } else {
            return null
        }
    }


    /**
     * Generate an expression that can generate a random Map
     */
    private fun generateMap(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the map
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irMapType: IrType?,
        mapIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        if (!mapIrClass.isMap()) {
            return null
        }

        // get element type
        val elementTypes = extractTypeArgument(
            receivedTypeArgument = receivedTypeArguments, irType = irMapType
        ).take(2)

        if (elementTypes.size == 2) {
            val keyTypeArg = elementTypes[0]
            val valueTypeArg = elementTypes[1]

            /**
             * Generate an expr that do something like this:
             * makeMap(size, pairFactoryFunction)
             */
            val sizeExpr = getRandomConfigExpr.dotCall(
                randomConfigAccessor.randomCollectionSize(builder)
            )

            val keyType = keyTypeArg.typeOrNull.crashOnNull {
                "Key type passed to makeMap() must not be null. This is a bug."
            }

            val makeKeyLambdaExpr: IrExpression = run {
                val lambda = makeLocalLambdaWithoutBody(keyType).buildBody { keyLambdaBuilder ->
                    declarationParent?.let { parent = it }
                    val keyLambda = this
                    body = keyLambdaBuilder.irBlockBody {
                        // call something to return a random key
                        val randomKey = generateRandomType(
                            declarationParent = keyLambda,
                            constructorParam = null,
                            enclosingClass = enclosingClass,
                            receivedType = keyType,
                            targetType = keyType,
                            builder = keyLambdaBuilder,
                            getRandomContextExpr = getRandomContextExpr,
                            getRandomConfigExpr = getRandomConfigExpr,
                            initMetaData = randomFunctionMetaData,
                            optionalParamMetaDataForReporting = MapKeyReportData(
                                keyType = keyType.dumpKotlinLike(),
                                paramName = param?.name?.asString(),
                                enclosingClassName = enclosingClass?.name?.asString(),
                            ),
                            typeMap = TypeMap.Companion.emptyTODO,
                            tempVarName = null
                        )
                        +keyLambdaBuilder.irReturn(randomKey)
                    }
                }
                makeIrFunctionExpr(
                    lambda = lambda,
                    functionType = pluginContext.irBuiltIns.functionN(0).typeWith(keyType),
                )
            }

            val valueType = valueTypeArg.typeOrNull.crashOnNull {
                "Value type passed to makeMap() must not be null. This is a bug."
            }
            val makeValueLambda: IrExpression = run {
                val valueLambda = makeLocalLambdaWithoutBody(valueType).buildBody { valueLambdaBuilder ->
                    declarationParent?.let { parent = it }
                    val valueLambda = this
                    body = valueLambdaBuilder.irBlockBody {
                        // call something to return a random value
                        val randomValue = generateRandomType(
                            declarationParent = valueLambda,
                            constructorParam = null,
                            enclosingClass = enclosingClass,
                            receivedType = valueType,
                            targetType = valueType,
                            builder = valueLambdaBuilder,
                            getRandomContextExpr = getRandomContextExpr,
                            getRandomConfigExpr = getRandomConfigExpr,
                            initMetaData = randomFunctionMetaData,
                            optionalParamMetaDataForReporting = MapValueReportData(
                                valueType = valueType.dumpKotlinLike(),
                                paramName = param?.name?.asString(),
                                enclosingClassName = enclosingClass?.name?.asString(),
                            ),
                            typeMap = TypeMap.Companion.emptyTODO,
                            tempVarName = null
                        )
                        +valueLambdaBuilder.irReturn(randomValue)
                    }
                }
                makeIrFunctionExpr(
                    lambda = valueLambda,
                    functionType = pluginContext.irBuiltIns.functionN(0).typeWith(valueType),
                )
            }

            val makeMapFunctionCall = mapAccessor.makeMapFunction(builder)
                .withValueArgs(sizeExpr, makeKeyLambdaExpr, makeValueLambda)
                .withTypeArgs(keyType, valueType)

            return makeMapFunctionCall
        } else {
            return null
        }
    }


    private fun generateHashMap(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the map
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irMapType: IrType?,
        mapIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        val rt = templateToGenerateMap(
            isMapCheck = { mapIrClass.isHashMap() },
            makeMapFunctionCall = mapAccessor.makeHashMap(builder),
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irMapType = irMapType,
            mapIrClass = mapIrClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
        return rt
    }

    private fun generateLinkedHashMap(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the map
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irMapType: IrType?,
        mapIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        return templateToGenerateMap(
            isMapCheck = { mapIrClass.isLinkedHashMap() },
            makeMapFunctionCall = mapAccessor.makeLinkedHashMap(builder),
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irMapType = irMapType,
            mapIrClass = mapIrClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
    }


    private fun templateToGenerateMap(
        isMapCheck: () -> Boolean,
        makeMapFunctionCall: IrCall,
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the map
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irMapType: IrType?,
        mapIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        if (!isMapCheck()) {
            return null
        }
        val mapExpr = generateMap(
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irMapType = irMapType,
            mapIrClass = mapAccessor.clzz.owner,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
        if (mapExpr != null) {
            val elementTypes = extractTypeArgument(
                receivedTypeArgument = receivedTypeArguments, irType = irMapType
            ).take(2)
            val keyTypeArg = elementTypes[0]
            val valueTypeArg = elementTypes[1]
            return makeMapFunctionCall
                .withTypeArgs(keyTypeArg.typeOrFail, valueTypeArg.typeOrFail)
                .withValueArgs(mapExpr)
        } else {
            return null
        }
    }


    private fun generateSet(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        setIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        return templateToGenerateSet(
            setCheck = { setIrClass.isSet() },
            listToSetFunctionCall = { setAccessor.listToSet(builder) },
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irListType = irListType,
            setIrClass = setIrClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
    }

    private fun generateHashSet(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        setIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        return templateToGenerateSet(
            setCheck = { setIrClass.isHashSet() },
            listToSetFunctionCall = { setAccessor.makeHashSet(builder) },
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irListType = irListType,
            setIrClass = setIrClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
    }

    private fun generateLinkedHashSet(
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        setIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        return templateToGenerateSet(
            setCheck = { setIrClass.isLinkedHashSet() },
            listToSetFunctionCall = { setAccessor.makeLinkedHashSet(builder) },
            declarationParent = declarationParent,
            receivedTypeArguments = receivedTypeArguments,
            param = param,
            enclosingClass = enclosingClass,
            irListType = irListType,
            setIrClass = setIrClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData,
        )
    }


    private fun templateToGenerateSet(
        setCheck: () -> Boolean,
        listToSetFunctionCall: () -> IrCall,
        declarationParent: IrDeclarationParent?,
        /**
         * Typed received externally
         */
        receivedTypeArguments: List<IrTypeArgument>?,
        /**
         * The param that holds the list
         */
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        irListType: IrType?,
        setIrClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        if (!setCheck()) {
            return null
        }

        val elementTypeArg = extractTypeArgument(
            receivedTypeArgument = receivedTypeArguments, irType = irListType
        ).firstOrNull()

        if (elementTypeArg != null) {

            val elementType = elementTypeArg.typeOrNull.crashOnNull {
                val paramNamePrefix = param?.let { "${param.name}:" } ?: ""
                "$paramNamePrefix Set's element type must be specified. It is null here."
            }

            val listExpr = generateList(
                declarationParent = declarationParent,
                receivedTypeArguments = receivedTypeArguments,
                param = param,
                enclosingClass = enclosingClass,
                irListType = irListType,
                listIrClass = listAccessor.clzz.owner,
                getRandomContextExpr = getRandomContextExpr,
                getRandomConfigExpr = getRandomConfigExpr,
                builder = builder,
                randomFunctionMetaData = randomFunctionMetaData,
            )
            if (listExpr != null) {
                return listToSetFunctionCall()
                    .withTypeArgs(elementType)
                    .withValueArgs(listExpr)
            } else {
                return null
            }
        } else {
            return null
        }
    }


    private fun generateRandomObj(
        irClass: IrClass,
        builder: DeclarationIrBuilder,
    ): IrExpression? {
        if (irClass.isObject) {
            return builder.irGetObject(irClass.symbol)
        } else {
            return null
        }
    }

    /**
     * Random enum is generate using either "entries" or "values" from the enum class.
     */
    private fun generateRandomEnum(
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression? {
        if (irClass.isEnumClass) {
            val getRandom = getRandomConfigExpr.dotCall(randomConfigAccessor.random(builder))
            if (irClass.hasEnumEntries) {
                // make an IR to access "entries"

                val irEntries = irClass.declarations.firstOrNull {
                    it.getNameWithAssert().toString() == "entries"
                } as? IrProperty

                val irEntriesFunction = irEntries?.getter.crashOnNull {
                    "enum ${irClass.name} does not have \"entries\" field"
                }

                // then call randomFunction on "entries" accessor ir
                val rt = builder.irCall(irEntriesFunction)
                    .extensionDotCall(builder.irCall(randomAccessor.randomOnCollectionOneArg))
                    .withValueArgs(getRandom)

                return rt
            } else {
                // make an IR to access "values"

                val irValuesFunction = (irClass.declarations
                    .firstOrNull { it.getNameWithAssert().toString() == "values" } as? IrFunction)

                val irValues = irValuesFunction
                    .crashOnNull { "enum ${irClass.name} does not have \"values\" field" }

                val rt = builder.irCall(irValues)
                    .extensionDotCall(builder.irCall(randomAccessor.randomFunctionOnArrayOneArg))
                    .withValueArgs(getRandom)
                return rt
            }

            throw IllegalArgumentException("Impossible - Enum ${irClass.name} does not have entries or values()")

        } else {
            return null
        }
    }

    /**
     * Concrete class is final or open class that is:
     * - not abstract
     * - not enum
     * - not object
     */
    private fun generateRandomConcreteClass(
        declarationParent: IrDeclarationParent?,
        irType: IrType?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {

        if (irClass.isArrayList() ||
            irClass.isHashSet() ||
            irClass.isLinkedHashSet() ||
            irClass.isHashMap() ||
            irClass.isLinkedHashMap() ||
            arrayAccessor.isArray(irClass) ||
            irClass.isObject ||
            irClass.isEnumClass ||
            !irClass.isFinalOrOpenConcrete()
        ) {
            // these cases are handled by other dedicated functions, so return null here.
            return null
        }

        val randomPrimitiveExpr = generateRandomPrimitive(
            type = irType ?: irClass.defaultType,
            builder = builder,
            getRandomContext = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
        )

        if (randomPrimitiveExpr != null) {
            return randomPrimitiveExpr
        }

        val baseTypeMap: TypeMap = randomFunctionMetaData.initTypeMap
        val typeMapFromType: TypeMap = irType?.makeTypeMap() ?: TypeMap.Companion.empty

        val constructor = getConstructor(irClass)

        if (constructor != null) {
            val constructorParamsWithIndex: Iterable<IndexedValue<IrValueParameter>> =
                constructor.valueParameters.withIndex()
            val paramExpressions: List<IrExpression> = constructorParamsWithIndex.map { (_, param) ->

                val typeMapFromParam: TypeMap = param.makeTypeMap()

                val paramTypeMap: TypeMap = typeMapFromParam
                    .mergeAndOverwriteWith(typeMapFromType)
                    .bridgeType(baseTypeMap)
                    .mergeAndOverwriteWith(baseTypeMap)

                val receivedType: IrType? = if (param.isGeneric()) {
                    paramTypeMap.get(param.getTypeParamFromGenericParam())
                } else {
                    null
                }?.getIrTypeOrNull()

                generateRandomConstructorParam(
                    declarationParent = declarationParent,
                    receivedType = receivedType,
                    param = param,
                    enclosingClass = irClass,
                    builder = builder,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    randomFunctionMetaData = randomFunctionMetaData,
                    typeMapForParam = paramTypeMap,
                )
            }

            val constructorCall = builder.irCallConstructor(
                callee = constructor.symbol, typeArguments = emptyList()
            ).withValueArgs(paramExpressions)

            return constructorCall

        } else {
            return throwUnableToRandomizeException(
                builder, "${irClass.name} does not have a usable constructor"
            )
        }
    }

    /**
     * Extract concrete type from provided generic type argument from various sources.
     * TODO this function is problematic, try to find a way to replace it with TypeMap
     */
    private fun extractTypeArgument(
        /**
         * Explicit provided type, highest priority
         */
        receivedTypeArgument: List<IrTypeArgument>?,
        /**
         * just some IrType object that may contains type information in its argument
         */
        irType: IrType?,
    ): List<IrTypeArgument> {
        val rt: List<IrTypeArgument> = stopAtFirstNotNull(
            { receivedTypeArgument },
            { (irType as? IrSimpleType)?.arguments },
        ) ?: emptyList()
        return rt
    }

    private fun ifNotNullElse(
        type: IrType,
        candidate: IrExpression,
        onCandidateNotNull: IrExpression,
        onCandidateNull: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {
        return builder.irIfNull(type, candidate, onCandidateNull, onCandidateNotNull)
    }

    /**
     * Construct an if-else expression using `RandomConfig.nextBool` as condition. Like this
     * ```
     * if(randomConfig.nextBool()){
     *    [randomPart]
     * }else{
     *    null << always return null on else
     * }
     * ```
     */
    private fun randomOrNull(
        builder: DeclarationIrBuilder,
        /**
         * An expr to get a [RandomConfig]
         */
        getRandomContext: IrExpression,
        /**
         * this is the return type of the if-else expr
         */
        type: IrType,
        randomPart: IrExpression,
    ): IrExpression {
        return randomIfElse(
            builder = builder,
            getRandomContextExpr = getRandomContext,
            type = type,
            truePart = randomPart,
            elsePart = builder.irNull()
        )
    }

    /**
     * Construct an if-else expression using `RandomConfig.nextBool` as condition. Like this
     * ```
     * if(randomConfig.nextBool()){
     *    [truePart]
     * }else{
     *    [elsePart]
     * }
     * ```
     */
    private fun randomIfElse(
        builder: DeclarationIrBuilder,
        /**
         * An expr to get a [RandomConfig]
         */
        getRandomContextExpr: IrExpression,
        /**
         * this is the return type of the if-else expr
         */
        type: IrType, truePart: IrExpression, elsePart: IrExpression
    ): IrExpression {
        val conditionExpr = getRandomContextExpr.dotCall(randomConfigAccessor.nextBoolean(builder))
        return builder.irIfThenElse(
            type = type, condition = conditionExpr, thenPart = truePart, elsePart = elsePart
        )
    }

    private fun generateRandomSealClass(
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        if (irClass.isSealed()) {
            TODO()
        } else {
            return null
        }
    }

    private fun generateRandomAbstractClassAndInterface(
        declarationParent: IrDeclarationParent?,
        irType: IrType,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        initMetadata: InitMetaData,
    ): IrExpression? {
        if (irClass.isAbstract() && !irClass.isSealed() && irClass.isAnnotatedWithRandomizable()) {
            val candidateClasses = extractIrClassesFromRandomizableAnnotation(irClass)
            if (candidateClasses.isNotEmpty()) {
                val rt = builder.irBlock {
                    val candidateCount = candidateClasses.size
                    val candidateIndexExpr: IrCall =
                        getRandomConfigExpr.dotCall(randomConfigAccessor.randomizableCandidateIndex(builder))
                            .withValueArgs(builder.irInt(candidateCount))
                    val candidateIndexVar = irTemporary(candidateIndexExpr, nameHint = "classCandidateIndex")
                    val whenResultExpr: IrWhen = builder.irWhen(
                        type = irType,
                        branches = candidateClasses.withIndex().map { (index, candidate) ->
                            builder.irBranch(
                                condition = builder.irEquals(candidateIndexExpr, builder.irInt(index)),
                                result = generateRandomPrimaryClass(
                                    candidate,
                                    builder,
                                    initMetadata,
                                    declarationParent,
                                    getRandomContextExpr,
                                    getRandomConfigExpr
                                ) ?: throwUnableToRandomizeException(
                                    builder,
                                    "Unable to generate random for $candidate"
                                )
                            )
                        } + builder.irElseBranch(
                            throwUnableToRandomizeException(builder, msgIr = builder.irConcat().apply {
                                addArgument(builder.irString("Illegal index for @Randomizable annotation's candidate class at ${irClass.name}. The index should be within [0, $candidateCount), but it is "))
                                addArgument(builder.irGet(candidateIndexVar))
                                addArgument(builder.irString("."))
                            })
                        )
                    )
                    +whenResultExpr
                }
                println("z99:${rt.dumpKotlinLike()}")
                return rt
            } else {
                return null
            }
        } else {
            return null
        }
    }


    private fun generateRandomConstructorParam(
        /**
         * In case of generic param, this object only contains the generic type from its constructor.
         * If there's a [receivedType], then [receivedType] it must be prioritized over this, because this one does not contain enough information to generate the correct call.
         */
        param: IrValueParameter,
        /**
         * Received type argument is type information passed down from higher level to the param represented by [param].
         * This is not null when [param] is holding a generic
         */
        receivedType: IrType?,
        enclosingClass: IrClass,
        declarationParent: IrDeclarationParent?,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomContext]
         */
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        randomFunctionMetaData: InitMetaData,
        /**
         * Allow any generic type within [param], [receivedType] to look up its type. These include the type of the param itself.
         */
        typeMapForParam: TypeMap,
    ): IrExpression {
        val paramType = (param.type as? IrSimpleTypeImpl)!!

        return generateRandomType(
            declarationParent = declarationParent,
            enclosingClass = enclosingClass,
            constructorParam = param,
            receivedType = receivedType,
            targetType = paramType,
            builder = builder,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            optionalParamMetaDataForReporting = ParamReportData.Companion.fromIrElements(
                param, paramType, enclosingClass
            ),
            initMetaData = randomFunctionMetaData,
            typeMap = typeMapForParam,
            tempVarName = param.name.asString()
        )
    }

    /**
     * Generate random for either [targetType] or [receivedType]
     */
    private fun generateRandomType(
        targetType: IrType,
        /**
         * Received type argument is generic type information passed down from higher level or wherever.
         * This can be used to look-up type (could be concrete or intermediate generic) for [targetType].
         */
        receivedType: IrType?,
        declarationParent: IrDeclarationParent?,
        /**
         * [constructorParam] is the constructor param holding whatever returned by this
         */
        constructorParam: IrValueParameter?,
        /**
         * this is the class enclosing the [constructorParam]
         */
        enclosingClass: IrClass?,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomContext]
         */
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        /**
         * This optional object is for generating error reporting expression.
         * If given, a more descriptive message can be generated, otherwise, the message will be based on [targetType]
         */
        optionalParamMetaDataForReporting: ReportData,
        initMetaData: InitMetaData,
        typeMap: TypeMap,
        tempVarName: String?,
    ): IrExpression {
        val primitive = generateRandomPrimitive(
            type = targetType,
            builder = builder,
            getRandomContext = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
        )

        if (primitive != null) {
            return primitive
        }

        val receivedTypeClassifier = receivedType?.classifierOrNull

        if (
            targetType.isTypeParameter()
            && (receivedTypeClassifier == null || receivedTypeClassifier !is IrClassSymbol)
        ) {
            /**
             * This is the case in which param type is generic, but does not receive any "concrete" type from the outside.
             * This construct an expr that passes the generic from random() function to [RandomContext] to get a random instance.
             */
            return generateRandomTypeForTypelessGeneric(
                receivedType = receivedType,
                targetType = targetType,
                builder = builder,
                getRandomContextExpr = getRandomContextExpr,
                optionalParamMetaDataForReporting = optionalParamMetaDataForReporting,
                tempVarName = tempVarName,
            )
        } else {
            val _receivedType = receivedType as? IrSimpleType
            /**
             * This is the case in which it is possible to retrieve a concrete/define class for the generic type.
             */
            return generateRandomTypeWithDefinedType(
                param = constructorParam,
                enclosingClass = enclosingClass,
                declarationParent = declarationParent,
                receivedType = _receivedType, // TODO why is there received type here? this may not be correct
                targetType = targetType,
                builder = builder,
                getRandomContextExpr = getRandomContextExpr,
                getRandomConfigExpr = getRandomConfigExpr,
                optionalParamMetaDataForReporting = optionalParamMetaDataForReporting,
                initMetaData = initMetaData,
                typeMap = typeMap,
            )
        }
    }

    /**
     * This is the case in which it is possible to retrieve a concrete/define class for the generic type.
     */
    private fun generateRandomTypeWithDefinedType(
        /**
         * Declaration parent is for generating lambda down the line. For now, it is only for the lambda passed to List() function
         */
        declarationParent: IrDeclarationParent?,
        param: IrValueParameter?,
        enclosingClass: IrClass?,
        receivedType: IrSimpleType?,
        targetType: IrType,
        builder: DeclarationIrBuilder,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        optionalParamMetaDataForReporting: ReportData,
        initMetaData: InitMetaData,
        typeMap: TypeMap
    ): IrExpression {
        var actualParamType = receivedType ?: targetType
        // TODO this is not good !!!!
        actualParamType = (actualParamType as? IrSimpleType)?.let { replaceTypeArgument(it, typeMap) }!!
        val clazz = actualParamType.classOrNull?.owner
        if (clazz != null) {

            val randomInstanceExpr = generateRandomClass(
                declarationParent = declarationParent,
                receivedTypeArguments = receivedType?.arguments,
                param = param,
                enclosingClass = enclosingClass,
                irType = actualParamType,
                irClass = clazz,
                getRandomContextExpr = getRandomContextExpr,
                getRandomConfigExpr = getRandomConfigExpr,
                builder = builder,
                initMetadata = initMetaData,
                prevTypeMap = typeMap
            )

            if (randomInstanceExpr != null) {

                val nonNullRandom = builder.irBlock {
                    /**
                     * random from random context
                     */
                    val randomFromRandomContextCall =
                        getRandomContextExpr.extensionDotCall(randomContextAccessor.randomFunction(builder))
                            .withTypeArgs(actualParamType)

                    /**
                     * store random-from-context in a var because it will be used in 2 places:
                     * - a null check
                     * - else branch of an if-else below
                     */

                    val nameHint = "randomFromContextVar_${param?.name?.asString() ?: ""}"
                    val varRandomFromRandomContext =
                        irTemporary(randomFromRandomContextCall, nameHint).apply {
                            this.type = actualParamType.makeNullable()
                        }

                    val getRandomFromRandomContext = irGet(varRandomFromRandomContext)

                    +irIfNull(
                        type = actualParamType,
                        subject = getRandomFromRandomContext,
                        thenPart = randomInstanceExpr,
                        elsePart = getRandomFromRandomContext
                    )
                }

                if (actualParamType.isNullable()) {
                    return randomOrNull(
                        builder = builder,
                        getRandomContext = getRandomConfigExpr,
                        type = actualParamType,
                        randomPart = nonNullRandom
                    )
                } else {
                    return randomOrThrow(
                        builder = builder,
                        randomExpr = nonNullRandom,
                        type = actualParamType,
                        optionalParamMetaDataForReporting,
                        tempVarName = param?.name?.asString()
                    )
                }
            } else {

                val paramNameText = param?.name?.let { "param $it:" } ?: ""

                throw IllegalArgumentException(
                    "unable to construct an expression to generate a random instance for $paramNameText${clazz.name}"
                )
            }
        } else {
            throw IllegalArgumentException("$targetType cannot provide a class.")
        }
    }

    /**
     * TODO This function is highly problematic by its nature. Try to find a better way to swap type without resolving to recursive call.
     */
    private fun replaceTypeArgument(
        irType: IrSimpleType,
        typeMap: TypeMap
    ): IrSimpleType {
        val newArg = irType.arguments.map { arg ->

            val argType = arg.typeOrNull
            val argClassifier = argType?.classifierOrNull

            val newArg: IrTypeArgument = when (argClassifier) {
                is IrClassSymbol -> {
                    val spType = argType as? IrSimpleType
                    if (spType != null) {
                        replaceTypeArgument(argType, typeMap)
                    } else {
                        arg
                    }
                }

                is IrTypeParameterSymbol -> {
                    val typeParamOrTypeArg = typeMap.get(argClassifier.owner)
                    val irType = typeParamOrTypeArg?.getIrTypeOrNull()
                    if (irType is IrSimpleType) {
                        // type info already exist, juts pass it along
                        irType
                    } else {
                        // type info does not exist, construct a synthetic type
                        // TODO this branch is problematic, it works with @Randomizable, but it can erase type because the synthetic type is not complete
                        val classifier = when (typeParamOrTypeArg) {
                            is TypeParamOrArg.Arg -> typeParamOrTypeArg.typeArg.typeOrNull?.classifierOrNull
                            is TypeParamOrArg.Param -> typeParamOrTypeArg.typeParam.symbol
                            null -> null
                        }
                        val argSimpleType = (arg as? IrSimpleType)
                        if (argSimpleType != null && classifier != null) {
                            val alteredArg = IrSimpleTypeImpl(
                                kotlinType = arg.originalKotlinType,
                                classifier = classifier,
                                nullability = arg.nullability,
                                arguments = argSimpleType.arguments,
                                annotations = arg.annotations,
                                abbreviation = arg.abbreviation,
                            )
                            alteredArg
                        } else {
                            arg
                        }
                    }
                }

                else -> arg
            }
            newArg
        }
        val rt = IrSimpleTypeImpl(
            kotlinType = irType.originalKotlinType,
            classifier = irType.classifier,
            nullability = irType.nullability,
            arguments = newArg,
            annotations = irType.annotations,
            abbreviation = irType.abbreviation
        )
        return rt
    }

    /**
     * This is the case in which param type is generic, but does not receive any concrete type from the outside
     * This construct ane expr that pass the generic from random() function to [RandomContext] to get a random instance.
     */
    private fun generateRandomTypeForTypelessGeneric(
        receivedType: IrType?,
        targetType: IrType,
        builder: DeclarationIrBuilder,
        getRandomContextExpr: IrExpression,
        optionalParamMetaDataForReporting: ReportData,
        tempVarName: String?,
    ): IrExpression {
        val paramTypeForRandomFunction = receivedType as? IrSimpleType

        val nonNullRandom = getRandomContextExpr
            .extensionDotCall(randomContextAccessor.randomFunction(builder))
            .apply {
                if (paramTypeForRandomFunction != null) {
                    this.withTypeArgs(paramTypeForRandomFunction)
                }
            }

        /**
         * use [isMarkedNullable] here because [isNullable] always returns true for generic type
         */
        val rt = if (targetType.isMarkedNullable()) {
            randomOrNull(
                builder = builder,
                getRandomContext = getRandomContextExpr,
                type = targetType,
                randomPart = nonNullRandom,
            )
        } else {
            return randomOrThrow(
                builder = builder,
                randomExpr = nonNullRandom,
                type = targetType,
                metaData = optionalParamMetaDataForReporting,
                tempVarName = tempVarName,
            )
        }
        // println("z16: ${rt.dumpKotlinLike()}")
        return rt
    }

    /**
     * Construct an express that:
     * - either return whatever [randomExpr] returns if such value is not null
     * - or throw an exception at runtime
     */
    private fun randomOrThrow(
        builder: DeclarationIrBuilder,
        randomExpr: IrExpression,
        type: IrType,
        metaData: ReportData,
        tempVarName: String?
    ): IrExpression {
        return builder.irBlock {
            val nameHint = "randomResult_${tempVarName ?: ""}"
            val randomResultVar = irTemporary(randomExpr, nameHint)
            val getRandomResult = irGet(randomResultVar)
            val throwExceptionExpr = throwUnableToRandomizeException(
                builder = this,
                msg = metaData.makeMsg(),
            )
            +irIfNull(type, getRandomResult, throwExceptionExpr, getRandomResult)
        }
    }

    /**
     * Construct an Ir to throw an instance of [UnableToMakeRandomException]
     */
    private fun throwUnableToRandomizeException(
        builder: IrBuilderWithScope, msg: String?
    ): IrThrowImpl {
        return builder.irThrow(unableToMakeRandomExceptionAccessor.callConstructor(builder, msg))
    }


    /**
     * Construct an Ir to throw an instance of [UnableToMakeRandomException]
     */
    private fun throwUnableToRandomizeException(
        builder: IrBuilderWithScope, msgIr: IrExpression
    ): IrThrowImpl {
        return builder.irThrow(unableToMakeRandomExceptionAccessor.callConstructor(builder, msgIr))
    }

    /**
     * TODO add logic to pick a constructor:
     *  - prioritize annotated constructors
     *  - pick randomly
     */
    private fun getConstructor(targetClass: IrClass): IrConstructor? {
        val primary = targetClass.primaryConstructor
        if (primary != null) {
            return primary
        } else {
            return null
        }
    }

    /**
     * Generate an [IrExpression] that will return a random value for a parameter ([param])
     */
    private fun generateRandomPrimitive(
        type: IrType,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomConfig]
         */
        getRandomContext: IrExpression,
        getRandomConfigExpr: IrExpression,
    ): IrExpression? {
        val isNullable = type.isNullable()
        val randomFunctionCall = when {
            type.isInt2(isNullable) -> randomConfigAccessor.nextInt(builder)
            type.isUInt2(isNullable) -> randomConfigAccessor.nextUInt(builder)
            type.isLong2(isNullable) -> randomConfigAccessor.nextLong(builder)
            type.isULong2(isNullable) -> randomConfigAccessor.nextULong(builder)
            type.isByte2(isNullable) -> randomConfigAccessor.nextByte(builder)
            type.isUByte2(isNullable) -> randomConfigAccessor.nextUByte(builder)
            type.isShort2(isNullable) -> randomConfigAccessor.nextShort(builder)
            type.isUShort2(isNullable) -> randomConfigAccessor.nextUShort(builder)
            type.isBoolean2(isNullable) -> randomConfigAccessor.nextBoolean(builder)
            type.isFloat2(isNullable) -> randomConfigAccessor.nextFloat(builder)
            type.isDouble2(isNullable) -> randomConfigAccessor.nextDouble(builder)
            type.isChar2(isNullable) -> randomConfigAccessor.nextChar(builder)
            type.isString2(isNullable) -> randomConfigAccessor.nextStringUUID(builder)
            type.isUnit2(isNullable) -> randomConfigAccessor.nextUnit(builder)
            type.isNumber2(isNullable) -> randomConfigAccessor.nextNumber(builder)
            type.isAny2(isNullable) -> randomConfigAccessor.nextAny(builder)
            type.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")
            else -> null
        }

        return randomFunctionCall?.let {
            randomFromRandomContextOrRandomConfig(
                type = type,
                getRandomContext = getRandomContext,
                randomFromConfigRandomExpr = getRandomConfigExpr.dotCall(randomFunctionCall),
                builder = builder,
            )
        }
    }


    private fun randomFromRandomContextOrRandomConfig(
        type: IrType, getRandomContext: IrExpression,
        /**
         * [randomFromConfigRandomExpr] return a random instance of [type]
         */
        randomFromConfigRandomExpr: IrExpression, builder: DeclarationIrBuilder
    ): IrExpression {
        val randomFromContext =
            getRandomContext.extensionDotCall(randomContextAccessor.randomFunction(builder)).withTypeArgs(type)
        val nonNullExpr = evaluateRandomContextThenRandomConfig(
            type = type,
            randomFromRandomContext = randomFromContext,
            randomFromRandomConfig = randomFromConfigRandomExpr,
            builder = builder,
        )
        if (type.isNullable()) {
            return randomOrNull(builder, getRandomContext, type, nonNullExpr)
        } else {
            return nonNullExpr
        }
    }

    private fun evaluateRandomContextThenRandomConfig(
        type: IrType,
        randomFromRandomContext: IrExpression,
        randomFromRandomConfig: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {
        return builder.irBlock {

            val randomFromContextVar = irTemporary(
                value = randomFromRandomContext,
                nameHint = "randomFromContextVar",
                irType = type,
            )
            val getRandomFromContextVar = irGet(randomFromContextVar)

            +builder.irIfNull(
                type = type,
                subject = getRandomFromContextVar,
                thenPart = randomFromRandomConfig,
                elsePart = getRandomFromContextVar
            )
        }
    }

    private fun makeIrFunctionExpr(
        lambda: IrSimpleFunction,
        functionType: IrType,
    ): IrFunctionExpressionImpl {
        return IrFunctionExpressionImpl(
            startOffset = lambda.startOffset,
            endOffset = lambda.endOffset,
            type = functionType,
            function = lambda,
            origin = IrStatementOrigin.LAMBDA
        )
    }

    private fun makeLocalLambdaWithoutBody_forMakeRandom(
        returnType: IrType,
        visibility: DescriptorVisibility = DescriptorVisibilities.LOCAL,
        name: Name = SpecialNames.ANONYMOUS
    ): IrSimpleFunction {
        return pluginContext.irFactory.buildFun {
            this.name = name
            origin = BaseObjects.declarationOrigin
            this.visibility = visibility
            this.returnType = returnType
            modality = FINAL
            isSuspend = false
        }
    }


    /**
     * Make a local lambda.
     */
    private fun makeLocalLambdaWithoutBody(
        /**
         * Return type of the lambda
         */
        returnType: IrType,
        beforeStandardConfig: IrFunctionBuilder.() -> Unit = {},
        afterStandardConfig: IrFunctionBuilder.() -> Unit = {},
        visibility: DescriptorVisibility = DescriptorVisibilities.LOCAL,
    ): IrSimpleFunction {
        return pluginContext.irFactory.buildFun {
            beforeStandardConfig(this)
            this.name = SpecialNames.ANONYMOUS
            origin = BaseObjects.declarationOrigin
            this.visibility = visibility
            this.returnType = returnType
            modality = FINAL
            isSuspend = false
            afterStandardConfig(this)
        }
    }

    private fun IrSimpleFunction.buildBody(configBuilder: IrSimpleFunction.(DeclarationIrBuilder) -> Unit): IrSimpleFunction {
        val builder = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = this.symbol,
        )
        this.configBuilder(builder)
        return this
    }
}
