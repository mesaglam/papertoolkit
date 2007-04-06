package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.HTTPStatusEvent;
	import flash.events.MouseEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	

	public class HighlightInkXMLParser {
	
		private var xmlPath:String;
		private var strokeTimestamps:Array = new Array();
		private var onComplete:Function;
		
		
		public function HighlightInkXMLParser(path:String, onCompleteFcn:Function) {			
			xmlPath = path;
			onComplete = onCompleteFcn;
			
			// load some XML
			var loader:URLLoader = new URLLoader();
			loader.addEventListener(Event.COMPLETE, loadXML);
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
				var ts:Number = stroke.@begin;
				//trace(ts);
				strokeTimestamps.push(ts);
			}	
			
			//trace(strokeTimestamps);
			//trace(strokeTimestamps.indexOf(1173517703839));

			onComplete();
		}
			
		public function get timestamps():Array {
			return strokeTimestamps;
		}
		
		public function shouldHighlight(stroke:InkStroke):Boolean {
			//trace("Highlight? " + stroke.beginTimestamp);
			return strokeTimestamps.indexOf(stroke.beginTimestamp) != -1;
		}
	}
}