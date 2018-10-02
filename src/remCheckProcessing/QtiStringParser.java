package remCheckProcessing;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class QtiStringParser {
	
	private Map<String, Map<String, String>> assessmentObjectsData = new HashMap(); //data structure that contains the assessment descriptor, question num, and skill code
	
 	public QtiStringParser (Map<String, Map<String, String>> assessmentObjsWithRem) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		
		Map<String, Map<String, String>> assessmentQuestionInfo = new HashMap();
		setAuthentication("udeluma", "Ashland58"); //Authentication needed to access export on Alfresco.
		
		for (Entry<String, Map<String, String>> assessmentEntry: assessmentObjsWithRem.entrySet()) {
			
			//System.out.println("Searching assessment URLs"); //Convert to a log entry?
			
			String url = assessmentEntry.getValue().get("Url");
			String descriptor = assessmentEntry.getKey();
			
			String assessmentTestName = "no";
			Map<String, String> hrefAndSkills = new HashMap();
			Map<String, String> questionAndSkills = new HashMap();

			 
			ZipInputStream manifestZipInputStream = retrieveZip(url);
			ZipEntry manifestZipEntry = manifestZipInputStream.getNextEntry();
			
			while(manifestZipEntry != null) {
				if (manifestZipEntry.getName().contains("imsmanifest")) {
					
					//System.out.println("Found the manifest");//Convert to a log entry?
					
					DocumentBuilderFactory manifestDBFactory = DocumentBuilderFactory.newInstance(); //used xPath here because marshalling QTI objects was slow and I only need to extract strings from the XML.
					DocumentBuilder manifestdBuilder;
					
					manifestdBuilder = manifestDBFactory.newDocumentBuilder();
					
					Document manifestXMLDoc = manifestdBuilder.parse(manifestZipInputStream);
					manifestXMLDoc.getDocumentElement().normalize();
					
					XPath manifestXPath = XPathFactory.newInstance().newXPath();
					
					
					String manifestResourceExpression = "/manifest/resources/resource"; //This is essentially the hierarchy of the QTI XML. I start with the highest level to grade the hrefs of each question file.
					
					NodeList manifestHRefList = (NodeList) manifestXPath.compile(manifestResourceExpression).evaluate(manifestXMLDoc, XPathConstants.NODESET);

					
					 for (int i = 0; i < manifestHRefList.getLength(); i++) { //Yuck. NodeList is not of class iterable so need to use the old-school Java loop here.
						 Node manifestResourceNode = manifestHRefList.item(i);
						 String href = manifestResourceNode.getAttributes().getNamedItem("href").getTextContent();

						 if (manifestResourceNode.getAttributes().getNamedItem("type") != null) {
							 String assessmentTestTypeName = manifestResourceNode.getAttributes().getNamedItem("type").getTextContent();
							 if (assessmentTestTypeName.contains("imsqti_test_xmlv2p1") ||assessmentTestTypeName.contains("imsqti_assessment_xmlv2p1")) {
								 assessmentTestName = manifestResourceNode.getAttributes().getNamedItem("href").getTextContent(); //Find the assessmentTest file which contains the sequence of questions.
							 }
							 
						 }
						 
						 String manifestGUIDExpression = "metadata/curriculumStandardsMetadataSet/curriculumStandardsMetadata/setOfGUIDs/labelledGUID/GUID"; //Create a new expression to dig deeper to grab the skill paths.
						 NodeList manifestGUIDList = (NodeList) manifestXPath.compile(manifestGUIDExpression).evaluate(manifestResourceNode, XPathConstants.NODESET);
						 for (int x = 0; x < manifestGUIDList.getLength(); x++) {
							 Node manifestGUIDNode = manifestGUIDList.item(x);
							 hrefAndSkills.put(href, manifestGUIDNode.getTextContent());
						 }
					  }
					 
					
					ZipInputStream assessmentTestZipInputStream = retrieveZip(url);
					ZipEntry assessmentTestZipEntry = assessmentTestZipInputStream.getNextEntry();

					while(assessmentTestZipEntry != null) {
							if (assessmentTestZipEntry.getName().contains(assessmentTestName)) {
								
								//System.out.println("Found the assessmentTest file"); // Convert to a log entry?
								
								DocumentBuilderFactory assessmentTestDBFactory = DocumentBuilderFactory.newInstance();
								DocumentBuilder assessmentTestDBuilder;
								
								assessmentTestDBuilder = assessmentTestDBFactory.newDocumentBuilder();
								
								Document assessmentTestDoc = assessmentTestDBuilder.parse(assessmentTestZipInputStream);
								assessmentTestDoc.getDocumentElement().normalize();
								
								XPath assessmentTestXPath = XPathFactory.newInstance().newXPath();
								
								String assessmentTestItemExpression = "/assessmentTest/testPart/assessmentSection/assessmentItemRef";
								
								NodeList assessmentTestItemNodeList = (NodeList) assessmentTestXPath.compile(assessmentTestItemExpression).evaluate(assessmentTestDoc, XPathConstants.NODESET);
								
								
								 for (int i = 0; i < assessmentTestItemNodeList.getLength(); i++) {
									 Node assessmentTestItemNode = assessmentTestItemNodeList.item(i);

									 String questionNum = "Question_" + (i+1);
									 questionAndSkills.put(questionNum, hrefAndSkills.get(assessmentTestItemNode.getAttributes().getNamedItem("href").getTextContent()));
								  }
								break;
								}
							//System.out.println("Looking for next entry"); Log entry?
							assessmentTestZipEntry = assessmentTestZipInputStream.getNextEntry();
							
							}
					assessmentTestZipInputStream.close();
					//System.out.println("Closing assessment Test"); Log entry?
					
					break;
				}
				
				manifestZipEntry = manifestZipInputStream.getNextEntry();
			}
			manifestZipInputStream.close();
			assessmentObjectsData.put(descriptor, questionAndSkills);

			//updateAssessmentObjectsData(descriptor, questionsAndSkills, questionNumbers);
		}
		
	}
		
		
		

	public Map<String, Map<String, String>> getAssessmentObjectsData() {
		return this.assessmentObjectsData;
	}
	
	private void setAuthentication(String user, String pass) {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
		
	}
	
	private ZipInputStream retrieveZip(String assessmentUrl) throws IOException {
		
		URL url = new URL(assessmentUrl);
		URLConnection alfrescoConnection = url.openConnection();
		ZipInputStream alfrescoZipInputStream = new ZipInputStream(alfrescoConnection.getInputStream());
		return alfrescoZipInputStream;

	}

}
