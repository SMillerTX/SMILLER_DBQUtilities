package com.smiller.dbq.tools;

public class TestVerifyFieldNames {

    public static void main(String[] args) {
        String xsdFilePath = "verifyFieldNamesFiles/DBQENDOThyroidAndParathyroid-3.8.xsd";
        String dbqRulesFile = "verifyFieldNamesFiles/DBQ ENDO Thyroid & Parathyroid (IEPD v 3.8) - Pg 1.json";
        try {
            VerifyFieldNames.process(xsdFilePath, dbqRulesFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
