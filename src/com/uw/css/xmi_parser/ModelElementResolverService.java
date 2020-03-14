package com.uw.css.xmi_parser;

import com.sdmetrics.model.ModelElement;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.uw.css.utils.Relationships;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ModelElementResolverService {

    Boolean isRelationship(ModelElement modelElement){
        String type = modelElement.getType().getName();
        for( Relationships rel : Relationships.values()){
            if(rel.name().equals(type)){
                return true;
            }
        }
        return false;
    }

    Boolean isQueryObject(ModelElement modelElement){
        String type = modelElement.getType().getName();
        switch (type){
            case "class":
                return true;
            case "operation":
                return true;
            case "interface":
                return true;
//            case "property":
//                return true;
        }
        return false;
    }
    public void getOwnedOperations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
       if(modelElement.getType().getName().equals(Relationships.operation.name()) && modelElement.getName().length()>0){
           Collection<ModelElement> ownedoperations = modelElement.getRelations("ownedoperations");
           if(ownedoperations != null){
               for(ModelElement op: ownedoperations){
                   Component owner = new Component(op.getName(), op.getType().getName(),false);
                   Component child = new Component(modelElement.getName(), modelElement.getType().getName(),false);
                   RelationshipItem relationshipItem = new RelationshipItem(modelElement.getType().getName(), owner,child);
                   relationshipItems.add(relationshipItem);
               }
           }
       }
    }

    public void getPropertyRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        if(modelElement.getType().getName().equals(Relationships.property.name())){
//            modelElement.getat
        }
        Collection<ModelElement> relations = modelElement.getRelations("ownedoperations");
        if(relations != null){
            for(ModelElement relation: relations){
                Component owner = new Component(modelElement.getName(), modelElement.getType().getName(),false);
//                Collection<ModelElement> generalizations = relation.getRelations("generalizations");
//                for(ModelElement g: generalizations){
//                    Component child = new Component(g.getName(), g.getType().getName(),false);
//                    RelationshipItem relationshipItem = new RelationshipItem(relation.getType().getName(), owner,child);
//                    relationshipItems.add(relationshipItem);
//                }
            }
        }
    }

    public void getAllRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        getGeneralizationRelations(relationshipItems, modelElement);
        getSupplierRelations(relationshipItems,modelElement);
        getOwnedOperations(relationshipItems,modelElement);
        getAssociationRelations(relationshipItems,modelElement);
    }

    private void getGeneralizationRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement) {
        Collection<ModelElement> relations = modelElement.getRelations("general");
        if(relations != null){
            for(ModelElement relation: relations){
                Component owner = new Component(modelElement.getName(), modelElement.getType().getName(),false);
                Collection<ModelElement> generalizations = relation.getRelations("generalizations");
                for(ModelElement g: generalizations){
                    Component child = new Component(g.getName(), g.getType().getName(),false);
                    RelationshipItem relationshipItem = new RelationshipItem(relation.getType().getName(), owner,child);
                    relationshipItems.add(relationshipItem);
                }
            }
        }
    }

    private void getAssociationRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement) {
        if(modelElement.getType().getName().equals(Relationships.association.name())){
            Collection<ModelElement> ownedends = (Collection<ModelElement>) modelElement.getSetAttribute("ownedends");
            Iterator<ModelElement> iterator = ownedends.iterator();
            ModelElement owner = iterator.next().getRefAttribute("propertytype");
            Component fromItem = new Component(owner.getName(),owner.getType().getName(),false);
            if(iterator.hasNext()){
                ModelElement child = iterator.next().getRefAttribute("propertytype");
                Component toItem = new Component(child.getName(),child.getType().getName(),false);
                RelationshipItem relationshipItem = new RelationshipItem(modelElement.getType().getName(),fromItem,toItem);
                relationshipItems.add(relationshipItem);
            }
        }
    }

    private void getSupplierRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        Collection<ModelElement> relations = modelElement.getRelations("supplier");
        if(relations != null){
            for(ModelElement relation: relations){
                Component owner = new Component(modelElement.getName(), modelElement.getType().getName(),false);
                Collection<ModelElement> clients = (Collection<ModelElement>) relation.getSetAttribute("client");
                for(ModelElement g: clients){
                    Component child = new Component(g.getName(), g.getType().getName(),false);
                    RelationshipItem relationshipItem = new RelationshipItem(relation.getType().getName(), owner,child);
                    relationshipItems.add(relationshipItem);
                }
            }
        }
    }
    List<RelationshipItem> resolveRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        getAllRelations(relationshipItems,modelElement);
        return relationshipItems;
    }

    List<Component> resolveComponents(List<Component> components, ModelElement modelElement){
        if(isQueryObject(modelElement)){
            Component component = new Component(modelElement.getName(), modelElement.getType().getName(),true);
            if(!components.contains(component))
                components.add(component);
        }
        return components;
    }
}
