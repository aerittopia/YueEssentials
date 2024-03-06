package com.aeritt.yue.essentials.config.command;

import com.aeritt.yue.api.command.base.CommandCategory;
import lombok.Getter;

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
	private String shortDescription = "$t{essentials.commands.categories.utility.info.shortDescription}";
	private String fullDescription = "$t{essentials.commands.categories.utility.info.fullDescription}";
	private String roleId = "856952987896774696";
	private String embedId = "essentials.embed.commands.embeds.info";
	private List<String> allowedChannels = new ArrayList<>();
}
