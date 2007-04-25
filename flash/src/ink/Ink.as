package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.display.Stage;
	import flash.filters.GradientBevelFilter;
	import flash.display.DisplayObject;
	import flash.geom.Rectangle;
	import utils.MathUtils;

	
	// Ink is a display object, but the InkClusters store the data and do cool calculations for us...
	public class Ink extends Sprite {
	
		private var padding:int = 60;
		private var paddingSmall:int = 10;
		
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		private var mostRecentStroke:InkStroke;
		private var mostRecentCluster:InkCluster;

		// it's recursive in the sense that clusters are just Ink objects themselves...
		// Thus, an Ink can have Ink objects as its children
		private var clusters:Array/*<InkCluster>*/ = new Array();
		
		private var strokeCount:Number = 0;

		private var currentlyPreviewing:InkStroke;

		public function set color(c:uint):void {
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

		public function startPreview(stroke:InkStroke):void {
			currentlyPreviewing = stroke;
			addChild(currentlyPreviewing);
		}
		public function stopPreview():void {
			if (currentlyPreviewing != null) {
				removeChild(currentlyPreviewing);
				currentlyPreviewing = null;
			}
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
			
			// which cluster does this stroke belong to?
			if (mostRecentCluster == null) {
				// we just started, so create a new cluster
				mostRecentCluster = new InkCluster();
				clusters.push(mostRecentCluster);
			} else if (mostRecentStroke != null) {
				// see if this stroke is close to the previous stroke or not
				var dDist:Number = MathUtils.distance(stroke.avgX, stroke.avgY, 
													  mostRecentStroke.avgX, mostRecentStroke.avgY);
				if (dDist > InkCluster.CLUSTER_GAP) {
					// if it's > 500 anoto dots away from the last ink stroke, 
					// we assume it's in a different cluster (e.g, new page)
					// we scan the current clusters, to see if it is within the threshold 
					// of any of the clusters.
					// 
					// we choose the closest one...
					// if not, then we add a whole new cluster!
					var foundCluster:Boolean = false;
					for each (var c:InkCluster in clusters) {
						trace("Scanning Cluster: " + c);
						if (c.closeEnoughTo(stroke)) {
							trace("Found Cluster");
							mostRecentCluster = c;
							foundCluster = true;
							break;
						}
					}
					if (!foundCluster) {
						trace("New Cluster");
						// didn't find any
						mostRecentCluster = new InkCluster();
						clusters.push(mostRecentCluster);
					}
				}
			}
			
			mostRecentCluster.addStroke(stroke);
			addChild(stroke); // add the stroke as a child for display
			
			mostRecentStroke = stroke;

			// update statistics			
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
		public function recenterMostRecentCluster(rect:Rectangle):void {
			var minX:Number = Number.MAX_VALUE;
			var minY:Number = Number.MAX_VALUE;
			
			if (currentlyPreviewing != null) {
				minX = Math.min(currentlyPreviewing.minX, minX);
				minY = Math.min(currentlyPreviewing.minY, minY);
			}
			if (mostRecentCluster != null) {
				// trace("Recenter Most Recent Cluster to: " + rect);
				// trace("Most Recent Cluster: " + mostRecentCluster.toString());
				minX = Math.min(mostRecentCluster.minX, minX);
				minY = Math.min(mostRecentCluster.minY, minY);
			}
			x = -minX + paddingSmall;
			y = -minY + paddingSmall;
		}

        // move the ink so that we can see the most recent ink strokes!
        // basically, we should recenter() first, and then find the delta
        // to the most recent stroke
        //
        // Actually, we should check to see if the most recent samples are already visible
        // if they are, we need not change the view!
        //
        // OR, we can cluster the strokes, and make sure the most recent cluster is visible.
        // Now Unused... 
        private function recenterMostRecentStroke(rect:Rectangle):void {
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