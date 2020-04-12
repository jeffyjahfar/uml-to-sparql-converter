package com.uw.css.xmi_parser;

import com.uw.css.utils.Relationships;
import org.codeontology.Ontology;
import java.util.List;


public class QueryConstructionService {

    String setPrefix(){
        return "PREFIX woc: <"+Ontology.WOC + ">\n\n";
    }

    String constructSelectStatement(List<Component> componentList, Boolean distinctResult, String query){
        query = query + "SELECT ";

        if(distinctResult){
            query+="DISTINCT ";
        }
        for(Component component: componentList){
            if(component.isSelectItem){
                query = query.concat("?").concat(component.name).concat(" ");
            }
        }
        return query;
    }

    public String constructWhereStatement(String query, List<RelationshipItem> relationshipItems, List<Component> components){
        query = query.concat("\n WHERE {\n ");

        //iteration on components
        for(Component c : components){
            query = query.concat("?").concat(c.name).concat(" ").concat(" a ");
            query = query.concat(resolveWOCType(c.getType())).concat(".\n");
        }

        //iteration on relations
        for(RelationshipItem relationshipItem: relationshipItems){
            Component fromItem = relationshipItem.getFromItem();
            Component toItem = relationshipItem.getToItem();
            query = query.concat("?").concat(fromItem.name).concat(" ");
            query = query.concat(resolveRelationshipWOCType(relationshipItem.getName())).concat(" ");
            query = query.concat("?").concat(toItem.name).concat(".\n");
        }
        //close where clause
        query = query.concat("\n}");
        return query;
    }

    private String resolveRelationshipWOCType(String typename) {
        Relationships r = Relationships.valueOf(typename);
        switch (r){
            case generalization:
                return Ontology.EXTENDS_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case dependency:
                return Ontology.DEPENDENCY_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case association:
                return Ontology.REFERENCES_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case operation:
                return Ontology.HAS_METHOD_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case property:
                return Ontology.HAS_FIELD_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case interfacerealization:
                return Ontology.IMPLEMENTS_PROPERTY.toString().replace(Ontology.WOC,"woc:");
        }
        return "";
    }

    public String resolveWOCType(String typename){
        switch (typename){
            case "class":
                return Ontology.CLASS_ENTITY.toString().replace(Ontology.WOC,"woc:");
            case "operation":
                return Ontology.METHOD_ENTITY.toString().replace(Ontology.WOC,"woc:");
            case "interface":
                return Ontology.INTERFACE_ENTITY.toString().replace(Ontology.WOC,"woc:");
            default:
                throw new IllegalStateException("Unexpected value: " + typename);
        }
    }
}
