"use strict";
exports.__esModule = true;
var ts = require("typescript");
var fs = require("fs");
var inFile = "src/ts/data/test.d.ts";
var outFile = "src/ts/data/plotlyTypes.json";
// Build a program using the set of root file names in fileNames
var program = ts.createProgram([inFile], {
    target: ts.ScriptTarget.ES5,
    module: ts.ModuleKind.CommonJS
});
//TODO: support Partial<>
var sourceFiles = program.getSourceFiles();
var targetSourceFile = sourceFiles.filter(function (sourceFile) { return sourceFile.fileName === inFile; })[0];
var checker = program.getTypeChecker();
var declarations = targetSourceFile.getChildren()[0].getChildren();
var interfaces = declarations
    .filter(function (node) { return ts.isInterfaceDeclaration(node); })
    .map(function (node) {
    return serializeInterface(node);
});
var typeAliases = declarations
    .filter(function (node) { return ts.isTypeAliasDeclaration(node); })
    .map(function (node) {
    return serializeTypeAlias(node);
});
var constants = declarations
    .filter(function (node) { return ts.isVariableStatement(node); })
    .map(function (node) {
    return serializeConstant(node);
});
var functions = declarations
    .filter(function (node) { return ts.isFunctionDeclaration(node); })
    .map(function (node) {
    return serializeFunction(node);
});
var output = {
    constants: constants,
    functions: functions,
    interfaces: interfaces,
    typeAliases: typeAliases
};
// print out the doc
fs.writeFileSync(outFile, JSON.stringify(output, undefined, 4));
function getDocumentation(node) {
    var symbol = checker.getSymbolAtLocation(node.name);
    return ts.displayPartsToString(symbol.getDocumentationComment(checker));
}
function getInterfaceNodeProps(interfaceNode) {
    return interfaceNode.members.map(function (member) { return serializeSignature(member); });
}
function findInterfaceProps(name) {
    var interfaceNode = declarations.filter(function (node) { return ts.isInterfaceDeclaration(node) && node.name.text === name; });
    if (interfaceNode.length !== 1)
        throw new Error("Expected to find exactly one interface");
    return getInterfaceNodeProps(interfaceNode[0]);
}
/** Serialize a class symbol information */
function serializeInterface(interfaceNode) {
    var props = getInterfaceNodeProps(interfaceNode);
    if (interfaceNode.heritageClauses !== undefined && interfaceNode.heritageClauses.length > 0) {
        var superClass = interfaceNode.heritageClauses[0].types[0].getText();
        var superClassProps = findInterfaceProps(superClass);
        props = props.concat(superClassProps);
    }
    // let superClasses = interfaceNode.heritageClauses.map((clause) => clause.types[0]);
    return {
        name: interfaceNode.name.text,
        props: props,
        documentation: getDocumentation(interfaceNode)
    };
}
/** Serialize a symbol into a json object */
function serializeSignature(member) {
    if (ts.isMethodSignature(member)) {
        return serializeMethodSignature(member);
    }
    else if (ts.isPropertySignature(member)) {
        return serializePropertySignature(member);
    }
    else {
        throw Error("Signature is not method or property : " + member.name);
    }
}
function serializeMethodSignature(methodSignature) {
    methodSignature.name.parent = methodSignature;
    return {
        name: methodSignature.name.getText(),
        returnType: getReturnType(methodSignature),
        parameters: methodSignature.parameters.map(function (param) { return serializeParameter(param); }),
        documentation: getDocumentation(methodSignature),
        signatureType: "FunctionSignature"
    };
}
function serializePropertySignature(propertySignature) {
    return {
        name: propertySignature.name.getText(),
        type: serializeTypeOfNode(propertySignature),
        documentation: getDocumentation(propertySignature),
        signatureType: "PropertySignature",
        optional: isOptional(propertySignature)
    };
}
function isOptional(node) {
    return node.getChildren().find(function (child) { return child.kind == ts.SyntaxKind.QuestionToken; }) != undefined;
}
function getTypeNode(node) {
    return node.getChildren().filter(function (child) { return ts.isTypeNode(child); })[0];
}
function isKeyWordTypeNode(nodeTypeAsNode) {
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
        nodeTypeAsNode.kind == ts.SyntaxKind.NeverKeyword;
}
// function isPartialTypeNode(typeNode : ts.TypeNode)
function serializeTypeNode(typeNode) {
    if (ts.isUnionTypeNode(typeNode)) {
        return serializeUnionType(typeNode);
    }
    else if (ts.isLiteralTypeNode(typeNode)) {
        return serializeLiteralType(typeNode);
    }
    else if (ts.isTypeReferenceNode(typeNode)) {
        return serializeReferenceType(typeNode);
    }
    else if (ts.isFunctionTypeNode(typeNode)) {
        return serializeFunctionType(typeNode);
    }
    else if (ts.isTupleTypeNode(typeNode)) {
        return serializeTupleType(typeNode);
    }
    else if (isKeyWordTypeNode(typeNode)) {
        return serializeKeywordType(typeNode);
    }
    else if (ts.isArrayTypeNode(typeNode)) {
        return serializeArrayType(typeNode);
    }
    else if (ts.isTypeLiteralNode(typeNode)) {
        return serializeTypeLiteral(typeNode);
    }
    else if (ts.isIntersectionTypeNode(typeNode)) {
        return serializeIntersectionType(typeNode);
    }
    else if (ts.isParenthesizedTypeNode(typeNode)) {
        return serializeParenthesizedType(typeNode);
    }
    else {
        throw Error("node is of an unknown type: " + typeNode.getText() + " Type is : " + typeof typeNode);
    }
}
function serializeTypeOfNode(node) {
    // For some reason sometimes the type is a node and in those cases we use the type as a node
    var nodeTypeAsNode = getTypeNode(node);
    return serializeTypeNode(nodeTypeAsNode);
}
function serializeUnionType(typeNode) {
    return {
        types: typeNode.types.map(function (node) { return serializeTypeNode(node); }),
        kotPlotTypeType: "UnionType"
    };
}
function serializeLiteralType(typeNode) {
    return {
        literal: typeNode.literal.getText().replace(/'/g, "").replace(/"/g, ""),
        kotPlotTypeType: "LiteralType"
    };
}
function serializeReferenceType(typeNode) {
    // let typeNode = getTypeNode(node);
    // if(!ts.isTypeReferenceNode(typeNode)) return false;
    // for(let child of typeNode.getChildren()){
    //     // Partial types are a reference type that have a type as a child
    //     if(ts.isTypeNode(child)) return true;
    // }
    // return false;
    // let typeChildren = typeNode.getChildren().filter((child) => ts.isTypeNode(child));
    // typeNode.typeArguments
    if (typeNode.typeArguments === undefined || typeNode.typeArguments.length == 0) {
        return {
            typeName: typeNode.getText(),
            kotPlotTypeType: "ReferenceType"
        };
    }
    else {
        return {
            name: typeNode.typeName.escapedText.toString(),
            kotPlotTypeType: "ParameterizedType",
            typeArguments: typeNode.typeArguments.map(function (argument) { return serializeTypeNode(argument); })
        };
    }
}
function serializeTupleType(typeNode) {
    return {
        tupleTypes: typeNode.elementTypes.map(function (typeNode) { return serializeTypeNode(typeNode); }),
        kotPlotTypeType: "TupleType"
    };
}
function serializeFunctionType(typeNode) {
    return {
        // name: typeNode.name.getText(),
        parameters: typeNode.parameters.map(function (param) { return serializeParameter(param); }),
        returnType: getReturnType(typeNode),
        kotPlotTypeType: "FunctionType"
    };
}
function serializeKeywordType(typenode) {
    return {
        typeName: typenode.getText(),
        kotPlotTypeType: "ReferenceType"
    };
}
function serializeArrayType(typenode) {
    return {
        elementType: serializeTypeNode(typenode.elementType),
        kotPlotTypeType: "ArrayType"
    };
}
function serializeTypeLiteral(typenode) {
    return {
        nestedProperties: typenode.members.map(function (member) { return serializePropertySignature(member); } /* serializeTypeOfNode(member)*/),
        kotPlotTypeType: "TypeLiteral"
    };
}
function serializeIntersectionType(typenode) {
    return {
        types: typenode.types.map(function (type) {
            return serializeTypeNode(type);
        }),
        kotPlotTypeType: "IntersectionType"
    };
}
function serializeParenthesizedType(typeNode) {
    return serializeTypeNode(typeNode.type);
}
function serializeParameter(parameter) {
    return {
        name: parameter.name.getText(),
        type: serializeTypeOfNode(parameter),
        optional: isOptional(parameter)
    };
}
function getReturnType(typeNode) {
    var returnTypeNode = typeNode.getChildren().filter(function (child) { return ts.isTypeNode(child); })[0];
    if (returnTypeNode === undefined) {
        var returning = {
            kotPlotTypeType: "ReferenceType",
            typeName: ""
        };
        return returning;
    }
    return serializeTypeNode(returnTypeNode);
}
function serializeTypeAlias(typeAlias) {
    return {
        name: typeAlias.name.getText(),
        type: serializeTypeNode(typeAlias.type)
    };
}
function serializeConstant(variableStatement) {
    return {
        name: variableStatement.declarationList.declarations[0].name.getText(),
        type: serializeTypeNode(variableStatement.declarationList.declarations[0].type)
    };
}
function serializeFunction(functionDeclaration) {
    return {
        name: functionDeclaration.name.getText(),
        returnType: getReturnType(functionDeclaration),
        parameters: functionDeclaration.parameters.map(function (param) { return serializeParameter(param); }),
        documentation: getDocumentation(functionDeclaration),
        signatureType: "FunctionSignature"
    };
}
