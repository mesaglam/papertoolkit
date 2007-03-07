package {		
	
	import flash.display.Sprite;
	import ink.Ink;
	import flash.net.URLLoader;
	import flash.events.Event;
	import flash.net.URLRequest;
	
	public class InkViz extends Sprite {
	
		private var ink1:Ink = new Ink();
		private var ink2:Ink = new Ink();
		
		private var path:String = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\PaperToolkit\\penSynch\\data\\XML\\2007_02_28__00_26_54_SheetAndOneRegion.xml";
		private var path2:String = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\PaperToolkit\\penSynch\\data\\XML";

		public function InkViz() {			



			trace("InkViz Constructor");
			trace(path);
			
			this.addChild(ink1);
			
			ink2.x = 20;
			
			this.addChild(ink2);
			
			// load some XML
			var loader:URLLoader = new URLLoader();
			loader.addEventListener(Event.COMPLETE, loadXML);
			loader.load(new URLRequest(path));
		}
		
		
		private function loadXML(e:Event):void {
			var xml:XML = new XML(e.target.data);
			trace(xml);
		}
	}
}