package tools {	

	import flash.events.*;
	import flash.net.*;
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.display.Shape;
	import flash.utils.Timer;

	import mx.controls.TextArea;

	import ink.Ink;
	import ink.InkStroke;


	
	public class EventVizBackend extends Sprite {
		
		private var sock:XMLSocket;
		private var debugTextArea:TextArea;
		private var codeTextArea:TextArea;
		private var g:Graphics;
		private var app:Sprite = new Sprite();

		private var pixelsPerInch:Number = 50; // 72 is normal

		// a "hashtable" for having access to the regions by name...
		private var regionsByName:Object = new Object();

		public function EventVizBackend():void {
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
		public function setCodeTextArea(codeText:TextArea):void {
			codeTextArea = codeText;
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

		private function debugOut(msg:String):void {
            debugTextArea.text = msg + "\n" + debugTextArea.text;
		}

        private function dataHandler(event:DataEvent):void {
            //trace("dataHandler: " + event);
            var message:XML = new XML(event.text);
            debugOut(message.toXMLString());

            // if it is a new sheet, then draw it!
            if (event.text.indexOf("<sheet")>-1) {
				var sheets:XMLList = message.descendants("sheet");
				
				for each (var sheet:XML in sheets) {
					g.lineStyle(3, 0xDADADA);
					g.drawRect(0, 0, parseFloat(sheet.@w)*pixelsPerInch, parseFloat(sheet.@h)*pixelsPerInch);

					// for each region... draw a box
					var regions:XMLList = sheet..region;
					for each (var region:XML in regions) {
						var regionBox:Region = new Region(region.@name, toInches(region.@x), toInches(region.@y), toInches(region.@w), toInches(region.@h));
						app.addChild(regionBox);
						
						// associate it by name
						regionsByName[region.@name] = regionBox;
		            	// trace(regionsByName[region.@name]);
					}
				}			
            } else if (event.text.indexOf("<showMe")>-1) {
            	
            	// get the string, and the location!
            	// position the event text at the right place!
            	
            	var regionName:String = message.@regionName;
            	var regionToAnimate:Region = regionsByName[regionName];
            	if (regionToAnimate != null) {
            		regionToAnimate.setMessage(message.@msg);
					regionToAnimate.animate();            		
            	}
            	if (message.code) {
            		codeTextArea.text = message.code.text();
            	}
            }
        }

		public function loadMappings(event:MouseEvent):void {
			sock.send("load most recent pattern mappings\n");		
		}

		public function exit(event:MouseEvent):void {
			sock.send("exitserver\n");
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