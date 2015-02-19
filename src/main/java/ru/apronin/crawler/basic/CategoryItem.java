package ru.apronin.crawler.basic;

public class CategoryItem implements Comparable<CategoryItem>{

	private String pageid;
	private String title;
	private String type;
	private int level;

	public String getPageid() {
		return pageid;
	}

	public void setPageid(String pageid) {
		this.pageid = pageid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int compareTo(CategoryItem other) {		
		return this.title.compareTo(other.title);
	}

}
