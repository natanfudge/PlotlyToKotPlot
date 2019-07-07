import * as ts from "typescript";
import {
    ArrayType,
    Constant,
    DeclarationFile,
    FunctionSignature, FunctionType,
    Interface, IntersectionType, KotPlotType, LiteralType, Parameter,
    PropertySignature, ReferenceType,
    Signature, TupleType,
    TypeAlias, TypeLiteral, UnionType
} from "./types";
import * as fs from "fs";

let inFile = "src/ts/data/test.d.ts";
let outFile = "src/ts/data/plotlyTypes.json";
// Build a program using the set of root file names in fileNames
let program = ts.createProgram([inFile], {
    target: ts.ScriptTarget.ES5,
    module: ts.ModuleKind.CommonJS
});


let sourceFiles = program.getSourceFiles();

let targetSourceFile = sourceFiles.filter((sourceFile) => sourceFile.fileName === inFile)[0];

let checker = program.getTypeChecker();

let declarations = targetSourceFile.getChildren()[0].getChildren();
let interfaces: Interface[] = declarations
    .filter((node) => ts.isInterfaceDeclaration(node))
    .map((node) => {
        return serializeInterface(node as ts.InterfaceDeclaration)
    });
let typeAliases: TypeAlias[] = declarations
    .filter((node) => ts.isTypeAliasDeclaration(node))
    .map((node) => {
        return serializeTypeAlias(node as ts.TypeAliasDeclaration)
    });

let constants : Constant[] = declarations
    .filter((node) => ts.isVariableStatement(node))
    .map((node) => {
        return serializeConstant(node as ts.VariableStatement)
    });

let functions : FunctionSignature[] = declarations
    .filter((node) => ts.isFunctionDeclaration(node))
    .map((node) => {
        return serializeFunction(node as ts.FunctionDeclaration)
    });

let output : DeclarationFile = {
    constants,
    functions,
    interfaces,
    typeAliases
};


// print out the doc
fs.writeFileSync(outFile, JSON.stringify(output, undefined, 4));


function getDocumentation(node: ts.InterfaceDeclaration | ts.MethodSignature | ts.PropertySignature | ts.FunctionDeclaration): string {
    let symbol = checker.getSymbolAtLocation(node.name);
    return ts.displayPartsToString(symbol.getDocumentationComment(checker))
}


/** Serialize a class symbol information */
function serializeInterface(interfaceNode: ts.InterfaceDeclaration): Interface {
    let props = interfaceNode.members.map((member) => {
            member.parent = interfaceNode;
            return serializeSignature(member);
        }
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
    methodSignature.name.parent = methodSignature;
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

function serializeTypeNode(typeNode: ts.TypeNode): KotPlotType {
    if (ts.isUnionTypeNode(typeNode)) {
        return serializeUnionType(typeNode)
    } else if (ts.isLiteralTypeNode(typeNode)) {
        return serializeLiteralType(typeNode)
    } else if (ts.isTypeReferenceNode(typeNode)) {
        return serializeReferenceType(typeNode)
    } else if (ts.isFunctionTypeNode(typeNode)) {
        return serializeFunctionType(typeNode)
    } else if (ts.isTupleTypeNode(typeNode)) {
        return serializeTupleType(typeNode)
    } else if (isKeyWordTypeNode(typeNode)) {
        return serializeKeywordType(typeNode)
    } else if (ts.isArrayTypeNode(typeNode)) {
        return serializeArrayType(typeNode)
    } else if (ts.isTypeLiteralNode(typeNode)) {
        return serializeTypeLiteral(typeNode)
    } else if (ts.isIntersectionTypeNode(typeNode)){
        return serializeIntersectionType(typeNode)
    } else if(ts.isParenthesizedTypeNode(typeNode)) {
        return serializeParenthesizedType(typeNode)
    }else{

            throw Error("node is of an unknown type: " + typeNode.getText() + " Type is : " + typeof typeNode)
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

function serializeIntersectionType(typenode: ts.IntersectionTypeNode): IntersectionType {
    return {
        types: typenode.types.map((type) => {
            return serializeTypeNode(type)
        }),
        kotPlotTypeType: "IntersectionType"
    }
}

function serializeParenthesizedType(typeNode : ts.ParenthesizedTypeNode) : KotPlotType{
    return serializeTypeNode(typeNode.type)
}

function serializeParameter(parameter: ts.ParameterDeclaration): Parameter {
    return {
        name: parameter.name.getText(),
        type: serializeTypeOfNode(parameter)
    }
}

function getReturnType(typeNode: ts.FunctionTypeNode | ts.MethodSignature | ts.FunctionDeclaration): KotPlotType {
    let returnTypeNode = typeNode.getChildren().filter((child) => ts.isTypeNode(child))[0] as ts.TypeNode;
    if(returnTypeNode === undefined){
        let returning : ReferenceType = {
            kotPlotTypeType : "ReferenceType",
            name : ""
        };
        return returning
    }
    return serializeTypeNode(returnTypeNode)
}


function serializeTypeAlias(typeAlias: ts.TypeAliasDeclaration): TypeAlias {
    return {
        name: typeAlias.name.getText(),
        type: serializeTypeNode(typeAlias.type)
    }
}

function serializeConstant(variableStatement: ts.VariableStatement): Constant {
    return {
        name: variableStatement.declarationList.declarations[0].name.getText(),
        type: serializeTypeNode(variableStatement.declarationList.declarations[0].type)
    }

}

function serializeFunction(functionDeclaration: ts.FunctionDeclaration): FunctionSignature {
    return {
        name: functionDeclaration.name.getText(),
        returnType: getReturnType(functionDeclaration),
        parameters: functionDeclaration.parameters.map((param) => serializeParameter(param)),
        documentation: getDocumentation(functionDeclaration),
        signatureType: "FunctionSignature"
    }

}
