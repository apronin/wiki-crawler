package ru.apronin.crawler.basic;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WikiService {
	
	private static final String CANONICALURL_ATR = "canonicalurl";
	private static final String PAGE_TAG = "page";
	private static final String PAGE_EXTRACT = "extract";
	private static final String CATEGORY_MEMBER_TAG_NAME = "cm";
	private static final String CATEGORY_MEMBER_TYPE = "type";
	private static final String CATEGORY_MEMBER_TITLE = "title";
	private static final String CATEGORY_MEMBER_PAGEID = "pageid";
	public static final WikiService INSTANCE = new WikiService();


	private WikiService() {
		
	}

	public List<CategoryItem> getCategoryItems(String categoryName) throws Exception {
		List<CategoryItem> result = new ArrayList<CategoryItem>();
		String url = "http://ru.wikipedia.org/w/api.php?action=query&format=xml&list=categorymembers&cmprop=title%7Ctype%7Cids&cmlimit=500%7Ccmcontinue&cmtitle=Category:" + URLEncoder.encode(categoryName, "UTF-8");
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.getEntity().getContent());
		NodeList categories = document.getElementsByTagName(CATEGORY_MEMBER_TAG_NAME);
		for (int i = 0; i < categories.getLength(); i++) {
			Node category = categories.item(i);
			CategoryItem item = new CategoryItem();
			item.setPageid(category.getAttributes().getNamedItem(CATEGORY_MEMBER_PAGEID).getNodeValue());
			item.setTitle(category.getAttributes().getNamedItem(CATEGORY_MEMBER_TITLE).getNodeValue().replace("Категория:", ""));
			item.setType(category.getAttributes().getNamedItem(CATEGORY_MEMBER_TYPE).getNodeValue());
			result.add(item);
		}
		Collections.sort(result);
		return result;
	}

	public PageItem getPageText(String pageId) throws Exception{
		String url = "http://ru.wikipedia.org/w/api.php?format=xml&action=query&prop=extracts%7Cinfo&inprop=url&explaintext&exsectionformat=plain&pageids=" + pageId;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.getEntity().getContent());
		PageItem item = new PageItem();
		item.setText(document.getElementsByTagName(PAGE_EXTRACT).item(0).getTextContent());
		item.setUrl(document.getElementsByTagName(PAGE_TAG).item(0).getAttributes().getNamedItem(CANONICALURL_ATR).getNodeValue());
		return item;
	}



}
