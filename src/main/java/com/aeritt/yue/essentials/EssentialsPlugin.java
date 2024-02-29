package com.aeritt.yue.essentials;

import com.aeritt.yue.api.YuePlugin;
import com.aeritt.yue.essentials.command.CommandInitializer;
import com.aeritt.yue.essentials.config.ConfigManager;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EssentialsPlugin extends YuePlugin {
	public EssentialsPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void onLoad() {
		getApplicationContext().getBean(ConfigManager.class).loadConfigs();
	}

	@Override
	public void onEnable() {
		getApplicationContext().getBean(CommandInitializer.class).initialize();
	}

	@Override
	public void onUnload() {

	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onReload() {

	}

	@Override
	public ApplicationContext createApplicationContext() {
		ApplicationContext parentContext = ((SpringPluginManager) getWrapper().getPluginManager()).getApplicationContext();

		AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
		childContext.setClassLoader(getWrapper().getPluginClassLoader());
		childContext.setParent(parentContext);
		childContext.registerBean(PluginWrapper.class, this::getWrapper);
		childContext.register(EssentialsConfiguration.class);
		childContext.scan("com.aeritt.yue.essentials");
		childContext.refresh();

		this.applicationContext = childContext;

		return childContext;
	}
}
