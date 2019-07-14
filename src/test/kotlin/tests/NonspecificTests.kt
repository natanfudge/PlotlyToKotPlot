package tests

import org.junit.jupiter.api.Test
import p2kotplot.plotlytypes.*
import util.SignatureListBuilder
import kotlin.test.assertEquals

class NonspecificTests {
    @Test
    fun `sortedSoUnionsAreLast sorts correctly`(){
        val signatures = listOf(
            PropertySignature(
                name = "ShouldComeLater",
                type = UnionType(listOf(ReferenceType("thing1"),LiteralType("thing2"))),
                documentation = "",
                optional = false
            ),
            PropertySignature(
                name = "ShouldComeEarlier",
                type = ReferenceType("ShouldComeEarlierType"),
                documentation = "",
                optional = false
            )
        )
        val sorted = signatures.sortedSoUnionsAreLast()
        assertEquals("ShouldComeEarlier", sorted[0].name, "The reference type should come earlier")
    }
}