package {		
	import ink.Ink;
	import ink.InkXMLParser;

	import flash.display.Sprite;
	import flash.events.Event;
	
	public class InkViz extends Sprite {
		
		private var path1:String = pathParent + "2007_02_28__00_26_54_SheetAndOneRegion.xml";
		private var path2:String = pathParent + "2007_03_06__18_14_23_HelloHappyFaceB7.xml";
		private var path3:String = pathParent + "2007_03_07__11_39_19_SmallPaperUISketch.xml";

		private var pathParent:String = "../../penSynch/data/XML/";
		//private var pathParent:String = "../data/";

		public function InkViz() {			
			trace("InkViz Constructor");
			//trace(path1);
						
			var inkXML:InkXMLParser = new InkXMLParser(path3);
			this.addChild(inkXML.ink);
			
		}
		
	}
}