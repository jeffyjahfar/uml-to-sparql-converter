package com.uw.css.xmi_parser;

import java.util.ArrayList;
import java.util.List;

public class SparqlQuery {
    String query;
    List<RelationshipItem> relationshipItems;
    List<Component> components;

    public SparqlQuery() {
        query = null;
        relationshipItems = new ArrayList<>();
        components = new ArrayList<>();
    }

    public SparqlQuery(String query, List<RelationshipItem> relationshipItems, List<Component> components) {
        this.query = query;
        this.relationshipItems = relationshipItems;
        this.components = components;
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
        QueryConstructionService queryConstructionService = new QueryConstructionService();
        query = "";
        query = query + queryConstructionService.setPrefix();
        query = queryConstructionService.constructSelectStatement(components,false, query);
        query = queryConstructionService.constructWhereStatement(query,relationshipItems,components);
        return query;
    }
}
