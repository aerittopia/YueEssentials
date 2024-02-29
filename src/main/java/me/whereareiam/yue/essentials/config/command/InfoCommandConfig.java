package me.whereareiam.yue.essentials.config.command;

import lombok.Getter;
import me.whereareiam.yue.api.command.base.CommandCategory;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InfoCommandConfig {
	private boolean enabled = true;
	private List<String> command = List.of(
			"info",
			"botinfo"
	);
	private CommandCategory category = CommandCategory.UTILITY;
	private String shortDescription = "$t{essentials.command.utility.info.shortDescription}";
	private String fullDescription = "$t{essentials.command.utility.info.fullDescription}";
	private String roleId = "1209471775805800478";
	private String embedId = "essentials.embed.commands.embeds.info";
	private List<String> allowedChannels = new ArrayList<>();
}
