package tools {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import mx.controls.TextArea;
	import ink.Ink;
	import ink.InkStroke;

	
	public class DesignToolsBackend extends Sprite {
		
		private var inkWell:Ink;
		private var currInkStroke:InkStroke;
		
		public function DesignToolsBackend():void {
			inkWell = new Ink();
			addChild(inkWell);
			currInkStroke = new InkStroke();
		}
		
		public function recenter():void {
			inkWell.resetLocation();
		}

        private function dataHandler(event:DataEvent):void {
            //trace("dataHandler: " + event);
            // trace(event.text); // parse the text and assemble InkStrokes...
            
            var inkXML:XML = new XML(event.text);
            // trace("XML: " + inkXML.toXMLString());

			trace(inkXML.@x + " " + inkXML.@y + " " + inkXML.@f + " " + inkXML.@t + " " + inkXML.@p);

			var xVal:Number = parseFloat(inkXML.@x);
			var yVal:Number = parseFloat(inkXML.@y);

			var penUp:Boolean = inkXML.@p == "U";
			if (penUp) {
				// add to the inkWell
				inkWell.addStroke(currInkStroke);

				// reposition it to the minimum (with some padding) after each stroke
				inkWell.resetLocation();

				// start up a new stroke
   				currInkStroke = new InkStroke();
			} else {
				// add samples to the current stroke
				currInkStroke.addPoint(xVal, yVal, parseFloat(inkXML.@f));
			}		
        }
 	}
}