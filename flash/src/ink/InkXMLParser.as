package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.HTTPStatusEvent;
	import flash.events.MouseEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	

	public class InkXMLParser {
	
		private var inkWell:Ink = new Ink();
		private var xmlPath:String;
		private var highlightedStrokes:HighlightInkXMLParser;
		
		public function InkXMLParser(path:String, hStrokes:HighlightInkXMLParser) {			
			xmlPath = path;
			highlightedStrokes = hStrokes;
			
			// load some XML
			var loader:URLLoader = new URLLoader();
			loader.addEventListener(Event.COMPLETE, loadXML);
			loader.addEventListener(HTTPStatusEvent.HTTP_STATUS, httpStatusHandler);
			loader.load(new URLRequest(xmlPath));

			// wow, xml is a native type!?
			// The following is a legal statement:
			// var xml:XML = <foo>Baz</foo>;
			// trace(xml.toXMLString());
		}

		
		
		private function loadXML(e:Event):void {
			var xml:XML = new XML(e.target.data);
			//trace(xml);

			// get all the strokes (somewhere down the XML tree)
			var strokes:XMLList = xml.descendants("stroke");
			
			for each (var stroke:XML in strokes) {
				//trace(stroke);
			
				var points:XMLList = stroke..p;
				//trace(points.toXMLString());

				var inkStroke:InkStroke = new InkStroke(stroke.@begin, stroke.@end);				
				if (highlightedStrokes.shouldHighlight(inkStroke)) {
					trace("Highlighted!");
					inkStroke.highlighted = true;
				}
				
				for each (var point:XML in points) {
					//trace(point.@x + " " + point.@y);
					inkStroke.addPoint(point.@x, point.@y, point.@f);
				}
				inkWell.addStroke(inkStroke);
			}			
		}
		
		private function httpStatusHandler(event:HTTPStatusEvent):void {
            trace("httpStatusHandler: " + event);
        }
		
		public function get ink():Ink {
			return inkWell;
		}
	}
}