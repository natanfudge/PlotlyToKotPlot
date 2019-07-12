//-------- TOP LEVEL ----------------//
export interface DeclarationFile {
    typeAliases: TypeAlias[]
    constants: Constant[]
    interfaces: Interface[]
    functions: FunctionSignature[]
}

//------------------------------------//
//--------- TYPE ALIAS ---------------//
export interface TypeAlias {
    name: string
    type: KotPlotType
}

//-----------------------------------//
//---------- CONSTANT ---------------//
export interface Constant {
    name: string
    type: KotPlotType
}

//------------------------------------//
//------------INTERFACE -------------//
export interface Interface {
    name: string
    documentation: string
    props: Signature[]
}


//----------------------------------//
//-------- SIGNATURE ----------------//
export interface Signature {
    documentation: string
    signatureType: string
}

export interface PropertySignature extends Signature {
    name: string
    type: KotPlotType
    // isPartial : boolean

    optional: boolean
}

export interface FunctionSignature extends Signature {
    name: string
    returnType: KotPlotType
    parameters: Parameter[]
}


//----------------------------------//
//-------- TYPE --------------------//
export interface KotPlotType {
    kotPlotTypeType: string
}

export interface LiteralType extends KotPlotType {
    literal: string
}

export interface UnionType extends KotPlotType {
    types: KotPlotType[]
}

export interface ReferenceType extends KotPlotType {
    typeName: string
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
    nestedProperties: PropertySignature[]
}

export interface IntersectionType extends KotPlotType {
    types: KotPlotType[]
}

export interface ParameterizedType extends KotPlotType {
    name: string,
    typeArguments: KotPlotType[]
}

//--------------------------------//
//------- PARAMETER --------------//
export interface Parameter {
    name: string
    type: KotPlotType
    optional: boolean
}

