package {
	import flash.display.MovieClip;

	public class MainController extends MovieClip {
		private static var instance:MainController = null;

		public function MainController() {
			trace("Hello World");
			instance = this;
		}

		// not a true singleton, as AS3 isn't as awesome as Java
		public static function getInstance():MainController {
			return instance;
		}
	}
}