package apiBrowser {
	
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
			trace("copyCode Click Handler");
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

			// make xml out of it, no doubt
	
            if (event.text.indexOf("<ink")>-1) {
				// var inkData:XMLList = message.descendants("ink");
				// trace(inkData.toXMLString());
	        	var parser:InkRawXMLParser = new InkRawXMLParser(new XML(event.text));
	        	if (inkWell != null) {
		        	inkContainer.removeChild(inkWell);
	        	}
	        	inkWell = parser.ink;
				inkContainer.addChild(inkWell);
            } else if (event.text.indexOf("<methods")>-1) {
				var methodsData:XMLList = message.descendants("method");
				trace(methodsData.toXMLString());

				var methods:Array = new Array();
				var method1:Object = new Object();				
				var method2:Object = new Object();
				methods.push(method1, method2);
				method1.label = "hello";
				method2.label = "wee";
				method1.data = "daaah";
				method2.data = "daaaaah";
				methodCallDropDown.dataProvider = methods;
				
				// populate the combo box
				
            }
		
			
        }
        
        public function addMethodCall(event:MouseEvent):void {
        	
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

	}
}