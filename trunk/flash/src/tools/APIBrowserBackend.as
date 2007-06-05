package tools {
	
	import flash.events.MouseEvent;
	import flash.display.StageDisplayState;
	import flash.display.Stage;
	import flash.display.Sprite;
	import flash.display.Graphics;

	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.events.SecurityErrorEvent;
	import flash.events.DataEvent;
	import flash.events.IEventDispatcher;

	import flash.net.XMLSocket;
	import ink.InkRawXMLParser;
	import ink.Ink;
	import mx.controls.Label;
	import mx.controls.ComboBox;
	import mx.controls.TextArea;
	import flash.system.System;
	import flash.text.TextField;
	import components.APIBrowser;
	import java.JavaIntegration;
	import java.Constants;
	import ink.InkStroke;
	import ink.InkUtils;
	import mx.containers.Canvas;
	import flash.geom.Rectangle;	
	
	public class APIBrowserBackend extends Sprite implements Tool {

		// whether or not we are in fullscreen mode
		private var fullScreen:Boolean = false;

		// the Adobe flash stage
		private var stageObj:Stage;

		// communications with the Java backend
		private var javaServer:JavaIntegration;

		// the sprite to hold the ink!
		private var inkContainer:Sprite = new Sprite();
		private var g:Graphics;

		private var inkWell:Ink = null;

		private var theParent:APIBrowser;

        private var methodsToCallInJava:Array = new Array(); // of Strings



		// for streaming strokes... 
		// don't we have a live ink container we can use?
		private var currInkStroke:InkStroke;

		// override the ink's thickness with this value
		private var inkStrokeThickness:Number = 3.4;

		// in case the numbers are too big, we should subtract them by this number
		// this number is set by the first sample that comes in that uses scientific notation
		private var xMinOffset:Number = -1;
		private var yMinOffset:Number = -1;

		// add ink strokes to this component
		private var inkCanvas:Canvas;

		// a circle to remind us where the pen was last seen
		private var penTipCrossHair:Sprite = new Sprite();


		// constructor
		public function APIBrowserBackend(browserParent:APIBrowser):void {
			theParent = browserParent;
			g = inkContainer.graphics;
			addChild(inkContainer);

			inkWell = new Ink();
			addChild(inkWell);
			currInkStroke = new InkStroke();
			currInkStroke.inkWidth = inkStrokeThickness;

			// draw the pen tip
			penTipCrossHair.graphics.lineStyle(1, 0xDC3322);
			penTipCrossHair.graphics.drawCircle(0, 0, 4);
			penTipCrossHair.x = -10000;
			inkWell.addChild(penTipCrossHair);

			inkCanvas = theParent.inkCanvas;
		}

		// copies the code to the clipboard
		public function copyCodeHandler(event:MouseEvent):void {
			System.setClipboard(theParent.codeArea.text);
		}

		public function nextHandler(event:MouseEvent):void {
			theParent.welcomeText.visible = false;
	        javaServer.send("next");
		}

		public function prevHandler(event:MouseEvent):void {
			theParent.welcomeText.visible = false;
	        javaServer.send("prev");
		}
		
		// switches between full screen and not
		public function toggleFullScreen(event:MouseEvent):void {
			trace("toggleFullScreen");
			if (fullScreen) {
				stage.displayState = StageDisplayState.NORMAL;
			} else {
				stage.displayState = StageDisplayState.FULL_SCREEN;
			}
			fullScreen = !fullScreen;
		}
 		public function saveImage(event:MouseEvent):void {
	        javaServer.send("saveImage");
 		}

		// asks the java server to System.exit(0);
 		public function exit(event:MouseEvent):void {
	        javaServer.send("exitApplication");
 		}

		// for when we wrap the tool in HTML
		public function showExitButton():void {
			theParent.exitButton.visible = true;
		}
		
		//
		public function set javaBackend(javaInteg:JavaIntegration):void {
			javaServer = javaInteg;
			javaServer.addConnectListener(connectListener);
		}


		// flash only functions
 		public function zoomIn(event:MouseEvent):void {
 			inkContainer.scaleX *= 1.25;
 			inkContainer.scaleY *= 1.25;
 		}
 		public function zoomOut(event:MouseEvent):void {
 			inkContainer.scaleX *= .8;
 			inkContainer.scaleY *= .8;
 		}
 		public function zoomReset(event:MouseEvent):void {
 			inkContainer.scaleX = 1;
 			inkContainer.scaleY = 1;
 			
 			if (inkWell != null) {
	 			inkWell.resetLocation();
 			}
 		}

		//
 		public function connectListener():void {
			trace("ApiBrowserClient connected\n");
			javaServer.send(Constants.API_MODE);
		}

		private function debugOut(msg:String):void {
			javaServer.send("Flash client says: [" + msg + "]");
		}

		// deal with messages sent from Java
		// these should work with real-time pen events,
		// and also be able to load batched ink
        public function processMessage(msgText:String):void {
			// trace(event.text);
	        var message:XML = new XML(msgText);
			theParent.infoTextArea.text = msgText + "\n" + theParent.infoTextArea.text;

            var msgName:String = message.name();

			// make xml out of it, no doubt
			var parser:InkRawXMLParser;
	
			switch(msgName) {
				case "highlight":
					var inkXMLData:XMLList = message..ink;
					for each (var inkXML:XML in inkXMLData) {
			        	parser = new InkRawXMLParser(inkXML, 0xFF99AA, 3.4);
						// add these strokes on TOP of the previous strokes, with a better highlight. =)
						inkContainer.addChild(parser.ink);					
					}
					trace(inkXML.toXMLString());
				break;
				
				case "ink":
					// var inkData:XMLList = message.descendants("ink");
					// trace(inkData.toXMLString());
		        	parser = new InkRawXMLParser(message);
		        	if (inkWell != null) {
			        	inkContainer.removeChild(inkWell);
		        	}
		        	inkWell = parser.ink;
					inkContainer.addChild(inkWell);
				theParent.numStrokesDisplayed.text = inkWell.numStrokes + "";
				break;
				
				case "methods":
					var methodsData:XMLList = message.descendants("method");
					trace(methodsData.toXMLString());
	
					// get all the methods (somewhere down the XML tree)
					// populate the combo box
					var methods:Array = new Array();
					for each (var method:XML in methodsData) {
						//trace(stroke);
						var methodItem:Object = new Object();				
						methodItem.label = method.@name;
						methodItem.data = method.@className + "." + method.@name;
						
						methods.push(methodItem);
					}			
					theParent.methodCallDropdown.dataProvider = methods;
				break;
			
				case "penDownEvent":
					// start up a new stroke
	   				currInkStroke = new InkStroke();
	   				currInkStroke.inkWidth = inkStrokeThickness;
	   				
	   				// add it to the stage
					inkWell.addChild(currInkStroke);
				break;
				
				case "p":
					handleInk(msgText);
				break;
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
				currInkStroke.rerenderWithCurves();
				
				// penUps are a duplicate of the last regular sample
				// so, we do nothing, but possibly reposition it to the minimum 
				// (with some padding) after each stroke
				inkWell.recenterMostRecentCluster(new Rectangle(inkCanvas.x, inkCanvas.y, 
   																inkCanvas.width, inkCanvas.height));
			} else {
				// add samples to the current stroke
				currInkStroke.addPoint(xVal, yVal, parseFloat(inkXML.@f));

				penTipCrossHair.x = xVal;
				penTipCrossHair.y = yVal;
			}	
		}


        
        //
        public function addMethodCall(event:MouseEvent):void {
        	// get current value in combo box
        	
        	var methodCallString:String = "ink = " + theParent.methodCallDropdown.selectedItem.data + "(ink);"; // or list of inkstrokes!
			methodsToCallInJava.push(theParent.methodCallDropdown.selectedItem.label);
        	
        	// add it to the end of the text area
        	theParent.codeArea.text = theParent.codeArea.text + "\n" + methodCallString;
        	
        	// call this method in java!
			javaServer.send("callMethods: ["+ methodsToCallInJava.toString() +"]");
			
			theParent.numMethodsAdded.text = methodsToCallInJava.length + "";			
        }
	}
}