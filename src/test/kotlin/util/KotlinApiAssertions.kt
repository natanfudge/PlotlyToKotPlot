package util

import p2kotplot.BuilderClassComponents
import p2kotplot.BuilderFunctionComponents
import p2kotplot.KotlinApi
import p2kotplot.ParameterComponents
import p2kotplot.ast.Enum
import p2kotplot.plotlytypes.DeclarationFile
import p2kotplot.plotlytypes.FunctionSignature
import p2kotplot.plotlytypes.Interface
import p2kotplot.plotlytypes.Parameter

private inline fun <T> assertEquals(expected: T, actual: T, lazyMessage: () -> String) {
    if (expected != actual) error(lazyMessage() + "\nExpected: <$expected>, Actual: <$actual> \n\n\n\n\n\n\n\n\n\n\n")
}

private fun <T> List<T>.difference(other: List<T>, iterableName: String): String {
    val missing = this.filter { it !in other }
    val extraneous = other.filter { it !in this }
    val missingError = if (missing.isNotEmpty()) "These $iterableName are missing: $missing\n" else ""
    val extraneousError =
        if (extraneous.isNotEmpty()) "These $iterableName are not supposed to exist: $extraneous\n" else ""
    return missingError + extraneousError
}

private fun <T> List<T>.assertEqualsTo(other: List<T>, iterableName: String, elementAsserter: T.(T, Int) -> Unit) {
    assertEquals(this.size, other.size) {
        "$iterableName amount is not equal. (Expected: <${this.size}>, Actual: <${other.size}>) \n${this.difference(other, iterableName)}"
    }
    this.forEachIndexed { i, function ->
        function.elementAsserter(other[i], i)
    }
}

private fun <T> List<T>.assertEqualsToPrimitiveList(other: List<T>, iterableName: String) {
    val difference = this.difference(other, iterableName)

    assertEquals(difference, "") { difference }
}

private fun <T> T.assertEqualsToPrimitive(other: T, objectsName: String, position: Int) {
    assertEquals(this, other) { "${objectsName}s at position $position are not equal. Expected: <$this>, Actual: <$other>" }
}

infix fun KotlinApi.assertEqualsTo(other: KotlinApi) {
    this.enums.assertEqualsTo(other.enums, "Enum", Enum::assertEqualsTo)
    this.topLevelFunctions.assertEqualsTo(
        other.topLevelFunctions,
        "Top Level Function",
        BuilderFunctionComponents::assertEqualsTo
    )
    this.builderClasses.assertEqualsTo(other.builderClasses, "Builder Class", BuilderClassComponents::assertEqualsTo)
}

fun BuilderClassComponents.assertEqualsTo(other: BuilderClassComponents, position: Int) {
    this.arrayFieldNames.assertEqualsToPrimitiveList(other.arrayFieldNames, "Array Field")
    this.builderFunctions.assertEqualsTo(
        other.builderFunctions,
        "Builder Function",
        BuilderFunctionComponents::assertEqualsTo
    )
    this.applyStatements.assertEqualsToPrimitiveList(other.applyStatements, "Apply Statement")
    this.constructorArguments.assertEqualsTo(
        other.constructorArguments,
        "Constructor Argument",
        ParameterComponents::assertEqualsTo
    )
    this.name.assertEqualsToPrimitive(other.name, "Builder Class Name", position)
}

fun BuilderFunctionComponents.assertEqualsTo(other: BuilderFunctionComponents, position: Int) {
//    this.documentation.assertEqualsToPrimitive(other.documentation, "Builder Function Documentation", position)
    this.parameters.assertEqualsTo(other.parameters, "Builder Function Parameters", ParameterComponents::assertEqualsTo)
    this.body.assertEqualsToPrimitive(other.body, "Builder Function Body", position)
    this.builderNameOfConstructedType.assertEqualsToPrimitive(
        other.builderNameOfConstructedType,
        "Constructed Type Of Builder Function",
        position
    )
    this.hasInitParam.assertEqualsToPrimitive(
        other.hasInitParam,
        "Whether A Builder Function Has An Init Param",
        position
    )
    this.name.assertEqualsToPrimitive(other.name, "Builder Function Name", position)
}

fun Enum.assertEqualsTo(other: Enum, position: Int) {
    this.elements.assertEqualsToPrimitiveList(other.elements, "Enum Elements")
    this.name.assertEqualsToPrimitive(other.name, "Enum Name", position)
}

fun ParameterComponents.assertEqualsTo(other: ParameterComponents, position: Int) {
    this.name.assertEqualsToPrimitive(other.name, "Parameter Name", position)
    this.isOptional.assertEqualsToPrimitive(other.isOptional, "Whether Or Not A Parameter Is Optional", position)
    this.type.assertEqualsToPrimitive(other.type, "Parameter Type", position)
    this.documentation.assertEqualsToPrimitive(other.documentation, "Parameter Documentation", position)
}


infix fun DeclarationFile.assertEqualsTo(other: DeclarationFile) {
    assertEquals(this.constants, other.constants) { "Constants are not equal" }
    assertEquals(this.typeAliases, other.typeAliases) { "Constants are not equal" }

    this.functions.assertEqualsTo(other.functions, "Function", FunctionSignature::assertEqualsTo)
    this.interfaces.assertEqualsTo(other.interfaces, "Interface", Interface::assertEqualsTo)

}

fun Interface.assertEqualsTo(other: Interface, position: Int) {
    assertEquals(this.documentation, other.documentation) {
        "Documentation of interfaces at position $position are not equal"
    }

    assertEquals(this.name, other.name) { "Name of interfaces at position $position are not equal" }
    assertEquals(this.props, other.props) { "Props of interfaces at position $position are not equal" }
}

fun FunctionSignature.assertEqualsTo(other: FunctionSignature, position: Int) {

    assertEquals(this.documentation, other.documentation) {
        "functions of interfaces at position $position are not equal"
    }
    assertEquals(this.name, other.name) { "Names of functions at position $position are not equal" }
    assertEquals(this.returnType, other.returnType) { "Return Types of functions at position $position are not equal" }
    assertEquals(this.parameters.size, other.parameters.size) {
        "Parameter amounts of functions at position $position is not equal."
    }


    this.parameters.forEachIndexed { i, parameter ->
        parameter.assertEqualsTo(other.parameters[i], position, i)
    }

}

fun Parameter.assertEqualsTo(other: Parameter, functionPosition: Int, parameterPosition: Int) {
    val positionData = "[Parameter at position $parameterPosition of function at position $functionPosition]"
    assertEquals(this.optional, other.optional) { "One parameter is optional while the other is not. $positionData" }


    assertEquals(this.name, other.name) { "Name of parameters are not equal. $positionData" }


    assertEquals(this.type, other.type) {
        "Types of parameters are not equal. $positionData"
    }

}
