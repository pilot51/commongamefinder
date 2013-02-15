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

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	private static Scanner in = new Scanner(System.in);
	private ArrayList<User> users = new ArrayList<User>();
	
	public static void main(String[] args) {
		new Main();
	}
	
	private Main() {
		promptForUsers();
		System.out.println("\nMatching games for users:" );
		for (User user : users) {
			System.out.println(user.getName());
		}
		System.out.println("\nMatched games:");
		for (String game : matchGames()) {
			System.out.println(game);
		}
	}
	
	private void promptForUsers() {
		String username;
		do {
			int n = users.size() + 1;
			System.out.println("User #" + n + ": " + (n > 2 ? " (press enter again to match games)" : ""));
			username = in.nextLine();
			if (username.isEmpty()) break;
			users.add(new User(username, SteamHandler.getGames(username)));
		} while (users.size() < 2 || !username.isEmpty());
	}
	
	private ArrayList<String> matchGames() {
		ArrayList<String> matchedGames = new ArrayList<String>();
		for (User user : users) {
			if (matchedGames.isEmpty()) {
				matchedGames.addAll(user.getGames());
			} else {
				for (int i = matchedGames.size() - 1; i >= 0; i--) {
					if (!user.getGames().contains(matchedGames.get(i))) matchedGames.remove(i);
				}
			}
		}
		return matchedGames;
	}
}
