package p2kotplot.plotlytypes

import p2kotplot.ast.DefaultOverloadNum
import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import sun.plugin.dom.exception.InvalidStateException


fun String.hasArrayTypePrefix() = this.startsWith(UnionArrayMarkerPrefix)
fun String.getArrayTypeElementName() = this.removePrefix(UnionArrayMarkerPrefix)
private const val UnionArrayMarkerPrefix = "[UNION_ARRAY_MARKER]"

//TODO: solution is incomplete. best solution is probably to add a "overloadNum" parameter to `add`.
data class UnionType(val types: List<KotPlotType>) : KotPlotType {

//    private fun KotPlotType.getName(): String {
//        assert(this is ReferenceType || this is ArrayType || this is ParameterizedType)
//        { "Non-literal Union types contain only reference types or array types." }
//        return when (this) {
//            is ParameterizedType -> {
//                assert(this.name == "Array" && this.typeArguments.size == 1) { "Non-literal Union types contain only reference types or array types." }
//                UnionArrayMarkerPrefix + this.typeArguments[0].getName()
//            }
//
//            is ReferenceType -> this.typeName
//            is ArrayType -> this.elementType.getName()
//            else -> throw InvalidStateException("Impossible")
//        }
//
//    }

    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        isPartial: Boolean,
        overloadNum: Int
    ) {

        val literals = types.filterIsInstance<LiteralType>()
        val types = types.filter { it !is LiteralType }

        assert(literals.isEmpty() || types.isEmpty()) { "A union type contains only literals or only non-literals" }

        // Literals are turned into one big enums
        fun addEnumOfLiterals() {
            val createdEnumName = literals.joinToString("Or") { it.literal }
            builder.addEnum(name = createdEnumName, elements = literals.map { it.literal })
            builder.addParameter(
                name = nameAsParameter,
                type = createdEnumName,
                belongsToFunction = functionAppearsIn,
                paramInConstructorOfClass = builderClassIn,
                optional = isOptional,
                documentation = documentationAsParameter
            )
        }

        //TODO for now we just add overloads for every union type, a more complete solution would create entire new classes
        // Things that are not literals in a union type duplicate the builder functions and created a new override.
        fun addOverloadsOfTypes() {
            // Just add parameter to the constructor with an "any" type
            builder.addParameter(
                name = nameAsParameter,
                optional = isOptional,
                documentation = documentationAsParameter,
                type = "Any",
                paramInConstructorOfClass = builderClassIn,
                belongsToFunction = "NONE - THIS IS AN ARBITRARY PLACEHOLDER SO IT ISN'T IN ANY FUNCTION"
            )

            val existingParameters = builder.getParametersOfFunction(functionAppearsIn)

            // Add the other types as duplicates of the original function
            types.forEachIndexed { i, type ->
                // Add the existing parameters
                for (parameter in existingParameters) {
                    builder.addParameter(
                        parameter.name,
                        parameter.type,
                        parameter.optional,
                        parameter.belongsToFunction,
                        overloadNum = DefaultOverloadNum + i,
                        paramInConstructorOfClass = "NONE - THIS IS AN ARBITRARY PLACEHOLDER SO IT ISN'T IN ANY CLASS",
                        documentation = parameter.documentation
                    )
                }
                // Add the additional typed parameter
                type.add(
                    builder,
                    typeData,
                    "NONE - THIS IS AN ARBITRARY PLACEHOLDER SO IT ISN'T IN ANY CLASS",
                    nameAsParameter,
                    isOptional,
                    functionAppearsIn,
                    documentationAsParameter,
                    overloadNum = DefaultOverloadNum + i
                )
            }

        }

        if (literals.isNotEmpty()) addEnumOfLiterals()
        if (types.isNotEmpty()) addOverloadsOfTypes()
        val x = 2

    }
}