package server;

public enum Command {
	
	CMD_HELLO("hello")	
	, CMD_LETTER("letter")	
	, CMD_DISCONNECTED("disconnected")
	, CMD_REFERSH_PLAYERS("refresh_player_list")
	, CMD_WORD_UPDATED("word_updated")
	, CMD_UNKNOWN("");
	
	String cmd;
	
	Command(String cmd) {
		this.cmd = cmd;
	}
	
	@Override
	public String toString() {
		return cmd;
	}
	
	public static Command resolve(String cmd) {
		for(Command c : Command.values()) {
			if (c.cmd.equals(cmd)) { return c; }
		}
		return CMD_UNKNOWN;
	}
	
}
