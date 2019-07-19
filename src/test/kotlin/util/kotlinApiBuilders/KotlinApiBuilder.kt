package util.kotlinApiBuilders

import p2kotplot.*
import p2kotplot.ast.Enum
import p2kotplot.ast.toBuilderName
import util.TestBuilder
import util.kotlinApiBuilders.BuilderFunctionType.*



@TestBuilder
class KotlinApiBuilder {
    /*private*/ val builderClasses = mutableListOf<BuilderClassComponents>()
    /*private*/ val topLevelFunctions = mutableListOf<BuilderFunctionComponents>()
    /*private*/ val enums = mutableListOf<Enum>()

    inline fun builderClass(name: String, init: BuilderClassBuilder.() -> Unit) {
        builderClasses.add(BuilderClassBuilder().apply(init).build(name))
    }

    inline fun topLevelFunction(
        name: String,
//        documentation: String = "",
        hasInitParam: Boolean,
        init: BuilderFunctionBuilder.() -> Unit = {}
    ) {
        topLevelFunctions.add(
            BuilderFunctionBuilder(forPrimitiveArray = false).apply(init).build(
                name,
//                documentation,
                name.toBuilderName(),
                hasInitParam,
                functionType = TopLevel
            )
        )
    }

    inline fun enum(name: String, constants: EnumConstantsBuilder.() -> Unit) {
        enums.add(Enum(name, EnumConstantsBuilder().apply(constants).build()))
    }

    fun build() = KotlinApi(builderClasses, topLevelFunctions, enums)

}


