import { readFileSync, stat } from "fs";
import * as ts from "typescript";
import * as fs from "fs";

//TODO: add documentation of members

//-------- TOP LEVEL ----------------//

interface Interface {
  name: string
  documentation : string
  props: Signature[]
}
//----------------------------------//
//-------- SIGNATURE ----------------//
interface Signature {
  documentation : string
}

interface Property extends Signature {
  name: string
  type: KotPlotType
}

interface Method extends Signature {
  name: string
  returnType: KotPlotType
  parameters: Parameter[]
}



//----------------------------------//
//-------- TYPE --------------------//
interface KotPlotType {

}

interface LiteralType extends KotPlotType {
  literal: string
}

interface UnionType extends KotPlotType {
  types: KotPlotType[]
}

interface ReferenceType extends KotPlotType {
  name: string
}

interface FunctionType extends KotPlotType {
  parameters: Parameter[]
  returnType: KotPlotType
}

interface TupleType extends KotPlotType {
  tupleTypes: KotPlotType[]
}

interface ArrayType extends KotPlotType {
  elementType: KotPlotType
}

interface TypeLiteral extends KotPlotType {
  nestedTypes: KotPlotType[]
}

//--------------------------------//
//------- PARAMETER --------------//
interface Parameter {
  name: string
  type: KotPlotType
}
//---------------------------------//





/** Generate documentation for all classes in a set of .ts files */
function generateDocumentation(
  fileNames: string[],
  options: ts.CompilerOptions
): void {
  // Build a program using the set of root file names in fileNames
  let program = ts.createProgram(fileNames, options);


  let targetSourceFile = program.getSourceFiles().filter((sourceFile) => sourceFile.fileName == fileNames[0])[0];
  // let children = targetSourceFile.getChildren()
  let output: Interface[] = targetSourceFile.getChildren()[0].getChildren()
    .filter((node) => ts.isInterfaceDeclaration(node))
    .map((node) => {
      // let interfaceNode = 
      // let symbol = checker.getSymbolAtLocation(interfaceNode.name);
      return serializeInterface(node as ts.InterfaceDeclaration)
    });



  // print out the doc
  fs.writeFileSync("plotlyTypes.json", JSON.stringify(output, undefined, 4));

  return;




  /** Serialize a class symbol information */
  function serializeInterface(interfaceNode: ts.InterfaceDeclaration): Interface {
    // x : ts.isTypeElement
    // let x : ts.MethodSignature
    let props = interfaceNode.members.map((member) =>
      serializeSignature(member)
    );
    return {
      name: interfaceNode.name.text,
      props
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

  function serializeMethodSignature(methodSignature: ts.MethodSignature): Method {
    return {
      name: methodSignature.name.getText(),
      returnType: getReturnType(methodSignature),
      parameters: methodSignature.parameters.map((param) => serializeParameter(param))
    }
  }

  function serializePropertySignature(propertySignature: ts.PropertySignature): Property {

    return {
      name: propertySignature.name.getText(),
      type: serializeTypeOfNode(propertySignature)
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
    // let symbol = 
    // let type = checker.getTypeOfSymbolAtLocation(symbol, symbol.valueDeclaration!)
    return serializeTypeNode(nodeTypeAsNode)
  }

  function serializeUnionType(typeNode: ts.UnionTypeNode): UnionType {
    return {
      types: typeNode.types.map((node) => serializeTypeNode(node))
    }
  }

  function serializeLiteralType(typeNode: ts.LiteralTypeNode): LiteralType {
    return {
      literal: (typeNode.literal as ts.StringLiteral).getText()
    }
  }

  function serializeReferenceType(typeNode: ts.TypeReferenceNode): ReferenceType {
    return {
      name: typeNode.getText()
    }
  }



  function serializeTupleType(typeNode: ts.TupleTypeNode): TupleType {
    return {
      tupleTypes: typeNode.elementTypes.map((typeNode) => serializeTypeNode(typeNode))
    }
  }



  function serializeFunctionType(typeNode: ts.FunctionTypeNode): FunctionType {
    return {
      // name: typeNode.name.getText(),
      parameters: typeNode.parameters.map((param) => serializeParameter(param)),
      returnType: getReturnType(typeNode)
    }
  }

  function serializeKeywordType(typenode: ts.KeywordTypeNode): ReferenceType {
    return {
      name: typenode.getText()
    }
  }

  function serializeArrayType(typenode: ts.ArrayTypeNode): ArrayType {
    return {
      elementType: serializeTypeNode(typenode.elementType)
    }
  }

  function serializeTypeLiteral(typenode: ts.TypeLiteralNode): TypeLiteral {
    return {
      nestedTypes: typenode.members.map((member) => serializeTypeOfNode(member))
    }
  }

  function serializeParameter(parameter: ts.ParameterDeclaration): Parameter {
    return {
      name: parameter.name.getText(),
      type: serializeTypeOfNode(parameter)
    }
  }

  function getReturnType(typeNode: ts.FunctionTypeNode | ts.MethodSignature): KotPlotType {
    let returnTypeNode = typeNode.getChildren().filter((child) => ts.isTypeNode(child))[0] as ts.TypeNode;
    return serializeTypeNode(returnTypeNode)
  }

  // function serializeType(typeNode: ts.TypeElement): Type {
  //   if(ts.isTypeReferenceNode(typeNode) ){

  //   }
  //   let symbol = checker.getSymbolAtLocation(typeNode.name)
  //   let type = checker.getTypeOfSymbolAtLocation(symbol, symbol.valueDeclaration!)
  //   if (type.isClassOrInterface()) {
  //     let serializedClass = checker.typeToString(type);
  //     return serializedClass;
  //   } else if (type.isUnionOrIntersection()) {
  //     return {};

  //   } else {
  //     let signature = typeNode as ts.MethodSignature
  //     let returnType = checker.getSignatureFromDeclaration(signature)
  //     // let signature = 
  //     // let returnType = checker.getReturnTypeOfSignature(signature)
  //     // let symbol = checker.getSymbolAtLocation(typeNode.name)
  //     // let returnType = checker.getReturnTypeOfSignature(checker.getSignaturesOfType(checker.getTypeOfSymbolAtLocation(checker.getSymbolAtLocation(typeNode)) ,null))
  //     // It's a function
  //     let functionSymbol = type.symbol;
  //     let functionName = functionSymbol.name;
  //     // let returnType = functionSymbol.
  //     let x = checker.getReturnTypeOfSignature()

  //     return {}
  //   }

  // }

}

generateDocumentation(["plotly.d.ts"], {
  target: ts.ScriptTarget.ES5,
  module: ts.ModuleKind.CommonJS
});