package com.WazaBe.HoloEverywhere.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.LayoutInflater.LayoutInflaterCreator;
import com.WazaBe.HoloEverywhere.Setting;
import com.WazaBe.HoloEverywhere.SystemServiceManager;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.ThemeManager.ThemedIntentStarter;

public class Application extends android.app.Application implements
		ThemedIntentStarter {
	public static final class Config extends Setting<Config> {
		public static enum PreferenceImpl {
			JSON, XML
		}

		private static final String DEFAULT_HOLO_EVERYWHERE_PACKAGE = "com.WazaBe.HoloEverywhere";

		private static void onStateChange(Config config) {
			String p = config.holoEverywherePackage.getValue();
			if (p != null && p.length() > 0) {
				config.setWidgetsPackage(p + ".widget");
				config.setPreferencePackage(p + ".preference");
			}
		}

		private final SettingListener<Config> _DEFAULT_SETTINGS_LISTENER = new SettingListener<Config>() {
			@Override
			public void onAttach(Config config) {
				onStateChange(config);
			}

			@Override
			public void onDetach(Config config) {
			}

			@Override
			public void onPropertyChange(Config config, Property<?> property) {
				if (property == config.holoEverywherePackage) {
					onStateChange(config);
				}
			}

		};
		@SettingProperty(create = true, defaultBoolean = false)
		private BooleanProperty alwaysUseParentTheme;
		@SettingProperty(create = true, defaultBoolean = false)
		private BooleanProperty debugMode;
		@SettingProperty(create = true)
		private BooleanProperty disableContextMenu;
		@SettingProperty(create = true, defaultString = Config.DEFAULT_HOLO_EVERYWHERE_PACKAGE)
		private StringProperty holoEverywherePackage;
		@SettingProperty(create = true, defaultEnum = "JSON", enumClass = PreferenceImpl.class)
		private EnumProperty<PreferenceImpl> preferenceImpl;
		@SettingProperty(create = true)
		private StringProperty preferencePackage;
		@SettingProperty(create = true, defaultBoolean = false)
		private BooleanProperty useThemeManager;

		@SettingProperty(create = true)
		private StringProperty widgetsPackage;

		public Config attachDefaultListener() {
			return addListener(_DEFAULT_SETTINGS_LISTENER);
		}

		public Config detachDefaultListener() {
			return removeListener(_DEFAULT_SETTINGS_LISTENER);
		}

		public String getHoloEverywherePackage() {
			return holoEverywherePackage.getValue();
		}

		public PreferenceImpl getPreferenceImpl() {
			return preferenceImpl.getValue();
		}

		public String getPreferencePackage() {
			return preferencePackage.getValue();
		}

		public String getWidgetsPackage() {
			return widgetsPackage.getValue();
		}

		public boolean isAlwaysUseParentTheme() {
			return alwaysUseParentTheme.getValue();
		}

		public boolean isDebugMode() {
			return debugMode.getValue();
		}

		public boolean isDisableContextMenu() {
			return disableContextMenu.getValue();
		}

		public boolean isUseThemeManager() {
			return useThemeManager.getValue();
		}

		@Override
		protected void onInit() {
			attachDefaultListener();
			SystemServiceManager.register(LayoutInflaterCreator.class);
		}

		public Config setAlwaysUseParentTheme(boolean alwaysUseParentTheme) {
			this.alwaysUseParentTheme.setValue(alwaysUseParentTheme);
			return this;
		}

		public Config setDebugMode(boolean debugMode) {
			this.debugMode.setValue(debugMode);
			return this;
		}

		public Config setDisableContextMenu(boolean disableContextMenu) {
			this.disableContextMenu.setValue(disableContextMenu);
			return this;
		}

		public Config setHoloEverywherePackage(String holoEverywherePackage) {
			this.holoEverywherePackage.setValue(holoEverywherePackage);
			return this;
		}

		public Config setPreferenceImpl(PreferenceImpl preferenceImpl) {
			this.preferenceImpl.setValue(preferenceImpl);
			return this;
		}

		public Config setPreferencePackage(String preferencePackage) {
			this.preferencePackage.setValue(preferencePackage);
			return this;
		}

		public Config setUseThemeManager(boolean useThemeManager) {
			this.useThemeManager.setValue(useThemeManager);
			return this;
		}

		public Config setWidgetsPackage(String widgetsPackage) {
			this.widgetsPackage.setValue(widgetsPackage);
			return this;
		}
	}

	private static Application lastInstance;

	public static Config getConfig() {
		return Setting.get(Config.class);
	}

	public static Application getLastInstance() {
		return Application.lastInstance;
	}

	public static boolean isDebugMode() {
		return Application.getConfig().isDebugMode();
	}

	public Application() {
		Application.lastInstance = this;
	}

	@Override
	public void onTerminate() {
		LayoutInflater.clearInstances();
		super.onTerminate();
	}

	@Override
	@SuppressLint("NewApi")
	public void startActivities(Intent[] intents) {
		startActivities(intents, null);
	}

	@Override
	@SuppressLint("NewApi")
	public void startActivities(Intent[] intents, Bundle options) {
		for (Intent intent : intents) {
			startActivity(intent, options);
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void startActivity(Intent intent) {
		startActivity(intent, null);
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		if (Application.getConfig().isAlwaysUseParentTheme()) {
			ThemeManager.startActivity(this, intent, options);
		} else {
			superStartActivity(intent, -1, options);
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void superStartActivity(Intent intent, int requestCode,
			Bundle options) {
		if (VERSION.SDK_INT >= 16) {
			super.startActivity(intent, options);
		} else {
			super.startActivity(intent);
		}
	}
}
