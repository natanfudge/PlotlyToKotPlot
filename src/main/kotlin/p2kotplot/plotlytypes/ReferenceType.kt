package p2kotplot.plotlytypes

import p2kotplot.ast.FlatBuilderRepresentation
import p2kotplot.ast.TypeData
import p2kotplot.ast.toBuilderName

fun ReferenceType.isPrimitive() = when (typeName.toLowerCase()) {
    "number", "string", "any", "boolean" -> true
    else -> false
}

fun KotPlotType.isPrimitiveType() = this is ReferenceType && this.isPrimitive()

data class ReferenceType(val typeName: String) : KotPlotType {

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
        showInConstructor: Boolean,
        isForArray: Boolean
    ) {
        fun emitPrimitiveType() {
            builder.addParameter(
                name = nameAsParameter,
                type = this.typeName.toTitleCase(),
                belongsToFunction = functionAppearsIn,
                inConstructorOfClass =  paramInConstructorOfClass,
                inBuilderFunctionInClass = builderFunctionInClass,
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
                inClass = builderFunctionInClass,
                builderNameOfConstructedType = if(isPrimitive()) null else typeName.toBuilderName(),
                isOptional = isOptional,
                isForArray = isForArray
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




