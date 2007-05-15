package tools {
	
	import flash.display.Stage;
	import flash.display.Sprite;
	import flash.display.StageDisplayState;
	import flash.display.NativeWindow;
	import flash.system.Shell;
	import flash.events.InvokeEvent;
	import flash.filesystem.File;
	import flash.net.navigateToURL;
	import flash.net.URLRequest;
	
	import java.JavaIntegration;
	import flash.events.DataEvent;
	import flash.events.Event;
	import components.DesignTools;
	import components.Whiteboard;
	import java.Constants;
	
	
	// Helps developers navigate the Paper Toolkit visually...
	public class ToolExplorerBackend extends Sprite {
		
		private var stageObj:Stage;
		private var window:NativeWindow;
		private var app:ToolExplorer;

		// the mode to start in... 
		private var modeName:String;

		// the port that the Java back end is listening on
		private var portNum:int;
		private var javaBackend:JavaIntegration;

		// constructor
		public function ToolExplorerBackend(appObj:ToolExplorer):void {
			app = appObj;
			app.window.addEventListener(Event.CLOSE, windowCloseHandler);
			window = app.window;		
			stageObj = window.stage;
			window.width = 1280;
			window.height = 720;
			addListenerForCommandLineArguments();
			setupToolList();
		}			


		// this is called after the command line arguments are processed
		private function start():void {
			// toggleFullScreen();
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addConnectListener(connectListener);
			javaBackend.addMessageListener(msgListener);
			
			// set the mode
			if (modeName == Constants.WHITEBOARD_MODE) {
				whiteBoardClicked();
			}
		}

        private function connectListener(event:Event):void {
			// notify java that we have started
	        javaBackend.send("Connected");
        }


		// handle messages
        private function msgListener(event:DataEvent):void {
	        //trace(event.text);
	        
	        var message:XML = new XML(event.text);
	        var msgName:String = message.name();
	        // trace(message.toXMLString());
	        // trace("Message Name: " + msgName);
			
			if (app.currentState == Constants.DESIGN_MODE) {
		        // pass ink samples to the design tools data handler
				app.designToolPanel.processMessage(event.text);
			} else if (app.currentState == Constants.WHITEBOARD_MODE) {
				app.whiteBoardPanel.processMessage(event.text);
			} else if (app.currentState == Constants.API_MODE) {
				app.apiBrowserPanel.processMessage(event.text);
			} else if (msgName == "pens") {
				// get all the pens and populate the combo box
				var pensXML:XMLList = message.descendants("pen");
				// trace(pensXML.toXMLString());
				var pens:Array = new Array();
				for each (var pen:XML in pensXML) {
					//trace(stroke);
					var penItem:Object = new Object();				
					penItem.label = pen.@name + ":" + pen.@port;
					penItem.data = pen.@server + ":" + pen.@port;
					penItem.name = pen.@name;
					penItem.server = pen.@server;
					penItem.port = pen.@port;
					pens.push(penItem);
				}
				app.penList.dataProvider = pens;
			} else {
				trace("ToolExplorerBackend: Message Unhandled [" + message.toXMLString() + "]");
			}
		}

		// Switches between full screen and restored window state.
		public function toggleFullScreen():void {
			trace("toggleFullScreen");
			if (stageObj.displayState == StageDisplayState.FULL_SCREEN) {
				stageObj.displayState = StageDisplayState.NORMAL;
			} else {
				stageObj.displayState = StageDisplayState.FULL_SCREEN;
			}
		}

		// Exits the Application...		
		public function exit():void {
			javaBackend.send("exitServer");
			Shell.shell.exit();
		}
		
		public function addListenerForCommandLineArguments():void {
			Shell.shell.addEventListener(InvokeEvent.INVOKE, processCommandLineArguments);
		}

		// process the command line arguments
		public function processCommandLineArguments(invocation:InvokeEvent):void{
			var arguments:Array;
			var currentDir:File;
			
			arguments = invocation.arguments;
		    currentDir = invocation.currentDirectory;
		    
			trace("Current Directory: " + currentDir.nativePath);
		    if (arguments.length > 0) {
		    	// trace(arguments);
		    	for each (var arg:String in arguments) {
		    		trace(arg);
		    		
		    		if (arg.indexOf("port:") == 0) {
		    			portNum = parseInt(arg.substr(arg.indexOf("port:")+5));
		    			trace("Port: " + portNum);
		    		}
		    		if (arg.indexOf("mode:") == 0) {
		    			modeName = arg.substr(arg.indexOf("mode:")+5);
		    			trace("Mode: " + modeName);
		    		}
		    	}
		    }
		    start();
		}

		// handlers for Flex GUI buttons
		public function browseToAuthorWebsite():void {
			navigateToURL(new URLRequest("http://graphics.stanford.edu/~ronyeh"));			
		}
		public function browseToHCIWebsite():void {
			navigateToURL(new URLRequest("http://hci.stanford.edu/"));			
		}
		public function browseToDocumentationWebsite():void {
			navigateToURL(new URLRequest("http://hci.stanford.edu/paper/documentation/"));			
		}

		
		//
		// A bunch of handlers for GUI buttons that invoke something in Java-land.
		// 
		public function designClicked():void {
			// communicate which pen is currently selected
			var penObj:Object = app.penList.selectedItem;
			if (penObj != null) {
				javaBackend.send("<pen name='"+penObj.name+"' server='"+penObj.server+"' port='"+penObj.port+"'/>");
			}

			// say that we are in design mode
			app.currentState = Constants.DESIGN_MODE;
			javaBackend.send(Constants.DESIGN_MODE);
		}
		public function apiExplorerClicked():void {
			app.currentState = Constants.API_MODE;
			app.apiBrowserPanel.tool.javaBackend = javaBackend;
		}
		public function paperUIsClicked():void {
			app.currentState = Constants.PAPER_UI_MODE;
			javaBackend.send(Constants.PAPER_UI_MODE);
		}
		public function toolboxClicked():void {
			app.currentState = Constants.TOOLBOX_MODE;
			javaBackend.send(Constants.TOOLBOX_MODE);
		}
		public function codeAndDebugClicked():void {
			app.currentState = Constants.CODE_AND_DEBUG_MODE;
			javaBackend.send(Constants.CODE_AND_DEBUG_MODE);
		}
		public function whiteBoardClicked():void{
			app.currentState=Constants.WHITEBOARD_MODE;
			app.whiteBoardPanel.tool.javaBackend = javaBackend;
			// the whiteboard component sends the start message
		}
		public function backButtonClicked():void{
			app.currentState="";
			javaBackend.send(Constants.MAIN_MENU_MODE);
		}
		
		// Event Handler for the event save and replay button in the toolboxj
		public function eventSaveAndReplayClicked():void{
			app.currentState=Constants.EVENT_SAVE_AND_REPLAY_MODE;
			// TODO: like whiteboard, we should set the javaBackend
			javaBackend.send(Constants.EVENT_SAVE_AND_REPLAY_MODE);
		}


		public function selectTool():void {
			app.toolList.selectedItem.data();
		}

		private function setupToolList():void {
			var toolsArr:Array = new Array();
			toolsArr.push({label:Constants.MAIN_MENU_MODE, data:backButtonClicked});
			toolsArr.push({label:Constants.DESIGN_MODE, data:designClicked});
			toolsArr.push({label:Constants.API_MODE, data:apiExplorerClicked});
			toolsArr.push({label:Constants.CODE_AND_DEBUG_MODE, data:codeAndDebugClicked});
			toolsArr.push({label:Constants.PAPER_UI_MODE, data:paperUIsClicked});
			toolsArr.push({label:Constants.TOOLBOX_MODE, data:toolboxClicked});
			toolsArr.push({label:Constants.WHITEBOARD_MODE, data:whiteBoardClicked});
			app.toolList.dataProvider = toolsArr;
		}
		
		// event handler
		private function windowCloseHandler(e:Event):void {
			trace("Window Closing.");
			exit();
		}
	}
}