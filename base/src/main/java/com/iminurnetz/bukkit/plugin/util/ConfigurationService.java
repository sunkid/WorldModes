/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid@iminurnetz.com
 */
package com.iminurnetz.bukkit.plugin.util;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;

public class ConfigurationService {

	private static final String CONFIG_FILE = "config.yml";
    private FileConfiguration config;
	
	private final BukkitPlugin plugin;
    private final double lastChangedInVersion;

	// global settings
	public static final String SETTINGS_TAG = "settings";
	public static final String USER_SETTINGS_TAG = SETTINGS_TAG + ".user";
		
	public static final boolean ALLOW_OPS = true;
	public static final boolean IS_ENABLED = false;
	public static final boolean DEBUG = false;

    public ConfigurationService(BukkitPlugin plugin, double lastChangedInVersion) {
		this.plugin = plugin;
		this.lastChangedInVersion = lastChangedInVersion;
		load();
	}

	/**
	 * @return the plugin
	 */
	public BukkitPlugin getPlugin() {
		return plugin;
	}

	/**
	 * @return the lastChangedInVersion
	 */
    public double getLastChangedInVersion() {
		return lastChangedInVersion;
	}

	protected void load() {
        File configFile = getPlugin().getDataFile(CONFIG_FILE);
        if (!configFile.exists()) {
            getPlugin().writeResourceToDataFolder(CONFIG_FILE);
        }
		
        config = getPlugin().getConfig();
        double version = config.getDouble(SETTINGS_TAG + ".version", -1);
        if (version != getLastChangedInVersion()) {
            getPlugin().log(Level.WARNING, "Your configuration file is outdated (" + version + " vs. " + getLastChangedInVersion() + "), please read config-new.yml");
            getPlugin().writeResourceToDataFolder(CONFIG_FILE, "config-new.yml");
        }
	}

	public boolean isEnabled() {
		return getSettingsAsBoolean("enabled", IS_ENABLED);
	}

	public boolean isDebug() {
		return getUserSettingsAsBoolean("debug", DEBUG);
	}
	
	public boolean getSettingsAsBoolean(String setting) {
		return getSettingsAsBoolean(setting, false);
	}
	
	public boolean getSettingsAsBoolean(String setting, boolean defaultBool) {
        return config.getBoolean(SETTINGS_TAG + "." + setting, defaultBool);
	}

	public boolean getUserSettingsAsBoolean(String setting) {
		return getUserSettingsAsBoolean(setting, false);
	}
	
	public boolean getUserSettingsAsBoolean(String setting, boolean defaultBool) {
        return config.getBoolean(USER_SETTINGS_TAG + "." + setting, defaultBool);
	}

	public int getSettingsAsInt(String setting) {
		return getSettingsAsInt(setting, 0);
	}
	
	public int getSettingsAsInt(String setting, int defaultInt) {
        return config.getInt(SETTINGS_TAG + "." + setting, defaultInt);
	}

	public int getUserSettingsAsInt(String setting) {
		return getUserSettingsAsInt(setting, 0);
	}
	
	public int getUserSettingsAsInt(String setting, int defaultInt) {
        return config.getInt(USER_SETTINGS_TAG + "." + setting, defaultInt);
	}

	public String getSettings(String setting) {
		return getSettings(setting, "");
	}

	public String getSettings(String setting, String defaultString) {
        return config.getString(SETTINGS_TAG + "." + setting, defaultString);
	}

    public FileConfiguration getConfiguration() {
        return config;
    }

    public Set<String> getConfigurationNodes(String path) {
        return config.getConfigurationSection(path).getValues(false).keySet();
    }
}
