package papertoolkit.devices.displays;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import papertoolkit.devices.Device;

public class HTMLDisplay {

	private String head = "";
	private List<String> body = new ArrayList<String>();
	private String name;
	private Device parentDevice;
	private String title;

	public HTMLDisplay(Device device, String n) {
		parentDevice = device;
		name = n;
	}

	public void setTitle(String string) {
		title = string;
		head = "<head><title>" + string + "</title></head>";
	}

	public void addImage(File imageFile) {
		// TODO: Currently assumes it's in the same directory
		// later, support full URLss...
		body.add("<p><img src='" + imageFile.getName() + "'/></p>");
	}

	public void addText(String string) {
		body.add("<h2>" + string + "</h2>");

	}

	public void showNow() {
		// write to a file
		File homeDir = FileSystemView.getFileSystemView().getHomeDirectory();
		File destFile = new File(homeDir, name + ".html");

		try {
			PrintWriter out = new PrintWriter(destFile);
			out.println("<html>");
			out.println(head);
			out.println("<body>");
			if (title != null) {
				out.println("<h1>" + title + "</h1>");
			}
			for (String line : body) {
				out.println(line);
			}
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// open it!
		parentDevice.openFile(destFile);
	}

}
