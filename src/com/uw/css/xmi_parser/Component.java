package com.uw.css.xmi_parser;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Component {
    String name;
    String type;
    Boolean isSelectItem;

    public Component(String name, String type, Boolean isSelectItem) {
        this.name = name;
        this.type = type;
        this.isSelectItem = isSelectItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSelectItem() {
        return isSelectItem;
    }

    public void setSelectItem(Boolean selectItem) {
        isSelectItem = selectItem;
    }

    @Override
    public boolean equals(Object obj) {
        Component component = (Component) obj;
        if(this.name.equals(component.name) && this.type.equals(component.type)){
            return true;
        }
        return false;
    }
}
