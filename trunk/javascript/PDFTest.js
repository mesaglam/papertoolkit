app.addMenuItem({
	cName: "What's My Title?",						// Name of the Submenu 
	cParent: "File",								// under the File Menu
	cExec: "app.alert(event.target.info.title, 3);",// title of the document
	cEnable: "event.rc = (event.target != null);",	// document != null
	nPos: 0
});

app.addToolButton( {
    cName: "myToolButton",
    cExec: "app.alert('Someone pressed me!')",
    cTooltext: "Push Me!",
    cLabel: "Press Me Button",
    nPos: 0
});

//console.println(this.mouseX + ", " + this.mouseY); // 0,0 is the bottom left of the document! =\


// lists properties and methods of an object
// usage: listProps(app, "function, boolean")
// usage: listProps(app, "all")
function listProps(obj, typeFilter) {
	// allow all properties
	var allPropsOK = (typeFilter == "all");
	
	console.println(obj + "{");
	for (var prop in obj) {
		var typeOfProperty = typeof(obj[prop]);
		if (allPropsOK) {
		    console.println("\t" + prop + "\t::\t" + typeOfProperty);
		} else if (typeFilter.indexOf(typeOfProperty) != -1) { // contains the type we want
		    console.println("\t" + prop + "\t::\t" + typeOfProperty);
		}
	}
	console.println("}");
}

//global; // the global object of type Global

// some code to track the location of a mouse
function printMouseCoordinates() {
	console.println("[" + this.mouseX + "," + this.mouseY + "]"); // in Points! And works across multiple monitors.
}
var ckMouse = app.setInterval("printMouseCoordinates()", 100); // every 100 milliseconds
var timeout = app.setTimeOut("app.clearInterval(ckMouse); app.clearTimeOut(timeout)", 2000); // executes this line after 2 seconds




// load a URL in the default browser (nice!)
app.launchURL("http://www.yahoo.com/");

// load a PDF File
var oReturnVal = app.browseForDoc(null);
typeof oReturnVal



// some path functions
app.getPath("user", "javascript");
app.getPath("user", "root");
app.getPath("app", "root");
app.getPath(); // same as above
app.getPath("user", "documents") + "/A New Document.pdf"
// /C/Documents and Settings/Ron Yeh/My Documents/A New Document.pdf


// exports the fields of this document to a text file (tab-delimited)
this.exportAsText({
    cPath: app.getPath("user", "documents") + "/Messages.txt"
});


// document can get a URL
// false means do not append to current document
this.getURL("http://www.yahoo.com/", false)



// interacting with soap
SOAP // is an object of type SOAP
var cURL = "http://www.whitemesa.net/wsdl/r2/base.wsdl";
var service = SOAP.connect(cURL);
for (var prop in service) console.println(prop);
service.echoString("bob");


// beware of windows compressed file extraction wizard (it misses files!)

// adds a ? box with a textual annotation
var annot=this.addAnnot({
    page:0,
    type: "Text",
    point: [400,500],
    contents: "Call Smith to get help on this paragraph.",
    popupRect: [400,400,550,500],
    popupOpen: true,
    noteIcon: "Help"
});

// use the annotations features to drag out the regions
// then, save regions by iterating through the annotations and either
// writing them to disk, 
// sending this info to a SOAP server
// or hitting a URL of a local java server with GET requests with embedded information! (this doesn't sound too bad)


// prints out the annotation types
var annots = this.getAnnots();
console.println("\n");
for (var i=0; i<annots.length; i++) {
    var currAnnot = annots[i];
    console.println(i + " ---------------");
    console.println(currAnnot.type);
    console.println(currAnnot.rect);
    console.println(currAnnot.seqNum);
    console.println(currAnnot.author);
}


// a square annotation
var annot=this.addAnnot({
    author: "R3 Designer",
    fillColor: ["RGB", .5, .5, .65],
    lock: true,
    name: "Region_0",
    opacity: 0.5,
    page:0,
    popupOpen: false,
    popupRect: [0,0,200,150],
    print: true,
    readOnly: false,
    rect: [0,0,200,150],
    seqNum: 0,    
    strokeColor: ["RGB", 0.2, 0.2, 0.2],
    type: "Square",
});

// sends info w/ the annotations to the server
this.submitForm({cURL: "http://localhost:8888/", cSubmitAs: "XFDF", bAnnotations: true});
