package com.uw.css.uml.parser;

import java.util.ArrayList;
import java.util.List;

public class SparqlQuery {

    String query;
    List<RelationshipItem> relationshipItems;
    List<Component> components;
    private int rdfLines;
    private boolean includeMethodParamsSelect;

    public SparqlQuery() {
        this(null,new ArrayList<>(),new ArrayList<>(),false);
    }

    public SparqlQuery(String query, List<RelationshipItem> relationshipItems, List<Component> components,boolean includeMethodParamsSelect) {
        this.query = query;
        this.relationshipItems = relationshipItems;
        this.components = components;
        this.includeMethodParamsSelect = includeMethodParamsSelect;
    }

    public SparqlQuery(boolean includeMethodParamsInSelect) {
        this(null,new ArrayList<>(),new ArrayList<>(),includeMethodParamsInSelect);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<RelationshipItem> getRelationshipItems() {
        return relationshipItems;
    }

    public void setRelationshipItems(List<RelationshipItem> relationshipItems) {
        this.relationshipItems = relationshipItems;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    //takes a list of queryitems and appends them together to construct the final query
    public String constructQuery(){
        QueryConstructionService queryConstructionService = new QueryConstructionService(includeMethodParamsSelect);
        query = "";
        query = query + queryConstructionService.setPrefix();
        query = queryConstructionService.constructSelectStatement(components,true, query);
        query = queryConstructionService.constructWhereStatement(query,relationshipItems,components);
//        query = queryConstructionService.addFilterStatement(query,components);
        rdfLines = queryConstructionService.getNumLines();
        return query;
    }

    public int getRDFLines(){
        return rdfLines;
    }
}
