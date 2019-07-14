package util

import p2kotplot.plotlytypes.DeclarationFile
import p2kotplot.plotlytypes.FunctionSignature
import p2kotplot.plotlytypes.Interface
import p2kotplot.plotlytypes.Parameter
import kotlin.test.assertEquals


infix fun DeclarationFile.isEqualTo(other: DeclarationFile) {
    assertEquals(this.constants, other.constants, "Constants are not equal")
    assertEquals(this.typeAliases, other.typeAliases, "Constants are not equal")
    assertEquals(this.functions.size, other.functions.size, "Function amount is not equal")
    assertEquals(this.interfaces.size, other.interfaces.size, "Interface amount is not equal")

    this.functions.forEachIndexed { i, function ->
        function.assertEqualTo(other.functions[i], i)
    }

    this.interfaces.forEachIndexed { i, theInterface ->
        theInterface.assertEqualTo(other.interfaces[i], i)
    }
}

fun Interface.assertEqualTo(other: Interface, position: Int) {
    assertEquals(
        this.documentation,
        other.documentation,
        "Documentation of interfaces at position $position are not equal"
    )
    assertEquals(this.name, other.name, "Name of interfaces at position $position are not equal")
    assertEquals(this.props, other.props, "Props of interfaces at position $position are not equal")
}

fun FunctionSignature.assertEqualTo(other: FunctionSignature, position: Int) {

    assertEquals(this.documentation, other.documentation, "functions of interfaces at position $position are not equal")
    assertEquals(this.name, other.name, "Names of functions at position $position are not equal")
    assertEquals(this.returnType, other.returnType, "Return Types of functions at position $position are not equal")
    assertEquals(
        this.parameters.size,
        other.parameters.size,
        "Parameter amounts of functions at position $position is not equal."
    )

    this.parameters.forEachIndexed { i, parameter ->
        parameter.assertEqualTo(other.parameters[i], position, i)
    }

}

fun Parameter.assertEqualTo(other: Parameter, functionPosition: Int, parameterPosition: Int) {
    val positionData =  "[Parameter at position $parameterPosition of function at position $functionPosition]"
    assertEquals(
        this.optional,
        other.optional,
        "One parameter is optional while the other is not. $positionData"
    )
    assertEquals(
        this.name,
        other.name,
        "Name of parameters are not equal. $positionData"
    )
    assertEquals(
        this.type,
        other.type,
        "Types of parameters are not equal. $positionData"
    )
}

