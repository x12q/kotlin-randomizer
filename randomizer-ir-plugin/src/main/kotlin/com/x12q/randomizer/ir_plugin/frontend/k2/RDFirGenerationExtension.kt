package com.x12q.randomizer.ir_plugin.frontend.k2

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.base.BaseObjects.randomizableName
import com.x12q.randomizer.ir_plugin.frontend.k2.util.RDPredicates
import com.x12q.randomizer.ir_plugin.frontend.k2.util.isAnnotatedRandomizable
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.resolvePhase
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

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
    /**
     * Predicate provider is used to create predicate that allow quick access to declarations that meet certain requirement.
     * To use a predicate, must do 2 things:
     * - register it in [registerPredicates] below
     * - use it
     * Important: predicate can only resolve top-level annotation. Annotation to nested class, or function will not be recognized.
     */
    val predicateProvider = session.predicateBasedProvider

    fun example_use_predicate(){
        /**
         * Find all symbol annotated with randomizable annotation
         */
        val annotatedSymbols = predicateProvider.getSymbolsByPredicate(RDPredicates.annotatedRandomizable)
    }
    override fun FirDeclarationPredicateRegistrar.registerPredicates(){
//        register(RDPredicates.annotatedRandomizable)
    }


    override fun getNestedClassifiersNames(classSymbol: FirClassSymbol<*>, context: NestedClassGenerationContext): Set<Name>{
        val rt = mutableSetOf<Name>()
        if(classSymbol.isAnnotatedRandomizable(session)){
            rt += BaseObjects.companionObjName
        }
        return rt
    }

    /**
     * Generate companion object here.
     * Remember, generate function for such companion in the same generation extension class.
     * Remember to set origin of companion obj to: FirDeclarationOrigin.Plugin
     */
    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext
    ): FirClassLikeSymbol<*>? {

        if (owner is FirRegularClassSymbol){
            when(name){
                BaseObjects.companionObjName -> generateCompanionObjDeclaration(owner)
                else -> error("Can't generate class ${owner.classId.createNestedClassId(name).asSingleFqName()}") //TODO why throw an exception here
            }

            return super.generateNestedClassLikeDeclaration(owner, name, context)
        }else{
            return null
        }
    }

    /**
     * generate function for companion obj here.
     * remember to set origin of the generated function to: FirDeclarationOrigin.Plugin
     */
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        return super.generateFunctions(callableId, context)
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        classSymbol.fir.resolvePhase
        val rt = mutableSetOf<Name>()
        if (classSymbol.isAnnotatedRandomizable(session)) {
            rt += randomizableName
        }

        return rt
    }


    /**
     * Create companion object if there isn't one already. Return that companion object.
     */
    private fun generateCompanionObjDeclaration(owner: FirRegularClassSymbol): FirRegularClassSymbol? {
        if (owner.companionObjectSymbol != null) return null
        val companion = createCompanionObject(owner, RandomizableDeclarationKey)
        return companion.symbol
    }
}
