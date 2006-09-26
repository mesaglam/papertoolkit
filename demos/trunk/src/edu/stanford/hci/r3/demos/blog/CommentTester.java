package edu.stanford.hci.r3.demos.blog;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;

public class CommentTester extends Application {

	public CommentTester() {
		super("Comment Tester");
		
		Sheet s = new Sheet(8.5, 11);
		
		Region r = new Region("CommentTest", 0.5, 0.5, 7.5, 7.5);

		r.addContentFilter(new CommentCollector(null, 0));
		
		s.addRegion(r);
		
		addSheet(s);
	}

	public static void main(String[] args) {
		CommentTester print = new CommentTester();
		// print.generateCode();

		PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.loadApplication(print);
	}
	
}

