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
import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib.*
import com.x12q.kotlin.randomizer.ir_plugin.backend.reporting.ErrMsg
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
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.*
import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.ir_plugin.util.stopAtFirstNotNull
import com.x12q.kotlin.randomizer.lib.*
import com.x12q.kotlin.randomizer.lib.annotations.Randomizable
import com.x12q.kotlin.randomizer.lib.rs.RdRs
import com.x12q.kotlin.randomizer.lib.rs.isOk
import com.x12q.kotlin.randomizer.lib.util.developerErrorMsg
import com.x12q.kotlin.randomizer.lib.util.impossibleErr
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.descriptors.Modality.*
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
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import javax.inject.Inject
import kotlin.collections.plus


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
    private val builderAccessor: RandomizerContextBuilderAccessor,
    private val rdRsAccessor: RdRsAccessor,
) : RDBackendTransformer() {

    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression {
        completeRandomFunctionCall(expression)
        return super.visitFunctionAccess(expression)
    }

    private fun extractClassReferencesFromRandomizableAnnotation(targetClass: IrClass): List<IrClassReference> {
        val randomizableAnnotation = targetClass.getAnnotation(Randomizable::class)
        val classListParamArgPair: Pair<IrValueParameter, IrExpression?>? =
            randomizableAnnotation?.getAllArgumentsWithIr()?.firstOrNull { (param, _) ->
                param.name == randomizableAccessor.classesParamName
            }
        if (classListParamArgPair != null) {
            val arg = classListParamArgPair.second
            if (arg == null) {
                return emptyList()
            }

            val classReferences = (arg as? IrVararg)?.elements?.mapNotNull { it as? IrClassReference } ?: emptyList()

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
            val clzz =
                classRef.classType.classOrNull?.owner.crashOnNull { impossibleErr("ClassRef $classRef passed to ${randomizableAccessor.classId} is from a null class.") }
            if (clzz.isInterface || clzz.modality == ABSTRACT) {
                throw IllegalArgumentException("${clzz} must not be abstract")
            }
        }
    }

    /**
     * This involves:
     * - completing "makeRandom" lambda
     */
    private fun completeRandomFunctionCall(irCall: IrFunctionAccessExpression) {
        val function = irCall.symbol.owner
        if (!isRandomFunctions(function, builderAccessor.irType)) {
            return
        }

        val randomFunction = function

        completeMakeRandomLambda(
            randomFunctionCall = irCall,
            randomFunction = randomFunction,
        )
    }

    private fun completeMakeRandomLambda(randomFunctionCall: IrFunctionAccessExpression, randomFunction: IrFunction) {

        val makeRandomLambdaParam =
            randomFunction.getMakeRandomParam().crashOnNull { developerErrorMsg("makeRandom param does not exist") }

        val providedMakeRandomArg = randomFunctionCall.getArgAtParam(makeRandomLambdaParam)
        val decParent = currentDeclarationParent

        if (providedMakeRandomArg == null) {

            /**
             * makeRandom arg is not provided => generate a default one.
             * Signature (randomContext: RandomContext)->T, with T being the type arg passed to the call
             */

            val makeRandomReturnType = randomFunctionCall.typeArguments.firstOrNull()
                .crashOnNull { developerErrorMsg("Type argument of makeRandom cannot be null") }

            val makeRandomLambda = generateMakeRandomLambda(
                returnType = makeRandomReturnType,
                declarationParent = decParent,
            )

            val makeRandomLambdaArg = makeIrFunctionExpr(
                lambda = makeRandomLambda, functionType = pluginContext.irBuiltIns.functionN(1).typeWith(
                    randomContextAccessor.irType, makeRandomReturnType
                )
            )
            println("x12q:${makeRandomLambda.dumpKotlinLike()}")
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
            name = Name.identifier("randomContext"),
            type = randomContextAccessor.irType,
            origin = BaseObjects.declarationOrigin
        )

        newMakeRandomLambda.addTypeParameter(
            name = "T", upperBound = randomContextAccessor.irType
        )

        newMakeRandomLambda.addTypeParameter(
            name = "E", upperBound = returnType
        )

        val body = generateMakeRandomBody(
            returnType = returnType,
            makeRandomFunction = newMakeRandomLambda,
            initMetadata = InitMetaData(
                targetClass = returnType.classOrNull?.owner.crashOnNull { "class/type passed to random() function must be defined." },
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
        returnType: IrType,
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
                irType = returnType,
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
        irType: IrType,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
        declarationParent: IrDeclarationParent?,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
    ): IrExpression? {
        val rt = generateRandomType(
            declarationParent = declarationParent,
            constructorParam = null,
            enclosingClass = null,
            receivedType = irType,
            targetType = irType,
            builder = builder,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            initMetaData = randomFunctionMetaData,
            optionalParamMetaDataForReporting = ListReportData(
                valueType = irType.dumpKotlinLike(),
                paramName = null,
                enclosingClassName = null,
            ),
            typeMap = TypeMap.Companion.emptyTODO,
            tempVarName = null
        ).getIrExpression()
        return rt
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
            throw InnerClassNotSupportedException(ErrMsg.err3("Inner class ${irClass.fqNameWhenAvailable?.asString()?.let { "($it)" }}is not supported for now."))
        }

        val rt = stopAtFirstNotNull(
            { generateRandomObj(irClass, builder) },
            { generateRandomEnum(irClass, getRandomContextExpr, builder) },
            {
                generateRandomConcreteClass_2(
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
                    param = param,
                    irClass = irClass,
                    irType = irType.crashOnNull {
                        developerErrorMsg("type of interface (${irClass.name}) cannot be null")
                    },
                    builder = builder,
                    declarationParent = declarationParent,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    initMetadata = initMetadata,
                )
            },
            {
                generateRandomAbstractClassAndInterface(
                    param = param,
                    irClass = irClass,
                    irType = irType.crashOnNull {
                        developerErrorMsg("type of interface (${irClass.name}) cannot be null")
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
        val rt = stopAtFirstNotNull({
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
        }, {
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
        }, {
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
        }, {
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
        }, {
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
        }, {
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
        }, {
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
        }, {
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
        }, {
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
        if (listIrClass.isArrayList()) {
            return generateCollectionUsingListAsInput(
                factoryFunctionCall = listAccessor.makeArrayList(builder),
                declarationParent = declarationParent,
                receivedTypeArguments = receivedTypeArguments,
                param = param,
                enclosingClass = enclosingClass,
                irListType = irListType,
                getRandomContextExpr = getRandomContextExpr,
                getRandomConfigExpr = getRandomConfigExpr,
                builder = builder,
                randomFunctionMetaData = randomFunctionMetaData,
            )
        } else {
            return null
        }
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
        if (arrayAccessor.isArray(arrayIrClass)) {
            return generateCollectionUsingListAsInput(
                factoryFunctionCall = arrayAccessor.makeArray(builder),
                declarationParent = declarationParent,
                receivedTypeArguments = receivedTypeArguments,
                param = param,
                enclosingClass = enclosingClass,
                irListType = irListType,
                getRandomContextExpr = getRandomContextExpr,
                getRandomConfigExpr = getRandomConfigExpr,
                builder = builder,
                randomFunctionMetaData = randomFunctionMetaData,
            )
        } else {
            return null
        }
    }

    /**
     * This creates an expression that invoke [factoryFunctionCall] to create a collection object.
     * The [factoryFunctionCall] must looks like this: makeCollection<Type>(listOf(element1, element2, ...))
     */
    private fun generateCollectionUsingListAsInput(
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
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {

        // extract element type from the collection type declaration
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
                return factoryFunctionCall.withTypeArgs(tt).withValueArgs(listExpr)
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
                impossibleErr("$paramNamePrefix: List element type must be specified. It is null here.")
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
                    ).getIrExpression()
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

            val rt = listAccessor.makeList(builder).withValueArgs(sizeExpr, randomElementLambdaExpr).withTypeArgs(type)

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
                        ).getIrExpression()
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
                        ).getIrExpression()
                        +valueLambdaBuilder.irReturn(randomValue)
                    }
                }
                makeIrFunctionExpr(
                    lambda = valueLambda,
                    functionType = pluginContext.irBuiltIns.functionN(0).typeWith(valueType),
                )
            }

            val makeMapFunctionCall =
                mapAccessor.makeMapFunction(builder).withValueArgs(sizeExpr, makeKeyLambdaExpr, makeValueLambda)
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
            return makeMapFunctionCall.withTypeArgs(keyTypeArg.typeOrFail, valueTypeArg.typeOrFail)
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
                return listToSetFunctionCall().withTypeArgs(elementType).withValueArgs(listExpr)
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
                    .extensionDotCall(builder.irCall(randomAccessor.randomOnCollectionOneArg)).withValueArgs(getRandom)

                return rt
            } else {
                // make an IR to access "values"

                val irValuesFunction =
                    (irClass.declarations.firstOrNull { it.getNameWithAssert().toString() == "values" } as? IrFunction)

                val irValues = irValuesFunction.crashOnNull { "enum ${irClass.name} does not have \"values\" field" }

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
     * Generate an instance of a concrete class.
     */
    private fun generateRandomConcreteClass_2(
        declarationParent: IrDeclarationParent?,
        irType: IrType?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): IrExpression? {
        if (irClass.isArrayList() || irClass.isHashSet() || irClass.isLinkedHashSet() || irClass.isHashMap() || irClass.isLinkedHashMap() || arrayAccessor.isArray(
                irClass
            ) || irClass.isObject || irClass.isEnumClass || !irClass.isFinalOrOpenConcrete()
        ) {
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

        val candidateExprs = generateRandomIrExpressionForEachConstructors(
            declarationParent = declarationParent,
            irType = irType,
            irClass = irClass,
            getRandomContextExpr = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
            builder = builder,
            randomFunctionMetaData = randomFunctionMetaData
        )

        if (candidateExprs.isEmpty()) {
            val className = irClass.fqNameWhenAvailable?.asString() ?: "unknown class"
            return throwUnableToRandomizeException(builder, ErrMsg.err4("Cannot use any constructors of class [$className] to generate a random instance"))
        }

        val type = irType.crashOnNull {
            developerErrorMsg("cannot generate random instance of class ${irClass.name} without irType")
        }

        val rt = builder.irBlock {
            val candidateCount = candidateExprs.size
            val candidateIndexExpr: IrCall =
                getRandomConfigExpr.dotCall(randomConfigAccessor.randomizableCandidateIndex(builder))
                    .withValueArgs(builder.irInt(candidateCount))
            val candidateIndexVar = irTemporary(candidateIndexExpr, nameHint = "constructorCandidateIndex")

            val whenResultExpr: IrWhen =
                builder.irWhen(
                    type = type, branches = candidateExprs.withIndex().map { (index, candidate) ->
                        builder.irBranch(
                            condition = builder.irEquals(candidateIndexExpr, builder.irInt(index)), result = candidate
                        )
                    } + builder.irElseBranch(
                        throwUnableToRandomizeException(builder, msgIr = builder.irConcat().apply {
                            addArgument(builder.irString("Illegal constructor candidate index for class ${irClass.name}. The index should be within [0, ${candidateCount - 1}], but it is "))
                            addArgument(builder.irGet(candidateIndexVar))
                            addArgument(builder.irString("."))
                        })
                    )
                )
            +whenResultExpr
        }
        return rt
    }

    private fun generateRandomIrExpressionForEachConstructors(
        declarationParent: IrDeclarationParent?,
        irType: IrType?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        randomFunctionMetaData: InitMetaData,
    ): List<IrExpression> {

        val baseTypeMap: TypeMap = randomFunctionMetaData.initTypeMap
        val typeMapFromType: TypeMap = irType?.makeTypeMap() ?: TypeMap.Companion.empty
        val candidateConstructors: List<IrConstructor> = getConstructorCandidates(irClass)

        val rt: List<IrExpression> = candidateConstructors.mapNotNull { constructor ->

            val paramExpressions: MutableList<IrExpression> = mutableListOf()

            var firstPassedOverThrowExpression: PassedOverThrowExpression?=null
            for (param in constructor.valueParameters) {
                val typeMapFromParam: TypeMap = param.makeTypeMap()
                val paramTypeMap: TypeMap =
                    typeMapFromParam.mergeAndOverwriteWith(typeMapFromType).bridgeType(baseTypeMap)
                        .mergeAndOverwriteWith(baseTypeMap)
                val receivedType: IrType? = if (param.isGeneric()) {
                    paramTypeMap.get(param.getTypeParamFromGenericParam())
                } else {
                    null
                }?.getIrTypeOrNull()

                val paramExprRs = generateRandomConstructorParam(
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
                if (paramExprRs.isOk()) {
                    paramExpressions.add(paramExprRs.value)
                } else {
                    firstPassedOverThrowExpression = paramExprRs.err
                    break
                }
            }
            val q = if (firstPassedOverThrowExpression!=null) {
                null
            } else {
                val constructorCall = builder.irCallConstructor(
                    callee = constructor.symbol, typeArguments = emptyList()
                ).withValueArgs(paramExpressions)
                constructorCall as IrExpression
            }
            q
        }

        return rt
    }


    /**
     * Extract concrete type from provided generic type argument from various sources.
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
     *    [nonNullPart]
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
        nonNullPart: IrExpression,
    ): IrExpression {
        return randomIfElse(
            builder = builder,
            getRandomContextExpr = getRandomContext,
            type = type,
            truePart = nonNullPart,
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
        declarationParent: IrDeclarationParent?,
        param: IrValueParameter?,
        irType: IrType,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        initMetadata: InitMetaData,
    ): IrExpression? {
        if (irClass.isSealed()) {
            val rt = randomOrThrow(
                builder = builder,
                randomExpr = getRandomContextExpr.extensionDotCall(randomContextAccessor.randomFunction(builder))
                    .withTypeArgs(irType),
                type = irType,
                reportData = ParamReportData(
                    param?.name?.asString(), irType.dumpKotlinLike(), irType.classFqName?.asString()
                ),
                tempVarName = null
            )
            return rt

        } else {
            return null
        }
    }

    private fun generateRandomAbstractClassAndInterface(
        declarationParent: IrDeclarationParent?,
        param: IrValueParameter?,
        irType: IrType,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        initMetadata: InitMetaData,
    ): IrExpression? {
        if (irClass.isAbstract() && !irClass.isSealed()) {
            // look for value in random context
            val rt = randomOrThrow(
                builder = builder,
                randomExpr = getRandomContextExpr.extensionDotCall(randomContextAccessor.randomFunction(builder))
                    .withTypeArgs(irType),
                type = irType,
                reportData = ParamReportData(
                    param?.name?.asString(), irType.dumpKotlinLike(), irType.classFqName?.asString()
                ),
                tempVarName = null
            )
            return rt

        } else {
            return null
        }
    }

    /**
     * Generate random from candidate class from @Randomizable annotation.
     */
    private fun generateRandomAbstractClassAndInterface_usingRandomizableAnnotation(
        declarationParent: IrDeclarationParent?,
        irType: IrType,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        initMetadata: InitMetaData,
    ): IrExpression? {
        return null
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
                        branches = candidateClasses.withIndex().map { (index, candidateClass) ->
                            builder.irBranch(
                                condition = builder.irEquals(candidateIndexExpr, builder.irInt(index)),
                                result = generateRandomPrimaryClass(
                                    irType = irType,
                                    builder = builder,
                                    randomFunctionMetaData = initMetadata,
                                    declarationParent = declarationParent,
                                    getRandomContextExpr = getRandomContextExpr,
                                    getRandomConfigExpr = getRandomConfigExpr
                                ) ?: throwUnableToRandomizeException(
                                    builder, "Unable to generate random for $candidateClass"
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
    ): RdRs<IrExpression, PassedOverThrowExpression> {
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
    ): RdRs<IrExpression, PassedOverThrowExpression> {
        val primitive = generateRandomPrimitive(
            type = targetType,
            builder = builder,
            getRandomContext = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
        )

        if (primitive != null) {
            return RdRs.Ok(primitive)
        }

        val receivedTypeClassifier = receivedType?.classifierOrNull

        if (targetType.isTypeParameter() && (receivedTypeClassifier == null || receivedTypeClassifier !is IrClassSymbol)) {
            /**
             * This is the case in which param type is generic, but does not receive any "concrete" type from the outside.
             * This construct an expr that passes the generic from random() function to [RandomContext] to get a random instance.
             */
            return RdRs.Ok(
                generateRandomTypeForTypelessGeneric(
                    receivedType = receivedType,
                    targetType = targetType,
                    builder = builder,
                    getRandomContextExpr = getRandomContextExpr,
                    optionalParamMetaDataForReporting = optionalParamMetaDataForReporting,
                    tempVarName = tempVarName,
                )
            )
        } else {
            /**
             * This is the case in which it is possible to retrieve a concrete/define class for the generic type.
             */
            // return generateRandomTypeWithDefinedType(
            return generateRandomTypeWithDefinedTypeOrThrowInGeneratedCode(
                param = constructorParam,
                enclosingClass = enclosingClass,
                declarationParent = declarationParent,
                receivedType = receivedType as? IrSimpleType,
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

    private fun generateRandomTypeWithDefinedTypeOrThrowInGeneratedCode(
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
    ): RdRs<IrExpression, PassedOverThrowExpression> {
        try {
            return RdRs.Ok(
                generateRandomTypeWithDefinedType(
                    declarationParent = declarationParent,
                    param = param,
                    enclosingClass = enclosingClass,
                    receivedType = receivedType,
                    targetType = targetType,
                    builder = builder,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    optionalParamMetaDataForReporting = optionalParamMetaDataForReporting,
                    initMetaData = initMetaData,
                    typeMap = typeMap,
                )
            )
        } catch (e: Exception) {
            when(e){
                is IllegalArgumentException -> {
                    val errMsg = e.message ?: ""
                    return RdRs.Err(
                        PassedOverThrowExpression(
                            throwExpress = throwUnableToRandomizeException(builder = builder, msg = ErrMsg.err2(errMsg)),
                            errMsg = errMsg,
                        )
                    )
                }
                else -> throw e
            }
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
        val actualParamType: IrType = run {
            val tp0 = receivedType ?: targetType
            val typeWithReplacement = (tp0 as? IrSimpleType)?.let { replaceTypeArgument(tp0, typeMap) }
            typeWithReplacement ?: tp0
        }
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

                    val nameHint = "randomFromContext_${param?.name?.asString() ?: ""}"

                    val varRandomFromRandomContext = run {
                        /**
                         * random from random context
                         * store random-from-context in a var because it will be used in 2 places:
                         * - a null check
                         * - else branch of an if-else below
                         */
                        val randomFromRandomContextCall =
                            getRandomContextExpr
                                .extensionDotCall(randomContextAccessor.randomFunction(builder))
                                .withTypeArgs(actualParamType)

                        irTemporary(randomFromRandomContextCall, nameHint).apply {
                            this.type = actualParamType.makeNullable()
                        }
                    }

                    val getRandomFromRandomContext = irGet(varRandomFromRandomContext)

                    +irIfNull(
                        type = actualParamType,
                        subject = getRandomFromRandomContext,
                        thenPart = randomInstanceExpr,
                        elsePart = getRandomFromRandomContext
                    )
                }


                val rt = if (actualParamType.isNullable()) {

                    val randomRsFromRandomContext =
                        getRandomContextExpr
                            .extensionDotCall(randomContextAccessor.randomRsFunction(builder))
                            .withTypeArgs(actualParamType)

                    evaluateRandomRs(
                        type = actualParamType,
                        getRandomContext = getRandomContextExpr,
                        randomRsFromRandomContext = randomRsFromRandomContext,
                        nonNullPart = nonNullRandom,
                        builder = builder,
                    )
                } else {
                    randomOrThrow(
                        builder = builder,
                        randomExpr = nonNullRandom,
                        type = actualParamType,
                        optionalParamMetaDataForReporting,
                        tempVarName = param?.name?.asString()
                    )
                }
                return rt
            } else {

                val paramNameText = param?.name ?: "[unknown param name]"
                val inClassName = enclosingClass?.let { it.fqNameWhenAvailable?.asString() } ?: "[unknown enclosing class]"
                throw IllegalArgumentException(

                    ErrMsg.err1("unable to construct an expression to generate a random instance for param $paramNameText:${clazz.fqNameWhenAvailable} in class $inClassName")
                )
            }
        } else {
            throw IllegalArgumentException("$targetType cannot provide a class.")
        }
    }

    /**
     * Replace generic type arg in [irType] using information from [typeMap]
     */
    private fun replaceTypeArgument(
        irType: IrSimpleType, typeMap: TypeMap
    ): IrSimpleType {
        val newTypeArg = irType.arguments.map { arg ->

            val argType = arg.typeOrNull
            val argClassifier = argType?.classifierOrNull

            val newArg: IrTypeArgument = when (argClassifier) {
                is IrClassSymbol -> {
                    if (argType is IrSimpleType) {
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
                        arg
                    }
                }

                else -> arg
            }
            newArg
        }
        val rt = IrSimpleTypeImpl(
            classifier = irType.classifier,
            nullability = irType.nullability,
            arguments = newTypeArg,
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

        val nonNullRandom = getRandomContextExpr.extensionDotCall(randomContextAccessor.randomFunction(builder)).apply {
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
                nonNullPart = nonNullRandom,
            )
        } else {
            return randomOrThrow(
                builder = builder,
                randomExpr = nonNullRandom,
                type = targetType,
                reportData = optionalParamMetaDataForReporting,
                tempVarName = tempVarName,
            )
        }
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
        reportData: ReportData,
        tempVarName: String?
    ): IrExpression {
        return builder.irBlock {
            val nameHint = "randomResult_${tempVarName ?: ""}"
            val randomResultVar = irTemporary(randomExpr, nameHint)
            val getRandomResult = irGet(randomResultVar)
            val throwExceptionExpr = throwUnableToRandomizeException(
                builder = this,
                msg = reportData.makeMsg(),
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

    @Deprecated("Kept for reference. Don't use.")
    private fun getConstructor(targetClass: IrClass): IrConstructor? {
        val primary = targetClass.primaryConstructor
        if (primary != null) {
            return primary
        } else {
            return null
        }
    }

    private fun getConstructorCandidates(targetClass: IrClass): List<IrConstructor> {

        val allPublicConstructors = targetClass.constructors.filter { it.isPublic() || it.isInternal() }.toList()

        val annotatedConstructors = run {
            val onlyAnnotatedConstructors = allPublicConstructors.filter { it.isAnnotatedWithRandomizable() }

            val primaryConstructor = targetClass.primaryConstructor?.let { primaryCon ->
                // primary constructor is considered annotated if the class itself is annotated
                if (targetClass.isAnnotatedWithRandomizable() && primaryCon !in onlyAnnotatedConstructors) {
                    primaryCon
                } else {
                    null
                }
            }
            if (primaryConstructor != null) {
                onlyAnnotatedConstructors + primaryConstructor
            } else {
                onlyAnnotatedConstructors
            }
        }

        if (annotatedConstructors.isNotEmpty()) {
            return annotatedConstructors
        } else {
            return allPublicConstructors
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

        val rt = randomFunctionCall?.let {
            randomPrimitiveFromRandomContextOrRandomConfig(
                type = type,
                getRandomContext = getRandomContext,
                randomFromConfigRandomExpr = getRandomConfigExpr.dotCall(randomFunctionCall),
                builder = builder,
            )
        }
        return rt
    }


    private fun randomPrimitiveFromRandomContextOrRandomConfig(
        type: IrType, getRandomContext: IrExpression,
        /**
         * [randomFromConfigRandomExpr] return a random instance of [type]
         */
        randomFromConfigRandomExpr: IrExpression, builder: DeclarationIrBuilder
    ): IrExpression {
        return if (type.isNullable()) {
            randomPrimitiveFromRandomContextOrRandomConfig_nullable(
                type = type,
                getRandomContext = getRandomContext,
                randomFromConfigRandomExpr = randomFromConfigRandomExpr,
                builder = builder,
            )
        } else {
            randomPrimitiveFromRandomContextOrRandomConfig_notNull(
                type = type,
                getRandomContext = getRandomContext,
                randomFromConfigRandomExpr = randomFromConfigRandomExpr,
                builder = builder,
            )
        }
    }

    private fun randomPrimitiveFromRandomContextOrRandomConfig_notNull(
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

    private fun randomPrimitiveFromRandomContextOrRandomConfig_nullable(
        type: IrType, getRandomContext: IrExpression,
        /**
         * [randomFromConfigRandomExpr] return a random instance of [type]
         */
        randomFromConfigRandomExpr: IrExpression, builder: DeclarationIrBuilder
    ): IrExpression {
        val randomRsFromContext =
            getRandomContext.extensionDotCall(randomContextAccessor.randomRsFunction(builder)).withTypeArgs(type)
        val randomFromRandomContext =
            getRandomContext.extensionDotCall(randomContextAccessor.randomFunction(builder)).withTypeArgs(type)

        val nonNullExpr = evaluateRandomRsForPrimitive(
            type = type,
            randomFromContext = randomFromRandomContext,
            randomRsFromRandomContext = randomRsFromContext,
            randomFromRandomConfig = randomFromConfigRandomExpr,
            getRandomContext = getRandomContext,
            builder = builder,
        )
        return nonNullExpr
    }

    private fun evaluateRandomRsForPrimitive(
        type: IrType,
        getRandomContext: IrExpression,
        randomFromContext: IrExpression,
        randomRsFromRandomContext: IrExpression,
        randomFromRandomConfig: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {

        val nonNullPart = evaluateRandomContextThenRandomConfig(
            type = type,
            randomFromRandomContext = randomFromContext,
            randomFromRandomConfig = randomFromRandomConfig,
            builder = builder,
        )


        return evaluateRandomRs(
            type = type,
            getRandomContext = getRandomContext,
            randomRsFromRandomContext = randomRsFromRandomContext,
            nonNullPart = nonNullPart,
            builder = builder
        )
    }

    private fun evaluateRandomRs(
        type: IrType,
        getRandomContext: IrExpression,
        randomRsFromRandomContext: IrExpression,
        nonNullPart: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {
        val rt = builder.irBlock {

            val randomRsFromContext = irTemporary(
                value = randomRsFromRandomContext,
                nameHint = "randomRsFromContext",
                irType = rdRsAccessor.clzz.typeWith(listOf(type, rdRsAccessor.noRandomizerErrIrType))
            )

            val randomRsFromContextVar = irGet(randomRsFromContext)

            val isOkCall = randomRsFromRandomContext.extensionDotCall(
                rdRsAccessor.isOkFunction(
                    builder = builder,
                    vType = type,
                    eType = rdRsAccessor.noRandomizerErrIrType,
                )
            )
            val isOkVar = irTemporary(
                value = isOkCall, nameHint = "isOk", irType = pluginContext.irBuiltIns.booleanType
            )
            +builder.irIfThenElse(
                type = type,
                condition = builder.irGet(isOkVar),
                thenPart = randomRsFromContextVar.dotCall { rdRsAccessor.value(builder) },
                elsePart = run {

                    if (type.isNullable()) {
                        randomOrNull(
                            builder = builder,
                            getRandomContext = getRandomContext,
                            type = type,
                            nonNullPart = nonNullPart
                        )
                    } else {
                        nonNullPart
                    }
                },
            )
        }
        return rt
    }

    private fun evaluateRandomContextThenRandomConfig(
        type: IrType,
        randomFromRandomContext: IrExpression,
        randomFromRandomConfig: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {
        val rt = builder.irBlock {

            val randomFromContextVar = irTemporary(
                value = randomFromRandomContext,
                nameHint = "randomFromContext",
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
        return rt
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

    /**
     * Extract ir expression from a [RdRs]
     */
    private fun RdRs<IrExpression, PassedOverThrowExpression>.getIrExpression(): IrExpression{
        if(this.isOk()){
            return this.value
        }else{
            return this.err.throwExpress
        }
    }
}
