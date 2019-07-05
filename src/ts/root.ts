import * as ts from "typescript";
import {Constant, DeclarationFile, FunctionSignature, Interface, TypeAlias} from "./types";
import * as fs from "fs";
import {serializeConstant, serializeFunction, serializeInterface, serializeTypeAlias} from "./serialization";

let file = "src/ts/data/plotly.d.ts";
// Build a program using the set of root file names in fileNames
let program = ts.createProgram([file], {
    target: ts.ScriptTarget.ES5,
    module: ts.ModuleKind.CommonJS
});


export function getProgram(){
    return program
}


let sourceFiles = program.getSourceFiles();

let targetSourceFile = sourceFiles.filter((sourceFile) => sourceFile.fileName === file)[0];

let declarations = targetSourceFile.getChildren()[0].getChildren();


let interfaces : Interface[] = declarations
    .filter((node) => ts.isInterfaceDeclaration(node))
    .map((node) => {
        return serializeInterface(node as ts.InterfaceDeclaration)
    });


let typeAliases : TypeAlias[] = declarations
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
fs.writeFileSync("src/ts/data/plotlyTypes.json", JSON.stringify(output, undefined, 4));