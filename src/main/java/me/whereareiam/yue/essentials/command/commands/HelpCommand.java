package me.whereareiam.yue.essentials.command.commands;

import me.whereareiam.yue.api.command.base.CommandBase;
import me.whereareiam.yue.api.command.base.CommandCategory;
import me.whereareiam.yue.api.command.management.CommandRegistrar;
import me.whereareiam.yue.api.discord.DiscordButtonManager;
import me.whereareiam.yue.api.util.message.MessageBuilderUtil;
import me.whereareiam.yue.api.util.message.MessageFormatterUtil;
import me.whereareiam.yue.essentials.config.command.CommandsConfig;
import me.whereareiam.yue.essentials.config.command.HelpCommandConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
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
	private final HelpCommandConfig helpCommand;
	private final MessageBuilderUtil messageBuilderUtil;
	private final DiscordButtonManager buttonManager;
	private final CommandRegistrar commandRegistrar;
	private final Guild guild;

	@Autowired
	public HelpCommand(MessageFormatterUtil messageFormatterUtil, CommandsConfig commandsConfig,
	                   MessageBuilderUtil messageBuilderUtil, DiscordButtonManager buttonManager,
	                   CommandRegistrar commandRegistrar, @Lazy Guild guild) {
		this.messageFormatterUtil = messageFormatterUtil;
		this.helpCommand = commandsConfig.getHelpCommand();
		this.messageBuilderUtil = messageBuilderUtil;
		this.buttonManager = buttonManager;
		this.commandRegistrar = commandRegistrar;
		this.guild = guild;
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
		List<CommandCategory> allowedCategories = getAllowedCategories(guild.getMember(user));

		List<Button> buttons = allowedCategories.stream()
				.map(category -> messageBuilderUtil.button(
						category.name().toLowerCase(),
						user)
				).toList();
		buttons.forEach(button -> buttonManager.addButton(button.getId(), this::buttonInteraction));

		List<MessageEmbed.Field> fields = allowedCategories.stream()
				.map(category ->
						messageBuilderUtil.field(user,
								"core.command.general.help.message.category." + category.name().toLowerCase() + ".name",
								"core.command.general.help.message.category." + category.name().toLowerCase() + ".value",
								true
						)
				).toList();

		EmbedBuilder embedBuilder = new EmbedBuilder(embed);
		fields.forEach(embedBuilder::addField);

		embed = embedBuilder.build();
		event.getHook().sendMessageEmbeds(embed).setActionRow(buttons).queue();
	}

	private List<CommandCategory> getAllowedCategories(Member member) {
		return commandRegistrar.getCommands().stream()
				.filter(command -> member.getRoles().stream()
						.anyMatch(role -> role.getId().equals(command.getRequiredRole()))
				)
				.map(CommandBase::getCategory)
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
