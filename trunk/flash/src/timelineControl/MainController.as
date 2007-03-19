package timelineControl {
	import flash.display.MovieClip;

	// We instantiate a movie clip so that it can control the timeline.
	public class MainController extends MovieClip {
		private static var instance:MainController = null;

		public function MainController() {
			trace("MainController created");
			instance = this;
		}

		// not a true singleton, as AS3 isn't as awesome as Java
		public static function getInstance():MainController {
			return instance;
		}
	}
}