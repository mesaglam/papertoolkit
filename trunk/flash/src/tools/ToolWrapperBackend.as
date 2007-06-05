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
	import flash.display.DisplayObject;
	import components.APIBrowser;
	
	
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

		// the tool that this Flash object wraps
		private var theTool:Tool;

		// Based on this string, we select the correct component to load into this wrapper
		private var toolToLoad:String = "None";



		// constructor
		public function ToolWrapperBackend(appObj:ToolWrapper):void {
			app = appObj;
			processParameters();
			start();
		}			

		// sets the tool to use with this wrapper
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

			loadTool();
		}

		// Based on a string that is passed in, load the corresponding tool into this wrapper (Flash/HTML)
		// Alternatively, the tools can be selected in the ToolExplorer (Apollo Application)
		private function loadTool():void {
			trace("Loading Tool: " + toolToLoad);
			switch(toolToLoad) {
				// add support for different components here
				
				case "Whiteboard":
					trace("Adding a Whiteboard");
					var wb:Whiteboard = new Whiteboard();
					wb.setStyle("left", 0);
					wb.setStyle("top", 0);
					wb.setStyle("right", 0);
					wb.setStyle("bottom", 0);
					wb.toolWrapperBackend = this;
					app.addChild(wb);
				break;
				
				case "APIBrowser":
					trace("Adding the Ink API Browser");
					var apib:APIBrowser = new APIBrowser();
					apib.setStyle("left", 5);
					apib.setStyle("top", 5);
					apib.setStyle("right", 5);
					apib.setStyle("bottom", 5);
					apib.toolWrapperBackend = this;
					app.addChild(apib);
				break;				
				
				default:
				break;
			}			
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
					else if (keyStr=="toolToLoad") {
						toolToLoad = valueStr;
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