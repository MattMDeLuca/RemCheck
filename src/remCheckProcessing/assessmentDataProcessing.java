package remCheckProcessing;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class assessmentDataProcessing {
	
	private Map<String, Map<String, String>> assessmentObjectsWithRemediation;
	private Map<String, Map<String, String>> remediationObjects;
	private Map<String, Map<String, String>> assessmentQuestionAndSkillsData;
	
	public assessmentDataProcessing(Map<String, Map<String, String>> assessmentObjects, Map<String, Map<String, String>> remediation,
			Map<String, Map<String, String>> assessmentQuestionData) throws IOException {
		
		this.assessmentObjectsWithRemediation = assessmentObjects;
		this.remediationObjects = remediation;
		this.assessmentQuestionAndSkillsData = assessmentQuestionData;

		
		FileWriter writer = new FileWriter("/Users/udeluma/Desktop/z testing folder/testFile.csv");
		
		writer.write("Test Name," + "Question Number," + "Remediation Object," + "Filetype," + "Skill\n");
		
		for (Entry<String, Map<String, String>> assessmentObject : assessmentObjects.entrySet()) { //Iterate through each assessment object that has remediation.
			String assessmentDescriptor = assessmentObject.getKey();
			writer.write(assessmentObject.getValue().get("Title In Sequence") + "\n");
			ArrayList<String> SortedQuestionNumbers = new ArrayList(assessmentQuestionAndSkillsData.get(assessmentDescriptor).keySet()); //Grab the questions and skills from the QTI packages for each assessment object.
			Collections.sort(SortedQuestionNumbers, new Comparator<String>() {
				public int compare(String s1, String s2) {
					int q1 = Integer.parseInt(s1.split("_")[1]);
					int q2 = Integer.parseInt(s2.split("_")[1]);
					return Integer.compare(q1, q2);
				}
				
			});
			for (String questionNumber: SortedQuestionNumbers) { //Iterate through each question and check the skill against the remediation objects that are associated with the assessment object in the initial loop.
				String formattedQuestionNumber = questionNumber.replace("_", " ");
				String questionSkill = assessmentQuestionAndSkillsData.get(assessmentObject.getKey()).get(questionNumber);
				writer.write("," + formattedQuestionNumber + "," + "," + "," + questionSkill + "\n");
				for (Entry<String, Map<String, String>> remObject : remediation.entrySet()) {
					if (remObject.getValue().get("Assessment Object Descriptor").contains(assessmentDescriptor)) {
						String remObjectSkill = remObject.getValue().get("Skill");
						if (remObjectSkill.contains(questionSkill)) {
							String remObjectName = remObject.getValue().get("Title In Sequence");
							String remObjectFileType = remObject.getValue().get("Filetype");
							remObject.getValue().put("Matched to Question", "Yes");
							writer.write("," + "," + remObjectName + "," + remObjectFileType + "," + remObjectSkill + "\n");
						}
					}
				}
			//	System.out.println(questionNumber);
			//	System.out.println(questionSkill);
				
			}
			
		}
	
		FileWriter remErrorWriter = new FileWriter("/Users/udeluma/Desktop/z testing folder/remErrors.csv");
		
		remErrorWriter.write("External ID," + "Remediation Object," + "Filetype," + "Associated Test," + "Skill\n");
		for (Entry<String, Map<String, String>> remObject: remediation.entrySet()) {
		if (remObject.getValue().get("Matched to Question") == null) {
			String associatedAssessment = assessmentObjects.get(remObject.getValue().get("Assessment Object Descriptor")).get("Title In Sequence");
			String remObjectExternalId = remObject.getValue().get("Remediation Object External ID");
			String remObjectSkill = remObject.getValue().get("Skill");
			String remObjectTitle = remObject.getValue().get("Title In Sequence");
			String remObjectFiletype = remObject.getValue().get("Filetype");
			remErrorWriter.write(remObjectExternalId + "," + remObjectTitle + "," + remObjectFiletype + "," + associatedAssessment + "," + remObjectSkill + "\n");
		}
	}
	remErrorWriter.close();
	writer.close();
		
	}


}


