package util.kotlinApiBuilders

import p2kotplot.ast.EnumConstant
import util.TestBuilder

@TestBuilder
class EnumConstantsBuilder {
    private val constants = mutableListOf<EnumConstant>()
    fun constant(name: String, originalName: String) {
        constants.add(EnumConstant(name, originalName))
    }

    fun build() = constants
}
