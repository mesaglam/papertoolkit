package edu.stanford.hci.r3.demos.blog;

import java.io.Serializable;
import java.util.Date;

public class BoingBoingEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9069625287416965577L;

	String title = "";
	String author = "";
	String body = "";
	String image = "";
	Date date = null;
	String link = "";
	String html = "";

	public String toString() {
		String result = "BoingBoingEntry:\n";
		result = result + "  Title:  [" + title + "]\n";
		result = result + "  Author: [" + author + "]\n";
		result = result + "  Image:  [" + image + "]\n";
		result = result + "  Link:   [" + link + "]\n";
		result = result + "  Date:   [" + date + "]\n";
		result = result + "  Body:\n" + body;
		result = result + "  \nHTML:\n" + html;
		return result;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
