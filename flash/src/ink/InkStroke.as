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

		private var strokeWidth:Number = 1.5;

		public function InkStroke(beginTS:String, endTS:String):void {			
			//trace(beginTS + " to " + endTS);
			buttonMode = true;
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
			
			g.lineStyle(modifiedStrokeWidth, 0xCCCCCC, modifiedAlpha);
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