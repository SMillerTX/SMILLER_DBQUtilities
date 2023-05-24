package com.smiller.dbq.tools.json;

import java.util.ArrayList;

public class DBQRules {
    public String dbqName;
    public String dbqVersion;
    public String rulesVersion;
    public ArrayList<Rule> rules;

    public String getDbqName() {
        return dbqName;
    }

    public void setDbqName(String dbqName) {
        this.dbqName = dbqName;
    }

    public String getDbqVersion() {
        return dbqVersion;
    }

    public void setDbqVersion(String dbqVersion) {
        this.dbqVersion = dbqVersion;
    }

    public String getRulesVersion() {
        return rulesVersion;
    }

    public void setRulesVersion(String rulesVersion) {
        this.rulesVersion = rulesVersion;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }
}
