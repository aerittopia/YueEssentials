package com.aeritt.yue.essentials.config.command;

import com.aeritt.yue.api.command.base.CommandCategory;
import lombok.Getter;

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
	private String shortDescription = "$t{essentials.commands.categories.general.help.shortDescription}";
	private String fullDescription = "$t{essentials.commands.categories.general.help.fullDescription}";
	private String roleId = "856952987896774696";
	private String embedId = "essentials.embed.commands.embeds.help";
	private String buttonCategoryId = "essentials.button.help.categories.";
	private String fieldCategoryId = "essentials.commands.categories.general.help.message.category.";
	private List<String> allowedChannels = new ArrayList<>();
}
