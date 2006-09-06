// loads this javascript whenever Adobe Acrobat loads
console.println("Loading the R3 Tools...");

// the add button
app.addToolButton( {
    cName: "Add Region",
    cExec: "addNewRegion()",
    cTooltext: "Add an R3 Region.",
    cLabel: "Add Region",
    nPos: 0
});

// save button
app.addToolButton( {
    cName: "Save Region Information",
    cExec: "saveRegionInformation()",
    cTooltext: "Save Region Information to a File",
    cLabel: "Save Region Information",
    nPos: 1
});

function addNewRegion() {
	console.println("Adding...");
}

function saveRegionInformation() {
	console.println("Saving...");
}