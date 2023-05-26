# SMILLER_DBQUtilities

## PURPOSE
This is a toolkit of utilities to assist with QA and Development efforts regarding DBQ validation and data ingestion efforts.

## TOOLS
### VerifyFieldNames
1. Extracts DBQ field names from both the XSD schema file and the manually-generated JSON validation rules file. 
2. A list of field names is generated for each file and listed in the console output for reference along with a count.
3. In addition, enumerated fields are listed as well. The field names are also appeneded with "(type:enum)"
4. When the matching process completes, a report is displayed in the console with results for each file.

Refer to src/test/java under com.smiller.dbq.tools and the Java main class TestVerifyFieldNames as an example of running this process.

*Future feature: Creating a file for the report



**If you have questions, you know where to find me. :-)**
