package p2kotplot.plotlytypes

import p2kotplot.ast.BuilderFunctionsType
import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import p2kotplot.ast.toBuilderName


data class ReferenceType(val name: String) : KotPlotType {
    override fun add(
        builder: FlatBuilderRepresentation,
        typeData: TypeData,
        builderClassIn: String?,
        nameAsParameter: String,
        functionAppearsIn: String
    ) {
        fun emitValueType() {
            builder.addParameter(
                name = nameAsParameter,
                type = this.name,
                belongsToFunction = functionAppearsIn,
                paramInConstructorOfClass = builderClassIn
            )
        }

        fun emitReferenceType() {
            builder.addBuilderClass(name = name.toBuilderName())
            builder.addBuilderFunction(
                name = nameAsParameter,
                inClass = builderClassIn,
//                type = BuilderFunctionsType.Reference,
                builderNameOfConstructedType = name.toBuilderName()
            )
            for (prop in typeData.findTypeProps(name)) {
                prop.type.add(
                    builder = builder,
                    builderClassIn = name.toBuilderName(),
                    typeData = typeData,
                    functionAppearsIn = nameAsParameter,
                    nameAsParameter = prop.name
                )
            }
        }

        when (this.name) {
            "number", "string" -> emitValueType()
            else -> emitReferenceType()
        }


    }


}




