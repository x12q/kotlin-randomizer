package com.x12q.randomizer.ir_plugin.frontend.k2

import com.x12q.randomizer.ir_plugin.frontend.k2.base.BaseObjects
import com.x12q.randomizer.ir_plugin.frontend.k2.util.RDPredicates
import org.jetbrains.kotlin.backend.common.serialization.checkIsFunctionTypeInterfacePackageFqName
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.copy
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunctionCopy
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.scopes.FirTypeScope
import org.jetbrains.kotlin.fir.scopes.getFunctions
import org.jetbrains.kotlin.fir.scopes.scopeForSupertype
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.toSymbol
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
@OptIn(SymbolInternals::class)
class RDFirGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {

//    val c = FirCompanionGenerationTransformer(session)
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
        val annotatedSymbols = predicateProvider.getSymbolsByPredicate(RDPredicates.annotatedRandomizable)
    }
//    override fun FirDeclarationPredicateRegistrar.registerPredicates(){
//        register(RDPredicates.annotatedRandomizable)
//    }


    fun FirClassSymbol<*>.needCompanionObj(): Boolean {
        return this.name.identifier.contains("Q123")
    }

//    fun FirClassSymbol<*>.needRandomFunction(): Boolean {
//        val rt = this.isCompanion && this.name == SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
//        return rt
//    }


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
     * Generate companion object here.
     * Remember, generate function for such companion in the same generation extension class.
     * Remember to set origin of companion obj to: BaseObjects.firDeclarationOrigin
     */
    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext
    ): FirClassLikeSymbol<*>? {

        if (owner is FirRegularClassSymbol) {
            when (name) {
                SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> {
                    val companionSymbol = generateCompanionObjDeclaration(owner, name)
                    return companionSymbol
                }

                else -> error(
                    "Can't generate class ${
                        owner.classId.createNestedClassId(name).asSingleFqName()
                    }"
                ) //TODO why throw an exception here
            }
        } else {
            return null
        }
    }


    override fun getTopLevelClassIds(): Set<ClassId> {
        return super.getTopLevelClassIds()
    }


    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        val rt = mutableSetOf<Name>()
        val origin = classSymbol.origin as? FirDeclarationOrigin.Plugin
        if (origin?.key == BaseObjects.Fir.randomizableDeclarationKey && classSymbol.isCompanion) {
            rt += SpecialNames.INIT
            rt += BaseObjects.randomFunctionName
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

    override fun getTopLevelCallableIds(): Set<CallableId> {
        return super.getTopLevelCallableIds()
    }

    /**
     * generate function for companion obj here.
     * remember to set origin of the generated function to: BaseObjects.firDeclarationOrigin
     */
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val owner = context?.owner
        val origin = owner?.origin as? FirDeclarationOrigin.Plugin
        if(origin?.key == BaseObjects.Fir.randomizableDeclarationKey && owner.isCompanion){
            if(callableId.callableName == BaseObjects.randomFunctionName){
                val f = buildSimpleFunction {
                    val builder = this
                    builder.symbol = FirNamedFunctionSymbol(callableId)
                    builder.origin = BaseObjects.Fir.declarationOrigin
                }.symbol
                return listOf(f)
            }
        }else{
            return emptyList()
        }

        return super.generateFunctions(callableId, context)

    }


    /**
     * Create companion object if there isn't one already. Return that companion object.
     */
    private fun generateCompanionObjDeclaration(owner: FirRegularClassSymbol, name: Name): FirRegularClassSymbol? {
        if (owner.companionObjectSymbol != null) {
            return null
        } else {
            val companion = createCompanionObject(owner, BaseObjects.Fir.randomizableDeclarationKey) {
                val anyClassId = ClassId(FqName("kotlin"), Name.identifier("Any"))
                superType(anyClassId.constructClassLikeType(emptyArray(), false))
            }
            val rt = companion.symbol
            return rt
        }
    }
}
