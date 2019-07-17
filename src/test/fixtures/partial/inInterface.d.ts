export function test(param : PartialInterface)

export interface Interface {
    prop1 : number,
    prop2 : Partial<PartialInterface>
}

export interface PartialInterface {
    prop : number
}