package ru.apronin.crawler;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ru.apronin.crawler.basic.CategoryItem;
import ru.apronin.crawler.basic.WikiService;

public class WikiServiceTest {
	private 
	WikiService example = WikiService.INSTANCE;
	
	@Test
	public void testGetPageListByCategory() throws Exception {		
		List<CategoryItem> result = WikiService.INSTANCE.getCategoryItems("Автомобили");
		assertNotNull(result);
		System.out.println(result.size());
		for (CategoryItem item : result) {
			System.out.println(String.format("id: %s; title: %s; type: %s", item.getPageid(), item.getTitle(), item.getType()));
		}
	}
	
	@Test
	public void testGetPageText() throws Exception {
		String text = WikiService.INSTANCE.getPageText("1781066").getText();
		assertNotNull(text);
		System.out.println(text);
	}

}
