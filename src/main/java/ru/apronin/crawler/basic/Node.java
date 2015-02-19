package ru.apronin.crawler.basic;

import java.util.LinkedList;

public class Node {

	private Node parent;
	private String category;
	private String text;
	private String code;
	private String pageId;
	private String name;

	public Node(Node parent, String category) {
		this.parent = parent;
		this.category = category;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = String.format("%03d", code);
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public int getLevel() {
		int i = 0;
		Node node = this;
		while (node.parent != null) {
			i++;
			node = node.parent;
		}
		return i;
	}

	public String getFileName() {
		LinkedList<String> path = getNesting();
		StringBuilder sb = new StringBuilder();
		if (path.size() > 0) {
			sb.append(path.pop());
			while (!path.isEmpty()) {
				sb.append("_").append(path.pop());
			}
		}
		return sb.toString();
	}

	public String getFolderPath() {
		LinkedList<String> path = getNesting();
		StringBuilder sb = new StringBuilder();
		while (!path.isEmpty()) {
			sb.append("/").append(path.pop());
		}
		sb.append("/");
		return sb.toString();
	}

	private LinkedList<String> getNesting() {
		LinkedList<String> nesting = new LinkedList<String>();
		Node node = this;
		while (node.parent != null) {
			nesting.push(node.getCode());
			node = node.parent;
		}
		nesting.push(node.getCode());
		return nesting;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Node [parent=" + parent + ", category=" + category + ", text="
				+ text + ", code=" + code + "]";
	}

}
