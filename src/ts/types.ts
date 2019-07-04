//-------- TOP LEVEL ----------------//

export interface Interface {
  name: string
  documentation : string
  props: Signature[]
}
//----------------------------------//
//-------- SIGNATURE ----------------//
export interface Signature {
  documentation : string
  signatureType : string
}

export interface Property extends Signature {
  name: string
  type: KotPlotType
}

export interface Method extends Signature {
  name: string
  returnType: KotPlotType
  parameters: Parameter[]
}



//----------------------------------//
//-------- TYPE --------------------//
export interface KotPlotType {
  kotPlotTypeType : string
}

export interface LiteralType extends KotPlotType {
  literal: string
}

export interface UnionType extends KotPlotType {
  types: KotPlotType[]
}

export interface ReferenceType extends KotPlotType {
  name: string
}

export interface FunctionType extends KotPlotType {
  parameters: Parameter[]
  returnType: KotPlotType
}

export interface TupleType extends KotPlotType {
  tupleTypes: KotPlotType[]
}

export interface ArrayType extends KotPlotType {
  elementType: KotPlotType
}

export interface TypeLiteral extends KotPlotType {
  nestedTypes: KotPlotType[]
}

//--------------------------------//
//------- PARAMETER --------------//
export interface Parameter {
  name: string
  type: KotPlotType
}
//---------------------------------//
