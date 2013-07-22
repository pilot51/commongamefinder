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
	
	private static int pages;
	
	static List<String> getSearchGames(OS os, Category cat) {
		List<String> list = SearchCache.getGames(os, cat);
		if (list == null) {
			list = new ArrayList<String>();
			pages = 0;
			for (int p = 1; p <= pages || pages == 0; p++) {
				list.addAll(parseSearch(downloadSearchList(os, cat, p)));
			}
			SearchCache.add(os, cat, list);
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
		if (pages == 0) {
			int pagePointer = data.lastIndexOf("&nbsp;...&nbsp;");
			if (pagePointer == -1) pagePointer = data.lastIndexOf("&nbsp;|&nbsp;");
			pages = Integer.parseInt(data.substring(data.indexOf(">", pagePointer) + 1, data.indexOf("</a>", pagePointer)));
		}
		List<String> list = new ArrayList<String>();
		while(data.contains("<h4>")) {
			data = data.substring(data.indexOf("<h4>") + 4);
			list.add(data.substring(0, data.indexOf("</h4>")));
		}
		return list;
	}
	
	enum OS {
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
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	enum Category {
		ANY(0, "Any category"),
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
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private static class SearchCache {
		private static final List<SearchCache> CACHES = new ArrayList<SearchCache>();
		private OS os;
		private Category category;
		private List<String> games;
		
		private SearchCache(OS os, Category category, List<String> games) {
			this.os = os;
			this.category = category;
			this.games = games;
		}
		
		private static void add(OS os, Category category, List<String> games) {
			CACHES.add(new SearchCache(os, category, games));
		}
		
		private static List<String> getGames(OS os, Category cat) {
			for (SearchCache c : CACHES) {
				if (c.os == os && c.category == cat) {
					return c.games;
				}
			}
			return null;
		}
	}
}
