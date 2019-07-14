package p2kotplot.plotlytypes

import kotlinx.serialization.json.JsonLiteral
import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import sun.plugin.dom.exception.InvalidStateException


fun String.hasArrayTypePrefix() = this.startsWith(UnionArrayMarkerPrefix)
fun String.getArrayTypeElementName() = this.removePrefix(UnionArrayMarkerPrefix)
private const val UnionArrayMarkerPrefix = "[UNION_ARRAY_MARKER]"

data class UnionType(val types: List<KotPlotType>) : KotPlotType {

    private fun KotPlotType.getName(): String {
        assert(this is ReferenceType || this is ArrayType || this is ParameterizedType)
        { "Non-literal Union types contain only reference types or array types." }
        return when (this) {
            is ParameterizedType -> {
                assert(this.name == "Array" && this.typeArguments.size == 1) { "Non-literal Union types contain only reference types or array types." }
                UnionArrayMarkerPrefix + this.typeArguments[0].getName()
            }

            is ReferenceType -> this.typeName
            is ArrayType -> this.elementType.getName()
            else -> throw InvalidStateException("Impossible")
        }

    }

    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        isPartial: Boolean
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

            // Add the first type as a parameter to the function. The other types will be duplicates of the original function.
            builder.addParameter(
                name = nameAsParameter,
                belongsToFunction = functionAppearsIn,
                type = types[0].getName(),
                documentation = documentationAsParameter,
                optional = isOptional,
                paramInConstructorOfClass = "NONE - THIS IS AN ARBITRARY PLACEHOLDER SO IT ISN'T IN ANY CLASS",
                overloadNumOfFunctionBelongingTo = FlatBuilderRepresentation.defaultOverloadNum
            )

            // Add the other types as duplicates of the original function
            for (i in 1 until types.size) {
                // Add the existing parameters
                for (parameter in existingParameters) {
                    builder.addParameter(
                        parameter.name,
                        parameter.type,
                        parameter.optional,
                        parameter.belongsToFunction,
                        overloadNumOfFunctionBelongingTo = FlatBuilderRepresentation.defaultOverloadNum + i,
                        paramInConstructorOfClass = parameter.paramInConstructorOfClass,
                        documentation = parameter.documentation
                    )
                }
                // Add the additional typed parameter
                builder.addParameter(
                    name = nameAsParameter,
                    belongsToFunction = functionAppearsIn,
                    type = types[i].getName(),
                    documentation = documentationAsParameter,
                    optional = isOptional,
                    paramInConstructorOfClass = "NONE - THIS IS AN ARBITRARY PLACEHOLDER SO IT ISN'T IN ANY CLASS",
                    overloadNumOfFunctionBelongingTo = FlatBuilderRepresentation.defaultOverloadNum + i
                )
            }


        }

        if (literals.isNotEmpty()) addEnumOfLiterals()
        if (types.isNotEmpty()) addOverloadsOfTypes()


    }

}