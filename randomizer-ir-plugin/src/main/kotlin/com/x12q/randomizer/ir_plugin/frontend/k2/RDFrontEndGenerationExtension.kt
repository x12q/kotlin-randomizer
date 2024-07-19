package com.x12q.randomizer.ir_plugin.frontend.k2

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.frontend.k2.util.RDPredicates
import com.x12q.randomizer.ir_plugin.frontend.k2.util.getRandomizableAnnotation
import com.x12q.randomizer.ir_plugin.frontend.k2.util.isAnnotatedRandomizable
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.getKClassArgument
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.getOwnerLookupTag
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.resolve.toFirRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.*
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.scopes.LexicalScope

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

    private fun FirClassSymbol<*>.needCompanionObj(): Boolean {
        val rt = this.isAnnotatedRandomizable(session) // || this.name.toString().contains("Q123")
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
        if (classSymbol.needCompanionObj()) {
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
        if (origin?.key == BaseObjects.Fir.randomizableDeclarationKey && classSymbol.isCompanion) {
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
            val origin = companionObjectSymbol.origin as? FirDeclarationOrigin.Plugin
            val ownerClassIsGeneratedCompanion =
                // TODO reconsider this, may only need to check if it is a companion obj
                companionObjectSymbol.isCompanion && origin?.key == BaseObjects.Fir.randomizableDeclarationKey
            if (ownerClassIsGeneratedCompanion) {
                val twoRandomFunctions = generate2RandomFunctions(companionObjectSymbol, callableId)
                val randomizerFunction = generateRandomizerFunction(companionObjectSymbol, callableId)
                val rt = ((twoRandomFunctions ?: emptyList()) + listOf(randomizerFunction)).filterNotNull()
                return rt

            }
        }
        return super.generateFunctions(callableId, context)
    }

    private fun generateRandomizerFunction(
        companionObjectSymbol: FirClassSymbol<*>,
        callableId: CallableId,
    ):FirNamedFunctionSymbol?{
        val functionName = callableId.callableName
        if(functionName == BaseObjects.randomizerFunctionName){
            val randomizerFunction = createMemberFunction(
                owner = companionObjectSymbol,
                key =  BaseObjects.Fir.randomizableDeclarationKey,
                name = functionName,
                returnTypeProvider = { typeParameters->
                    val enclosingClass = companionObjectSymbol.getOwnerLookupTag()?.toFirRegularClassSymbol(session)
                    val returnType = if (enclosingClass != null) {
                        val parametersAsArguments = typeParameters.map { it.toConeType() }.toTypedArray<ConeTypeProjection>()

                        BaseObjects.randomizerId.constructClassLikeType(
                            arrayOf(enclosingClass.constructType(parametersAsArguments, false)),
                            isNullable = false
                        )

                    } else {
                        throw IllegalStateException("Companion object $companionObjectSymbol is without an enclosing class")
                    }
                    returnType

                }
            ).symbol
            return randomizerFunction
        }else{
            return null
        }
    }

    /**
     * Generate 2 random functions:
     * - random()
     * - random(randomConfig)
     */
    private fun generate2RandomFunctions(
        companionObjectSymbol: FirClassSymbol<*>,
        callableId: CallableId,
    ): List<FirNamedFunctionSymbol>? {
        val functionName = callableId.callableName
        if (functionName == BaseObjects.randomFunctionName) {
            val randomFunction = createMemberFunction(
                owner = companionObjectSymbol,
                key = BaseObjects.Fir.randomizableDeclarationKey,
                name = functionName,
                returnTypeProvider = { typeParameters ->
                    val enclosingClass = companionObjectSymbol.getOwnerLookupTag()?.toFirRegularClassSymbol(session)
                    val returnType = if (enclosingClass != null) {

                        val typeParamAsArguments = typeParameters
                            .map { it.toConeType() }
                            .toTypedArray<ConeTypeProjection>()

                        enclosingClass.constructType(typeParamAsArguments, false)
                    } else {
                        throw IllegalStateException("Companion object $companionObjectSymbol is without an enclosing class")
                    }
                    returnType
                }
            ).symbol

            val randomFunctionWithRandomConfig = createMemberFunction(
                owner = companionObjectSymbol,
                key = BaseObjects.Fir.randomizableDeclarationKey,
                name = functionName,
                returnTypeProvider = { typeParameters ->

                    val enclosingClass = companionObjectSymbol.getOwnerLookupTag()?.toFirRegularClassSymbol(session)

                    val returnType = if (enclosingClass != null) {
                        val parametersAsArguments =
                            typeParameters.map { it.toConeType() }.toTypedArray<ConeTypeProjection>()
                        enclosingClass.constructType(parametersAsArguments, false)
                    } else {
                        throw IllegalStateException("Companion object $companionObjectSymbol is without an enclosing class")
                    }

                    returnType
                }
            ) {
                val buildingContext = this
                buildingContext.valueParameter(
                    name = BaseObjects.randomConfigParamName,
                    type = BaseObjects.Fir.randomConfigClassId.constructClassLikeType(
                        typeArguments = emptyArray(),
                        isNullable = false
                    ),
                )
            }.symbol
            return listOf(randomFunction, randomFunctionWithRandomConfig)
        } else {
            return null
        }
    }


    /**
     * Create companion object if there isn't one already. Return that companion object.
     */
    private fun createCompanionObjDeclaration(
        /**
         * This is the class that contain the intended companion object.
         */
        companionOwner: FirRegularClassSymbol
    ): FirRegularClassSymbol? {
        if (companionOwner.companionObjectSymbol != null) {
            return null
        } else {
            val companionClass = createCompanionObject(companionOwner, BaseObjects.Fir.randomizableDeclarationKey)
            val rt = companionClass.symbol
            return rt
        }
    }
}
