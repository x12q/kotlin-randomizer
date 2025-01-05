package com.x12q.kotlin.randomizer.ir_gradle_plugin

import com.x__q.randomizer_ir_gradle_plugin.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class RandomizerGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project): Unit = with(target) {
        extensions.create("kotlinRandomizer", RandomizerGradleExtension::class.java)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    /**
     * Id of the compiler plugin
     */
    override fun getCompilerPluginId(): String = BuildConfig.IR_PLUGIN_ID

    /**
     * Artifact address of the compiler plugin managed by this gradle plugin
     */
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = BuildConfig.IR_PLUGIN_GROUP_ID,
        artifactId = BuildConfig.IR_PLUGIN_ARTIFACT_ID,
        version = BuildConfig.IR_PLUGIN_VERSION
    )

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(RandomizerGradleExtension::class.java)
        return project.provider {
            listOf(
                SubpluginOption(key = "enable", value = extension.enable.get().toString())
            )
        }
    }
}
