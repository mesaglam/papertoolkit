package {		
	
	import flash.display.Sprite;
	import ink.Ink;
	
	public class InkViz extends Sprite {
	
		private var ink1:Ink = new Ink();
		private var ink2:Ink = new Ink();
		
		private var path:String = "C:\Documents and Settings\Ron Yeh\My Documents\Projects\PaperToolkit\penSynch\data\XML";

		public function InkViz() {			

			trace("InkViz Constructor");
			trace(path);
			
			this.addChild(ink1);
			
			ink2.x = 20;
			
			this.addChild(ink2);
			
			// load some XML
//			var loader:
		}
	}
}