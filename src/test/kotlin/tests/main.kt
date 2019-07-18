package tests

import org.junit.jupiter.api.Test
import p2kotplot.KotlinWriter
import p2kotplot.plotlytypes.arrParamName
import util.fixture

fun main() {
    ArrayTypeTest().apply {
        interfaceArray()
        primitive()
    }

    ReferenceTypeTest().apply {
        normal()
        primitive()
    }

    UnionTypeTest().apply {
        array()
        simpleEnum()
        simpleTypes()
        twoInterfaces()
    }
}
