package ru.apronin.crawler.basic;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageWorker implements Runnable{
	private static final String DELAY = "delay";
	private static final String RESULT_FOLDER = "result_folder";
	private static final String CSV_FILE_NAME = "csv_name";
	private static final Logger logger = Logger.getLogger(PageWorker.class.getName());
	private Node node;
	private Properties properties;
	
	public PageWorker(Node node, Properties properties) {
		this.node = node;
		this.properties = properties;
		logger.log(Level.INFO, String.format("create worker for page with category %s, level %s ", node.getCategory(), node.getLevel()));
	}

	@Override
	public void run() {
		FileChannel csvFile = null;
		FileChannel wikiTextFile = null;
		try {
			int delay = Integer.parseInt(properties.getProperty(DELAY));
			if(delay > 0){
				TimeUnit.SECONDS.sleep(delay);
			}
			PageItem pageItem = WikiService.INSTANCE.getPageText(node.getPageId());
			String csvString = String.format("%s;%s;%s;%s;%s;%s\n", node.getFileName(), node.getName(), pageItem.getUrl(), node.getCode(), node.getLevel(), pageItem.getText().length());
			logger.log(Level.INFO, "csv string: {0}", csvString);
			csvFile = FileChannel.open(Paths.get(properties.getProperty(CSV_FILE_NAME)), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			csvFile.write(ByteBuffer.wrap(csvString.getBytes()));
			logger.log(Level.INFO, "save text file to path {0} with file name {0}.txt", node.getFileName());
			new File(properties.getProperty(RESULT_FOLDER) + node.getFolderPath()).mkdirs();
			logger.log(Level.INFO, "folder created");
			Path path = Paths.get(properties.getProperty(RESULT_FOLDER) + node.getFolderPath()+node.getFileName() + ".txt");
			wikiTextFile = FileChannel.open(path,StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			wikiTextFile.write(ByteBuffer.wrap(pageItem.getText().getBytes()));
			logger.log(Level.INFO, "completed");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(csvFile != null){
					csvFile.close();
				}
				if(wikiTextFile != null){
					wikiTextFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
