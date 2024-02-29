package com.aeritt.yue.essentials.command;

import com.aeritt.yue.api.command.base.CommandBase;
import com.aeritt.yue.api.command.management.CommandRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class CommandInitializer {
	private final CommandRegistrar commandRegistrar;
	private final ApplicationContext ctx;

	@Autowired
	public CommandInitializer(CommandRegistrar commandRegistrar, @Qualifier ApplicationContext ctx) {
		this.commandRegistrar = commandRegistrar;
		this.ctx = ctx;
	}

	public void initialize() {
		ctx.getBeansOfType(CommandBase.class).values().forEach(commandRegistrar::registerCommand);
	}

	public void reload() {
		ctx.getBeansOfType(CommandBase.class).values().forEach(commandRegistrar::unregisterCommand);
		initialize();
	}
}
