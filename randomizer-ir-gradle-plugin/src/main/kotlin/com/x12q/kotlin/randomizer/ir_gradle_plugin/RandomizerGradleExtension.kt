package com.x12q.kotlin.randomizer.ir_gradle_plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class RandomizerGradleExtension(objects: ObjectFactory) {
  /**
   * enable or disable the ir-plugin
   */
  val enable: Property<Boolean> = objects.property(Boolean::class.java)
}
