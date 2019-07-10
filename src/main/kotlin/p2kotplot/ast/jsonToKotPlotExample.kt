package p2kotplot.ast

/*
  export function doMagic(amount: number, powder: Powder, mage : Mage, watcher : Mage)

interface Powder {
	amount : number
	name : string
	madeBy : Mage
}

interface Mage {
	level : number
	name : string
}

//type Mage = string | number

/**
    val arrayThing ...
    fun addOneOfMage(
 */
 */







// =>
/*
	Layer 0 (layers are not represented at memory):
	builderClasses = []
	builderFunctions = [ (doMagic, inClass = null) ]
	params = [{ (amount, number), builderFunction = doMagic, paramInConstructorOfClass = null }]
	Layer 1:
	builderClasses = [DoMagicBuilder]
	builderFunctions = [ (powder, inClass = DoMagicBuilder), (watcher, inClass = DoMagicBuilder)
	params = [ { (amount, number), builderFunction = powder, paramInConstructorOfClass = PowderBuilder },
	{ (name, string), builderFunction = powder, paramInConstructorOfClass) = PowderBuilder },
	{ (level, number), builderFunction = watcher, paramInConstructorOfClass = MageBuilder) },
	{ (name, string), builderFunction = watcher), paramInConstructorOfClass = MageBuilder } ]
	Layer 2:
	builderClasses = [MageBuilder, PowderBuilder]
	builderFunctions = [(madeBy, inClass = PowderBuilder)]
	params = []

 */

//
//fun doMagic(){
//    // val jsonObject = ...
//}
//
//class DoMagicBuilder(){
//    // val jsonMap =  ...
//
////    fun powder()
//
//    // fun build() { ... }
//}
//
//class MageBuilder(){
//    // val jsonMap =  ...
//
//
//    // fun build() { ... }
//}
//
//
//class PowderBuilder(){
//    // val jsonMap =  ...
//
//
//    // fun build() { ... }
//}