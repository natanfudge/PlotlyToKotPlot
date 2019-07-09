package p2kotplot.ast

data class BuilderClass(val name: String)
data class BuilderFunction(val name: String, val inClass: String)
data class Parameter(
    val name: String,
    val primitiveType: String,
    val belongsToFunction: String,
    val paramInConstructorOfClass: String
)

data class FlatBuilderRepresentation(
    val builderClasses: MutableList<BuilderClass>,
    val builderFunctions: MutableList<BuilderFunction>,
    val parameters: MutableList<Parameter>
){
    fun addBuilderClass(name : String) = builderClasses.add(BuilderClass(name))
//    fun addBuilderFunction( name :String,  inClass : String) = builderFunctions.add(builderFunctions)
}