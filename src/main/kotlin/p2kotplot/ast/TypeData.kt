package p2kotplot.ast

import p2kotplot.plotlytypes.*
import sun.plugin.dom.exception.InvalidStateException

class TypeData(
    interfaceTypeData: List<Interface>,
    typeAliasData: List<TypeAlias>
) {
    private val typeData: List<Interface> = inlineTypeAliases(interfaceTypeData, typeAliasData)


    private fun inlineTypeAliases(interfaceTypeData: List<Interface>, typeAliasData: List<TypeAlias>): List<Interface> {
        return interfaceTypeData.map { interfaceType ->
            Interface(name = interfaceType.name, documentation = interfaceType.documentation, props =
            interfaceType.props.map { prop ->
                when (prop) {
                    is PropertySignature -> PropertySignature(
                        name = prop.name,
                        documentation = prop.documentation,
                        type = prop.type.inlineFrom(typeAliasData),
                        optional = prop.optional
                    )
                    is FunctionSignature -> FunctionSignature(
                        name = prop.name,
                        documentation = prop.documentation,
                        parameters = prop.parameters.map {
                            Parameter(
                                name = it.name,
                                type = it.type.inlineFrom(typeAliasData),
                                optional = it.optional
                            )
                        },
                        returnType = prop.returnType.inlineFrom(typeAliasData)
                    )
                }
            }
            )
        }
    }

    private fun KotPlotType.inlineFrom(typeAliasData: List<TypeAlias>) = when (this) {
        is ReferenceType -> findTypeAliasOfReferenceType(this, typeAliasData)
        else -> this
    }


    private fun findTypeAliasOfReferenceType(type: ReferenceType, typeAliasData: List<TypeAlias>): KotPlotType {
        val foundTypeAlias = typeAliasData.find { it.name == type.name } ?: return type
        return foundTypeAlias.type
    }


    fun findTypeProps(name: String): List<PropertySignature> {
        val interfaceType =
            typeData.find { it.name == name } ?: throw InvalidStateException("Could not find class with name $name")
        if (interfaceType.props.any { it !is PropertySignature }) throw InvalidStateException("Did not expect to be directed to an interfaces with methods.")
        return interfaceType.props.filterIsInstance<PropertySignature>()

    }
}