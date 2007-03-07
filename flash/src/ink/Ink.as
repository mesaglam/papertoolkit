package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;

	
	public class Ink extends Sprite {
	
		private var strokes:Array = new Array(); // of InkStroke objects
		
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		public function Ink() {			
			buttonMode = true;			

			// add drag support
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}
		
        private function onMouseDown(evt:Event):void {
            this.startDrag();
        }
		
		public function addStroke(stroke:InkStroke):void {
			trace("Add Stroke");
			strokes.push(stroke);			
			addChild(stroke);
			
			xMin = Math.min(xMin, stroke.minX);
			yMin = Math.min(yMin, stroke.minY);
			xMax = Math.max(xMax, stroke.maxX);
			yMax = Math.max(yMax, stroke.maxY);			
		}
		
        private function onMouseUp(evt:Event):void {
			this.stopDrag();
        }
		
	}
}