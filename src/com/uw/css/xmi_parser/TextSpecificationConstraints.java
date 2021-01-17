package com.uw.css.xmi_parser;

public class TextSpecificationConstraints extends RelationshipItem {
    String specification;

    public TextSpecificationConstraints(String name, Component component, String specification) {
        super(name,component,null);
        this.specification = specification;
    }
}
