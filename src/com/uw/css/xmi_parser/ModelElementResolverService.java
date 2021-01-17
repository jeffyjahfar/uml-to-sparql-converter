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
            case "property":
                return true;
//            case "parameter":
//                return true;
        }
        return false;
    }
    public void getOwnedOperations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
       if(modelElement.getType().getName().equals(Relationships.operation.name()) && modelElement.getName().length()>0){
           Collection<ModelElement> ownedoperations = modelElement.getRelations("ownedoperations");
           if(ownedoperations != null){
               for(ModelElement op: ownedoperations){
                   Component owner = new Component(op, op.getType().getName(),false);
                   Component child = new Component(modelElement, modelElement.getType().getName(),false);
                   RelationshipItem relationshipItem = new RelationshipItem(modelElement.getType().getName(), owner,child);
                   relationshipItems.add(relationshipItem);
                   getAnnotationRelations(relationshipItems,modelElement);
               }
           }
       }
    }

    public String resolveParameterKind(ModelElement modelElement){
        String kind = modelElement.getPlainAttribute("kind");
        switch (kind){
            case "return":
                return Relationships.returnType.name();
            case "in":
                return Relationships.parameter.name();
            case "out":
                return Relationships.parameter.name();
            case "inout":
                return Relationships.parameter.name();
            default:
                return "";
        }
    }

    public void getOwnedParameters(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        if(modelElement.getType().getName().equals(Relationships.operation.name()) && modelElement.getName().length()>0){
            Collection<ModelElement> ownedparameters = modelElement.getOwnedElements();
            if(ownedparameters != null){
                for(ModelElement op: ownedparameters){
                    if(op.getType().getName().equals(Relationships.parameter.name())){
                        String relationshipType = resolveParameterKind(op);
                        ModelElement parametertype = op.getRefAttribute("parametertype");
                        if(relationshipType.equals(Relationships.returnType.name())){
                            if(parametertype!=null && !parametertype.getName().isEmpty()) {
                                Component child = new Component(parametertype, parametertype.getType().getName(), false);
                                Component owner = new Component(modelElement, modelElement.getType().getName(), false);
                                RelationshipItem relationshipItem = new RelationshipItem(relationshipType, owner, child);
                                relationshipItems.add(relationshipItem);
                            }
                        }else{
                            Component child = new Component(op, op.getType().getName(),false);
                            Component owner = new Component(modelElement, modelElement.getType().getName(),false);
                            RelationshipItem relationshipItem = new RelationshipItem(relationshipType, owner,child);
                            relationshipItems.add(relationshipItem);
                            if(parametertype != null){
                                Component child2 = new Component(parametertype, parametertype.getType().getName(),false);
                                RelationshipItem relationshipItem2 = new RelationshipItem(Relationships.type.name(), child,child2);
                                relationshipItems.add(relationshipItem2);
                            }
                        }
                    }

                }
            }
        }
    }

    public void getAllRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        getGeneralizationRelations(relationshipItems, modelElement);
        getSupplierRelations(relationshipItems,modelElement);
        getOwnedOperations(relationshipItems,modelElement);
        getOwnedParameters(relationshipItems,modelElement);
        getAssociationRelations(relationshipItems,modelElement);
        getInterfaceImplementationRelations(relationshipItems,modelElement);
        getOwnedAttributes(relationshipItems,modelElement);
        getSequenceDiagramRelations(relationshipItems,modelElement);
        resolveInteractionDiagram(relationshipItems,modelElement);
    }

    private void getOwnedAttributes(List<RelationshipItem> relationshipItems, ModelElement modelElement) {
        if(modelElement.getType().getName().equals("class") || modelElement.getType().getName().equals("interface")){
            Collection<ModelElement> ownedElements = modelElement.getOwnedElements();
            if(ownedElements != null){
                for(ModelElement op: ownedElements){
                    if(op.getType().getName().equals(Relationships.property.name()) && !op.getName().isEmpty()){
                        Component child = new Component(op, op.getType().getName(),false);
                        Component owner = new Component(modelElement, modelElement.getType().getName(),false);
                        RelationshipItem relationshipItem = new RelationshipItem(Relationships.property.name(), owner,child);
                        relationshipItems.add(relationshipItem);
                    }
                }
            }
        }
    }

    private void getInterfaceImplementationRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement) {
        String type = modelElement.getType().getName();
        if(type.equals(Relationships.interfacerealization.name())){
            Collection<ModelElement> implementations = modelElement.getRelations("interfacerealizations");
            ModelElement owner = modelElement.getRefAttribute("contract");
            Iterator<ModelElement> iterator = implementations.iterator();
            Component toItem = new Component(owner,owner.getType().getName(),false);
            if(iterator.hasNext()){
                ModelElement child = iterator.next();
                Component fromItem = new Component(child,child.getType().getName(),false);
                RelationshipItem relationshipItem = new RelationshipItem(type,fromItem,toItem);
                relationshipItems.add(relationshipItem);
            }
        }
        if(type.equals(Relationships.realization.name())){
            ModelElement client = (ModelElement) modelElement.getSetAttribute("client").iterator().next();
            ModelElement owner = (ModelElement) modelElement.getSetAttribute("supplier").iterator().next();
            Component fromItem = new Component(client,client.getType().getName(),false);
            Component toItem = new Component(owner,owner.getType().getName(),false);
            RelationshipItem relationshipItem = new RelationshipItem(type,fromItem,toItem);
            relationshipItems.add(relationshipItem);
        }
    }

    private void getGeneralizationRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement) {
        Collection<ModelElement> relations = modelElement.getRelations("general");
        if(relations != null){
            for(ModelElement relation: relations){
                Component owner = new Component(modelElement, modelElement.getType().getName(),false);
                Collection<ModelElement> generalizations = relation.getRelations("generalizations");
                for(ModelElement g: generalizations){
                    Component child = new Component(g, g.getType().getName(),false);
                    RelationshipItem relationshipItem = new RelationshipItem(relation.getType().getName(), child,owner);
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
            Component fromItem = new Component(owner,owner.getType().getName(),false);
            if(iterator.hasNext()){
                ModelElement child = iterator.next().getRefAttribute("propertytype");
                Component toItem = new Component(child,child.getType().getName(),false);
                RelationshipItem relationshipItem = new RelationshipItem(modelElement.getType().getName(),fromItem,toItem);
                relationshipItems.add(relationshipItem);
            }
        }
    }

    private void getSupplierRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        Collection<ModelElement> relations = modelElement.getRelations("supplier");
        if(relations != null){
            for(ModelElement relation: relations){
                Component owner = new Component(modelElement, modelElement.getType().getName(),false);
                Collection<ModelElement> clients = (Collection<ModelElement>) relation.getSetAttribute("client");
                for(ModelElement g: clients){
                    Component child = new Component(g, g.getType().getName(),false);
                    RelationshipItem relationshipItem = new RelationshipItem(relation.getType().getName(), child,owner);
                    relationshipItems.add(relationshipItem);
                }
            }
        }
    }
    List<RelationshipItem> resolveRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        getAllRelations(relationshipItems,modelElement);
        return relationshipItems;
    }

    List<Component> resolveComponents(List<Component> components, ModelElement modelElement, Boolean suppressVisibility){
        if(isQueryObject(modelElement) && !modelElement.getName().isEmpty()){
            Component component = new Component(modelElement, modelElement.getType().getName(),true);
            List<String> modifiers = new ArrayList<>();
            if(!suppressVisibility){
                if(modelElement.getType().hasAttribute("visibility")){
                    String visibility = modelElement.getPlainAttribute("visibility");
                    if(visibility != null){
                        modifiers.add(visibility);
                    }
                }
            }

            if(modelElement.getType().hasAttribute("abstract")){
                String isAbstract = modelElement.getPlainAttribute("abstract");
                if(isAbstract.equals("true")){
                    modifiers.add("abstract");
                }
            }
            if(modelElement.getType().hasAttribute("static")){
                String isStatic = modelElement.getPlainAttribute("static");
                if(isStatic.equals("true")){
                    modifiers.add("static");
                }
            }
            if(modelElement.getType().hasAttribute("final")){
                String isFinal = modelElement.getPlainAttribute("final");
                if(isFinal.equals("true")){
                    modifiers.add("final");
                }
            }
            component.setModifiers(modifiers);
            if(!components.contains(component))
                components.add(component);
        }
        return components;
    }

    void getSequenceDiagramRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        if(modelElement.getType().getName().equals("message")){
            ModelElement receiveevent = modelElement.getRefAttribute("receiveevent").getRefAttribute("covered").getRefAttribute("represents").getRefAttribute("propertytype");
            Component receiveComp = new Component(receiveevent,receiveevent.getType().getName(),Boolean.FALSE);
            ModelElement sendevent = modelElement.getRefAttribute("sendevent").getRefAttribute("covered").getRefAttribute("represents").getRefAttribute("propertytype");
            Component sendComp = new Component(sendevent,sendevent.getType().getName(),Boolean.FALSE);
            RelationshipItem relationshipItem = new RelationshipItem(Relationships.references.name(), sendComp,receiveComp);
            relationshipItems.add(relationshipItem);
        }

    }

    void resolveInteractionDiagram(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        if(modelElement.getType().getName().equals("interaction")){
            Collection<ModelElement> ownedElements = modelElement.getOwnedElements();
            for(ModelElement m:ownedElements){
                if(m.getType().getName().equals("message")){
                    m.getRefAttribute("signature");
                }
            }
            ModelElement receiveevent = modelElement.getRefAttribute("receiveevent").getRefAttribute("covered").getRefAttribute("represents").getRefAttribute("propertytype");
//            Component receiveComp = new Component(receiveevent,receiveevent.getType().getName(),Boolean.FALSE);
//            ModelElement sendevent = modelElement.getRefAttribute("sendevent").getRefAttribute("covered").getRefAttribute("represents").getRefAttribute("propertytype");
//            Component sendComp = new Component(sendevent,sendevent.getType().getName(),Boolean.FALSE);
//            RelationshipItem relationshipItem = new RelationshipItem(Relationships.references.name(), sendComp,receiveComp);
//            relationshipItems.add(relationshipItem);
        }

    }


    void getAnnotationRelations(List<RelationshipItem> relationshipItems, ModelElement modelElement){
        String stereotype = modelElement.getPlainAttribute("stereotype");
        if(stereotype.equals(Relationships.override.name())){
            Component comp = new Component(modelElement,modelElement.getType().getName(),Boolean.FALSE);
            RelationshipItem relationshipItem = new TextSpecificationConstraints(Relationships.annotation.name(),comp,Override.class.getName());
            relationshipItems.add(relationshipItem);
        }
    }
}
