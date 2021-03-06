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
import java.util.List;

public class User {
	static final List<User> CACHE = new ArrayList<User>();
	private String name;
	private List<String> games;
	
	User(String name, List<String> games) {
		this.name = name;
		this.games = games;
		CACHE.add(this);
	}
	
	String getName() {
		return name;
	}
	
	List<String> getGames() {
		return games;
	}
}
