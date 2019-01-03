package game;

import java.util.Collections;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Sets;

public enum PlayerStatus {
	
	INVISIBLE, CREATED, WAITING_FOR_ENTER_WORD, PLAYING, WAITING_FOR_LETTER;
	
	public static Set<PlayerStatus> busyStatuses = Sets.immutableEnumSet( WAITING_FOR_ENTER_WORD, PLAYING, WAITING_FOR_LETTER);

}
