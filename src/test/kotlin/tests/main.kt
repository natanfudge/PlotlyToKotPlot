package tests

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
