package tools {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import mx.controls.TextArea;
	import ink.Ink;
	import ink.InkStroke;
	import flash.display.Stage;

	
	public class WhiteboardBackend extends Sprite {
		
		private var inkWell:Ink;
		
		private var sock:XMLSocket;
		private var debugText:TextArea;

		private var currInkStroke:InkStroke;
		
		// in case the numbers are too big, we should subtract them by this number
		// this number is set by the first sample that comes in that uses scientific notation
		private var xMinOffset:Number = -1;
		private var yMinOffset:Number = -1;
		
		private var theParent:Whiteboard = null;
		
		public function WhiteboardBackend(dbt:TextArea):void {
			inkWell = new Ink();
			addChild(inkWell);
			
			currInkStroke = new InkStroke();
			
			debugText = dbt;
			trace(dbt);
			trace("Ink Client Started.");
			startListening();
		}
		
 		public function startListening():void {
			sock = new XMLSocket();
			configureListeners(sock);
			sock.connect("localhost", 6544);
		}

		public function setParent(p:Whiteboard):void {
			theParent = p;
		}

        private function configureListeners(dispatcher:IEventDispatcher):void {
            dispatcher.addEventListener(Event.CLOSE, closeHandler);
            dispatcher.addEventListener(Event.CONNECT, connectHandler);
            dispatcher.addEventListener(DataEvent.DATA, dataHandler);
            dispatcher.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
            dispatcher.addEventListener(ProgressEvent.PROGRESS, progressHandler);
            dispatcher.addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
        }

        private function closeHandler(event:Event):void {
            trace("closeHandler: " + event);
        }

        private function connectHandler(event:Event):void {
            trace("connectHandler: " + event);
        }

		public function recenter():void {
			inkWell.recenter();
		}
		
		public function recenterMostRecent(stage:Stage):void {
			inkWell.recenterMostRecent(stage);
		}
		

        private function dataHandler(event:DataEvent):void {
            //trace("dataHandler: " + event);
            // trace(event.text); // parse the text and assemble InkStrokes...
            
            var inkXML:XML = new XML(event.text);
            // trace("XML: " + inkXML.toXMLString());

			trace(inkXML.@x + " " + inkXML.@y + " " + inkXML.@f + " " + inkXML.@t + " " + inkXML.@p);



			var xStr:String = inkXML.@x;
			var xExp:String = "";
			var xEIndex:int = xStr.indexOf("E");
			var xVal:Number = 0;
			// handle scientific notation
			if (xEIndex > -1) {
				xExp = xStr.substr(xEIndex+1);
				xStr = xStr.substr(0, xEIndex);
				xVal = parseFloat(xStr)*Math.pow(10, parseInt(xExp)); // scientific notation

				// Figure out a minimum offset to reduce these large numbers!
				if (xMinOffset == -1){
					// uninitialized
					xMinOffset = xVal;
				}

				xVal = xVal - xMinOffset;
			} else {
				xVal = parseFloat(inkXML.@x);				
			}
			

			var yStr:String = inkXML.@y;
			var yExp:String = "";
			var yEIndex:int = yStr.indexOf("E");
			var yVal:Number = 0;
			// handle scientific notation
			if (yEIndex > -1) {
				yExp = yStr.substr(yEIndex+1);
				yStr = yStr.substr(0, yEIndex);
				yVal = parseFloat(yStr)*Math.pow(10, parseInt(yExp)); // scientific notation

				if (yMinOffset == -1){
					// uninitialized
					yMinOffset = yVal;
				}

				yVal = yVal - yMinOffset;
			} else {
				yVal = parseFloat(inkXML.@y);
			}



			trace(xVal + ", " + yVal);

			var penUp:Boolean = inkXML.@p == "U";
			if (penUp) {
				// add to the inkWell
				inkWell.addStroke(currInkStroke);

				// reposition it to the minimum (with some padding) after each stroke
				inkWell.recenterMostRecent(theParent.stage);

				// start up a new stroke
   				currInkStroke = new InkStroke();
			} else {
				// add samples to the current stroke
				currInkStroke.addPoint(xVal, yVal, parseFloat(inkXML.@f));
			}		
        }

        private function ioErrorHandler(event:IOErrorEvent):void {
            trace("ioErrorHandler: " + event);
        }

        private function progressHandler(event:ProgressEvent):void {
            trace("progressHandler loaded:" + event.bytesLoaded + " total: " + event.bytesTotal);
        }

        private function securityErrorHandler(event:SecurityErrorEvent):void {
            trace("securityErrorHandler: " + event);
        }
 	}
}