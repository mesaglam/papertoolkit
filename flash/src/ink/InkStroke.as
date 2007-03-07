package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;

	public class InkStroke extends Sprite {
	
		private var xSamples:Array = new Array();
		private var ySamples:Array = new Array();
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		private var g:Graphics = graphics;

		private var strokeWidth:Number = 1;

		public function InkStroke(beginTS:String, endTS:String):void {			
			trace(beginTS + " to " + endTS);
			buttonMode = true;
		}

		
		public function addPoint(xVal:Number, yVal:Number):void {
			g.lineStyle(strokeWidth, 0xAAAAAA, .95);
			strokeWidth += 0.1;
			if (xSamples.length == 0) {
				g.moveTo(xVal, yVal);				
			}
			else {
				g.lineTo(xVal, yVal);
			}
			xSamples.push(xVal);
			ySamples.push(yVal);

			
			
			xMin = Math.min(xMin, xVal);
			xMax = Math.max(xMax, xVal);
			yMin = Math.min(yMin, yVal);
			yMax = Math.max(yMax, yVal);
			
			trace(xVal + " " + yVal);
		}
		
		public function get minX():Number {
			return xMin;
		}
		public function get minY():Number {
			return yMin;
		}
		public function get maxX():Number {
			return xMax;
		}
		public function get maxY():Number {
			return yMax;
		}
	}
}