package papertoolkit.demos.gigaprints2006.blog;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.ink.InkStroke;


public class CommentTester extends Application {

	public static void main(String[] args) {
		CommentTester print = new CommentTester();
		// print.generateCode();

		PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.loadApplication(print);
	}

	public CommentTester() {
		super("Comment Tester");

		Sheet s = new Sheet(8.5, 11);

		s.addConfigurationPath(new File("."));

		Region r = new Region("CommentTest", 0.5, 0.5, 7.5, 7.5);
		CommentCollector cc = new CommentCollector(null, 0) {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
			}
		};
		r.addEventHandler(cc);
		s.addRegion(r);

		addSheet(s);

	}

}
