package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import p2kotplot.ast.toBuilderName


data class ReferenceType(val name: String) : KotPlotType {
    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        isOptional: Boolean,
        functionAppearsIn: String
    ) {
        fun emitValueType() {
            builder.addParameter(
                name = nameAsParameter,
                type = this.name.toTitleCase(),
                belongsToFunction = functionAppearsIn,
                paramInConstructorOfClass = builderClassIn,
                optional = isOptional
            )
        }

        fun emitReferenceType() {
            builder.addBuilderClass(name = name.toBuilderName())
            builder.addBuilderFunction(
                name = nameAsParameter,
                inClass = builderClassIn,
//                type = BuilderFunctionsType.Reference,
                builderNameOfConstructedType = name.toBuilderName(),
                isOptional = isOptional
            )
            for (prop in typeData.findTypeProps(name)) {
                prop.type.add(
                    builder = builder,
                    builderClassIn = name.toBuilderName(),
                    typeData = typeData,
                    functionAppearsIn = nameAsParameter,
                    nameAsParameter = prop.name,
                    isOptional = prop.optional
                )
            }
        }

        when (this.name) {
            "number", "string" -> emitValueType()
            else -> emitReferenceType()
        }


    }


}




