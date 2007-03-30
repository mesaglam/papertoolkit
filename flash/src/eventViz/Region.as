package eventViz
{
	import flash.display.Sprite;
	import flash.text.TextField;
	import mx.controls.Label;
	import flash.text.TextFormat;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	import flash.text.TextFieldAutoSize;
	
	public class Region extends Sprite {
		
		private var rx:Number;
		private var ry:Number;
		private var rw:Number;
		private var rh:Number;
		
		private var rName:String;
		private var rText:TextField;
		private var rMessage:TextField;
		
		private var timer:Timer; // for highlighting the region that was clicked on...
		private var textTimer:Timer; // for fading out the showMe text
		private var tfShowMe:TextFormat;

		public function Region(regionName:String, xVal:Number, yVal:Number, wVal:Number, hVal:Number):void {
			rName = regionName;

			x = xVal;
			y = yVal;

			rx = xVal;
			ry = yVal;
			rw = wVal;
			rh = hVal;

			drawDefault();			

			// large text
			var tf:TextFormat = new TextFormat();
			tf.font = "Trebuchet MS";
			tf.size = 17;

			rText = new TextField();
			rText.x = 7;
			rText.y = 9;
			rText.selectable = false;
			rText.textColor = 0xFFFFFF;
			rText.autoSize = TextFieldAutoSize.LEFT;
			rText.text = rName;
			rText.setTextFormat(tf);


			// smaller monospaced text for showMe
			tfShowMe = new TextFormat();
			tfShowMe.font = "Lucida Console";
			tfShowMe.bold = true;
			tfShowMe.size = 18; // for UIST Figure // 14;
			
			rMessage = new TextField();
			rMessage.x = 7;
			rMessage.y = 35;
			rMessage.selectable = true;
			rMessage.textColor = 0xF1F1FF;
			rMessage.autoSize = TextFieldAutoSize.LEFT;
			rMessage.text = "";
			rMessage.setTextFormat(tfShowMe);

			addChild(rMessage);
			addChild(rText);
		}
		
		public function drawDefault():void {
			graphics.clear();
			graphics.beginFill(0xDADADA, 0.2);
			graphics.lineStyle(1, 0xDADADA);
			graphics.drawRect(0, 0, rw, rh);
		}
		public function drawHighlighted():void {
			graphics.clear();
			graphics.beginFill(0xFBCBCB, 0.45); // for UIST figure
//			graphics.beginFill(0xCBCBDD, 0.35);
			graphics.lineStyle(1, 0xBBBBDD);
			graphics.drawRect(0, 0, rw, rh);
		}
		public function setMessage(msg:String):void {
			rMessage.text = msg;
			rMessage.setTextFormat(tfShowMe);
			if (textTimer != null) {
				textTimer.stop();
			}
			
			textTimer = new Timer(3000);
			textTimer.addEventListener(TimerEvent.TIMER, textTimerHandler);
			textTimer.start();
		}
		public function animate():void {
			if (timer != null) {
				timer.stop();
			}
			timer = new Timer(350);
			timer.addEventListener(TimerEvent.TIMER, timerHandler);
			drawHighlighted();
			timer.start();
		}
		
		private function timerHandler(event:TimerEvent):void {
			drawDefault();
			timer.stop();
		}
		private function textTimerHandler(event:TimerEvent):void {
			rMessage.text = "";			
			textTimer.stop();
		}
	}
}