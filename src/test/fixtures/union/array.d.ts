export function testPrimitive(param: string[] | Array<number>)

export function testHalfInterface(param: string[] | Interface1[])
//
// export function testInterface(param: Interface1[] | Interface2[])
//
// export function testTwoTypes(param: (string | boolean)[])
//
// export function testUnionOfTwoTypes(param: (string | boolean)[] | Interface1[])

interface Interface1 {
    prop: string
}
//
// interface Interface2 {
//     prop: boolean
// }