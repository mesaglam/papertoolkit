package tools {
	
	import flash.display.Stage;
	import flash.display.Sprite;
	import flash.display.StageDisplayState;
	import flash.events.InvokeEvent;
	import flash.filesystem.File;
	import flash.net.navigateToURL;
	import flash.net.URLRequest;
	
	import java.JavaIntegration;
	import flash.events.DataEvent;
	import flash.events.Event;
	import components.DesignTools;
	import components.Whiteboard;
	import flash.display.LoaderInfo;
	
	
	// Helps developers navigate the Paper Toolkit visually...
	public class ToolWrapperBackend extends Sprite {
		
		// Strings used for Message Passing with Java, and for Flex GUI States
		public static const DESIGN_MODE:String = "Design";
		public static const CODE_AND_DEBUG_MODE:String = "Code and Debug";
		public static const PAPER_UI_MODE:String = "Paper UI";
		public static const TOOLBOX_MODE:String = "Toolbox";
		public static const API_MODE:String = "API Explorer";
		public static const MAIN_MENU_MODE:String = "Welcome Screen";
		public static const WHITEBOARD_MODE:String = "Whiteboard";
		
		private var stageObj:Stage;
		private var app:ToolWrapper;

		// the mode to start in... 
		private var modeName:String;

		// the port that the Java back end is listening on
		private var portNum:int;
		private var javaBackend:JavaIntegration;

		private var theTool:Tool;

		// constructor
		public function ToolWrapperBackend(appObj:ToolWrapper):void {
			app = appObj;
			processParameters();
			start();
		}			

		public function set tool(t:Tool):void {
			theTool = t;
			theTool.javaBackend = javaBackend;
			theTool.showExitButton();
		}

		// this is called after the command line arguments are processed
		private function start():void {
			// toggleFullScreen();
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addMessageListener(msgListener);
			trace("Created Java Connection.");
		}

		// handle messages
        private function msgListener(event:DataEvent):void {
	        var message:XML = new XML(event.text);
	        var msgName:String = message.name();
	        trace(message.toXMLString());
	        // trace("Message Name: " + msgName);
			theTool.processMessage(event.text);
		}


		// retrieve parameters from the host HTML page
		private function processParameters():void {
			// for storing the parameters
			var paramObj:Object;

			try {
				var keyStr:String;
				var valueStr:String;
				paramObj = LoaderInfo(app.root.loaderInfo).parameters;
				for (keyStr in paramObj) {
					valueStr = String(paramObj[keyStr]);
					trace(keyStr + ":\t" + valueStr);
					if (keyStr=="port") {
						portNum = parseInt(valueStr);
					}
				}
			} catch (error:Error) {
				trace(error);
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
		}
	}
}