# RemCheck
A tool for checking QTI packages archived in a content management system using a metadata export.

This project is not yet complete. As of 10/2/18, it can:

- Parse a specific metadata export from Alfresco for QTI assessment.
- Display the name of the assessment in somewhat sequential ordered based on the sequence metadata in the export.
- Allow the user to select some or all of the assessment to be changed.


Upcoming features:
- Determing for which assessment the user wants to crosscheck assessment data.
- Parsing the QTI assessment data from Alfresco and writing it to file for the user.
- Determining if there is mismanaged assessment data from the ones the user wants checked.

There are some deprecated classes inside of this program that I built initially for testing; they are slowly being rebuilt to work with the JavaFX UI I am building.
