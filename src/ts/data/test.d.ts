import {Font, Padding} from "./plotly";

export function doMagic(amount: number, powder: MagicPowder[], mage : Mage)

// type Foo = {
//     obj1key1: string
//     obj1key2: number
// } & {
//     obj2key1: string
//     obj2key2: number
// };

/**
 * class powderBuilder
 */

export interface MagicPowder {
    powderName: string
    powderWeight: number
    // maker : {
    //     testKey1 : number
    //     testKey2 : string
    // }
}

export interface Mage {
    age : number
    name : string
}

//
// export interface Person {
//     name : string
//     age : number
// }

// export interface SpecialSkill{
//     age : number,
//     name: string
// }