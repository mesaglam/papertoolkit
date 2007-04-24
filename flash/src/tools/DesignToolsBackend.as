package tools {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import mx.controls.TextArea;
	import ink.Ink;
	import ink.InkStroke;
	import ink.InkUtils;
	import flash.geom.Rectangle;
	import mx.containers.Canvas;
	import components.DesignTools;
	
	// 
	public class DesignToolsBackend extends Sprite {
		
		private var inkWell:Ink;
		private var currInkStroke:InkStroke;
		private var theParent:DesignTools;
		private var inkCanvas:Canvas;

		// in case the numbers are too big, we should subtract them by this number
		// this number is set by the first sample that comes in that uses scientific notation
		private var xMinOffset:Number = -1;
		private var yMinOffset:Number = -1;

		// constructor		
		public function DesignToolsBackend():void {
			if (inkWell != null) {
				removeChild(inkWell);
			}
		}
		
		public function resetInk():void {
			inkWell = new Ink();
			addChild(inkWell);
			currInkStroke = new InkStroke();
		}
		
		// provides access to the mxml components
		public function setParent(p:DesignTools):void {
			theParent = p;
			inkCanvas = theParent.inkCanvas;
		}

		// processes the xml text
        public function processMessage(msgText:String):void {
        	if (inkWell==null) {
				resetInk();
        	}

			// DEBUG
			// Draw a box at the inkCanvas rectangle, because that is where we will display our ink
			if (false) {
				graphics.clear();
				graphics.lineStyle(1, 0x654565);
	        	graphics.drawRect(inkCanvas.x, inkCanvas.y, inkCanvas.width, inkCanvas.height);
			}
			
            //trace("dataHandler: " + event);
            // trace(msgText); // parse the text and assemble InkStrokes...
            var msg:XML = new XML(msgText);
            
            // this whole switching thing isn't the smartest...
            // perhaps we should get the node name of the XML?
            // because as it stands, <p would mess us up if it came
            // before penDownEvent
            
			var msgName:String = msg.name();
            
			if (msgName=="penDownEvent") {
	   			trace("Pen Down");
				// start up a new stroke
   				currInkStroke = new InkStroke();
   				// add it to the stage, but don't consider it "added"
   				inkWell.startPreview(currInkStroke);
   			} else if (msgName=="p") {
   				// trace("Handling Ink");
				handleInk(msgText);

	   			// recenter the cluster so that it is visible for us...
	   			inkWell.recenterMostRecentCluster(new Rectangle(inkCanvas.x, inkCanvas.y, 
	   															inkCanvas.width, inkCanvas.height));

	   		} else if (msgName=="penUpEvent") {
	   			trace("Pen Up");
	   			
	   			// rerender the last stroke with curves
	   			// if the large jump would otherwise cause chunky looking curves
	   			if (currInkStroke.isLargeJump) {
	   				currInkStroke.rerenderWithCurves();
	   			}
	   			
	   			
	   			// recognize 
	   			/*
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
	   			*/
	   		}
        }
        
        // deal with the ink samples
        // create ink objects, and render them to screen...
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
				trace("Handling Pen Up");
				
				// remove the temporary InkStroke child
				inkWell.stopPreview();
				// add it more permanently
				inkWell.addStroke(currInkStroke);
				
				// penUps are a duplicate of the last regular sample
				// so, we do nothing, but possibly reposition it to the minimum 
				// (with some padding) after each stroke
				//inkWell.recenterMostRecent(theParent.inkCanvas);
			} else {
				// add samples to the current stroke
				currInkStroke.addPoint(xVal, yVal, parseFloat(inkXML.@f));
			}	
		}

	}
}