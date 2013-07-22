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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private final Container pane = getContentPane(),
	                        nameFields = new Container();
	private final JButton btnAddUser = new JButton("Add user"),
	                      btnFindGames = new JButton("Match games");
	private final JComboBox<SteamHandler.OS> selectOs = new JComboBox<SteamHandler.OS>(SteamHandler.OS.values());
	private final JComboBox<SteamHandler.Category> selectCat = new JComboBox<SteamHandler.Category>(SteamHandler.Category.values());
	private final JLabel gameListHeader = new JLabel("Matched games:");
	private final JList<String> gameList = new JList<String>();
	private final List<User> users = new ArrayList<User>();
	private List<String> games;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main();
			}
		});
	}
	
	private Main() {
		super("Common Game Finder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		nameFields.setLayout(new BoxLayout(nameFields, BoxLayout.Y_AXIS));
		addNameField();
		addNameField();
		pane.add(nameFields);
		btnAddUser.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnAddUser.addActionListener(this);
		pane.add(btnAddUser);
		pane.add(Box.createVerticalStrut(10));
		pane.add(selectOs);
		pane.add(selectCat);
		pane.add(Box.createVerticalStrut(10));
		btnFindGames.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnFindGames.addActionListener(this);
		pane.add(btnFindGames);
		pane.add(Box.createVerticalStrut(10));
		gameListHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		gameListHeader.setVisible(false);
		pane.add(gameListHeader);
		gameList.setVisible(false);
		pane.add(gameList);
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAddUser) {
			addNameField();
			pack();
		} else if (e.getSource() == btnFindGames) {
			users.clear();
			for (int i = nameFields.getComponentCount() - 1; i >= 0; i--) {
				String username = ((JTextField)nameFields.getComponent(i)).getText();
				boolean fromCache = false;
				if (!username.isEmpty()) {
					for (User u : User.CACHE) {
						if (u.getName().equalsIgnoreCase(username)) {
							users.add(u);
							fromCache = true;
							break;
						}
					}
					if (!fromCache) {
						users.add(new User(username, SteamHandler.getUserGames(username)));
					}
				} else {
					nameFields.remove(i);
				}
			}
			games = matchGames();
			if (games != null && !games.isEmpty()) {
				gameList.setListData(games.toArray(new String[games.size()]));
				gameListHeader.setVisible(true);
				gameList.setVisible(true);
			} else {
				gameListHeader.setVisible(false);
				gameList.setVisible(false);
			}
			pack();
		}
	}
	
	private void addNameField() {
		JTextField field = new JTextField(8);
		field.setAlignmentX(Component.CENTER_ALIGNMENT);
		nameFields.add(field);
	}
	
	private List<String> matchGames() {
		List<String> matchedGames = new ArrayList<String>();
		for (User user : users) {
			if (matchedGames.isEmpty()) {
				matchedGames.addAll(user.getGames());
			} else {
				for (int i = matchedGames.size() - 1; i >= 0; i--) {
					if (!user.getGames().contains(matchedGames.get(i))) matchedGames.remove(i);
				}
			}
		}
		SteamHandler.OS selectedOs = (SteamHandler.OS)selectOs.getSelectedItem();
		SteamHandler.Category selectedCat = (SteamHandler.Category)selectCat.getSelectedItem();
		if (selectedOs != SteamHandler.OS.ANY || selectedCat != SteamHandler.Category.ANY) {
			List<String> searchGames = SteamHandler.getSearchGames(selectedOs, selectedCat);
			for (int i = matchedGames.size() - 1; i >= 0; i--) {
				if (!searchGames.contains(matchedGames.get(i))) matchedGames.remove(i);
			}
		}
		return matchedGames;
	}
}
