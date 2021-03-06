package ink {		
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.HTTPStatusEvent;
	import flash.events.MouseEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import mx.core.UIComponent;
	

	// a local URL may look like this:
	// file:///C:/Documents%20and%20Settings/Ron%20Yeh/My%20Documents/Projects/PaperToolkit/flash/src/InkVizDefault.html?fileName=2007_03_10__01_09_38_SketchedPaperUI.xml
	// but once it is parsed by InkViz... it just prepends ../../penSynch/data/XML/ to the fileName
	public class InkRawXMLParser {
	
		private var inkWell:Ink = new Ink();
		
		private var inkColor:uint;
		private var inkWidth:Number;
		
		public function InkRawXMLParser(xmlData:XML, 
										inkColorVal:uint=0xDADADA, inkWidthVal:Number=0.8):void {			
			inkColor = inkColorVal;
			inkWidth = inkWidthVal;
			processXML(xmlData);
		}

		private function processXML(xml:XML):void {
			trace("InkRawXMLParser: " + xml.@begin + ", " + xml.@end);
			
			// get all the strokes (somewhere down the XML tree)
			var strokes:XMLList = xml.descendants("stroke");
			
			for each (var stroke:XML in strokes) {
				//trace(stroke);
			
				var points:XMLList = stroke..p;
				//trace(points.toXMLString());

				var inkStroke:InkStroke = new InkStroke(stroke.@begin, stroke.@end);				
				
				inkStroke.inkWidth = inkWidth;
				inkStroke.inkColor = inkColor;
				
				for each (var point:XML in points) {
					//trace(point.@x + " " + point.@y);
					inkStroke.addPoint(point.@x, point.@y, point.@f);
				}
				inkWell.addStroke(inkStroke);
			}			
		}
		
		public function get ink():Ink {
			return inkWell;
		}
	}
}