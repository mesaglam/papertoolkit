package timelineControl {	

	import flash.events.*;
	import flash.net.*;
	
	public class R3FlashControl {
		
		private var sock:Socket;
		
		public function R3FlashControl() {
			trace("R3 Flash Control Created.");
			startListening();
		}
		
		public function startListening() {
			trace("R3 Started Listening");
			
			sock = new Socket();
			sock.addEventListener(ProgressEvent.SOCKET_DATA, socketDataHandler);			
			sock.connect("localhost", 6543);
			sock.writeUTFBytes("Hello, Server!");
		}

		// callback
		private function socketDataHandler(event:ProgressEvent):void {
			var str:String = sock.readUTFBytes(sock.bytesAvailable);
			trace("Incoming Message: [" + str + "]");
			
			if (str.length < 4 || str.indexOf("[[") == -1 || str.indexOf("]]") == -1) {
				trace("Invalid Message: " + str);
				return;
			}
			
			// where to move the main timeline to
			var destinationFrameLabel:String = str.substring(str.indexOf("[[")+2, str.indexOf("]]"));
			
			// moves the document to the frame with label == str
			trace("Destination Frame: [" + destinationFrameLabel + "]");
			MainController.getInstance().gotoAndPlay(destinationFrameLabel);
		}
	}
}