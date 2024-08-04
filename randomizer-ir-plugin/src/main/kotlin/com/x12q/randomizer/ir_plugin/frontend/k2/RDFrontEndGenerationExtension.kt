package com.x12q.randomizer.ir_plugin.frontend.k2

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.frontend.k2.util.RDPredicates
import com.x12q.randomizer.ir_plugin.frontend.k2.util.isAnnotatedRandomizable
import org.jetbrains.kotlin.contracts.description.EventOccurrencesRange
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.isInlineOnly
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.builder.buildAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.declarations.utils.isInline
import org.jetbrains.kotlin.fir.expressions.builder.*
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.getOwnerLookupTag
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.*
import org.jetbrains.kotlin.fir.resolve.toFirRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.*
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.*

/**
 * For generating new declaration (new functions, new classes, properties)
 *
 * To trigger generation: get...Names()/get...ids() functions must return something
 * The corresponding generation function: generate...() return declarations that is fully resolved:
 * - status must be [FirResolvedDeclarationStatus]
 * - type ref must be [FirResolvedTypeRef]
 * - resolvePhase must be [FirResolvePhase.BODY_RESOLVE]
 * There's no need to generate function body or property initializer, that will be handled by the backend IR generator
 *
 */
class RDFrontEndGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {

    /**
     * Predicate provider is used to create predicate that allow quick access to declarations that meet certain requirement.
     * To use a predicate, must do 2 things:
     * - register it in [registerPredicates] below
     * - use it
     * Important: predicate can only resolve top-level annotation. Annotation to nested class, or function will not be recognized.
     */
    val predicateProvider = session.predicateBasedProvider
    val randomConfigType = BaseObjects.RandomConfig_ClassId.constructClassLikeType()
    val randomizerCollectionType = BaseObjects.RandomizerCollection_Id.constructClassLikeType()
    val unitConeType = session.builtinTypes.unitType.coneType
    val strBuilder = StringBuilder()

    fun example_use_predicate() {
        /**
         * Find all symbol annotated with randomizable annotation
         */
//        val annotatedSymbols = predicateProvider.getSymbolsByPredicate(RDPredicates.annotatedRandomizable)
    }
    //FirExtension.registerPredicates


    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        /**
         * Must register a predicate to detect @Randomizable, so that such annotations are resolve on COMPILER_REQUIRED_ANNOTATIONS.
         * Otherwise, the annotation won´t be available for generator to use.
         */
        register(RDPredicates.annotatedRandomizable)
    }

    private fun FirClassSymbol<*>.needModifyCompanionObj(): Boolean {
        val rt = this.isAnnotatedRandomizable(session)
        return rt
    }

    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext
    ): Set<Name> {
        val rt = mutableSetOf<Name>()
        /**
         * TODO return name if:
         * - annotation on concrete class (primary constructor) + all properties are randomizable (not abstract)
         * - annotation on constructor + concrete class + all constructor arg are not abstract
         * - annotated on object
         */
        if (classSymbol.needModifyCompanionObj()) {
            rt += SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
        }
        return rt
    }

    /**
     * This function is triggered for each name returned by [getNestedClassifiersNames]
     * Remember to set origin of companion obj to: BaseObjects.firDeclarationOrigin.
     * Companion objects for @Randomizable classes are generated here.
     */
    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext,
    ): FirClassLikeSymbol<*>? {
        if (!session.predicateBasedProvider.matches(RDPredicates.annotatedRandomizable, owner)) return null
        if (owner is FirRegularClassSymbol) {
            when (name) {
                SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> {
                    val companionSymbol = createCompanionObjDeclaration(owner)
                    return companionSymbol
                }

                else -> throw IllegalStateException(
                    "Can't generate class ${
                        owner.classId.createNestedClassId(name).asSingleFqName()
                    }"
                )
            }
        } else {
            return null
        }
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        val rt = mutableSetOf<Name>()
        val origin = classSymbol.origin as? FirDeclarationOrigin.Plugin
//        if (origin?.key == BaseObjects.Fir.randomizableDeclarationKey && classSymbol.isCompanion) {
        if (classSymbol.isCompanion) {
            rt += SpecialNames.INIT // to generate constructor for companion obj
            rt += BaseObjects.randomFunctionName // to generate random() function declaration
//            rt += BaseObjects.getRandomConfigFromAnnotationFunctionName // to generate getRandomConfig() function declaration
//            rt += BaseObjects.randomizerFunctionName // to generate randomizer() function declaration
        }
        return rt
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val rt = mutableListOf(
            createDefaultPrivateConstructor(
                owner = context.owner,
                key = BaseObjects.Fir.randomizableDeclarationKey,
                generateDelegatedNoArgConstructorCall = true,
            ).symbol,
        )
        return rt
    }

    /**
     * generate function for companion obj here.
     */
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val companionObjectSymbol = context?.owner
        if (companionObjectSymbol != null) {
            val rt = generate2RandomFunctions(companionObjectSymbol, callableId)
            return rt
        }
        return super.generateFunctions(callableId, context)
    }

    /**
     * Generate 2 random functions:
     * - random<Type,Type,...>()
     * - random<Type,Type,...>(randomConfig)
     */
    private fun generate2RandomFunctions(
        companionObjectSymbol: FirClassSymbol<*>,
        functionCallableId: CallableId,
    ): List<FirNamedFunctionSymbol> {
//        val random1 = generateRandom1(companionObjectSymbol, functionCallableId)
//        val random2 = generateRandom2(companionObjectSymbol, functionCallableId)

        val random1 = generateRandom1Family(companionObjectSymbol, functionCallableId)
        val random2 = generateRandom2Family(companionObjectSymbol, functionCallableId)

        return random1 + random2
    }

    /**
     * Generate random() functions that do not accept a RandomConfig.
     * They look like this
     * ```
     * random<Type,Type,...>(
     *    randomT1: (RandomConfig)->T1,
     *    randomT2: (RandomConfig)->T2,
     *    ...
     * )
     *
     * random<Type,Type,...>(
     *    randomT1: (RandomConfig)->T1,
     *    randomT2: (RandomConfig)->T2,
     *    randomizers: ClassRandomizerCollectionBuilder.()->Unit = {},
     *    ...
     * )
     * ```
     */
    private fun generateRandom1Family(
        /**
         * companion object that contains the random function
         */
        companionObjectSymbol: FirClassSymbol<*>,
        /**
         * name of the target function
         */
        functionCallableId: CallableId,
    ): List<FirNamedFunctionSymbol> {
        return listOfNotNull(
            generateRandom1(companionObjectSymbol, functionCallableId),
        )
    }

    /**
     * random<Type,Type,...>(
     *    randomT1: (RandomConfig)->T1,
     *    randomT2: (RandomConfig)->T2,
     *    randomizers: ClassRandomizerCollectionBuilder.()->Unit = {},
     *    ...
     * )
     */
    private fun generateRandom1(
        /**
         * companion object that contains the random function
         */
        companionObjectSymbol: FirClassSymbol<*>,
        /**
         * name of the target function
         */
        functionCallableId: CallableId,
    ): FirNamedFunctionSymbol? {

        val functionName = functionCallableId.callableName
        if (functionName == BaseObjects.randomFunctionName) {

            val enclosingClass = companionObjectSymbol.getOwnerLookupTag()?.toFirRegularClassSymbol(session)
            requireNotNull(enclosingClass) { "Enclosing class for ${companionObjectSymbol.name} does not exist. This is impossible." }

            val randomFunction = createMemberFunction(
                owner = companionObjectSymbol,
                key = BaseObjects.Fir.randomizableDeclarationKey,
                name = functionName,
                returnTypeProvider = { typeParameters ->
                    val returnType = run {
                        val typeParamAsArguments = typeParameters
                            .map { it.toConeType() }
                            .toTypedArray<ConeTypeProjection>()
                        enclosingClass.constructType(typeParamAsArguments, false)
                    }
                    returnType
                },
                config = {
                    this.status {
                        isInline = true
                    }
                    /**
                     * Port type params (aka generic types) from the enclosing class to the random() function
                     */
                    mirrorTypeParamFromEnclosingClassToFunction(
                        enclosingClass = enclosingClass,
                        functionBuildingContext = this
                    )
                }
            )

            val randomLambdasParam = makeGenericRandomLambdasParam(randomFunction)
            val randomizersBuilderConfigLambda = listOf(makeRdmBuilderConfigFunctionParam(randomFunction))
            randomFunction.replaceValueParameters(randomLambdasParam+randomizersBuilderConfigLambda)

            val rt = randomFunction.symbol
            return rt
        } else {
            return null
        }
    }

    /**
     * Generate this parameter (randomizer build config function) for random() function
     * ```randomizers: Function1<ClassRandomizerCollectionBuilder,Unit> ClassRandomizerCollectionBuilder.()->Unit = {}```
     */
    private fun makeRdmBuilderConfigFunctionParam(
        randomFunction: FirSimpleFunction,
    ): FirValueParameter {

        val rdmBuilderType = BaseObjects.RandomizerCollectionBuilder_Id.constructClassLikeType()

        /**
         * Build this type: ClassRandomizerCollectionBuilder.()->Unit
         * in other form, it is: @ExtensionFunctionType Function1<ClassRandomizerCollectionBuilder,Unit>
         */
        val rdmBuilderConfigFunctionType = ConeClassLikeTypeImpl(
            lookupTag = ConeClassLikeLookupTagImpl(classId = BaseObjects.Function1_ClassId),
            typeArguments = arrayOf(rdmBuilderType, unitConeType),
            isNullable = false,
            /**
             * This attribute turn ```Function1``` into ```ClassRandomizerCollectionBuilder.()->Unit``` by adding @ExtensionFunctionType in the background.
             */
            attributes = ConeAttributes.WithExtensionFunctionType
        )

        /**
         * Build the default argument, a blank lambda, for the rdmBuilderConfigFunction parameter.
         * It is like this: {}
         */
        val default = buildAnonymousFunctionExpression {
            val functionSymbol = FirAnonymousFunctionSymbol()
            anonymousFunction = buildAnonymousFunction {
                moduleData = session.moduleData
                origin = BaseObjects.Fir.firDeclarationOrigin
                returnTypeRef = session.builtinTypes.unitType
                symbol = functionSymbol
                isLambda = true
                hasExplicitParameterList = false
                typeRef = buildResolvedTypeRef {
                    type = rdmBuilderConfigFunctionType
                }

                body = buildBlock {
                    // empty body
                    // coneTypeOrNull = unitConeType
                }
                invocationKind = EventOccurrencesRange.EXACTLY_ONCE
                inlineStatus = InlineStatus.NoInline

                /**
                 * "this" argument
                 */
                receiverParameter = buildReceiverParameter {
                    typeRef = buildResolvedTypeRef {
                        type = rdmBuilderType
                    }
                }
            }
        }
        val paramName = BaseObjects.randomizersBuilderParamName

        /**
         * Construct the parameter to store the randomizer builder config lambda function.
         */
        val rt = buildValueParameter {
            name = paramName
            moduleData = session.moduleData
            origin = BaseObjects.Fir.firDeclarationOrigin
            symbol = FirValueParameterSymbol(paramName)
            returnTypeRef = rdmBuilderConfigFunctionType.toFirResolvedTypeRef()
            containingFunctionSymbol = randomFunction.symbol
            isCrossinline = false
            isNoinline = true
            isVararg = false
            defaultValue = default
        }

        return rt
    }


    /**
     * Generate random() functions that accept a RandomConfig.
     * They look like this
     * ```
     * random<Type,Type,...>(
     *    randomConfig:RandomConfig,
     *    randomT1: (RandomConfig)->T1,
     *    randomT2: (RandomConfig)->T2,
     *    ...
     * )
     *
     * random<Type,Type,...>(
     *    randomConfig:RandomConfig,
     *    randomT1: (RandomConfig)->T1,
     *    randomT2: (RandomConfig)->T2,
     *    randomizers: ClassRandomizerCollectionBuilder.()->Unit = {},
     *    ...
     * )
     * ```
     */
    private fun generateRandom2Family(
        /**
         * companion object that contains the random function
         */
        companionObjectSymbol: FirClassSymbol<*>,
        /**
         * name of the target function
         */
        functionCallableId: CallableId,
    ): List<FirNamedFunctionSymbol> {
        val rt = listOfNotNull(
            generateRandom2(companionObjectSymbol, functionCallableId)
        )
        return rt
    }


    /**
     * generate declaration for this:
     * ```
     * random<Type,Type,...>(
     *    randomT1: (RandomConfig)->T1,
     *    randomT2: (RandomConfig)->T2,
     *    ...
     * )
     * ```
     */
    @Deprecated("don't use, kept for reference only")
    private fun generateRandom1_old(
        /**
         * companion object that contains the random function
         */
        companionObjectSymbol: FirClassSymbol<*>,
        /**
         * name of the target function
         */
        functionCallableId: CallableId,
    ): FirNamedFunctionSymbol? {
        val functionName = functionCallableId.callableName
        if (functionName == BaseObjects.randomFunctionName) {

            val enclosingClass = companionObjectSymbol.getOwnerLookupTag()?.toFirRegularClassSymbol(session)
            requireNotNull(enclosingClass) { "Enclosing class for ${companionObjectSymbol.name} does not exist. This is impossible." }

            val randomFunction = createMemberFunction(
                owner = companionObjectSymbol,
                key = BaseObjects.Fir.randomizableDeclarationKey,
                name = functionName,
                returnTypeProvider = { typeParameters ->
                    val returnType = run {
                        val typeParamAsArguments = typeParameters
                            .map { it.toConeType() }
                            .toTypedArray<ConeTypeProjection>()

                        enclosingClass.constructType(typeParamAsArguments, false)
                    }
                    returnType
                },
                config = {
                    /**
                     * Port type params from the enclosing class to the random() function
                     */
                    mirrorTypeParamFromEnclosingClassToFunction(
                        enclosingClass = enclosingClass,
                        functionBuildingContext = this
                    )
                }
            )
            val randomLambdasParam = makeGenericRandomLambdasParam(randomFunction)
            randomFunction.replaceValueParameters(randomLambdasParam)

            val rt = randomFunction.symbol
            return rt
        } else {
            return null
        }
    }

    /**
     * generate declaration for this:
     * ```
     * random<Type,Type,...>(
     *    randomConfig:RandomConfig,
     *    randomT1: (RandomConfig)->T1,
     *    randomT2: (RandomConfig)->T2,
     *    ...
     * )
     * ```
     */
    private fun generateRandom2(
        /**
         * companion object that contains the random function
         */
        companionObjectSymbol: FirClassSymbol<*>,
        /**
         * name of the target function
         */
        functionCallableId: CallableId,
    ): FirNamedFunctionSymbol? {
        val functionName = functionCallableId.callableName

        if (functionName == BaseObjects.randomFunctionName) {

            val enclosingClass = companionObjectSymbol.getOwnerLookupTag()?.toFirRegularClassSymbol(session)
            requireNotNull(enclosingClass) { "Enclosing class for ${companionObjectSymbol.name} does not exist. This is impossible." }

            val randomFunctionWithRandomConfig = createMemberFunction(
                owner = companionObjectSymbol,
                key = BaseObjects.Fir.randomizableDeclarationKey,
                name = functionName,
                returnTypeProvider = { typeParameters ->

                    val returnType = run {
                        val parametersAsArguments =
                            typeParameters.map { it.toConeType() }.toTypedArray<ConeTypeProjection>()
                        enclosingClass.constructType(parametersAsArguments, false)
                    }

                    returnType
                },
                config = {
                    val functionBuildingContext = this

                    functionBuildingContext.valueParameter(
                        name = BaseObjects.randomConfigParamName,
                        type = BaseObjects.Fir.randomConfigClassId.constructClassLikeType(
                            typeArguments = emptyArray(),
                            isNullable = false
                        ),
                    )
                    this.status {
                        isInline = true
                    }
                    /**
                     * Port type params from the enclosing class to the random() function
                     */
                    mirrorTypeParamFromEnclosingClassToFunction(
                        enclosingClass = enclosingClass,
                        functionBuildingContext = functionBuildingContext
                    )
                }
            )
            val newValueParam = run {
                /**
                 * The current value params include the [RandomConfig] param, this one must be kept
                 */
                val currentValueParams = randomFunctionWithRandomConfig.valueParameters

                /**
                 * This is a list of lambda function to generate random for each generic type param in the target class
                 */
                val genericFactoryLambdaParams = makeGenericRandomLambdasParam(randomFunctionWithRandomConfig)

                /**
                 * This is a lambda that will be used to config randomizer collection builder.
                 */
                val randomizerBuilderConfigLambda = makeRdmBuilderConfigFunctionParam(randomFunctionWithRandomConfig)

                val params =
                    currentValueParams +
                    genericFactoryLambdaParams +
                    randomizerBuilderConfigLambda

                params
            }

            randomFunctionWithRandomConfig.replaceValueParameters(newValueParam)
            return randomFunctionWithRandomConfig.symbol
        } else {
            return null
        }
    }

    /**
     * For each type param of [enclosingClass], create a mirror type param in a function represented by [functionBuildingContext].
     * This is used in generating declaration for random() functions.
     */
    private fun mirrorTypeParamFromEnclosingClassToFunction(
        enclosingClass: FirRegularClassSymbol,
        functionBuildingContext: SimpleFunctionBuildingContext
    ) {
        enclosingClass.typeParameterSymbols.forEach { targetClassTypeParam ->
            functionBuildingContext.typeParameter(
                name = targetClassTypeParam.name,
                variance = targetClassTypeParam.variance,
                isReified = true,
                key = BaseObjects.Fir.randomizableDeclarationKey,
                config = {

                    targetClassTypeParam.resolvedBounds.forEach {
                        bound(it.type)
                    }
                }
            )
        }
    }


    /**
     * Create a generic random function for each type parameter of a [randomFunction].
     *
     * They are the parameter in this:
     * ```
     * fun <T1,T2> random(
     *    randomT1:(RandomConfig, RandomizerCollection)->T1, // <~~ this
     *    randomT2:(RandomConfig, RandomizerCollection)->T2, // <~~ this
     *    ...
     * )
     * ```
     */
    private fun makeGenericRandomLambdasParam(
        randomFunction: FirSimpleFunction
    ): List<FirValueParameter> {

        val rt = randomFunction.typeParameters.map { typeParam ->
            /**
             * For each type param of [randomFunction], create a generic random function, such as:
             * - randomT1 : Function2<RandomConfig, RandomizerCollection, T1> (RandomConfig)->T1
             * - randomT2 : Function2<RandomConfig, RandomizerCollection, T2> (RandomConfig)->T2
             */
            val randomLambda = BaseObjects.Function1_ClassId.constructClassLikeType(
                typeArguments = arrayOf(randomConfigType, typeParam.toConeType()),
                isNullable = false,
                attributes = ConeAttributes.WithExtensionFunctionType
            )
            val paramName = Name.identifier("random${typeParam.name}")

            buildValueParameter {
                name = paramName
                moduleData = session.moduleData
                origin = BaseObjects.Fir.randomizableDeclarationKey.origin
                symbol = FirValueParameterSymbol(paramName)
                returnTypeRef = randomLambda.toFirResolvedTypeRef()
                containingFunctionSymbol = randomFunction.symbol
                isCrossinline = false
                isNoinline = true
                isVararg = false
            }
        }

        return rt

    }


    /**
     * Create companion object if there isn't one already. Return that companion object.
     */
    private fun createCompanionObjDeclaration(
        /**
         * This is the class that contain the intended companion object.
         */
        companionOwner: FirRegularClassSymbol
    ): FirRegularClassSymbol {
        val currentCompanion = companionOwner.companionObjectSymbol
        if (currentCompanion != null) {
            return currentCompanion
        } else {
            val companionClass = createCompanionObject(companionOwner, BaseObjects.Fir.randomizableDeclarationKey)
            val rt = companionClass.symbol
            return rt
        }
    }
}
