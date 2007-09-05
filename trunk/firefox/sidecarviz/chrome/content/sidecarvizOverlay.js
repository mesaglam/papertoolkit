var SideCarViz = {
	init: function () {
		this.clipboard = Cc["@mozilla.org/widget/clipboard;1"].getService(Ci.nsIClipboard); // the system clipboard
		window.addEventListener('load', addJavaScripts, true);
	},
	
	startSideCar: function() {
	    gBrowser.loadURI("http://kotaku.com/");
	},
	
	copyToSideCar : function() {
		var selection = content.document.getSelection(); // what is selected in the window!
		
		this.startSideCar();
		
		// document.location --> chrome://browser/content/browser.xul
		// content.document.location --> the actual showing page's URL
	},

	startSideCar : function() {
		// question, can we access the HTML's DOM?
		// if we can, we can potentially talk to Flex!!! Damn... =)
		alert("Starting SideCar XXX");
	},


	// this is called many times, as one page "load" may fire multiple load events
	addJavaScripts : function() {
		// location.href --> chrome://browser/content/browser.xul
	
		// add a div to the document to show a tray of copied stuff?

	}
};

SideCarViz.init();