package com.aeritt.yue.essentials.command.commands;

import com.aeritt.yue.api.command.base.CommandBase;
import com.aeritt.yue.api.command.base.CommandCategory;
import com.aeritt.yue.api.command.management.CommandRegistrar;
import com.aeritt.yue.api.discord.DiscordButtonManager;
import com.aeritt.yue.api.discord.member.DiscordMemberManager;
import com.aeritt.yue.api.util.message.MessageBuilderUtil;
import com.aeritt.yue.api.util.message.MessageFormatterUtil;
import com.aeritt.yue.essentials.config.command.CommandsConfig;
import com.aeritt.yue.essentials.config.command.HelpCommandConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Lazy
@Component
public class HelpCommand extends CommandBase {
	private final MessageFormatterUtil messageFormatterUtil;
	private final MessageBuilderUtil messageBuilderUtil;
	private final DiscordButtonManager buttonManager;
	private final CommandRegistrar commandRegistrar;
	private final DiscordMemberManager memberManager;
	private final HelpCommandConfig helpCommand;

	@Autowired
	public HelpCommand(MessageFormatterUtil messageFormatterUtil, CommandsConfig commandsConfig,
	                   MessageBuilderUtil messageBuilderUtil, DiscordButtonManager buttonManager,
	                   CommandRegistrar commandRegistrar, DiscordMemberManager memberManager) {
		this.messageFormatterUtil = messageFormatterUtil;
		this.helpCommand = commandsConfig.getHelpCommand();
		this.messageBuilderUtil = messageBuilderUtil;
		this.buttonManager = buttonManager;
		this.commandRegistrar = commandRegistrar;
		this.memberManager = memberManager;
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		if (event.getOption("command") != null) {
			event.getHook().sendMessage("Ты опциональный пидор").queue();
			return;
		}

		buildHelpMessage(event);
	}

	private void buildHelpMessage(SlashCommandInteractionEvent event) {
		User user = event.getUser();

		MessageEmbed embed = messageBuilderUtil.embed(helpCommand.getEmbedId(), user, Optional.empty());
		List<CommandCategory> allowedCategories = getAllowedCategories(memberManager.getMember(user.getId()));

		List<Button> buttons = allowedCategories.stream()
				.map(category -> messageBuilderUtil.button(
						helpCommand.getButtonCategoryId() + category.name().toLowerCase(),
						user)
				).toList();
		buttons.forEach(button -> buttonManager.addButton(button.getId(), this::buttonInteraction));

		List<MessageEmbed.Field> fields = allowedCategories.stream()
				.map(category ->
						messageBuilderUtil.field(user,
								helpCommand.getFieldCategoryId() + category.name().toLowerCase() + ".name",
								helpCommand.getFieldCategoryId() + category.name().toLowerCase() + ".value",
								true
						)
				).toList();

		EmbedBuilder embedBuilder = new EmbedBuilder(embed);
		fields.forEach(embedBuilder::addField);

		embed = embedBuilder.build();

		if (!buttons.isEmpty())
			event.getHook().sendMessageEmbeds(embed).setActionRow(buttons).queue();
		else
			event.getHook().sendMessageEmbeds(embed).queue();
	}

	private List<CommandCategory> getAllowedCategories(Member member) {
		return commandRegistrar.getCommands().stream()
				.filter(command -> command.getRequiredRole().isEmpty() || member.getRoles().stream()
						.anyMatch(role -> role.getId().equals(command.getRequiredRole())))
				.map(CommandBase::getCategory)
				.distinct()
				.sorted(Comparator.comparing(Enum::name))
				.toList();
	}

	private void buttonInteraction(ButtonInteractionEvent event) {
		event.getMessage().editMessage("Ты пидор").setSuppressEmbeds(true).queue();
	}

	@Override
	public List<String> getCommandAliases() {
		return helpCommand.getCommand();
	}

	@Override
	public List<? extends CommandData> getCommand() {
		List<SlashCommandData> commandData = getCommandAliases().stream()
				.map(command ->
						Commands.slash(command, messageFormatterUtil.formatMessage(helpCommand.getShortDescription()))
				)
				.toList();

		commandData.forEach(command -> {
			command.addOption(
					OptionType.STRING,
					"command",
					messageFormatterUtil.formatMessage(
							helpCommand.getShortDescription().replace("shortDescription", "options.command.shortDescription")
					),
					false
			);

			command.setGuildOnly(isGuildOnly());
		});

		return commandData;
	}

	@Override
	public CommandCategory getCategory() {
		return CommandCategory.GENERAL;
	}

	@Override
	public String getRequiredRole() {
		return helpCommand.getRoleId();
	}

	@Override
	public List<String> getAllowedChannels() {
		return helpCommand.getAllowedChannels();
	}

	@Override
	public String getId() {
		return "helpCommand";
	}

	@Override
	public boolean isGuildOnly() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return helpCommand.isEnabled();
	}
}
