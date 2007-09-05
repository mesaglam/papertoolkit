var SCHandlers = {
	controlKeyIsDown : false,
	altKeyIsDown : false,
	clipboardChanged : false,
	currentURL : "", // a string containing the current document's URL
	currentData : "",
		
	init: function () {
		// alert("SCHandlers: " + this); // this is an SCHandlers object
		
		window.addEventListener("keyup", this.keyUp, false);
		window.addEventListener("keydown", this.keyDown, false);
		window.addEventListener("load", this.loadFirefox, true); // true --> dispatches multiple events 
		window.addEventListener("unload", this.unloadFirefox, false);
	},
	
	loadFirefox : function(e) {
		// check the URL, if it has changed, then pass it along
		if (SCHandlers.currentURL != content.document.location){
			SCHandlers.currentURL = content.document.location;
			
			if (SCHandlers.currentURL == "about:blank") {
				SideCarViz.notifyListenersOfNewTab();
			} else {
				SideCarViz.notifyListenersOfNewPageURL(content.document.location);
			}
		}
	},
	unloadFirefox : function(e) {
		// alert("Unload: " + e);
	},
	
	keyDown : function(e) {
		switch (e.keyCode) {
			case 67: // C
				if (SCHandlers.controlKeyIsDown) {
					// we copied something!
					SCHandlers.clipboardChanged = true;
				}
				break;
			case 88: // X
				if (SCHandlers.controlKeyIsDown) {
					// we cut something!
					SCHandlers.clipboardChanged = true;
				}
				break;
			case 17: // CTRL
				SCHandlers.controlKeyIsDown = true;
				break;
			case 18: // ALT
				SCHandlers.altKeyIsDown = true;
				break;
			
			default:
				// println("Unhandled Key Down: " + e.keyCode);
				break;
		}
	},
	
	// can't access "this" inside a handler... :-(
	keyUp : function(e) {
		switch (e.keyCode) {
			case 18: // ALT
				SCHandlers.altKeyIsDown = false;
				SCHandlers.segmentTyping();
				break;
			case 17: // CTRL
				// alert(this); // Chrome Window
				if (SCHandlers.clipboardChanged) {
					// we have copied or cut something
					// tell the listeners that the clipboard content has changed
					SideCarViz.notifyListenersOfNewClipboardContents();
					SCHandlers.clipboardChanged = false;
				}
				SCHandlers.controlKeyIsDown = false;
				SCHandlers.segmentTyping();
				break;
			case 13: // ENTER
				SCHandlers.segmentTyping();
				break;
			default:
				if (!SCHandlers.controlKeyIsDown && !SCHandlers.altKeyIsDown) {
					// println("Key Up: " + e.keyCode);
					SCHandlers.currentData += String.fromCharCode(e.keyCode);
				}
				break;
		}

	},
	
	// TODO: should also call this when the window loses focus, or something...
	segmentTyping : function() {
		// break up our typing into chunks with enter or control keys...
		if (SCHandlers.currentData != "") {
			SideCarViz.notifyListenersOfTyping(SCHandlers.currentData);
			SCHandlers.currentData = "";
		}
	}
};