/*
 * Copyright 2013 Mark Injerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SteamHandler {
	static List<String> getUserGames(String username) {
		return parseGames(downloadUserGames(username));
	}
	
	private static String downloadUserGames(String username) {
		StringBuffer strbuff = new StringBuffer();
		try {
			URL url = new URL("http://steamcommunity.com/id/" + username + "/games?tab=all&sort=name");
			InputStream input = new BufferedInputStream(url.openConnection().getInputStream());
			byte data[] = new byte[1024];
			int count;
			while ((count = input.read(data)) != -1) {
				strbuff.append(new String(data, 0, count));
			}
			input.close();
		} catch (IOException e) {
			return null;
		}
		return strbuff.toString().replaceAll("\r\n", "\n");
	}
	
	private static List<String> parseGames(String data) {
		List<String> list = new ArrayList<String>();
		while(data.contains("\"name\":\"")) {
			data = data.substring(data.indexOf("\"name\":\"") + 8);
			list.add(data.substring(0, data.indexOf("\"")));
		}
		return list;
	}
	
	private static final List<String> SEARCH_CACHE = new ArrayList<String>();
	private static int pages = Integer.MAX_VALUE;
	
	static List<String> getSearchGames() {
		List<String> list = new ArrayList<String>();
		if (SEARCH_CACHE.isEmpty()) {
			for (int p = 1; p <= pages; p++) {
				list.addAll(parseSearch(downloadSearchList(OS.ANY, Category.MULTI, p)));
			}
			SEARCH_CACHE.addAll(list);
		} else {
			list.addAll(SEARCH_CACHE);
		}
		return list;
	}
	
	private static String downloadSearchList(OS os, Category cat, int page) {
		StringBuffer strbuff = new StringBuffer();
		try {
			URL url = new URL("http://store.steampowered.com/search/?os=" + os.getId() + "&category1=998&category2=" + cat.getId()
			                  + "&sort_by=Name&sort_order=ASC&page=" + page);
			InputStream input = new BufferedInputStream(url.openConnection().getInputStream());
			byte data[] = new byte[1024];
			int count;
			while ((count = input.read(data)) != -1) {
				strbuff.append(new String(data, 0, count));
			}
			input.close();
		} catch (IOException e) {
			return null;
		}
		return strbuff.toString().replaceAll("\r\n", "\n");
	}
	
	private static List<String> parseSearch(String data) {
		if (pages == Integer.MAX_VALUE) {
			String pagesSnippet = data.substring(data.indexOf("&nbsp;<"), data.indexOf(">&gt;&gt;<"));
			pages = Integer.parseInt(pagesSnippet.substring(pagesSnippet.indexOf("&page=") + 6, pagesSnippet.indexOf("\" ")));
		}
		List<String> list = new ArrayList<String>();
		while(data.contains("<h4>")) {
			data = data.substring(data.indexOf("<h4>") + 4);
			list.add(data.substring(0, data.indexOf("</h4>")));
		}
		return list;
	}
	
	private enum OS {
		ANY("", "Any OS"),
		LINUX("linux", "Linux"),
		MAC("mac", "Mac"),
		WIN("win", "Windows"),
		STEAMPLAY("steamplay", "Steam Play");
		
		private String id;
		private String name;
		
		private OS(String id, String name) {
			this.id = id;
			this.name = name;
		}
		
		private String getId() {
			return id;
		}
		
		private String getName() {
			return name;
		}
	}
	
	private enum Category {
		ANY(0, "Any"),
		MULTI(1, "Multi-player"),
		COOP(9, "Co-op"),
		CROSS_MULTI(27, "Cross-Platform Multiplayer");
		
		private int id;
		private String name;
		
		private Category(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		private int getId() {
			return id;
		}
		
		private String getName() {
			return name;
		}
	}
}
