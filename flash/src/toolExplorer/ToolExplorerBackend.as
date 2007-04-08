package toolExplorer {
	import flash.display.Stage;
	import flash.display.Sprite;
	import flash.display.StageDisplayState;
	import flash.display.NativeWindow;
	import flash.system.Shell;
	import flash.events.InvokeEvent;
	import flash.filesystem.File;
	import flash.net.navigateToURL;
	import flash.net.URLRequest;
	
	
	public class ToolExplorerBackend extends Sprite {
		
		private var stageObj:Stage;
		private var window:NativeWindow;
		private var app:ToolExplorer;

		public function ToolExplorerBackend(win:NativeWindow):void {
			window = win;		
			stageObj = win.stage;

			window.width = 1280;
			window.height = 720;

			addListenerForCommandLineArguments();
		}			

		public function setApp(appObj:ToolExplorer):void{
			app = appObj;
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
			Shell.shell.exit();
		}
		
		public function addListenerForCommandLineArguments():void {
			Shell.shell.addEventListener(InvokeEvent.INVOKE, onInvokeEvent);
		}

		public function onInvokeEvent(invocation:InvokeEvent):void{
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
		    			var portNum:int = parseInt(arg.substr(arg.indexOf("port:")+5));
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
		
		private function start():void {
			// toggleFullScreen();
		}
		
		public function designClicked():void {
			app.currentState = "DesignClicked";
		}
		public function directionsClicked():void {
			app.currentState = "DirectionsClicked";
		}
		public function stickiesClicked():void {
			app.currentState = "StickiesClicked";
		}
		public function toolboxClicked():void {
			app.currentState = "ToolboxClicked";
		}
		public function yinyangClicked():void {
			app.currentState = "YinYangClicked";
		}
	}
}