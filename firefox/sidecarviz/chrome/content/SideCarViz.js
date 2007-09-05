var SideCarViz = {
	clipboard : null,
	clipboardHelper : null,
	
	init: function () {
		this.clipboard = Cc["@mozilla.org/widget/clipboard;1"].getService(Ci.nsIClipboard); // the system clipboard
		this.clipboardHelper = Components.classes["@mozilla.org/widget/clipboardhelper;1"].getService(Components.interfaces.nsIClipboardHelper);

		window.addEventListener('load', this.addJavaScripts, true);
	},
	
	copyToSideCar : function() {
		var selection = content.document.getSelection(); // what is selected in the window!
		this.copyString(selection);
		this.notifyListenersOfNewClipboardContents();
		// document.location --> chrome://browser/content/browser.xul
		// content.document.location --> the actual showing page's URL
	},
	
	getClipboardContents : function() {
		// create the transferable
		// http://developer.mozilla.org/en/docs/Using_the_Clipboard
		var trans = Components.classes["@mozilla.org/widget/transferable;1"].createInstance();
		if (trans) {
			trans = trans.QueryInterface(Components.interfaces.nsITransferable);
		}
		
		if (trans && this.clipboard ) {
			// register the data flavor you want
			trans.addDataFlavor("text/unicode");
			
			// get transferable from clipboard
			this.clipboard.getData(trans, this.clipboard.kGlobalClipboard);
			var dataObj = new Object();
			var len = new Object();
			trans.getTransferData("text/unicode", dataObj, len);			
			
			  
			if (dataObj) {
		    	dataObj = dataObj.value.QueryInterface(Components.interfaces.nsISupportsString);
		    }
		    if (dataObj) {
		      // ...do something with the data. remember len is in bytes, not chars, so we divide by 2 to turn unicode into utf-8
		      var dataStr = dataObj.data.substring(0, len.value / 2); 
		      return dataStr;
		    }
		}		
		return "";
	},

	// send this to the SideCar Eclipse Plugin / SideCar Flex Application
	notifyListenersOfNewClipboardContents : function() {
		println("SC::ClipboardContentsChanged url:[[" + content.document.location + "]] contents:[[" + this.getClipboardContents() + "]]");
	},
	
	notifyListenersOfNewTab : function() {
		println("SC::NewTab");
	},
	
	notifyListenersOfNewPageURL : function(url) {
		println("SC::NewPage url:[["+url+"]]");
	},
	
	notifyListenersOfTyping : function(str) {
		println("SC::UserTyped text:[["+str+"]]");
	},

	startSideCar : function() {
		// question, can we access the HTML's DOM?
		// if we can, we can potentially talk to Flex!!! Damn... =)
	},


	// this is called many times, as one page "load" may fire multiple load events
	addJavaScripts : function() {
		// location.href --> chrome://browser/content/browser.xul
	
		// add a div to the document to show a tray of copied stuff?

	},
	
	openURL : function(url /*String*/) {
	    gBrowser.loadURI(url); // gBrowser --> the Firefox Browser
	},
	
	copyString : function(str) {
		this.clipboardHelper.copyString(str);
		// alert("Copied: " + str)
	}
};

function println(msg) {
	SCNetwork.sendMessageToAllListeners(msg + "\r\n");
}

SCNetwork.init();
SCHandlers.init();
SCFile.init();
SideCarViz.init();
