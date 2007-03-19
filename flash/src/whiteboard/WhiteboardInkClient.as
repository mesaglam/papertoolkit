package whiteboard {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import mx.controls.TextArea;
	import ink.Ink;
	import ink.InkStroke;

	
	public class WhiteboardInkClient extends Sprite {
		
		private var inkWell:Ink;
		
		private var sock:XMLSocket;
		private var debugText:TextArea;

		private var currInkStroke:InkStroke;
		
		public function WhiteboardInkClient(dbt:TextArea) {
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
				inkWell.recenter();

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