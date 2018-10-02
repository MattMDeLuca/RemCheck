package remCheckProcessing;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class ExportParser {
	
	
	private static Map<String, Map<String, String>> remediationObjs = new HashMap(); //Remediation Objects data structure is assessment descriptor:
	private static Map<String, Map<String, String>> assessmentObjsWithRem = new HashMap();
	private Map<String, Map<String, String>> hierarchyMapping = new HashMap();
	public ArrayList<Map<String, Pair<Integer, Integer>>> assessmentHierarchy = new ArrayList();
	
	
	public ExportParser(InputStream exportInputStream) throws FileNotFoundException, IOException {
		BufferedReader exportFileReader = new BufferedReader(new InputStreamReader(exportInputStream));
				
		String[] headers = exportFileReader.readLine().split(","); //Since Java doesn't have a CSV processing library, we find the indices of the column heads and use them to find values in the CSV.
		int mediaType = Arrays.asList(headers).indexOf("Mediatype");
		int contentType = Arrays.asList(headers).indexOf("Contenttype");
		int fileType = Arrays.asList(headers).indexOf("Filetype");
		int titleInSequence = Arrays.asList(headers).indexOf("Title In Sequence");
		int descriptor = Arrays.asList(headers).indexOf("Descriptor");
		int parentDescriptor = Arrays.asList(headers).indexOf("Parent Descriptor");
		int url = Arrays.asList(headers).indexOf("Url");
		int skill = Arrays.asList(headers).indexOf("Skills");
		int externalID = Arrays.asList(headers).indexOf("External Id");
		int root = Arrays.asList(headers).indexOf("Root");
		int displaySequence = Arrays.asList(headers).indexOf("Display Seq");
		
		Map<String, Map<String, String>> assessmentObjects = new HashMap();
		Map<String, String> remediationContainers = new HashMap();
		
		exportFileReader.lines()
						.map(x -> x.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)) //regex I found that splits the lines on the correct comma, rather than splitting commas in text fields.
						.forEach(x -> {
							
							
							Map<String, String> hierarchyInformation = new HashMap();
							if (x[parentDescriptor].isEmpty()) {
								hierarchyInformation.put("Parent Descriptor", "root");
							}
							
							else {
								hierarchyInformation.put("Parent Descriptor", x[parentDescriptor]);
							}
							
							hierarchyInformation.put("Sequence Value", x[displaySequence].replaceAll("\"", "")); //Pull display sequence values to properly display assessment in the UI.
							hierarchyMapping.put(x[descriptor], hierarchyInformation);
							
							if (x[titleInSequence].contains("Remediation") ) { // Iterate through each item in the export to see if it is a remediation container. 
								remediationContainers.put(x[descriptor].replaceAll("\"", ""), x[parentDescriptor].replaceAll("\"", ""));
							}
							
							if (x[mediaType].contains("Test") ) { //Iterate through each item in the export to see if it's an assessment object. 
								Map<String, String> assessmentInformation = new HashMap(); //Gather all information about assessment objects in a map.
								assessmentInformation.put("External ID", x[externalID].replaceAll("\"", ""));
								assessmentInformation.put("Title In Sequence", x[titleInSequence].replaceAll("\"", ""));
								assessmentInformation.put("Url", x[url].replaceAll("\"", ""));
								assessmentObjects.put(x[descriptor].replaceAll("\"", ""), assessmentInformation);

							}
							
							
							if (remediationContainers != null) {

								if (remediationContainers.containsKey(x[parentDescriptor].replaceAll("\"", "")) == true) { //Check items to see if they are inside of the remediation containers.

									Map<String, String> remediationObjectInformation = new HashMap(); //Gather all information about remediation objects in a map.
									remediationObjectInformation.put("Remediation Object External ID", x[externalID].replaceAll("\"", ""));
									remediationObjectInformation.put("Assessment Object Descriptor", remediationContainers.get(x[parentDescriptor].replaceAll("\"", ""))); 
									remediationObjectInformation.put("Title In Sequence", x[titleInSequence].replaceAll("\"", ""));
									remediationObjectInformation.put("Filetype",  x[fileType].replaceAll("\"", ""));
									if (x[skill] != null) {remediationObjectInformation.put("Skill", x[skill].replaceAll("\"", ""));}
									if (x[skill].length() < 3) {remediationObjectInformation.put("Skill", "No skill aligned");}
									this.remediationObjs.put(x[descriptor].replaceAll("\"", ""), remediationObjectInformation); //Can't use the assessment descriptor as the key since there are multiple rem objects for each assessment.
								}
							
							
						}});

		
		exportFileReader.close();
				
		assessmentObjects.entrySet()
						 .stream()
						 .map(x -> x.getKey())
						 .forEach(x -> {Boolean hasRem = remediationContainers.values()
							 					  							  .stream()
							 					  							  .filter(y -> y.contains(x))
							 					  							  .findFirst()
							 					  							  .isPresent();

							 if (hasRem == true) {
								 Pair<Integer, Integer> sequenceValues = findSequenceValue(x, 0, 0);
								 Map<String, Pair<Integer, Integer>> hierarchyMap = new HashMap();
								 hierarchyMap.put(x, sequenceValues);
								 assessmentHierarchy.add(hierarchyMap);
								 System.out.println(assessmentObjects.get(x).get("Title In Sequence"));
								 this.assessmentObjsWithRem.put(x, assessmentObjects.get(x));
							 }
		
		});
			
		
		
		//wrote two new comparators to deal with how assessment objects should be displayed
	Comparator sortByTOCSequenceValue = new Comparator<Map<String, Pair<Integer, Integer>>>() { //this one compares the value of the object inside of it's TOC parent folder.
			public int compare(Map<String, Pair<Integer, Integer>> s1, Map<String, Pair<Integer, Integer>> s2) {
				int assessment1 = 0;
				int assessment2 = 0;
				for (Pair<Integer, Integer> i1: s1.values()) {assessment1 = i1.getKey();}
				for (Pair<Integer, Integer> i2: s2.values()) {assessment2 = i2.getKey();}
				return Integer.compare(assessment1, assessment2);	
			}
	};
	
	
	Comparator sortByInternalSequenceValue = new Comparator<Map<String, Pair<Integer, Integer>>>() { //this one compares the value of the object inside of the lesson/topic folder.
		public int compare(Map<String, Pair<Integer, Integer>> s1, Map<String, Pair<Integer, Integer>> s2) {
			int assessment1 = 0;
			int assessment2 = 0;
			for (Pair<Integer, Integer> i1: s1.values()) {assessment1 = i1.getValue();}
			for (Pair<Integer, Integer> i2: s2.values()) {assessment2 = i2.getValue();}
			return Integer.compare(assessment1, assessment2);	
		}
};
	
	Collections.sort(assessmentHierarchy, sortByTOCSequenceValue.thenComparing(sortByInternalSequenceValue));
	
	}
	

	public static Map<String, Map<String, String>> getAssessmentObjsWithRem() {
		return assessmentObjsWithRem;
	}
	
	public static Map<String, Map<String, String>> getRemediationObjs() {
		return remediationObjs;
	}
	
	//this recursive function helps to pull the hierarchy values from the list of parent and child descriptors from the export.
	private Pair<Integer, Integer> findSequenceValue(String descriptor, int TOCsequenceValue, int internalSequenceValue) {
		for (Entry<String, Map<String, String>> h : hierarchyMapping.entrySet()) {
			if (h.getKey().contains(descriptor)) {
				
				if(h.getValue().get("Parent Descriptor").contains("root")) {
					Pair<Integer, Integer> finalPair = new Pair(TOCsequenceValue, internalSequenceValue);
					return finalPair;}
				
					TOCsequenceValue = Integer.parseInt((h.getValue().get("Sequence Value")));
					internalSequenceValue += Integer.parseInt((h.getValue().get("Sequence Value")));
					return findSequenceValue(h.getValue().get("Parent Descriptor"), TOCsequenceValue, internalSequenceValue);				
			}
			
		}
		Pair<Integer, Integer> fakePear = new Pair(1, 1);
		return fakePear;
		
	}
}




