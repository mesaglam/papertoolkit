package tools {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import mx.controls.TextArea;
	import ink.Ink;
	import ink.InkStroke;
	import ink.InkUtils;
	
	public class DesignToolsBackend extends Sprite {
		
		private var inkWell:Ink;
		private var currInkStroke:InkStroke;
		private var theParent:DesignTools;

		// in case the numbers are too big, we should subtract them by this number
		// this number is set by the first sample that comes in that uses scientific notation
		private var xMinOffset:Number = -1;
		private var yMinOffset:Number = -1;

		// constructor		
		public function DesignToolsBackend():void {
			resetInk();
		}
		
		public function resetInk():void {
			if (inkWell != null) {
				removeChild(inkWell);
			}
			inkWell = new Ink();
			addChild(inkWell);
			currInkStroke = new InkStroke();
		}
		
		// provides access to the mxml components
		public function setParent(p:DesignTools):void {
			theParent = p;
		}

		public function recenter():void {
			inkWell.resetLocation();
		}

        public function processMessage(msgText:String):void {
            //trace("dataHandler: " + event);
            // trace(msgText); // parse the text and assemble InkStrokes...
            var msg:XML = new XML(msgText);
            
            // this whole switching thing isn't the smartest...
            // perhaps we should get the node name of the XML?
            // because as it stands, <p would mess us up if it came
            // before penDownEvent
            
			var msgName:String = msg.name();
            
			if (msgName=="penDownEvent") {
				// start up a new stroke
   				currInkStroke = new InkStroke();
   				// add it to the stage
				inkWell.addChild(currInkStroke);
   			} else if (msgName=="p") {
   				trace("Handling Ink");
				handleInk(msgText);
	   		} else if (msgName=="penUpEvent") {
	   			trace("Pen Up");
	   			// recognize
	   			
	   			var paperUI:XML = 
		        <sheet label="Paper UI">
			        <region label="Button">
				        <handler label="Click">
				        </handler>    
			        </region>    
			        <region label="Ink Input">
				        <handler label="Ink Collector">
				        </handler>    
			        </region>    
		        </sheet>;
	   			
	   			theParent.paperUIComponents.dataProvider = new XMLList(paperUI);
	   		}
        }
        
		private function handleInk(xmlTxt:String):void {
            var inkXML:XML = new XML(xmlTxt);
            // trace("XML: " + inkXML.toXMLString());
			// trace(inkXML.@x + " " + inkXML.@y + " " + inkXML.@f + " " + inkXML.@t + " " + inkXML.@p);

			var xVal:Number = 0;
			var xStr:String = inkXML.@x;
			xVal = InkUtils.getCoordinateValueFromString(xStr);
			// Figure out a minimum offset to reduce these large numbers!
			if (xMinOffset == -1) { // uninitialized
				xMinOffset = xVal;
			}
			xVal = xVal - xMinOffset;

			var yStr:String = inkXML.@y;
			var yVal:Number = 0;
			yVal = InkUtils.getCoordinateValueFromString(yStr);
			// Figure out a minimum offset to reduce these large numbers!
			if (yMinOffset == -1) { // uninitialized
				yMinOffset = yVal;
			}
			yVal = yVal - yMinOffset;

			// trace(xVal + ", " + yVal);

			var penUp:Boolean = inkXML.@p == "U";
			if (penUp) {
				inkWell.removeChild(currInkStroke);
				inkWell.addStroke(currInkStroke);
				
				// penUps are a duplicate of the last regular sample
				// so, we do nothing, but possibly reposition it to the minimum 
				// (with some padding) after each stroke
				inkWell.recenterMostRecent(theParent.stage);
			} else {
				// add samples to the current stroke
				currInkStroke.addPoint(xVal, yVal, parseFloat(inkXML.@f));
			}	
		}

	}
}