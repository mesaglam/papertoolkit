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

		public function APIBrowserBackend(stg:Stage):void {
			stageObj = stg;
			startListening();
			g = inkContainer.graphics;
		}

		public function copyCodeHandler(event:MouseEvent):void {
			trace("copyCode Click Handler");
		}

		public function nextHandler(event:MouseEvent):void {
			trace("next Click Handler");
			sendToJava("next");
		}

		public function prevHandler(event:MouseEvent):void {
			trace("prev Click Handler");
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
 		}
 		public function zoomOut(event:MouseEvent):void {
 		}
 		public function zoomReset(event:MouseEvent):void {
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
			trace(event.text);
			
			// make xml out of it, no doubt
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