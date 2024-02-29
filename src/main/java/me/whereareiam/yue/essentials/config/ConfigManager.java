package me.whereareiam.yue.essentials.config;

import me.whereareiam.yue.api.config.ConfigService;
import me.whereareiam.yue.essentials.config.command.CommandsConfig;
import me.whereareiam.yue.essentials.config.setting.SettingsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigManager {
	private final ConfigService configService;

	@Autowired
	public ConfigManager(ConfigService configService) {
		this.configService = configService;
	}

	public void loadConfigs() {
		configService.registerConfig(SettingsConfig.class, "", "settings.json");
		configService.registerConfig(CommandsConfig.class, "", "commands.json");
	}
}
