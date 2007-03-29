package eventViz
{
	import flash.display.Sprite;
	import flash.text.TextField;
	import mx.controls.Label;
	import flash.text.TextFormat;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	public class Region extends Sprite {
		
		private var rx:Number;
		private var ry:Number;
		private var rw:Number;
		private var rh:Number;
		
		private var rName:String;
		private var rText:TextField;
		
		private var timer:Timer;

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
			tf.size = 18;

			rText = new TextField();
			rText.x = 7;
			rText.y = 10;
			rText.selectable = false;
			rText.textColor = 0xFFFFFF;
			rText.text = rName;
			rText.setTextFormat(tf);
			addChild(rText);
		}
		
		public function drawDefault():void {
			graphics.clear();
			graphics.beginFill(0xDADADA, 0.25);
			graphics.lineStyle(1, 0xDADADA);
			graphics.drawRect(0, 0, rw, rh);
		}
		public function drawHighlighted():void {
			graphics.clear();
			graphics.beginFill(0xBBBBFF, 0.45);
			graphics.lineStyle(1, 0xBBBBFF);
			graphics.drawRect(0, 0, rw, rh);
		}
		
		public function animate():void {
			timer = new Timer(500);
			timer.addEventListener(TimerEvent.TIMER, timerHandler);
			drawHighlighted();
			timer.start();
		}
		
		private function timerHandler(event:TimerEvent):void {
			drawDefault();
			timer.stop();
		}

	}
}