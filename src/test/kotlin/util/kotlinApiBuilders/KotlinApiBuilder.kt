package util.kotlinApiBuilders

import p2kotplot.*
import p2kotplot.ast.Enum
import p2kotplot.ast.toBuilderName
import util.TestBuilder
import util.kotlinApiBuilders.BuilderFunctionType.*



@TestBuilder
class KotlinApiBuilder {
    private val builderClasses = mutableListOf<BuilderClassComponents>()
    private val topLevelFunctions = mutableListOf<BuilderFunctionComponents>()
    private val enums = mutableListOf<Enum>()

    fun builderClass(name: String, init: BuilderClassBuilder.() -> Unit) {
        builderClasses.add(BuilderClassBuilder().apply(init).build(name))
    }

    fun topLevelFunction(
        name: String,
//        documentation: String = "",
        hasInitParam: Boolean,
        init: BuilderFunctionBuilder.() -> Unit = {}
    ) {
        topLevelFunctions.add(
            BuilderFunctionBuilder().apply(init).build(
                name,
//                documentation,
                name.toBuilderName(),
                hasInitParam,
                functionType = TopLevel
            )
        )
    }

    fun enum(name: String, constants: EnumConstantsBuilder.() -> Unit) {
        enums.add(Enum(name, EnumConstantsBuilder().apply(constants).build()))
    }

    fun build() = KotlinApi(builderClasses, topLevelFunctions, enums)

}


