package apiBrowser {
	
	import flash.events.MouseEvent;
	import flash.display.StageDisplayState;
	import flash.display.Stage;	
	
	public class APIBrowserBackend {

		// whether or not we are in fullscreen mode
		private var fullScreen:Boolean = false;

		// the Adobe flash stage
		private var stage:Stage;

		public function APIBrowserBackend(stg:Stage):void {
			stage = stg;
		}

		public function copyCodeHandler(event:MouseEvent):void {
			trace("copyCode Click Handler");
		}
		
		//
		//			
		private function toggleFullScreen(event:MouseEvent):void {
			trace("toggleFullScreen");
			if (fullScreen) {
				stage.displayState = StageDisplayState.NORMAL;
			} else {
				stage.displayState = StageDisplayState.FULL_SCREEN;
			}
			fullScreen = !fullScreen;
		}


		public function test():void {
			trace("APIBrowserBackend test!");
		}
	}
}