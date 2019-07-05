import * as ts from "typescript";
import {
    Interface,
    Signature,
    PropertySignature,
    FunctionSignature,
    KotPlotType,
    UnionType,
    LiteralType,
    ReferenceType,
    TupleType,
    FunctionType,
    ArrayType,
    TypeLiteral,
    Parameter,
    TypeAlias, Constant
} from "./types";
import {getProgram} from "./root";

//TODO: add documentation of members

/** Generate documentation for all classes in a set of .ts files */
let checker = getProgram().getTypeChecker();


function getDocumentation(node: ts.InterfaceDeclaration | ts.MethodSignature | ts.PropertySignature | ts.FunctionDeclaration): string {
    let symbol = checker.getSymbolAtLocation(node.name);
    return ts.displayPartsToString(symbol.getDocumentationComment(checker))
}


/** Serialize a class symbol information */
export function serializeInterface(interfaceNode: ts.InterfaceDeclaration): Interface {
    let props = interfaceNode.members.map((member) =>
        serializeSignature(member)
    );

    return {
        name: interfaceNode.name.text,
        props,
        documentation: getDocumentation(interfaceNode)
    };
}


/** Serialize a symbol into a json object */
function serializeSignature(member: ts.TypeElement): Signature {

    if (ts.isMethodSignature(member)) {
        return serializeMethodSignature(member)
    } else if (ts.isPropertySignature(member)) {
        return serializePropertySignature(member)
    } else {
        throw Error("Signature is not method or property : " + member.name)
    }

}

function serializeMethodSignature(methodSignature: ts.MethodSignature): FunctionSignature {
    return {
        name: methodSignature.name.getText(),
        returnType: getReturnType(methodSignature),
        parameters: methodSignature.parameters.map((param) => serializeParameter(param)),
        documentation: getDocumentation(methodSignature),
        signatureType: "FunctionSignature"
    }
}

function serializePropertySignature(propertySignature: ts.PropertySignature): PropertySignature {

    return {
        name: propertySignature.name.getText(),
        type: serializeTypeOfNode(propertySignature),
        documentation: getDocumentation(propertySignature),
        signatureType: "PropertySignature"
    }

}


function getTypeNode(node: ts.Node): ts.TypeNode {

    return node.getChildren().filter((child) => ts.isTypeNode(child))[0] as ts.TypeNode
}

function isKeyWordTypeNode(nodeTypeAsNode: ts.TypeNode): nodeTypeAsNode is ts.KeywordTypeNode {
    return nodeTypeAsNode.kind == ts.SyntaxKind.AnyKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.UnknownKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.NumberKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.BigIntKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.ObjectKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.BooleanKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.StringKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.SymbolKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.ThisKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.VoidKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.UndefinedKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.NullKeyword ||
        nodeTypeAsNode.kind == ts.SyntaxKind.NeverKeyword
}

function serializeTypeNode(nodeTypeAsNode: ts.TypeNode): KotPlotType {
    if (ts.isUnionTypeNode(nodeTypeAsNode)) {
        return serializeUnionType(nodeTypeAsNode)
    } else if (ts.isLiteralTypeNode(nodeTypeAsNode)) {
        return serializeLiteralType(nodeTypeAsNode)
    } else if (ts.isTypeReferenceNode(nodeTypeAsNode)) {
        return serializeReferenceType(nodeTypeAsNode)
    } else if (ts.isFunctionTypeNode(nodeTypeAsNode)) {
        return serializeFunctionType(nodeTypeAsNode)
    } else if (ts.isTupleTypeNode(nodeTypeAsNode)) {
        return serializeTupleType(nodeTypeAsNode)
    } else if (isKeyWordTypeNode(nodeTypeAsNode)) {
        return serializeKeywordType(nodeTypeAsNode)
    } else if (ts.isArrayTypeNode(nodeTypeAsNode)) {
        return serializeArrayType(nodeTypeAsNode)
    } else if (ts.isTypeLiteralNode(nodeTypeAsNode)) {
        return serializeTypeLiteral(nodeTypeAsNode)
    } else {

        throw Error("node is of an unknown type: " + nodeTypeAsNode.getText() + " Type is : " + typeof nodeTypeAsNode)
    }
}

function serializeTypeOfNode(node: ts.Node): KotPlotType {
    // For some reason sometimes the type is a node and in those cases we use the type as a node
    let nodeTypeAsNode = getTypeNode(node);
    return serializeTypeNode(nodeTypeAsNode)
}

function serializeUnionType(typeNode: ts.UnionTypeNode): UnionType {
    return {
        types: typeNode.types.map((node) => serializeTypeNode(node)),
        kotPlotTypeType: "UnionType"
    }
}

function serializeLiteralType(typeNode: ts.LiteralTypeNode): LiteralType {
    return {
        literal: (typeNode.literal as ts.StringLiteral).getText().replace(/'/g, "").replace(/"/g, ""),
        kotPlotTypeType: "LiteralType"
    }
}

function serializeReferenceType(typeNode: ts.TypeReferenceNode): ReferenceType {
    return {
        name: typeNode.getText(),
        kotPlotTypeType: "ReferenceType"
    }
}


function serializeTupleType(typeNode: ts.TupleTypeNode): TupleType {
    return {
        tupleTypes: typeNode.elementTypes.map((typeNode) => serializeTypeNode(typeNode)),
        kotPlotTypeType: "TupleType"
    }
}


function serializeFunctionType(typeNode: ts.FunctionTypeNode): FunctionType {
    return {
        // name: typeNode.name.getText(),
        parameters: typeNode.parameters.map((param) => serializeParameter(param)),
        returnType: getReturnType(typeNode),
        kotPlotTypeType: "FunctionType"
    }
}

function serializeKeywordType(typenode: ts.KeywordTypeNode): ReferenceType {
    return {
        name: typenode.getText(),
        kotPlotTypeType: "ReferenceType"
    }
}

function serializeArrayType(typenode: ts.ArrayTypeNode): ArrayType {
    return {
        elementType: serializeTypeNode(typenode.elementType),
        kotPlotTypeType: "ArrayType"
    }
}

function serializeTypeLiteral(typenode: ts.TypeLiteralNode): TypeLiteral {
    return {
        nestedProperties: typenode.members.map((member) => serializePropertySignature(member as ts.PropertySignature)/* serializeTypeOfNode(member)*/),
        kotPlotTypeType: "TypeLiteral"
    }
}

function serializeParameter(parameter: ts.ParameterDeclaration): Parameter {
    return {
        name: parameter.name.getText(),
        type: serializeTypeOfNode(parameter)
    }
}

function getReturnType(typeNode: ts.FunctionTypeNode | ts.MethodSignature | ts.FunctionDeclaration): KotPlotType {
    let returnTypeNode = typeNode.getChildren().filter((child) => ts.isTypeNode(child))[0] as ts.TypeNode;
    return serializeTypeNode(returnTypeNode)
}


export function serializeTypeAlias(typeAlias: ts.TypeAliasDeclaration): TypeAlias {
    return {
        name : typeAlias.name.getText(),
        type : serializeTypeNode(typeAlias.type)
    }
}

export function serializeConstant(variableStatement: ts.VariableStatement): Constant {
    return{
        name: variableStatement.declarationList.declarations[0].name.getText(),
        type: serializeTypeNode(variableStatement.declarationList.declarations[0].type)
    }

}

export function serializeFunction(functionDeclaration: ts.FunctionDeclaration): FunctionSignature {
    return {
        name: functionDeclaration.name.getText(),
        returnType: getReturnType(functionDeclaration),
        parameters: functionDeclaration.parameters.map((param) => serializeParameter(param)),
        documentation: getDocumentation(functionDeclaration),
        signatureType: "FunctionSignature"
    }
}