package eventViz {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import flash.display.Graphics;
	import mx.controls.TextArea;
	import ink.Ink;
	import ink.InkStroke;
	import flash.display.Shape;
	import flash.utils.Timer;

	
	public class EventVizClient extends Sprite {
		
		private var sock:XMLSocket;
		private var debugTextArea:TextArea;
		private var g:Graphics;
		private var app:Sprite = new Sprite();

		private var pixelsPerInch:Number = 50; // 72 is normal

		public function EventVizClient():void {
			trace("Event Viz Client Started.");
			startListening();
			g = app.graphics;

			// add drag support
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
			app.buttonMode = true; // hand cursor

			app.x = 25;
			app.y = 75;
			addChild(app);
		}
		
        private function onMouseDown(evt:Event):void {
            app.startDrag();
        }
        
        private function onMouseUp(evt:Event):void {
			app.stopDrag();
        }

		
		public function setDebugTextArea(debugText:TextArea):void {
			debugTextArea = debugText;
		}
		
 		public function startListening():void {
			sock = new XMLSocket();
			configureListeners(sock);
			
			// this should be gotten from the query parameter...
			// for now, we'll hard code it...
			sock.connect("localhost", 8545);
			sock.send("EventVizClient connected\n");
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

		private function toInches(inchString:String):Number {
			return parseFloat(inchString)*pixelsPerInch;
		}

        private function dataHandler(event:DataEvent):void {
            //trace("dataHandler: " + event);
            var message:XML = new XML(event.text);
            debugTextArea.text = message.toXMLString() + "\n\n" + debugTextArea.text;

            // if it is a new sheet, then draw it!
            if (event.text.indexOf("<sheet")>-1) {
				var sheets:XMLList = message.descendants("sheet");
				
				for each (var sheet:XML in sheets) {
					g.lineStyle(1, 0xDADADA);
					g.drawRect(0, 0, parseFloat(sheet.@w)*pixelsPerInch, parseFloat(sheet.@h)*pixelsPerInch);

					// for each region... draw a box
					var regions:XMLList = sheet..region;
					for each (var region:XML in regions) {
						var regionBox:Shape = new Shape();
						regionBox.graphics.beginFill(0xDADADA, 0.25);
						regionBox.graphics.lineStyle(1, 0xDADADA);
						regionBox.graphics.drawRect(toInches(region.@x), toInches(region.@y), toInches(region.@w), toInches(region.@h));
						app.addChild(regionBox);
						
						lastRegionAdded = regionBox;
						lastRegionX = toInches(region.@x); 
						lastRegionY = toInches(region.@y);
						lastRegionW = toInches(region.@w);
						lastRegionH = toInches(region.@h);
					}
				}			
            } else if (event.text.indexOf("<showMe")>-1) {
            	if (lastRegionAdded != null) {
            		lastRegionAddedTimer = new Timer(1000);
					lastRegionAddedTimer.addEventListener(TimerEvent.TIMER, timerHandler);
            		
					lastRegionAdded.graphics.clear();
					lastRegionAdded.graphics.beginFill(0xBBBBFF, 0.45);
					lastRegionAdded.graphics.lineStyle(1, 0xDADADA);
					lastRegionAdded.graphics.drawRect(lastRegionX, lastRegionY, lastRegionH, lastRegionW);
					
					lastRegionAddedTimer.start();
            	}
            }
        }

		private function timerHandler(event:TimerEvent):void {
			lastRegionAdded.graphics.clear();
			lastRegionAdded.graphics.beginFill(0xDADADA, 0.25);
			lastRegionAdded.graphics.lineStyle(1, 0xDADADA);
			lastRegionAdded.graphics.drawRect(lastRegionX, lastRegionY, lastRegionH, lastRegionW);
			lastRegionAddedTimer.stop();
		}

		// just a hack to test animation for now
		private var lastRegionAdded:Shape;
		private var lastRegionAddedTimer:Timer;
		private var lastRegionX:Number;		
		private var lastRegionY:Number;		
		private var lastRegionW:Number;		
		private var lastRegionH:Number;		


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