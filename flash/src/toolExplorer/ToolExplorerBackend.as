package toolExplorer {
	import flash.display.Stage;
	import flash.display.Sprite;
	import flash.display.StageDisplayState;
	import flash.display.NativeWindow;
	import flash.system.Shell;
	
	
	public class ToolExplorerBackend extends Sprite {
		
		private var stageObj:Stage;
		private var window:NativeWindow;


		public function ToolExplorerBackend(win:NativeWindow):void {
			window = win;		
			stageObj = win.stage;

			window.width = 1280;
			window.height = 720;

			toggleFullScreen();
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
	}
}