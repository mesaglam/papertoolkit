package {		
	import ink.Ink;
	import ink.InkXMLParser;
	import ink.HighlightInkXMLParser;

	import flash.display.Sprite;
	import flash.display.LoaderInfo;
	import flash.events.Event;
	
	public class InkViz extends Sprite {
		
		// the actual file we will load
		private var path:String;

		// where to find the ink xml data files
		private var pathParent:String = "../../penSynch/data/XML/";

		// the list that specifies which strokes should be highlighted (through timestamps)
		private var highlightedStrokes:HighlightInkXMLParser; 
		
		public function InkViz() {			
			
			//trace("InkViz Constructor");
			//trace(path1);

			try {
				var keyStr:String;
				var valueStr:String;
				var paramObj:Object = LoaderInfo(this.root.loaderInfo).parameters;
				for (keyStr in paramObj) {
					valueStr = String(paramObj[keyStr]);
					//trace(keyStr + ":\t" + valueStr);
					
					if (keyStr == "fileName") {
						trace("File: " + valueStr);
						path = pathParent + valueStr;
					}
				}
			} catch (error:Error) {
				trace(error);
			}

			
			
			// avoid race conditions... by waiting for completion before moving on
			highlightedStrokes= new HighlightInkXMLParser("../data/highlightTheseStrokes.xml", onComplete);			
		}
		
		private function onComplete():void {
			if (path==null) {
				trace("Data path is null. Not loading any new XML today! We'll use old test data.");
				path = pathParent + "2007_03_10__01_09_38_SketchedPaperUI.xml";
			}
			var inkXML:InkXMLParser = new InkXMLParser(path, highlightedStrokes);
			this.addChild(inkXML.ink);
		}
		
		
		
	}
}