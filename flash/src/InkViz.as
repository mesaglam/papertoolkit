package {		
	
	import ink.Ink;
	import ink.InkXMLParser;
	

	import flash.display.Sprite;
	import flash.events.Event;
	
	public class InkViz extends Sprite {
	
		private var ink1:Ink = new Ink();
		private var ink2:Ink = new Ink();
		
		private var path1:String = pathParent + "2007_02_28__00_26_54_SheetAndOneRegion.xml";
		private var path2:String = pathParent + "2007_03_06__18_14_23_HelloHappyFaceB7.xml";

		private var pathParent:String = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\PaperToolkit\\penSynch\\data\\XML\\";

		public function InkViz() {			
			trace("InkViz Constructor");
			trace(path2);
						
			var inkXML:InkXMLParser = new InkXMLParser(path2);
			this.addChild(inkXML.ink);
			
		}
		
	}
}