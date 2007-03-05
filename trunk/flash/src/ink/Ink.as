package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;

	public class Ink extends Sprite {
	
		public function Ink() {			
			var g:Graphics = graphics;
			g.lineStyle(2, 0xAAAAAA, .95);
			g.moveTo(10,10);
			g.lineTo(50,50);

			buttonMode = true;
			

			// add drag support
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}
		
        private function onMouseDown(evt:Event):void {
            this.startDrag();
        }
		
        private function onMouseUp(evt:Event):void {
			this.stopDrag();
        }
		
	}
}