package p2kotplot.ast

import p2kotplot.plotlytypes.Interface
import p2kotplot.plotlytypes.TypeAlias
import p2kotplot.plotlytypes.builderClassName
import sun.plugin.dom.exception.InvalidStateException

fun createEmptyBuilder(builderName: String, builderType: BuilderType): Builder =
    Builder(
        builderFunctions = mutableListOf(
            BuilderFunction(
                functionName = builderName,
                parameters = mutableListOf()
            )
        ),
        builderClass = BuilderClass(
            name = builderName.builderClassName(),
            builders = mutableListOf(),
            buildStatements = mutableListOf(),
            constructorParameters = mutableListOf(),
            jsonArrayFields = mutableListOf()
        ),
        builderType = builderType
    )

data class TypeData(
    private val interfaceTypeData: List<Interface>,
    private val typeAliasData: List<TypeAlias>
){
    //TODO: handle type alias aswell
    fun findType(name: String): Interface {
        return interfaceTypeData.find { it.name == name }
            ?: throw InvalidStateException("Could not find class with name $name")
    }
}

class MutableBuilderTree(private val wrappedBuilder: Builder, private val data: TypeData, builderName: String/*, private val*/ ) {
    init {
        wrappedBuilder.builderFunctions.add(
            BuilderFunction(
                functionName = builderName,
                parameters = mutableListOf()
            )
        )
    }

    /**
     * We only operate on this one for starters, and then duplicate it with union types.
     */
    private fun standardBuilderFunction() = wrappedBuilder.builderFunctions[0]

    fun name() = standardBuilderFunction().functionName

    fun findType(name: String): Interface = data.findType(name)

    fun addPrimitiveParameter(name: String, type: String) {
//        val parameterName = name()
//        val name = wrappedBuilder.builderClass.
        standardBuilderFunction().parameters.add(BuilderParameter(name, type))
        wrappedBuilder.builderClass.constructorParameters.add(BuilderParameter(name, type))

        wrappedBuilder.builderClass.buildStatements.add(
            BuildStatement(
                variableName = name,
                builderType = BuildStatementType.Primitive
            )
        )
    }

    fun addArrayBuilder(
        builderName: String,
        arrayGenericType: String,
        init: MutableBuilderTree.() -> Unit
    ) {
        wrappedBuilder.builderClass.jsonArrayFields.add(
            ArrayField(arrayName = name(), arrayType = arrayGenericType)
        )

        wrappedBuilder.builderClass.buildStatements.add(
            BuildStatement(
                variableName = name(),
                builderType = BuildStatementType.Array
            )
        )

        addBuilder("addOneOf$builderName", BuilderType.Array, init)
    }

    fun addReferenceBuilder(builderName: String , init: MutableBuilderTree.() -> Unit) {
//        wrappedBuilder.builderClass.buildStatements.add(
//            BuildStatement(
//                variableName = name(),
//                builderType = BuildStatementType.Reference
//            )
//        )

        addBuilder(builderName, BuilderType.Reference, init)
    }

    private fun addBuilder(builderName: String, builderType: BuilderType, init: MutableBuilderTree.() -> Unit) {
        MutableBuilderTree(createEmptyBuilder(builderName, builderType).also {
            this.wrappedBuilder.builderClass.builders.add(it)
        }, builderName = builderName,data = data).apply(init)
    }

    fun extractFinishedBuilderAtTheEndOfProcessing() = wrappedBuilder
}