// mozilla developer center
// http://developer.mozilla.org/en/docs/DOM

// msdn
// http://msdn.microsoft.com/library/default.asp?url=/workshop/author/dhtml/reference/dhtml_reference_entry.asp


// the server socket
var serverSocket;

// array of streams for outputting to our clients
var clientOutputStreamArray = new Array();


// Strtup Code
// This Function is automatically called when Firefox Starts
// AND whenenver a new window is created
// See the End of this file to see where it's called
function initializeContentServer() {

	// See a list of DOM Events at:
	// http://en.wikipedia.org/wiki/DOM_Events
	window.addEventListener("load", start, false);
	window.addEventListener("unload", stop, false);

	window.addEventListener("mousedown", mouseDown, false);
	window.addEventListener("mouseup", mouseUp, false);
	window.addEventListener("keyup", keyUp, false);
	window.addEventListener("keydown", keyDown, false);

	window.addEventListener("DOMMouseScroll", mouseScroll, true);


    // fires too many events
    // window.addEventListener("scroll", windowScroll, false);
	
	window.addEventListener("focus", gotFocus, true);
	window.addEventListener("blur", lostFocus, true);
	
	
	// Mozilla XUL Events
	window.addEventListener("dragdrop", dragDrop, true);
	window.addEventListener("draggesture", dragStart, true);
	
	// too many events, when added to the window
	// window.addEventListener("dragenter", dragEnter, true);
	// window.addEventListener("dragexit", dragExit, true);
	
	
	
}

function dragStart(event) {
	dumpLine("Mouse: Drag-Started");
	dumpLine("Drag Started at " + event.clientX + " " + event.clientY + " Relative to the Firefox Window");
	dumpLine("Drag Started at " + event.screenX + " " + event.screenY + " Relative to the Desktop");
}

function dragEnter() {
	dumpLine("Mouse: Drag-Enter");
}

function dragExit() {
	dumpLine("Mouse: Drag-Exit");
}

function dragDrop(event) {
	dumpLine("Mouse: Drag-Dropped");
}


function windowScroll() {
	dumpLine("Window Scrolled");
}

function mouseDown() {
	dumpLine("MouseDown");
	dumpLine("Number of Clients: " + clientOutputStreamArray.length);
}

function mouseUp() {
	dumpLine("MouseUp");
}

function mouseScroll() {
	dumpLine("Mouse Scrolled");
}

function keyDown() {
	dumpLine("Key Down");
	
	writeToClients("Key Pressed!");
}

function keyUp() {
	dumpLine("Key Up");
}

function start() {
	dumpLine("Opening a new Firefox Window...");
}

function stop() {
	dumpLine("Closing a Firefox Window...");
}

function gotFocus(event) {
	dumpLine("Got Focus.");
}

function lostFocus(event) {
	dumpLine("Lost Focus.");
}


// sends this message across the wire to all clients
function writeToClients(message) {
	message = message + "\n";
	
	for (var i=0; i<clientOutputStreamArray.length;i++) {
		clientOutputStreamArray[i].write(message, message.length);
	}
}


//
// Function to Expose Firefox Information through a Socket
// Currently, this function is tied to the Button in our App
//
function startInformationServer() {
	// at this point top==this
	// top.location == chrome://browser/content/browser.xul
	

	// gBrowser is a shortcut to the current Browser
	// gBrowser.currentURI.spec shows the current text in the address bar of the current browser!
	dumpLine(gBrowser.currentURI.spec);


	// Loads a Location into the Current Browser Tab
    // gBrowser.loadURI("http://www.mozilla.org/");
    // see http://www.xulplanet.com/references/elemref/ref_browser.html
    
	// since javascript cannot do multiple threads, we have to use
	// event listeners to handle clients
	var socketListener = 
	{
		onSocketAccepted : function(sSocket, transport) {
			try {
				var outputString = "Hello " + transport.host + "\n";
				var outStream = transport.openOutputStream(0,0,0);
				outStream.write(outputString, outputString.length);

				dumpLine("Client " + clientOutputStreamArray.length + "Connected");

				// add it to the end of the array (grow the array automatically)
				clientOutputStreamArray[clientOutputStreamArray.length] = outStream;

			} catch(exception) {
				dump(exception);
			}
		},
		
		onStopListening : function(sSocket, status) {
			dumpLine("Client Disconnected");
		}
	
	};

    
    // open a server socket
	try {
	    serverSocket = Components.classes["@mozilla.org/network/server-socket;1"].createInstance(Components.interfaces.nsIServerSocket);
		// true means only connections from this machine
		// false manes connections from anywhere in the world
	    serverSocket.init(11111, false, -1);
	    serverSocket.asyncListen(socketListener);
	} catch (exception) {
		dump(exception);
	}
    

	document.getElementById("serverStatus").label = "R3 Server Started";
}


//
// stops the server
// 
function stopInformationServer() {
	dumpLine(gBrowser.currentURI.spec);

	// close all output streams
	for (var i=0; i<clientOutputStreamArray.length;i++) {
		clientOutputStreamArray[i].stream.close();
	}

	if (serverSocket) {
		serverSocket.close();
	}
	document.getElementById("serverStatus").label = "R3 Server Stopped";
		
}



// Writes to the Firefox Console
// must set the following in about:config
// browser.dom.window.dump.enabled = true
// and enable the -console flag when running firefox.exe
function dumpLine(string) {
	dump(string + "\n");
}


//
// Writes some data to a new Browser Window
//
function writeInfoToFakeConsole() {
	var file = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
	writeConsole(file);
	writeConsole(document.title);
	writeConsole(document.location.href);
	
	writeConsole(top.location.href);

	if (file instanceof Components.interfaces.nsILocalFile) {
		writeConsole("Instance Of!");	
		
		try {
			file.initWithPath("c:\\\\contentScraperConsole.log");
			writeConsole("No Error!");
		} catch (error) {
			writeConsole("Error: " + error);
		}
	}
}




//
// a simple console window
//
var consoleString = "";
var header = "<html><head><title>Debug Console</title></head><body><div style=\"font-family: 'Trebuchet MS', Verdana, Arial, monospace; font-size: 14px; font-weight: normal;\">";
var footer = "</body></html>";
function writeConsole(content) {
	if (top.consoleRef == null || top.consoleRef.closed) {
		top.consoleRef=window.open('','myconsole', 'width=350,height=800,menubar=0,toolbar=0,status=0,scrollbars=1,resizable=1');
	}
	
	var console = top.consoleRef.document;
	consoleString = consoleString + content + "<br/>";

	console.writeln(header);
	console.writeln(consoleString);
	console.writeln(footer);
	
	// scroll to the bottom
	top.consoleRef.scroll(0, top.consoleRef.scrollMaxY);
	console.close();
}



initializeContentServer();