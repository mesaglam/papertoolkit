package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.display.Stage;
	import flash.filters.GradientBevelFilter;
	import flash.display.DisplayObject;

	
	// Ink is a display object, but the InkClusters store the data and do cool calculations for us...
	public class Ink extends Sprite {
	
		private var padding:int = 60;
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		private var mostRecentStroke:InkStroke;
		private var mostRecentCluster:InkCluster;

		// it's recursive in the sense that clusters are just Ink objects themselves...
		// Thus, an Ink can have Ink objects as its children
		private var clusters:Array/*<Ink>*/ = new Array();
		
		private var strokeCount:Number = 0;

		public function setColor():void {
			// for each stroke, set the color!
		}
		
		public function get numStrokes():Number {
			return strokeCount;
		}

		public function get paddingX():Number {
			return padding;
		}
		public function get paddingY():Number {
			return padding;
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

		public function Ink() {
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
		
		// add an ink stroke for display
		// we keep around the most recent stroke and ink cluster, 
		// so that we can do cool calculations, like recentering
		public function addStroke(stroke:InkStroke):void {
			//trace("Add Stroke");
			strokeCount++;
			mostRecentStroke = stroke;
			if (mostRecentCluster == null) {
				mostRecentCluster = new InkCluster();
				clusters.push(mostRecentCluster);
			}
			
			mostRecentCluster.addStroke(stroke);
			addChild(stroke); // add the stroke as a child for display
			
			if (mostRecentStroke != null) {
				if (Math.abs(stroke.lastX - mostRecentStroke.lastX) > 800) {
					// if it's > 800 anoto dots away from the last ink stroke, we assume it's in a different cluster (i.e., page)
					// we scan the current clusters, to see if it is within the 800 threshold of any of the clusters
					// we choose the closest one...
					// if not, then we add a whole new cluster!
					// TODO
					trace("New Cluster");
				}
			}
			
			mostRecentStroke = stroke;
			
			xMin = Math.min(xMin, stroke.minX);
			yMin = Math.min(yMin, stroke.minY);
			xMax = Math.max(xMax, stroke.maxX);
			yMax = Math.max(yMax, stroke.maxY);			
		}
		
        
        // move the ink so that we can see it!
        // move the origin to near the top left of the display...
        public function resetLocation():void {
        	x = -xMin + padding;
        	y = -yMin + padding;
        }


		// make sure the most recent cluster is visible within this rectangle
		// if not, then at the very least, the most recent stroke...
		public function recenterMostRecentCluster():void {
			trace("Recenter Most Recent Cluster");
			mostRecentCluster.minX;
			mostRecentCluster.minY;
			mostRecentCluster.maxX;
			mostRecentCluster.maxY;
		///	xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		}

        // move the ink so that we can see the most recent ink strokes!
        // basically, we should recenter() first, and then find the delta
        // to the most recent stroke
        //
        // Actually, we should check to see if the most recent samples are already visible
        // if they are, we need not change the view!
        //
        // OR, we can cluster the strokes, and make sure the most recent cluster is visible.
        public function recenterMostRecent(parent:DisplayObject):void {
        	if (mostRecentStroke == null) {
        		return;
        	}
        	
        	var parentWidth:Number = parent.width;
        	var parentHeight:Number = parent.height;

        	trace("Parent Size: " + parentWidth + ", " + parentHeight);
        	trace("Current Location of Ink: " + x + ", " + y);
        	trace("Current Bounds of Ink: " + xMin + ", " + yMin + " --> " + xMax + ", " + yMax);
			trace("Most Recent Sample: " + mostRecentStroke.lastX + ", " + mostRecentStroke.lastY);
			
			// is the most recent sample visible?
			// i.e., is x + mostRecentStroke.lastX both > 0 and < stageWidth?
			// same for height

        	// are these visible?
        	var dXFromOrigin:Number = (mostRecentStroke.lastX + x);
        	var dYFromOrigin:Number = (mostRecentStroke.lastY + y);
        	
        	var lastSampleXOffStageRight:Boolean = dXFromOrigin > (parentWidth - padding);
        	var lastSampleXOffStageLeft:Boolean = dXFromOrigin < padding;

        	var lastSampleYOffStageBottom:Boolean = dYFromOrigin > parentHeight - padding; 
        	var lastSampleYOffStageTop:Boolean = dYFromOrigin < padding; 
        	
        	
        	// if it's off stage, then move the writing toward the center!
        	if (lastSampleXOffStageRight) {
        		x -= (dXFromOrigin - parentWidth + padding) + parentWidth/3;
        	} else if (lastSampleXOffStageLeft) {
        		x = -mostRecentStroke.lastX + padding + parentWidth/3;
        	}
        	
        	if (lastSampleYOffStageBottom) {
        		y -= (dYFromOrigin - parentHeight + padding) + parentHeight/3;
        	} else if (lastSampleYOffStageTop) {
        		y = -mostRecentStroke.lastY + padding + parentHeight/3;
        	}
        }
	}
}