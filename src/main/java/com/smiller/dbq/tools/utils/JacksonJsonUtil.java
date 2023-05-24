package com.smiller.dbq.tools.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
/* !!!!! IMPORTANT NOTE FOR DEVELOPERS !!!! */
/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
/* ALWAYS USE METHOD getMapper() TO OBTAIN AN ObjectMapper */
/* UNLESS THERE IS A CLEAR AND ABSOLUTE NECESSITY OTHERWISE */
/* THIS CREATES AN ObjectMapper WITH SEVERAL IMPORTANT AND CRITICAL CONFIGURATION PARAMETERS */
/* WHICH RESULT IN CONSISTENT BEHAVIOR AND RESULTS THROUGHOUT THE CODE */
/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
 * 
 * automagically create a POJO from a JSON string: https://json2csharp.com/json-to-pojo
 * For XML: https://json2csharp.com/code-converters/xml-to-java
 * 
 * */
public class JacksonJsonUtil {

	static Logger logger = Logger.getLogger("JacksonJsonUtil");
	private static ObjectMapper objectMapper = null;


	

	public static String prettyPrint(String json)
	{
		ObjectMapper objectMapper = getMapper();		
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);		
		try {
			return objectMapper.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			logger.log(Level.SEVERE, e.toString());
			return null;
		}		
	}
	
	public static String POJOtoJson(Object obj)
	{
		return POJOtoJson(obj, true);
	}
	
	public static String POJOtoJson(Object obj, boolean ignoreNullFields)
	{
		ObjectMapper objectMapper = getMapper();
		
		if(ignoreNullFields)
			objectMapper.setSerializationInclusion(Include.NON_NULL);

		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.log(Level.SEVERE, e.toString());
			return null;
		}		
	}
	
	public static ObjectMapper getMapper() {
		if(objectMapper == null) {
			objectMapper = JsonMapper.builder()
								.findAndAddModules()
			        			.build();
											
			// ObjectMapper defaults time to UTC, set to timezone we are running in
			objectMapper.setTimeZone(TimeZone.getDefault());
			
			objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			return objectMapper;
		}
		else
		{
			return objectMapper;
		}
	}
	
	public static <T> T jsonToPOJO(String jsonString, Class clazz)
	  throws Exception
	{
		return (T) getMapper().readValue(jsonString, clazz);
	}
	

	public static boolean isValidJSON(final String json)
	{
	    boolean valid = true;
	    
	    try
	    {
	      ObjectMapper objectMapper = new ObjectMapper();
	      objectMapper.readTree(json);
	    }
	    catch (Exception e)
	    {
	      valid = false;
		  logger.log(Level.INFO, e.toString());
	    }
	    
	    return valid;
	}
	


	
	
	

}
