package tools {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import mx.controls.TextArea;
	import ink.Ink;
	import ink.InkStroke;
	import flash.display.Stage;
	import java.JavaIntegration;
	import flash.display.DisplayObject;
	import flash.display.LoaderInfo;

	
	public class WhiteboardBackend extends Sprite {
		
		private var inkWell:Ink;
		private var debugText:TextArea;
		private var currInkStroke:InkStroke;
		// in case the numbers are too big, we should subtract them by this number
		// this number is set by the first sample that comes in that uses scientific notation
		private var xMinOffset:Number = -1;
		private var yMinOffset:Number = -1;
		private var theParent:Whiteboard = null;

		// a socket client that talks to the Java back end
		private var javaBackend:JavaIntegration;

		private var theStage:Stage;
		private var theRoot:DisplayObject;

		private var paramObj:Object;

		private var portNum:int = 8545; // default


		public function WhiteboardBackend():void {
			trace("Ink Client Started.");
			inkWell = new Ink();
			addChild(inkWell);
			currInkStroke = new InkStroke();
		}
		
		public function setDebugText(dbt:TextArea):void {
			debugText = dbt;
		}
		
		private function debugOut(msg:String):void {
			trace(msg);
			debugText.text = msg + "\n" + debugText;
		}

		public function setParent(p:Whiteboard):void {
			theParent = p;
		}

		public function recenter():void {
			inkWell.recenter();
		}
		
		public function setRoot(rt:DisplayObject):void {
			theRoot = rt;
		}
		
		public function setStage(stg:Stage):void {
			theStage = stg;
		}
		
		public function recenterMostRecent(stage:Stage):void {
			inkWell.recenterMostRecent(stage);
		}

		public function processParameters():void {
			try {
				var keyStr:String;
				var valueStr:String;
				paramObj = LoaderInfo(theRoot.loaderInfo).parameters;
				for (keyStr in paramObj) {
					valueStr = String(paramObj[keyStr]);
					trace(keyStr + ":\t" + valueStr);
					if (keyStr=="port") {
						portNum = parseInt(valueStr);
					}
				}
			} catch (error:Error) {
				trace(error);
			}
			
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addMessageListener(msgListener);
		}

        private function msgListener(event:DataEvent):void {
            //trace("dataHandler: " + event);
            trace(event.text); // parse the text and assemble InkStrokes...
            var msg:XML = new XML(event.text);
            
   			if (event.text.indexOf("<p")>-1) {
				handleInk(event.text);
	   		} else if (event.text.indexOf("<swatchColor") > -1){
	   			trace(msg..r + " " + msg..g + " " + msg..b);
	   			var intColor:int = 
	   				(parseInt(msg.@r) << 16) + 
	   				(parseInt(msg.@g) << 8) + 
	   				(parseInt(msg.@b));
	   			trace(intColor+"");
	   			theParent.colorSwatch.selectedColor = intColor;
   			}
        }
        

		private function handleInk(xmlTxt:String):void {
            var inkXML:XML = new XML(xmlTxt);
            // trace("XML: " + inkXML.toXMLString());

			// trace(inkXML.@x + " " + inkXML.@y + " " + inkXML.@f + " " + inkXML.@t + " " + inkXML.@p);

			

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

			// trace(xVal + ", " + yVal);

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

		public function zoomIn():void {
			scaleX *= 1.25;
			scaleY *= 1.25;
		}
		public function resetZoom():void {
			scaleX = 1;
			scaleY = 1;
			// whitebd.recenter();
			recenterMostRecent(stage);
		}
		public function zoomOut():void {
			scaleX *= .8;
			scaleY *= .8;
		}
		
		public function toggleDebugText():void {
			debugText.visible = !debugText.visible;
		}
        
        public function saveInk():void {
        	// send a message to java to save the current ink...
        	
        }
		public function exit():void {
			javaBackend.send("exitApplication");
		}
 	}
}