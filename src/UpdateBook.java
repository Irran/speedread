import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class UpdateBook extends HttpServlet{
	
    public String encodedByMD5(String str) throws NoSuchAlgorithmException {
    	MessageDigest md5 =  MessageDigest.getInstance("MD5");
    	Base64.Encoder encoder = Base64.getEncoder();
    	return encoder.encodeToString(md5.digest(str.getBytes()));
    }
    
    public String readToEnd(String path) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(path));
		StringBuffer content = new StringBuffer();
		String str = null;
		boolean isfirst = true;
	    while((str = file.readLine()) != null) {
	    	if(isfirst)
	    		isfirst = false;
	    	else
	    		content.append("\n");
	    	content.append(str);
	    }
	    return content.toString();
    }
    
    public String getTestBookList() throws IOException, NoSuchAlgorithmException {
    	String basepath = this.getServletContext().getRealPath("/") + "test_books/";
		BufferedReader resource = new BufferedReader(new FileReader(basepath + "resources.txt"));
		ArrayList<String> reviewNames = new ArrayList<String>();
		ArrayList<String> reviews = new ArrayList<String>();
		ArrayList<String> imageNames = new ArrayList<String>();
		ArrayList<String> images = new ArrayList<String>();
		String line = null;
		while((line = resource.readLine()) != null) {
			String review = line.substring(0, line.indexOf(','));
			String image = line.substring(line.indexOf(',')+1);
			reviewNames.add(review);
		    reviews.add(encodedByMD5(readToEnd(basepath + review)));
		    imageNames.add(image);
		    images.add(encodedByMD5(readToEnd(basepath + image)));
		}
		JsonObject testBooksList = new JsonObject();
		testBooksList.addProperty("resource", "list");
		testBooksList.addProperty("type", "test_books");
		JsonArray content = new JsonArray();
		for(int i=0;i<reviewNames.size();i++) {
			JsonArray item = new JsonArray();
			item.add(reviewNames.get(i));
			item.add(reviews.get(i));
			item.add(imageNames.get(i));
			item.add(images.get(i));
			content.add(item);
		}
		testBooksList.add("content", content);
		return testBooksList.toString();
    }
    
    public String getTrainingBookList(int level) throws IOException, NoSuchAlgorithmException {
    	String basepath = String.format("%sclass_training/level_%d/",this.getServletContext().getRealPath("/"), level);
		BufferedReader resource = new BufferedReader(new FileReader(basepath + "resources.txt"));
		ArrayList<String> reviewNames = new ArrayList<String>();
		ArrayList<String> reviews = new ArrayList<String>();
		ArrayList<String> imageNames = new ArrayList<String>();
		ArrayList<String> images = new ArrayList<String>();
		String line = null;
		while((line = resource.readLine()) != null) {
			String review = line.substring(0, line.indexOf(','));
			String image = line.substring(line.indexOf(',')+1);
			reviewNames.add(review);
		    reviews.add(encodedByMD5(readToEnd(basepath + review)));
		    imageNames.add(image);
		    images.add(encodedByMD5(readToEnd(basepath + image)));
		}
		JsonObject trainingBooksList = new JsonObject();
		trainingBooksList.addProperty("resource", "list");
		trainingBooksList.addProperty("type", "class_training");
		trainingBooksList.addProperty("level", level);
		JsonArray content = new JsonArray();
		for(int i=0;i<reviewNames.size();i++) {
			JsonArray item = new JsonArray();
			item.add(reviewNames.get(i));
			item.add(reviews.get(i));
			item.add(imageNames.get(i));
			item.add(images.get(i));
			content.add(item);
		}
		trainingBooksList.add("content", content);
		return trainingBooksList.toString();
    }
    
    public String getImage(int level, String name) throws IOException {
		String path = "";
		JsonObject ret = new JsonObject();
    	ret.addProperty("resource", "image");
    	ret.addProperty("name", name);
		if(level == -1) {
			ret.addProperty("type", "test_books");
			path = this.getServletContext().getRealPath("/") + "test_books/" + name;
		}
		else {
			ret.addProperty("type", "class_training");
			ret.addProperty("level", level);
			path = String.format("%sclass_training/level_%d/",this.getServletContext().getRealPath("/") , level) + name;
		}
    	String content = readToEnd(path);
    	ret.addProperty("content", content);
    	return ret.toString();
    }
    
    public String getReview(String name) throws IOException {
    	String path =this.getServletContext().getRealPath("/") + "test_books/" + name;
    	JsonParser parser = new JsonParser();
    	JsonObject content = (JsonObject) parser.parse(new FileReader(path));
    	JsonObject ret = new JsonObject();
    	ret.addProperty("resource", "review");
    	ret.addProperty("name", name);
    	ret.add("content", content);
    	return ret.toString();
    }
    
    public String getBook(int level,String name) throws IOException {
		String path = "";
		JsonObject ret = new JsonObject();
    	
    	ret.addProperty("name", name);
		if(level == -1) {
			path =this.getServletContext().getRealPath("/") + "test_books/" + name;
			ret.addProperty("resource", "review");
			ret.addProperty("type", "test_books");
		} 
		else {
			ret.addProperty("resource", "book");
			ret.addProperty("type", "class_training");
			ret.addProperty("level", level);
			path = String.format("%sclass_training/level_%d/",this.getServletContext().getRealPath("/") , level) + name;
		}
    	String content = readToEnd(path);
    	ret.addProperty("content", content);
    	return ret.toString();
    }

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("utf-8");
		PrintWriter out = resp.getWriter();
		try {
			String jsonstr = req.getParameter("jsonstr");
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonstr);
			String cmd = json.get("cmd").getAsString();
			String type = json.get("type").getAsString();
			if(cmd.equals("check")) {
				if(type.equals("test_books")) {
					out.println(getTestBookList());
				}else if(type.equals("class_training")) {
					int level = json.get("level").getAsInt();
					out.println(getTrainingBookList(level));
				}
			}else if(cmd.equals("get_resource")){
				String name = json.get("name").getAsString();
				if(type.equals("test_books")) {
					String tag = json.get("tag").getAsString();
					int level = -1;
					if(tag.equals("image"))
						out.println(getImage(level, name));
					else if(tag.equals("review"))
						out.println(getBook(level, name));
				}else if(type.equals("class_training")) {
					int level = json.get("level").getAsInt();
					String tag = json.get("tag").getAsString();
					if(tag.equals("image"))
						out.println(getImage(level, name));
					else if(tag.equals("book"))
						out.println(getBook(level, name));
				}
			}
		} catch (Exception e) {
			out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
