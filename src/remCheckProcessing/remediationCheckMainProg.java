package remCheckProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class remediationCheckMainProg {

	public static void main(String[] args) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException, JAXBException, XPathExpressionException {
		System.out.println("Parsing Export");
		//ExportParser exportParser = new ExportParser("/Users/udeluma/Desktop/z testing folder/New York Elevate Science Course 3-2018-08-29-1950 (4).csv", "udeluma", "Ashland58");
		System.out.println("Parsing QTI packages");
		//QtiParser qtiParser = new QtiParser(exportParser.assessmentObjsWithRem);
		//QtiStringParser qtiStringParser = new QtiStringParser(exportParser.assessmentObjsWithRem);
		System.out.println("Processing Assessment Data and Writing to File");
		//assessmentDataProcessing dataProcessor = new assessmentDataProcessing(exportParser.assessmentObjsWithRem, exportParser.remediationObjs, qtiStringParser.getAssessmentObjectsData());
		System.out.println("Remediation Review Complete");
		
		
		

	}

}
