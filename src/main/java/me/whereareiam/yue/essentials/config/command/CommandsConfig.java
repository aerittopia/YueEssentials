package me.whereareiam.yue.essentials.config.command;

import lombok.Getter;

@Getter
public class CommandsConfig {
	public InfoCommandConfig infoCommand = new InfoCommandConfig();
	public HelpCommandConfig helpCommand = new HelpCommandConfig();
}
