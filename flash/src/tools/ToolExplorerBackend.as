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
	import tools.DesignTools;
	import flash.events.Event;
	
	
	public class ToolExplorerBackend extends Sprite {
		
		public static const DESIGN_MODE:String = "DesignClicked";
		
		private var stageObj:Stage;
		private var window:NativeWindow;
		private var app:ToolExplorer;
		private var designTool:DesignTools;

		// the port that the Java back end is listening on
		private var portNum:int;
		private var javaBackend:JavaIntegration;

		public function ToolExplorerBackend(win:NativeWindow):void {
			window = win;		
			stageObj = win.stage;

			window.width = 1280;
			window.height = 720;

			addListenerForCommandLineArguments();
		}			


		// this is called after the command line arguments are processed
		private function start():void {
			// toggleFullScreen();
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addMessageListener(msgListener);
		}

        private function msgListener(event:DataEvent):void {
	        var message:XML = new XML(event.text);
	        //trace(message.toXMLString());
			if (app.currentState == DESIGN_MODE) {
		        // pass ink samples to the design tools data handler
				designTool.backend.processMessage(event.text);
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


		public function browseToAuthorWebsite():void {
			navigateToURL(new URLRequest("http://graphics.stanford.edu/~ronyeh"));			
		}
		public function browseToHCIWebsite():void {
			navigateToURL(new URLRequest("http://hci.stanford.edu/"));			
		}
		public function browseToDocumentationWebsite():void {
			navigateToURL(new URLRequest("http://hci.stanford.edu/paper/documentation/"));			
		}
		

		
		/**
		 * A bunch of handlers for GUI buttons that invoke something in Java-land.
		 */		
		public function designClicked():void {
			app.currentState = DESIGN_MODE;
			javaBackend.send("Design Clicked");
		}
		public function directionsClicked():void {
			app.currentState = "CompassClicked";
			javaBackend.send("Compass Clicked");
		}
		public function stickiesClicked():void {
			app.currentState = "StickiesClicked";
			javaBackend.send("Stickies Clicked");
		}
		public function toolboxClicked():void {
			app.currentState = "ToolboxClicked";
			javaBackend.send("Toolbox Clicked");
		}
		public function yinyangClicked():void {
			app.currentState = "YinYangClicked";
			javaBackend.send("YinYang Clicked");
		}
		public function backButtonClicked():void{
			app.currentState='';
			javaBackend.send("Back Clicked");
		}
		
		
		/**
		 * Setters for components that we can access from our MXML.
		 */
		public function setApp(appObj:ToolExplorer):void{
			app = appObj;
			app.window.addEventListener(Event.CLOSE, windowCloseHandler);
		}
		public function setDesignToolPanel(designToolPanel:DesignTools):void{
			designTool = designToolPanel;
		}

		private function windowCloseHandler(e:Event):void {
			trace("Window Closing.");
			exit();
		}
	}
}