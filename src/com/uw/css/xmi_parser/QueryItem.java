package com.uw.css.xmi_parser;

public class QueryItem {
    String variableName;
    Boolean isSelectItem;
    String constraint;
    String constraintType;

    public QueryItem(String variableName, Boolean isSelectItem, String constraint, String constraintType) {
        this.variableName = variableName;
        this.isSelectItem = isSelectItem;
        this.constraint = constraint;
        this.constraintType = constraintType;
    }

    public QueryItem() {
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public Boolean getSelectItem() {
        return isSelectItem;
    }

    public void setSelectItem(Boolean selectItem) {
        isSelectItem = selectItem;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }
}
