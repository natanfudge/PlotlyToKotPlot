package p2kotplot.plotlytypes

import p2kotplot.ast.*


fun String.hasArrayTypePrefix() = this.startsWith(UnionArrayMarkerPrefix)
fun String.getArrayTypeElementName() = this.removePrefix(UnionArrayMarkerPrefix)
private const val UnionArrayMarkerPrefix = "[UNION_ARRAY_MARKER]"

//TODO: make it obvious that when you have part of a union create a builder function that only ONE can be used of the union parts.
data class UnionType(val types: List<KotPlotType>) : KotPlotType {

    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderFunctionInClass: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String,
        documentationAsParameter: String,
        isPartial: Boolean,
        overloadNum: Int,
        paramInConstructorOfClass: String?,
        showInConstructor: Boolean
    ) {

        val literals = types.filterIsInstance<LiteralType>()
        val types = types.filter { it !is LiteralType }

        assert(literals.isEmpty() || types.isEmpty()) { "A union type contains only literals or only non-literals" }

        // Literals are turned into one big enums
        fun addEnumOfLiterals() {
            val createdEnumName = literals.joinToString("Or") { it.literal.toTitleCase() }
            builder.addEnum(
                name = createdEnumName,
                elements = literals.map { EnumConstant(name = it.literal.toTitleCase(), originalName = it.literal) })
            builder.addParameter(
                name = nameAsParameter,
                type = createdEnumName,
                belongsToFunction = functionAppearsIn,
                inBuilderFunctionInClass = builderFunctionInClass,
                inConstructorOfClass = builderFunctionInClass,
                optional = isOptional,
                documentation = documentationAsParameter,
                isEnumType = true
            )
        }

        fun addOverloadsOfTypes() {
            if (types.any { it.isPrimitiveType() }) {
                // Just add parameter to the constructor with an "any" type
                builder.addParameter(
                    name = nameAsParameter,
                    optional = isOptional,
                    documentation = documentationAsParameter,
                    type = "Any",
                    inConstructorOfClass = builderFunctionInClass,
                    inBuilderFunctionInClass = null,
                    belongsToFunction = "NONE - THIS IS AN ARBITRARY PLACEHOLDER SO IT ISN'T IN ANY FUNCTION",
                    isEnumType = false
                )
            }


            val existingParameters = builder.getParametersOfFunction(nameAsParameter)

            // Add the other types as duplicates of the original function
            types.forEachIndexed { i, type ->
                // Add the existing parameters
                for (parameter in existingParameters) {
                    builder.addParameter(
                        parameter.name,
                        parameter.type,
                        parameter.optional,
                        parameter.belongsToFunction,
                        overloadNum = StartingOverloadNum + i,
                        inBuilderFunctionInClass = builderFunctionInClass,
                        inConstructorOfClass = null,
                        documentation = parameter.documentation,
                        isEnumType = false
                    )
                }
                // Add the additional typed parameter
                type.add(
                    builder = builder,
                    typeData = typeData,
                    // If it's not a primitive type we need to keep the builderFunctionInClass for tracking purposes later on.
                    builderFunctionInClass = builderFunctionInClass,
                    nameAsParameter = nameAsParameter,
                    isOptional = isOptional,
                    functionAppearsIn = functionAppearsIn,
                    documentationAsParameter = documentationAsParameter,
                    overloadNum = StartingOverloadNum + i,
                    paramInConstructorOfClass = null,
                    //TODO: this might be unnecessary
                    showInConstructor = false
                )
            }

        }

        if (literals.isNotEmpty()) addEnumOfLiterals()
        if (types.isNotEmpty()) addOverloadsOfTypes()
        val x = 2

    }
}