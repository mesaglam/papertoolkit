package tools {
	import flash.display.LoaderInfo;
	import flash.display.Sprite;
	import flash.events.DataEvent;
	
	import ink.InkStroke;
	import ink.InkUtils;
	
	import java.JavaIntegration;
	
	public class GestureRecognizer {

		[Embed(source="../../images/Arrow.png")]
		private var arrow:Class;
		
		[Embed(source="../../images/Caret.png")]
		private var caret:Class;
		
		[Embed(source="../../images/Check.png")]
		private var check:Class;
		
		[Embed(source="../../images/Circle.png")]
		private var circle:Class;

		[Embed(source="../../images/Delete.png")]
		private var del:Class;

		[Embed(source="../../images/LeftBrace.png")]
		private var leftbrace:Class;

		[Embed(source="../../images/LeftBracket.png")]
		private var leftbracket:Class;

		[Embed(source="../../images/Pigtail.png")]
		private var pigtail:Class;

		[Embed(source="../../images/Question.png")]
		private var question:Class;

		[Embed(source="../../images/Rectangle.png")]
		private var rect:Class;

		[Embed(source="../../images/RightBrace.png")]
		private var rightbrace:Class;

		[Embed(source="../../images/RightBracket.png")]
		private var rightbracket:Class;

		[Embed(source="../../images/Star.png")]
		private var star:Class;

		[Embed(source="../../images/Triangle.png")]
		private var triangle:Class;

		[Embed(source="../../images/V.png")]
		private var v:Class;

		[Embed(source="../../images/X.png")]
		private var cross:Class;

		[Embed(source="../../images/Direction.png")]
		private var direction:Class;

		[Embed(source="../../images/spacer.gif")]
		private var spacer:Class;
		

		private const SCALE:Number = 2.2;
		private const PADDING:Number = 80;	
		
		private var portNum:int;
		private var javaBackend:JavaIntegration;

		// a circle to remind us where the pen was last seen
		private var penTipCrossHair:Sprite = new Sprite();

		private var gui:GestureRecognizerDisplay;
		public function GestureRecognizer(rec:GestureRecognizerDisplay):void {
			gui = rec;
			
			// start the communication with Java
			processParameters();
			start();

			penTipCrossHair.graphics.lineStyle(2, 0xDC3322);
			penTipCrossHair.graphics.drawCircle(-1, -1, 4);
			penTipCrossHair.x = -200;
			penTipCrossHair.scaleX = SCALE;
			penTipCrossHair.scaleY = SCALE;
	
			gui.recognizedGestureImage.source = spacer;
			gui.recognizedDirImage.source = direction;
			gui.rotater.duration  = 20;
		}

		private function showDirImage(angle:int, label:String):void {
			gui.gestureBox.visible = false;
			gui.dirBox.visible = true;
			gui.dirLabel.text = label;
			gui.rotater.angleTo = angle;
			gui.rotater.play();
		}

		private function showGestureImage(image:Class, label:String):void {
			gui.recognizedGestureImage.source = image;
			gui.gestureBox.visible = true;
			gui.dirBox.visible = false;
			gui.gestureLabel.text = label;
		}

		// this is called after the command line arguments are processed
		private function start():void {
			// toggleFullScreen();
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addMessageListener(msgListener);
		}

		// handle messages
        private function msgListener(event:DataEvent):void {
            var msg:XML = new XML(event.text);
            var msgName:String = msg.name();
            
            switch(msgName) {
            	case "penDownEvent":
            		if (currInkStroke!=null) {
            			gui.container.removeChild(currInkStroke);
            		}
            	
					// start up a new stroke
	   				currInkStroke = new InkStroke();
	   				currInkStroke.x = gui.inkStrokeCanvas.width/3;
	   				currInkStroke.y = gui.inkStrokeCanvas.height/3;
	   				currInkStroke.scaleX = SCALE;
	   				currInkStroke.scaleY = SCALE;
	   				currInkStroke.inkWidth = 3.3;
	   				currInkStroke.addChild(penTipCrossHair);
	   				gui.container.addChild(currInkStroke);
	   				xMinOffset = -1;
	   				yMinOffset = -1;
	            	break;
            	case "p":
            		handleInk(msg);
	            	break;
    			case "penUpEvent":
    				break;
    			case "recognized":
    				trace("Recognized: " + msg.@templateName);
    				var name:String = msg.@templateName;
    				switch(name) {
    					case "caret":
    						showGestureImage(caret, "Caret");
    						break;
    					case "delete":
    						showGestureImage(del, "Delete");
	    					break;
    					case "question":
    						showGestureImage(question, "Question Mark");
	    					break;
    					case "check":
    						showGestureImage(check, "Check Mark");
	    					break;
    					case "triangle":
    						showGestureImage(triangle, "Triangle");
	    					break;
    					case "x":
    						showGestureImage(cross, "X Mark");
	    					break;
    					case "rectangle":
    						showGestureImage(rect, "Rectangle");
	    					break;
    					case "circle":
    						showGestureImage(circle, "Circle");
	    					break;
    					case "arrow":
    						showGestureImage(arrow, "Arrow");
	    					break;
    					case "left square bracket":
    						showGestureImage(leftbracket, "Left Bracket");
	    					break;
    					case "right square bracket":
    						showGestureImage(rightbracket, "Right Bracket");
	    					break;
    					case "v":
    						showGestureImage(v, "V Mark");
	    					break;
    					case "left curly brace":
    						showGestureImage(leftbrace, "Left Curly Brace");
	    					break;
    					case "right curly brace":
    						showGestureImage(rightbrace, "Right Curly Brace");
	    					break;
    					case "star":
    						showGestureImage(star, "Star");
	    					break;
    					case "pigtail":
    						showGestureImage(pigtail, "Pigtail");
	    					break;
    					case "E":
    						showDirImage(0, "East");
	    					break;
    					case "S":
    						showDirImage(90, "South");
	    					break;
    					case "N":
    						showDirImage(270, "North");
	    					break;
    					case "W":
    						showDirImage(180, "West");
	    					break;
    					case "SE":
    						showDirImage(45, "South-East");
	    					break;
    					case "SW":
    						showDirImage(135, "South-West");
	    					break;
    					case "NE":
    						showDirImage(315, "North-East");
    						break;
    					case "NW":
    						showDirImage(225, "North-West");
    						break;
    				}
    				
    				break;
				default:
    				break;
            }
        }

		private var currInkStroke:InkStroke;
		private var xMinOffset:Number = -1;
		private var yMinOffset:Number = -1;
		

		private function handleInk(inkXML:XML):void {
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
			var penUp:Boolean = inkXML.@p == "UP";
			if (penUp) {
				currInkStroke.rerenderWithCurves();
				var newX:Number = (gui.inkStrokeCanvas.width - currInkStroke.strokeWidth)/2;
				var newY:Number = (gui.inkStrokeCanvas.height - currInkStroke.strokeHeight)/2;
				var diffPenTipX:Number = newX - currInkStroke.x;
				var diffPenTipY:Number = newY - currInkStroke.y;

				currInkStroke.x = newX;
				currInkStroke.y = newY;

			} else {
				// add samples to the current stroke
				currInkStroke.addPoint(xVal, yVal, parseFloat(inkXML.@f));
				penTipCrossHair.x = xVal;
				penTipCrossHair.y = yVal;
			}	
		}



		// retrieve parameters from the host HTML page
		private function processParameters():void {
			// for storing the parameters
			var paramObj:Object;
			try {
				var keyStr:String;
				var valueStr:String;
				paramObj = LoaderInfo(gui.root.loaderInfo).parameters;
				for (keyStr in paramObj) {
					valueStr = paramObj[keyStr] as String;
					trace(keyStr + ":\t" + valueStr);
					if (keyStr=="port") {
						portNum = parseInt(valueStr);
					}
				}
			} catch (error:Error) {
				trace(error);
			}
		}

		
		public function displayGestureResult(templateName:String):void {
			switch(templateName) {
				default:
				break;	
			}
			gui.gestureLabel.text = templateName;
		}
	}
}