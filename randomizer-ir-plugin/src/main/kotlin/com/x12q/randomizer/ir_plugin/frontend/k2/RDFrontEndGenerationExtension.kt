package com.x12q.randomizer.ir_plugin.frontend.k2

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.frontend.k2.util.RDPredicates
import com.x12q.randomizer.ir_plugin.frontend.k2.util.isAnnotatedRandomizable
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.getOwnerLookupTag
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.SimpleFunctionBuildingContext
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.resolve.toFirRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.*
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.*
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
    val randomConfigTypeArgument = BaseObjects.randomConfigClassId.constructClassLikeType()
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
            rt += BaseObjects.randomizerFunctionName // to generate randomizer() function declaration
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
        val random1 = generateRandom1(companionObjectSymbol, functionCallableId)
        val random2 = generateRandom2(companionObjectSymbol, functionCallableId)
        return listOfNotNull(random1, random2)
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

                val randomLambdaParam = currentValueParams + makeGenericRandomLambdasParam(randomFunctionWithRandomConfig)
                randomLambdaParam
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
    private fun mirrorTypeParamFromEnclosingClassToFunction(enclosingClass:FirRegularClassSymbol, functionBuildingContext:SimpleFunctionBuildingContext){
        enclosingClass.typeParameterSymbols.forEach { targetClassTypeParam ->
            functionBuildingContext.typeParameter(
                name = targetClassTypeParam.name,
                variance = targetClassTypeParam.variance,
                isReified = false,
                key = BaseObjects.Fir.randomizableDeclarationKey,
            )
        }
    }


    /**
     * Create a generic random function for each type parameter of a [randomFunction].
     *
     * They are the parameter in this:
     * ```
     * fun <T1,T2> random(
     *    randomT1:(RandomConfig)->T1, // <~~ this
     *    randomT2:(RandomConfig)->T2, // <~~ this
     *    ...
     * )
     * ```
     */
    private fun makeGenericRandomLambdasParam(
        randomFunction:FirSimpleFunction
    ): List<FirValueParameter> {

        val rt = randomFunction.typeParameters.map { typeParam ->
            /**
             * For each type param of [randomFunction], create a generic random function, such as:
             * - randomT1 : (RandomConfig)->T1
             * - randomT2 : (RandomConfig)->T2
             */
            val randomLambda = BaseObjects.function1Name.constructClassLikeType(
                typeArguments = arrayOf(randomConfigTypeArgument, typeParam.toConeType()),
                isNullable = false
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
