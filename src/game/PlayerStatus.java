package game;

import java.util.Set;

import jersey.repackaged.com.google.common.collect.Sets;

public enum PlayerStatus {
	
	INVISIBLE, CREATED, PLAYING;//;
	
	public static Set<PlayerStatus> busyStatuses = Sets.immutableEnumSet( PLAYING );//, WON, LOST );

}
