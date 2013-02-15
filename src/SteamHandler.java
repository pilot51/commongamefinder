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
import java.net.URLConnection;
import java.util.ArrayList;

public class SteamHandler {
	static ArrayList<String> getGames(String username) {
		return parseGames(downloadGameList(username));
	}
	
	private static String downloadGameList(String username) {
		StringBuffer strbuff = new StringBuffer();
		try {
			URLConnection conn = new URL("http://steamcommunity.com/id/" + username + "/games?tab=all&sort=name").openConnection();
			InputStream input = new BufferedInputStream(conn.getInputStream());
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
	
	private static ArrayList<String> parseGames(String data) {
		ArrayList<String> list = new ArrayList<String>();
		while(data.contains("\"name\":\"")) {
			data = data.substring(data.indexOf("\"name\":\"") + 8);
			list.add(data.substring(0, data.indexOf("\"")));
		}
		return list;
	}
}
