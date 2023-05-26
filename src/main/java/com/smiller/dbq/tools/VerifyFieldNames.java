package com.smiller.dbq.tools;

import com.smiller.dbq.tools.json.DBQRules;
import com.smiller.dbq.tools.json.Rule;
import com.smiller.dbq.tools.json.ValidationRule;
import com.smiller.dbq.tools.utils.JacksonJsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

public class VerifyFieldNames {

    static Logger logger = Logger.getLogger("VerifyFieldNames");


    public static void process(String xsdFilePath, String dbqRulesFilePath) throws Exception {
        try {

            String enumSuffix = " (type:enum)";
            //these are TreeSets....sorted alphabetically
            TreeSet<String> rulesFileFieldNames = retrieveRuleFileFieldNames(dbqRulesFilePath);
            TreeSet<String> xsdFileFieldNames = retrieveXsdFileNames(xsdFilePath);
            TreeSet<String> xsdFileEnumerationFields = retrieveXsdEnumerationFields(xsdFilePath);

            int rulesFileFieldNamesCount = rulesFileFieldNames.size();
            int xsdFileFieldNamesCount = xsdFileFieldNames.size();

            //need to update rulesFileFieldNames by tagging those which are enumeration fields
            for(String xsdFileEnumerationField : xsdFileEnumerationFields) {
                xsdFileFieldNames.remove(xsdFileEnumerationField);
                xsdFileFieldNames.add(xsdFileEnumerationField + enumSuffix);
            }


            //need to update xsdFileFieldNames by tagging those which are enumeration fields
            for(String xsdFileEnumerationField : xsdFileEnumerationFields) {
                if(rulesFileFieldNames.contains(xsdFileEnumerationField)) {
                    rulesFileFieldNames.remove(xsdFileEnumerationField);
                    rulesFileFieldNames.add(xsdFileEnumerationField + enumSuffix);
                }
            }

            out("");
            out("---- RULES FILE FIELD NAMES (total: " + rulesFileFieldNames.size() + ") -------------------------------------------------------");
            for(String s : rulesFileFieldNames) {
                out(s);
            }
            out("-----------------------------------------");

            out("");
            out("---- XSD FILE FIELD NAMES (total:" + xsdFileFieldNames.size() + ") --------------------------------------------------------------");
            for(String s : xsdFileFieldNames) {
                out(s);
            }
            out("-------------------------------------------");

            out("");
            out("---- XSD FILE ENUMERATION FIELDS (total:" + xsdFileEnumerationFields.size() + ") ------------------------------------------------");
            for(String s : xsdFileEnumerationFields) {
                out(s);
            }
            out("-------------------------------------------");

            //if there is a matching field name, remove it from the Set of fieldNames for each file.
            Set<String> matchedFields = new HashSet<>();
            for(String xsdFieldName : xsdFileFieldNames) {
                if(rulesFileFieldNames.contains(xsdFieldName)) {
                    matchedFields.add(xsdFieldName);
                }
            }

            rulesFileFieldNames.removeAll(matchedFields);
            xsdFileFieldNames.removeAll(matchedFields);

            reportUnmatchedFieldNames("RULES FILE: ", dbqRulesFilePath, rulesFileFieldNamesCount, rulesFileFieldNames);
            reportUnmatchedFieldNames("XSD FILE: ", xsdFilePath, xsdFileFieldNamesCount, xsdFileFieldNames);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static TreeSet<String> retrieveRuleFileFieldNames(String dbqRulesFilePath) throws Exception {
        TreeSet<String> rulesFileFieldNames = new TreeSet<>();
        String dbqRulesJson = FileUtils.readFileToString(retrieveFile(dbqRulesFilePath), "UTF-8");
        DBQRules dbqRules = JacksonJsonUtil.jsonToPOJO(dbqRulesJson, DBQRules.class);
        for(Rule rule : dbqRules.getRules()) {
            for(ValidationRule validationRule : rule.getValidationRules()) {
                rulesFileFieldNames.add(StringUtils.substringBetween(validationRule.rule, "[", "]"));
            }
        }


        return rulesFileFieldNames;
    }

    private static TreeSet<String> retrieveXsdFileNames(String xsdFilePath) throws Exception {
        TreeSet<String> xsdFileFieldNames = new TreeSet<>();

        StringBuffer expression = new StringBuffer("//xs:element[");
        expression.append("not(@name='Value') and ");
        expression.append("not(@name='Metadata') and ");
        expression.append("not(@name='code') and ");
        expression.append("not(@name='codeSystem') and ");
        expression.append("not(@name='version') and ");
        expression.append("not(@name='source')");
        expression.append("]");
        expression.append("/self::node()");
        NodeList nodeList = getNodeList(xsdFilePath, expression.toString());

        Node fieldNameNode = null;
        for(int i = 0; i < nodeList.getLength(); i++) {
            String fieldName = nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();
            xsdFileFieldNames.add(fieldName);
        }

        return xsdFileFieldNames;
    }

    private static TreeSet<String> retrieveXsdEnumerationFields(String xsdFilePath) throws IOException {
        TreeSet<String> xsdFileEnumerationFields = new TreeSet<>();

        StringBuffer expression = new StringBuffer("//xs:element[");
        expression.append("not(@name='Value') and ");
        expression.append("not(@name='Metadata') and ");
        expression.append("not(@name='code') and ");
        expression.append("not(@name='codeSystem') and ");
        expression.append("not(@name='version') and ");
        expression.append("not(@name='source')");
        expression.append("]");
        expression.append("/descendant::xs:enumeration/ancestor::xs:element[not(@name='Value')]");
        NodeList nodeList = getNodeList(xsdFilePath, expression.toString());

        Node fieldNameNode = null;
        for(int i = 0; i < nodeList.getLength(); i++) {
            fieldNameNode = nodeList.item(i);
            String fieldName = fieldNameNode.getNodeName();
            String fieldValue = fieldNameNode.getAttributes().getNamedItem("name").getNodeValue();
            xsdFileEnumerationFields.add(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue());
        }

        return xsdFileEnumerationFields;
    }


    private static void reportUnmatchedFieldNames(String header, String filePath, int initialSize, Set<String> fieldNames) {
        out(" ");
        out("****************************************************************************************************************");
        out("***** UNMATCHED " + header + "\"" + filePath + "\"");
        if(fieldNames.isEmpty()) {
            out("NO UNMATCHED FIELDS OUT OF " + initialSize + " :-) DO THE FORBIDDEN SACRED DANCE OF PROGRAMMER/QA MERRIMENT!");
        } else {
            out("***** TOTAL:" + fieldNames.size() + " OUT OF " + initialSize);
            out("*****");
            for (String fieldName : fieldNames) {
                out(fieldName);
            }
        }
        out("*****************************************************************************************************************");
    }

    private static NodeList getNodeList(String xsdFilePath, String expression) throws IOException {
        FileInputStream fileIS = null;
        try {
            fileIS = new FileInputStream(retrieveFile(xsdFilePath));
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(fileIS);
            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                @Override
                public String getNamespaceURI(String prefix) {
                    switch (prefix) {
                        case "xs": return "http://www.w3.org/2001/XMLSchema";
                        default:   return null;
                    }
                }
                @Override
                public String getPrefix(String namespaceURI) {
                    throw new UnsupportedOperationException();
                }
                @Override
                public Iterator<String> getPrefixes(String namespaceURI) {
                    throw new UnsupportedOperationException();
                }
            });
            return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(fileIS != null)
                fileIS.close();
        }
        return null;
    }

    private static File retrieveFile(String path) throws Exception {
        ClassLoader classLoader = VerifyFieldNames.class.getClassLoader();
        URL url = classLoader.getResource(path);
        return new File(url.getPath().replaceAll("%20", " "));
    }

    private static void out(Object o) {
        System.out.println(o);
    }
















}
