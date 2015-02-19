package ru.apronin.crawler.basic;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Crawler {

	private static final String MAX_THREAD = "max_thread";
	private static final String ROOT_CATEGORIES = "root_categories";

	private static final Logger logger = Logger.getLogger(Crawler.class.getName());
	
	private ExecutorService executorService;
	private Properties properties;
	private AtomicInteger pageCount;
	
	public Crawler(Properties properties) {
		this.executorService = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty(MAX_THREAD)));
		this.properties = properties;
		this.pageCount = new AtomicInteger();
	}
	
	public void start() throws Exception{		
		List<String> rootCategories = Arrays.asList(properties.getProperty(ROOT_CATEGORIES).split(","));
		for (int i = 0; i < rootCategories.size(); i++) {			
			Node node = new Node(null, rootCategories.get(i));
			node.setCode(i);
			walk(node);
			pageCount = new AtomicInteger();
		}
		executorService.shutdown();
		logger.log(Level.INFO, "page count {0}", pageCount);
	}
	
	public void walk(Node parent) throws Exception{
		LinkedList<Node> subcategories = new LinkedList<>();
		List<CategoryItem> subcategoriesList = new LinkedList<CategoryItem>();
		List<CategoryItem> pageList = new LinkedList<CategoryItem>();
		List<CategoryItem> items = WikiService.INSTANCE.getCategoryItems(parent.getCategory());
		for (CategoryItem item : items) {
			if(item.getType().equals("subcat")){
				subcategoriesList.add(item);
			} else {
			    pageList.add(item);
			}
		}

		for (int i = 0; i < subcategoriesList.size(); i++) {
			CategoryItem item = subcategoriesList.get(i);
			Node node = new Node(parent, parent.getCategory());
			node.setCode(i);
			node.setName(item.getTitle());
			node.setPageId(item.getPageid());
			node.setCategory(item.getTitle());
			subcategories.add(node);
		}
		
		for (int i = 0; i < pageList.size(); i++) {
			CategoryItem item = pageList.get(i);
			Node node = new Node(parent, parent.getCategory());
			node.setCode(i);
			node.setName(item.getTitle());
			node.setPageId(item.getPageid());
			node.setCategory(item.getTitle());
			PageWorker worker = new PageWorker(node, properties);
			executorService.execute(worker);
			pageCount.incrementAndGet();
		}
		
		if(pageCount.intValue() <= 400){
			for (Node subcategory : subcategories) {
				walk(subcategory);
			}
		} else {
			return;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(new File(args[0])));
		Crawler crawler = new Crawler(props);
		crawler.start();		
	}
}
