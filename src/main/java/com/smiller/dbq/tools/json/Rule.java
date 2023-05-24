package com.smiller.dbq.tools.json;

import java.util.ArrayList;

public class Rule {
    public String dbqField;
    public ArrayList<ValidationRule> validationRules;

    public String getDbqField() {
        return dbqField;
    }

    public void setDbqField(String dbqField) {
        this.dbqField = dbqField;
    }

    public ArrayList<ValidationRule> getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(ArrayList<ValidationRule> validationRules) {
        this.validationRules = validationRules;
    }
}
