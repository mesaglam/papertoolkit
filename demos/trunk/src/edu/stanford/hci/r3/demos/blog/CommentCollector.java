package edu.stanford.hci.r3.demos.blog;

import java.io.File;

import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;

public class CommentCollector extends InkCollector {

	private BoingBoingEntry bbentry;
	private Thread updater;
	
	private int index = 0;
	private int count = 0;
	
	private static final long ONE_MINUTE = 60L * 1000L;
	
	public CommentCollector(BoingBoingEntry bbentry, int index) {
		this.bbentry = bbentry;
		this.index = index;
		updater = new Thread() {
			public void run() {
			 	try {
					sleep(CommentCollector.ONE_MINUTE);
				} catch (InterruptedException e) { }
			long last = getLastTimestamp();
			long now = System.currentTimeMillis();
			if (last > 0 && now - last > CommentCollector.ONE_MINUTE) {
				CommentCollector.this.storeComment();
			}
			}
		};
		updater.start();
	}
	
	void storeComment() {
		Ink comment_ink = getInk();
		clear();
		
		File comment_image = new File("data/Blog/comments/comment" + index + "_" + count); 
		
		InkRenderer r = new InkRenderer(comment_ink);
		r.renderToJPEG(comment_image, new Pixels(72), new Inches(8.5), new Inches(8.5));
		
		// TODO: Update RSS feed here
		
		count++;
		
	}
	
}
