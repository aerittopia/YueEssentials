package me.whereareiam.yue.essentials.config.command;

import lombok.Getter;
import me.whereareiam.yue.api.command.base.CommandCategory;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HelpCommandConfig {
	private boolean enabled = true;
	private List<String> command = List.of(
			"help",
			"helpme"
	);
	private CommandCategory category = CommandCategory.GENERAL;
	private String shortDescription = "$t{core.commands.general.help.shortDescription}";
	private String fullDescription = "$t{core.commands.general.help.fullDescription}";
	private String roleId = "";
	private String embedId = "essentials.embed.commands.embeds.help";
	private List<String> allowedChannels = new ArrayList<>();
}
