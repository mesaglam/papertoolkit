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
	
	public class APIBrowserBackend extends Sprite {

		// whether or not we are in fullscreen mode
		private var fullScreen:Boolean = false;

		// the Adobe flash stage
		private var stageObj:Stage;

		// communications with the Java backend
		private var sock:XMLSocket;

		// the sprite to hold the ink!
		private var inkContainer:Sprite = new Sprite();
		private var g:Graphics;

		private var inkWell:Ink = null;

		private var welcomeTextLabel:Label;
		private var methodCallDropDown:ComboBox;		



		public function APIBrowserBackend(stg:Stage):void {
			stageObj = stg;
			startListening();
			g = inkContainer.graphics;
			addChild(inkContainer);
		}

		public function setWelcomeText(welcomeText:Label):void {
			welcomeTextLabel = welcomeText;
		}

		public function copyCodeHandler(event:MouseEvent):void {
			System.setClipboard(codeTextArea.text);
		}

		public function nextHandler(event:MouseEvent):void {
			welcomeTextLabel.setVisible(false);
			sendToJava("next");
		}

		public function prevHandler(event:MouseEvent):void {
			welcomeTextLabel.setVisible(false);
			sendToJava("prev");
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
			sendToJava("saveImage");
 		}
 		public function exit(event:MouseEvent):void {
			sendToJava("exitServer");
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
	 			inkWell.recenter();
 			}
 		}
 		

		private function sendToJava(msg:String):void {
			sock.send(msg + "\n");
		}

		//
 		public function startListening():void {
			sock = new XMLSocket();
			configureListeners(sock);
			
			// this should be gotten from the query parameter...
			// for now, we'll hard code it...
			sock.connect("localhost", 8545);
			sock.send("ApiBrowserClient connected\n");
		}

		//
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

		private function debugOut(msg:String):void {
			sendToJava("flash client says: [" + msg + "]");
		}

        private function dataHandler(event:DataEvent):void {
			// trace(event.text);
	        var message:XML = new XML(event.text);

			infoTextArea.text = event.text + "\n" + infoTextArea.text;
			

			// make xml out of it, no doubt
			var parser:InkRawXMLParser;
	
			if (event.text.indexOf("<highlight")>-1) {
				
				var inkXMLData:XMLList = message..ink;
				for each (var inkXML:XML in inkXMLData) {
		        	parser = new InkRawXMLParser(inkXML, 0xFF99AA, 3.4);
					// add these strokes on TOP of the previous strokes, with a better highlight. =)
					inkContainer.addChild(parser.ink);					
				}
				trace(inkXML.toXMLString());
			} else if (event.text.indexOf("<ink")>-1) {
				// var inkData:XMLList = message.descendants("ink");
				// trace(inkData.toXMLString());
	        	parser = new InkRawXMLParser(message);
	        	if (inkWell != null) {
		        	inkContainer.removeChild(inkWell);
	        	}
	        	inkWell = parser.ink;
				inkContainer.addChild(inkWell);
				
				
				numStrokesDisplayedLabel.text = inkWell.getNumStrokes() + "";
				
            } else if (event.text.indexOf("<methods")>-1) {
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
				methodCallDropDown.dataProvider = methods;
            }
        }
        
        private var methodsToCallInJava:Array = new Array(); // of Strings
        //
        public function addMethodCall(event:MouseEvent):void {
        	// get current value in combo box
        	
        	var methodCallString:String = "ink = " + methodCallDropDown.selectedItem.data + "(ink);"; // or list of inkstrokes!

			methodsToCallInJava.push(methodCallDropDown.selectedItem.label);
        	
        	// add it to the end of the text area
        	codeTextArea.text = codeTextArea.text + "\n" + methodCallString;
        	
        	// call this method in java!
			sendToJava("callMethods: ["+methodsToCallInJava.toString()+"]");
			
			numMethodsAddedLabel.text = methodsToCallInJava.length + "";			
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

		//
		public function setMethodCallDropdown(methodCalls:ComboBox):void {
			methodCallDropDown = methodCalls;
		}

		private var codeTextArea:TextArea;		
		public function setCodeArea(cArea:TextArea):void {
			codeTextArea = cArea;
		}


		private var numMethodsAddedLabel:Label;
		private var numStrokesDisplayedLabel:Label;
		public function setMetricsTextFields(nMethodsAdded:Label, nStrokesDisplayed:Label):void {
			numMethodsAddedLabel = nMethodsAdded;
			numStrokesDisplayedLabel = nStrokesDisplayed;
		}
		private var infoTextArea:TextArea;
		public function setDebugOutTextArea(infoArea:TextArea):void {
			infoTextArea = infoArea;
		}

	}
}