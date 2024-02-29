package com.aeritt.yue.essentials.command.commands;

import com.aeritt.yue.api.command.base.CommandBase;
import com.aeritt.yue.api.command.base.CommandCategory;
import com.aeritt.yue.api.language.LanguageService;
import com.aeritt.yue.api.service.PersonService;
import com.aeritt.yue.api.util.message.MessageBuilderUtil;
import com.aeritt.yue.api.util.message.MessageFormatterUtil;
import com.aeritt.yue.api.util.message.PlaceholderReplacement;
import com.aeritt.yue.essentials.config.command.CommandsConfig;
import com.aeritt.yue.essentials.config.command.InfoCommandConfig;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Lazy
@Component
public class InfoCommand extends CommandBase {
	private final MessageFormatterUtil messageFormatterUtil;
	private final InfoCommandConfig infoCommand;
	private final MessageBuilderUtil messageBuilderUtil;
	private final LanguageService languageService;
	private final PersonService personService;

	private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
	private final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
	private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

	@Autowired
	public InfoCommand(MessageFormatterUtil messageFormatterUtil, CommandsConfig commandsConfig,
	                   MessageBuilderUtil messageBuilderUtil, LanguageService languageService, PersonService personService) {
		this.messageFormatterUtil = messageFormatterUtil;
		this.infoCommand = commandsConfig.getInfoCommand();
		this.messageBuilderUtil = messageBuilderUtil;
		this.languageService = languageService;
		this.personService = personService;
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		User user = event.getUser();

		PlaceholderReplacement replacement = createPlaceholderReplacement();

		MessageEmbed embed = messageBuilderUtil.embed(infoCommand.getEmbedId(), user, Optional.of(replacement));

		event.getHook().sendMessageEmbeds(embed).queue();
	}

	private PlaceholderReplacement createPlaceholderReplacement() {
		DecimalFormat df = new DecimalFormat("00.0");

		String cpuLoad = df.format(osBean.getSystemLoadAverage());
		String cpuCores = String.valueOf(osBean.getAvailableProcessors());
		String memoryUsage = String.valueOf(getUsedMemory());
		String memoryTotal = String.valueOf(getTotalMemory());
		String memoryUsagePercentage = df.format(((double) getUsedMemory() / (double) getTotalMemory()) * 100);
		String uptime = String.format("%.1f", runtimeBean.getUptime() / 1000.0 / 60.0 / 60.0);
		String usersCount = String.valueOf(personService.getUserCount());
		final String languages = String.join(", ", languageService.getTranslations().keySet());

		return new PlaceholderReplacement(
				List.of("{osName}", "{osVersion}", "{osArch}", "{cpuLoad}", "{cpuCores}",
						"{memoryUsage}", "{memoryTotal}", "{memoryUsagePercentage}", "{uptime}",
						"{usersCount}", "{languages}"),
				List.of(System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"),
						cpuLoad, cpuCores, memoryUsage, memoryTotal, memoryUsagePercentage, uptime, usersCount, languages)
		);
	}

	private long getUsedMemory() {
		return memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
	}

	private long getTotalMemory() {
		return memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024;
	}


	@Override
	public List<String> getCommandAliases() {
		return infoCommand.getCommand();
	}

	@Override
	public List<? extends CommandData> getCommand() {
		List<SlashCommandData> commandData = getCommandAliases().stream()
				.map(command ->
						Commands.slash(command, messageFormatterUtil.formatMessage(infoCommand.getShortDescription()))
				)
				.toList();

		return commandData;
	}

	@Override
	public CommandCategory getCategory() {
		return CommandCategory.UTILITY;
	}

	@Override
	public String getRequiredRole() {
		return infoCommand.getRoleId();
	}

	@Override
	public List<String> getAllowedChannels() {
		return infoCommand.getAllowedChannels();
	}

	@Override
	public String getId() {
		return "infoCommand";
	}

	@Override
	public boolean isGuildOnly() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return infoCommand.isEnabled();
	}
}