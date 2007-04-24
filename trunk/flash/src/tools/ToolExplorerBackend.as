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
	
	
	// Helps developers navigate the Paper Toolkit visually...
	public class ToolExplorerBackend extends Sprite {
		
		// Strings used for Message Passing with Java, and for Flex GUI States
		public static const DESIGN_MODE:String = "Design";
		public static const CODE_AND_DEBUG_MODE:String = "Code and Debug";
		public static const PAPER_UI_MODE:String = "Paper UI";
		public static const TOOLBOX_MODE:String = "Toolbox";
		public static const API_MODE:String = "API Explorer";
		public static const MAIN_MENU_MODE:String = "Welcome Screen";
		public static const WHITEBOARD_MODE:String = "Whiteboard";
		
		private var stageObj:Stage;
		private var window:NativeWindow;
		private var app:ToolExplorer;
		private var designTool:DesignTools;

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

		public function selectTool():void {
			app.currentState=app.toolList.selectedItem.data;
		}

		private function setupToolList():void {
			var toolsArr:Array = new Array();
			toolsArr.push({label:MAIN_MENU_MODE, data:""});
			toolsArr.push({label:DESIGN_MODE, data:DESIGN_MODE});
			toolsArr.push({label:API_MODE, data:API_MODE});
			toolsArr.push({label:CODE_AND_DEBUG_MODE, data:CODE_AND_DEBUG_MODE});
			toolsArr.push({label:PAPER_UI_MODE, data:PAPER_UI_MODE});
			toolsArr.push({label:TOOLBOX_MODE, data:TOOLBOX_MODE});
			toolsArr.push({label:WHITEBOARD_MODE, data:WHITEBOARD_MODE});
			app.toolList.dataProvider = toolsArr;
		}

		// this is called after the command line arguments are processed
		private function start():void {
			// toggleFullScreen();
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addMessageListener(msgListener);
		}

        private function msgListener(event:DataEvent):void {
	        var message:XML = new XML(event.text);
	        var msgName:String = message.name();
	        // trace(message.toXMLString());
	        // trace("Message Name: " + msgName);
			if (app.currentState == DESIGN_MODE) {
		        // pass ink samples to the design tools data handler
				designTool.backend.processMessage(event.text);
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
	        	trace("Unhandled: " + message.toXMLString());
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
		    		
		    		if (arg.indexOf("port:") > -1) {
		    			portNum = parseInt(arg.substr(arg.indexOf("port:")+5));
		    			trace("Port: " + portNum);
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
			app.currentState = DESIGN_MODE;
			javaBackend.send(DESIGN_MODE);
		}
		public function directionsClicked():void {
			app.currentState = API_MODE;
			javaBackend.send(API_MODE);
		}
		public function stickiesClicked():void {
			app.currentState = PAPER_UI_MODE;
			javaBackend.send(PAPER_UI_MODE);
		}
		public function toolboxClicked():void {
			app.currentState = TOOLBOX_MODE;
			javaBackend.send(TOOLBOX_MODE);
		}
		public function yinyangClicked():void {
			app.currentState = CODE_AND_DEBUG_MODE;
			javaBackend.send(CODE_AND_DEBUG_MODE);
		}
		public function backButtonClicked():void{
			app.currentState="";
			javaBackend.send(MAIN_MENU_MODE);
		}
		
		
		// Setters for components that we can access from our MXML.
		public function set designToolPanel(designToolPanel:DesignTools):void{
			designTool = designToolPanel;
		}

		// event handler
		private function windowCloseHandler(e:Event):void {
			trace("Window Closing.");
			exit();
		}
	}
}