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
package com.iminurnetz.bukkit.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.ConfigurationException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.plugin.util.PluginLogger;

public abstract class BukkitPlugin extends JavaPlugin {
	protected Map<String, Object> root;
	protected PluginLogger logger;

	protected int MIN_SERVER_VERSION = 400;
	protected int MAX_SERVER_VERSION = Integer.MAX_VALUE;

	public BukkitPlugin() {
		logger = new PluginLogger(this);

		// we are loading plugin.yml ourselves
		URL configUrl = getClass().getResource("/plugin.yml");
		Yaml yaml = new Yaml(new SafeConstructor(), new Representer());
		InputStream stream = null;

		try {
			stream = configUrl.openStream();
			read(yaml.load(new UnicodeReader(stream)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot access plugin.yml");
		} catch (ConfigurationException e) {
			logger.log(Level.SEVERE, "Cannot access plugin.yml");
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}

		logger.log("initialized");
	}
	
	public PluginLogger getLogger() {
		return logger;
	}

	// simple shortcut
	public void log(String msg) {
		getLogger().log(msg);
	}

	// simple shortcut
	public void log(Level level, String msg) {
		getLogger().log(level, msg);
	}

    // simple shortcut
    public void log(Level level, String msg, Exception e) {
        getLogger().log(level, msg, e);
    }

    // simple shortcut
    public void log(String msg, Exception e) {
        log(Level.SEVERE, msg, e);
    }

    public String getName() {
		try {
			return root.get("name").toString();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, this.getClass().getSimpleName()
					+ " has no name yet!");
		}

		return "";
	}

	public String getVersion() {
		try {
			return root.get("version").toString();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, this.getClass().getSimpleName()
					+ " has no version yet!");
		}

		return "";
	}
	
	public int getServerVersion() {
	    String[] sv = getServer().getVersion().split("-");
	    int version = -1;
	    try {
	        version = Integer.valueOf(sv[3]);
	    } catch (Exception e) {
	        log("Unfamiliar version string " + getServer().getVersion());
	    }
	    
	    return version;
	}

	@SuppressWarnings("unchecked")
	private void read(Object input) throws ConfigurationException {
		try {
			root = (Map<String, Object>) input;
		} catch (ClassCastException e) {
			throw new ConfigurationException(
					"Root document must be an key-value structure");
		}
	}

	public String getFullMessagePrefix() {
		return getFullMessagePrefix(ChatColor.WHITE);
	}
	
	public String getFullMessagePrefix(ChatColor color) {
		return MessageUtils.colorize(color, "[" + getName() + " " + getVersion() + "] ");
	}
	
	public String getMessagePrefix() {
		return "[" + getName() + "] ";
	}
	
	public String getMessagePrefix(ChatColor color) {
		return MessageUtils.colorize(color, getMessagePrefix());
	}

	@Override
	public void onDisable() {
		getLogger().log("un-loaded");
	}
	
	@Override
	public final void onEnable() {
	    int serverVersion = getServerVersion();
        try {
            if (serverVersion > 0 &&
                (serverVersion < getMinimumServerVersion() || serverVersion > getMaximumServerVersion())) {

                throw new UnsupportedServerVersionException(
                        "This plugin only supports server versions "
                                + getMinimumServerVersion() + " to "
                                + getMaximumServerVersion());
            }

            if (serverVersion > 0) {
                log("Server version compatibility check succeeded");
            }

            enablePlugin();
        } catch (Exception e) {
            log("Error enabling! ABORTED", e);
            this.setEnabled(false);
        }
	}
	
	protected int getMinimumServerVersion() { return MIN_SERVER_VERSION; }
    protected int getMaximumServerVersion() { return MAX_SERVER_VERSION; }
    
    /**
     * This method will be called when the onEnable() method is called.
     * @throws Exception 
     */
    public abstract void enablePlugin() throws Exception;
}
