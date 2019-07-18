package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import p2kotplot.ast.toBuilderName

fun ReferenceType.isPrimitive() = when (typeName.toLowerCase()) {
    "number", "string", "any", "boolean" -> true
    else -> false
}

data class ReferenceType(val typeName: String) : KotPlotType {

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
        fun emitPrimitiveType() {
            builder.addParameter(
                name = nameAsParameter,
                type = this.typeName.toTitleCase(),
                belongsToFunction = functionAppearsIn,
                paramInConstructorOfClass = builderClassIn,
                optional = isOptional,
                documentation = documentationAsParameter,
                overloadNum = overloadNum,
                isEnumType = false
            )
        }


        fun emitReferenceType() {
            var builderClassName = typeName.toBuilderName()
            if (isPartial) builderClassName = "Partial$builderClassName"
            builder.addBuilderClass(name = builderClassName)
            builder.addBuilderFunction(
                name = nameAsParameter,
                inClass = builderClassIn,
                builderNameOfConstructedType = typeName.toBuilderName(),
                isOptional = isOptional
            )
            // Add all the types of the properties of the interface that has this type's name
            typeData.findInterfaceProperties(typeName).addTypes(
                builder,
                typeData,
                builderClassIn = typeName.toBuilderName(),
                isPartial = isPartial,
                functionAppearsIn = nameAsParameter,
                overloadNum = overloadNum

            )

        }
        if(this.isPrimitive()) emitPrimitiveType()
        else emitReferenceType()

//        when (this.typeName.toLowerCase()) {
//            "number", "string", "any", "boolean" -> emitValueType()
//            else -> emitReferenceType()
//        }


    }


}




