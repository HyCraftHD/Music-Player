package info.u_team.music_player.musicplayer;

import java.util.*;

public class Playlists {
	
	private List<Playlist> playlists;
	
	public Playlists() {
		playlists = new ArrayList<>();
	}
	
	public int size() {
		return playlists.size();
	}
	
	public Playlist get(int index) {
		return playlists.get(index);
	}
	
	public void add(Playlist playlist) {
		playlists.add(playlist);
	}
	
	public void remove(Playlist playlist) {
		playlists.remove(playlist);
	}
	
}