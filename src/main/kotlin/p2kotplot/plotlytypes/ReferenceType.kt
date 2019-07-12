package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import p2kotplot.ast.toBuilderName


data class ReferenceType(val typeName: String) : KotPlotType {

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
        fun emitValueType() {
            builder.addParameter(
                name = nameAsParameter,
                //TODO: this is completely wrong...
                type = this.typeName.toTitleCase(),
                belongsToFunction = functionAppearsIn,
                paramInConstructorOfClass = builderClassIn,
                optional = isOptional,
                documentation = documentationAsParameter
            )
        }



        fun emitReferenceType() {
            var builderClassName = typeName.toBuilderName()
            if(isPartial) builderClassName = "Partial$builderClassName"
            builder.addBuilderClass(name = builderClassName)
            builder.addBuilderFunction(
                name = nameAsParameter,
                inClass = builderClassIn,
                builderNameOfConstructedType = typeName.toBuilderName(),
                isOptional = isOptional
            )
            for (prop in typeData.findTypeProps(typeName)) {
                prop.type.add(
                    builder = builder,
                    typeData = typeData,
                    builderClassIn = typeName.toBuilderName(),
                    nameAsParameter = prop.name,
                    isOptional = isPartial || prop.optional,
                    functionAppearsIn = nameAsParameter,
                    documentationAsParameter = prop.documentation
                )
            }
        }

        when (this.typeName.toLowerCase()) {
            "number", "string", "any", "boolean" -> emitValueType()
            else -> emitReferenceType()
        }


    }


}




