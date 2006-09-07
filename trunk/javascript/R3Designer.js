// install the files in either the main Acrobat Javascripts directory, or your User's Acrobat Javascript's Directory
// the Main Directory is C:\Program Files\Adobe\Acrobat 7.0\Acrobat\Javascripts
// the User Directory is C:\Documents and Settings\<User Name>\Application Data\Adobe\Acrobat\7.0\JavaScripts
// THEN, make sure the following line is correct (i.e., comment out the incorrect line)
var userOrAppFlag = "user";  // installed in the User Directory
//var userOrAppFlag = "app"; // installed in the Main Directory

// how many regions are in this document
var numberOfRegions = 0;

// the rectangle that describes the size of the page
var mediaBox;

// the port through which we will talk to the AcrobatCommunicationServer on the local host
var port = 8888;





//////////
////////// Run these functions when this JavaScript file is loaded
//////////

// loads this javascript whenever Adobe Acrobat loads
console.println("Loading the R3 Tools...");

// raise privileges for some functions
trustedGetPath = app.trustedFunction(getJavaScriptPath);
trustedImportIcons = app.trustedFunction(importIcons);

// get the r3 plugin path
getJavaScriptPath(userOrAppFlag);

// load the icon resources and create the buttons
importIcons();
addButtons();





//////////
////////// Function Definitions
//////////

///
///
function getJavaScriptPath(userOrApp) {
	app.beginPriv();
	r3PluginPath = app.getPath(userOrApp, "javascript");
	app.endPriv();
}

/// 
/// 
function saveRegionInformation() {
	console.println("Saving the Region information. Make sure your AcrobatCommunicationServer is running and listening at port: " + port)
	// sends info w/ the annotations to the server
	this.submitForm({cURL: "http://localhost:"+port+"/", cSubmitAs: "XFDF", bAnnotations: true});
}

/// Loads icons from Disk
/// Uses the r3pluginPath. Make sure that points to the right place.
/// Do Relative Paths work for the icons?
function importIcons() {
	app.beginPriv();

	var tempDoc = app.newDoc();
	
	// import icons (20x20 pixels) from the file specified
	tempDoc.importIcon("moonIcon", r3PluginPath+"/moon20x20.png", 0);
	tempDoc.importIcon("saveIcon", r3PluginPath+"/save20x20.png", 0);
	
	// convert the icons to streams. set global variables to keep them around for this session.
	moonIcon = util.iconStreamFromIcon(tempDoc.getIcon("moonIcon"));
	saveIcon = util.iconStreamFromIcon(tempDoc.getIcon("saveIcon"));
	
	// close the doc now that we have grabbed the icon stream
	tempDoc.closeDoc(true);

	app.endPriv();
}

/// 
/// 
function addButtons() {	

	// the add button
	app.addToolButton( {
	    cName: "Add Region",
	    cExec: "addRectangularRegion()",
		cEnable: "event.rc = (event.target != null);",	// document != null
	    cTooltext: "Add an R3 Region.",
	    oIcon: moonIcon,
	    cLabel: "Add Region",
	    nPos: 0
	});
	
	// save button
	app.addToolButton( {
	    cName: "Save Region Information",
	    cExec: "saveRegionInformation()",
		cEnable: "event.rc = (event.target != null);",	// document != null
	    cTooltext: "Save Region Information to a File",
	    oIcon: saveIcon,
	    cLabel: "Save Region Information",
	    nPos: 1
	});
}


///
/// Create a Region
/// We will Layer Pattern on these regions...
///
function addRectangularRegion() {
	mediaBox = this.getPageBox("Media");
	var width = mediaBox[2] - mediaBox[0];
	var height = mediaBox[1] - mediaBox[3];
	var rname = "Region_" + numberOfRegions;
	
	this.addAnnot({
	    author: rname,
	    fillColor: ["RGB", .5, .5, .6],
	    lock: false,
	    name: rname,
	    opacity: 0.3, // translucent
	    page:0,
	    popupOpen: false,
	    print: true,
	    readOnly: false,
	    rect: [0,height-200,300,height],
	    seqNum: numberOfRegions,    
	    strokeColor: ["RGB", 0.2, 0.2, 0.2],
	    type: "Square",
	});
	numberOfRegions++;
}