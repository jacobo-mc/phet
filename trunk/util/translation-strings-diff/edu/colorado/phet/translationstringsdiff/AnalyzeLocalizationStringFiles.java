package edu.colorado.phet.translationstringsdiff;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Main class file for a stand-alone, command-line application that analyzes
 * the localization string files associated with a PhET Java simulation and
 * outputs a text file indicating which strings exist in which files.
 * 
 * The output generated by this program is in comma-separated, or CSV, format
 * and is intended to be viewed in a spreadsheet.
 * 
 * @author John Blanco
 *
 */
public class AnalyzeLocalizationStringFiles {

	private static final String MARKER_FOR_PROPERTY_PRESENT = "X";
	private static final String MARKER_FOR_PROPERTY_NOT_PRESENT = "";

	private static void printUsage(){
		System.out.println("Usage:   AnalyzeLocalizationStringFiles <localization_stem>");
		System.out.println("Example: AnalyzeLocalizationStringFiles nuclear-physics");
	}
	
	/**
	 * Creates a list of all properties found in the provided file lists.  If
	 * the property is found in multiple files, the value from the first
	 * instance found is the one that is used.
	 * 
	 * @param fileList
	 * @return
	 */
	private static Properties createMasterProperties(LocalizationFileList fileList) {

		Properties masterProperties = new Properties();
		
		for (int i=0; i<fileList.size(); i++){
			Properties properties = new Properties();
		    try {
		        properties.load(new FileInputStream((String)fileList.get(i)));
		    } catch (IOException e) {
		    	System.err.println("Exception caught: " + e);
		    	System.exit(1);
		    }
		    Enumeration propertyEnumeration = properties.propertyNames();
		    while (propertyEnumeration.hasMoreElements()){
		    	String propertyName = (String)propertyEnumeration.nextElement();
		    	if ( masterProperties.getProperty(propertyName) == null ){
		    		masterProperties.setProperty(propertyName, "dummy-value");
		    	}
		    }
		}
		
		return masterProperties;
	}
	
	/**
	 * Compare the list of master properties to the properties found in each
	 * if the string files and output comma-separated data that indicates
	 * which string property is found where.
	 * 
	 * @param masterProperties
	 * @param fileList
	 */
	private static void outputCoverageGrid(Properties masterProperties, LocalizationFileList fileList){
		
		Properties [] propertiesInFiles = new Properties[fileList.size()];

		// Output the headings and load the properties.
		System.out.print("Property Name,");
		for (int i = 0; i < fileList.size(); i++){
			
			System.out.print((String)fileList.get(i) + ",");
			
			propertiesInFiles[i] = new Properties();
		    try {
		    	propertiesInFiles[i].load(new FileInputStream((String)fileList.get(i)));
		    } catch (IOException e) {
		    	System.err.println("Exception caught: " + e);
		    	System.exit(1);
		    }
		}
		System.out.print("\n");
		
		// Output the comma-separated grid data that describes which properties appear where.
	    Enumeration propertyEnumeration = masterProperties.propertyNames();
	    while (propertyEnumeration.hasMoreElements()){
	    	String propertyName = (String)propertyEnumeration.nextElement();
	    	System.out.print( propertyName + ",");
	    	for (int i = 0; i < propertiesInFiles.length; i++){
	    		if (propertiesInFiles[i].getProperty(propertyName) != null){
	    	    	System.out.print( MARKER_FOR_PROPERTY_PRESENT );
	    		}
	    		else{
	    	    	System.out.print( MARKER_FOR_PROPERTY_NOT_PRESENT );
	    		}
	    		System.out.print(",");
	    	}
	    	System.out.println();
	    }
	}

	public static void main(String[] args) {
		
		// Check the arguments.
		if (args.length != 1){
			System.out.println("Error: Incorrect number of arguments (" + args.length + ").");
			printUsage();
			System.exit(1);
		}
		
		// Create the list of files to be processed.
		LocalizationFileList fileList = new LocalizationFileList(args[0]);
		
		if (fileList.size() == 0){
			// No matching files found.
			System.out.println("Error: No files found matching stem \"" + args[0] + "\"");
			printUsage();
			System.exit(1);
		}
		
		// Create a master list containing all the properties found in all the files.
		Properties masterProperties = createMasterProperties( fileList );
		
		// Print the output data.
		outputCoverageGrid(masterProperties, fileList);
	}
}
