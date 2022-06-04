package com.uw.css.uml.parser;

import com.sdmetrics.model.ModelElement;

import java.util.List;

public class Component {
    String name;
    String type;
    Boolean isSelectItem;
    List<String> modifiers;

    public Component(ModelElement modelElement, String type, Boolean isSelectItem) {
        this.name = modelElement.getName()+modelElement.getRunningID();
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

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
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
