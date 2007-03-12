package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;

	public class InkStroke extends Sprite {

		private static var defaultColor:uint = 0xCFCFCF;
		private static var highlightColor:uint = 0xFF99AA;

		private var xSamples:Array = new Array();
		private var ySamples:Array = new Array();
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		private var g:Graphics = graphics;

		private var strokeWidth:Number = 1.5;
		
		private var color:uint = defaultColor;
		
		private var highlight:Boolean = false;

		private var begin:Number;
		private var end:Number;
		
		public function InkStroke(beginTS:String, endTS:String):void {			
			//trace(beginTS + " to " + endTS);
			begin = beginTS;
			end = endTS;
			buttonMode = true;
		}		
		
		public function set highlighted(value):void {
			highlight=value;
			
			if (highlight) {
				color = highlightColor;
			} else {
				color = defaultColor;
			}
		}

		public function get beginTimestamp():Number {
			return begin;
		}

		public function addPoint(xVal:Number, yVal:Number, f:Number):void {
			// assume f is 0 to 128
			// later: cap it to 0 to 128
			
			// vary opacity and width based on the force
			// from about 70 to 100, treat the width the same, but vary opacity
			// from about 0 to 70, vary width
			// from about 100 to 128, vary width
			
			var modifiedStrokeWidth:Number = strokeWidth;
			var delta:Number = 70 - f;
			modifiedStrokeWidth -= delta * 0.01;
			
			
			var modifiedAlpha:Number = .95;
			modifiedAlpha -= delta * .01;
			
			g.lineStyle(modifiedStrokeWidth, color, modifiedAlpha);
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
			
			//trace(xVal + " " + yVal);
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