package com.uw.css.uml.parser;

public class RelationshipItem {
    String name;
    Component fromItem;
    Component toItem;

    public RelationshipItem(String name, Component fromItem, Component toItem) {
        this.name = name;
        this.fromItem = fromItem;
        this.toItem = toItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Component getFromItem() {
        return fromItem;
    }

    public void setFromItem(Component fromItem) {
        this.fromItem = fromItem;
    }

    public Component getToItem() {
        return toItem;
    }

    public void setToItem(Component toItem) {
        this.toItem = toItem;
    }
}
