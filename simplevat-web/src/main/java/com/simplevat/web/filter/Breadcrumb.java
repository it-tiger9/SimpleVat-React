package com.simplevat.web.filter;

import java.util.LinkedList;

public class Breadcrumb {
	
	private LinkedList<String> urlList = new LinkedList<String>();
	
	public void addUri(String uri){
		if(urlList.contains(uri)){
			urlList.remove(uri);
		}
		urlList.add(uri);
	}
	
	public String getCurrentUri(){
		return urlList.element();
	}
	
	public String getPreviousUri(){
		return urlList.get(urlList.size()-2);
	}
	
	public String getPopPreviousUri(){
		return urlList.get(urlList.size()-2);
	}
	
}