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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.plugin.util.PluginLogger;
import com.iminurnetz.util.DownloadUtils;

public abstract class BukkitPlugin extends JavaPlugin {
	private static final String BASE_BUKKIT_PLUGIN = "BaseBukkitPlugin";

    protected PluginLogger logger;

	protected int MIN_SERVER_VERSION = 400;
	protected int MAX_SERVER_VERSION = Integer.MAX_VALUE;

    private PluginDescriptionFile description;

    private static final String HOME_URL = "http://www.iminurnetz.com/mcStats.cgi";

    public static final String REPOSITORY = "https://raw.github.com/sunkid/BaseBukkitPlugin/master/release/";

	public BukkitPlugin() {
        try {
            InputStream is = getClass().getResourceAsStream("/plugin.yml");
            description = new PluginDescriptionFile(is);
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
        }
        
        logger = new PluginLogger(this);
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

    @Override
    public PluginDescriptionFile getDescription() {
        return description;
    }

    public String getName() {
        return getDescription().getName();
	}

	public String getVersion() {
        return getDescription().getVersion();
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

            boolean isNotBaseBukkitPlugin = getServer().getPluginManager().getPlugin(BASE_BUKKIT_PLUGIN) == null;
            if (isNotBaseBukkitPlugin) {
                updateAndLoadBaseBukkitPlugin();
            }

            enablePlugin();

            if (isNotBaseBukkitPlugin) {
                Configuration config = getConfig();
                if (!config.getBoolean("settings.disable-stats", false)) {
                    getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        public void run() {
                            postUsage();
                        }
                    });
                }
            }

        } catch (Exception e) {
            log("Error enabling! ABORTED", e);
            this.setEnabled(false);
        }
	}
	
    protected void postUsage() {
        log("Sending anonymous usage data...");
        log("This can be disabled by setting 'settings.disable-stats' to true in config.yml");

        HashMap<String, String> values = new HashMap<String, String>();
        String ip = getServer().getIp();
        if (ip.equals("")) {
            ip = "*";
        }

        values.put("ip", ip);
        values.put("port", String.valueOf(getServer().getPort()));
        values.put("plugin", getName());
        values.put("version", getVersion());

        try {
            log(DownloadUtils.post(HOME_URL, values));
        } catch (IOException e) {
            // ignored
        }
    }

    public void updateAndLoadBaseBukkitPlugin() throws Exception {
        Plugin plugin;

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File timeStamp = new File(dataFolder, "lastVersionCheck.txt");
        boolean checkServer = true;
        if (!timeStamp.exists()) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(timeStamp));
                out.write("remove this file to force the version check for BaseBukkitPlugin\n");
                out.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Cannot create time stamp", e);
            }
        } else {
            Date lastModified = new Date(timeStamp.lastModified());
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            
            checkServer = lastModified.before(yesterday.getTime());
        }

        File pFile = new File(dataFolder, BASE_BUKKIT_PLUGIN + ".jar");

        String baseVersion = "0";

        if (pFile.exists()) {
            JarFile jarFile = new JarFile(pFile);
            JarEntry entry = jarFile.getJarEntry("plugin.yml");
            InputStream stream = jarFile.getInputStream(entry);

            PluginDescriptionFile desc = new PluginDescriptionFile(stream);
            baseVersion = desc.getVersion();
            stream.close();
            jarFile.close();
        } else {
            checkServer = true;
        }

        PluginManager pm = getServer().getPluginManager();

        if (checkServer) {
            logger.log("BaseBukkitPlugin version check...");

            String latestVersion = getLatestVersionFromRepository();

            if (!latestVersion.equals(baseVersion)) {
                logger.log("Latest version " + latestVersion + " is newer than the installed version " + baseVersion);
                URL jarUrl = new URL(REPOSITORY + BASE_BUKKIT_PLUGIN + ".jar");
                DownloadUtils.download(logger, jarUrl, pFile);
            }
        }

        plugin = pm.loadPlugin(pFile);
        pm.enablePlugin(plugin);

        try {
            if (!timeStamp.setLastModified((new Date()).getTime())) {
                throw new Exception("Setting last modified time stamp did not succeed!");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot update time stamp", e);
        }
    }

    public static String getLatestVersionFromRepository() throws IOException {
        URL versionUrl = new URL(REPOSITORY + "version.txt");
        String latestVersion = DownloadUtils.readURL(versionUrl);
        return latestVersion.trim();
    }

    protected int getMinimumServerVersion() {
        return MIN_SERVER_VERSION;
    }

    protected int getMaximumServerVersion() { return MAX_SERVER_VERSION; }
    
    public void writeResourceToDataFolder(String in) {
        writeResourceToDataFolder(in, in);
    }

    public void writeResourceToDataFolder(String in, String out) {
        File dataFolder = getDataFolder();

        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                getLogger().log(Level.SEVERE, "Cannot create data directory at " + dataFolder.getAbsolutePath());
                return;
            }
        }

        byte[] buf = new byte[1024];
        int len;

        try {
            InputStream is = getClass().getResourceAsStream("/" + in);
            OutputStream os = new FileOutputStream(getDataFile(out));
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            is.close();
            os.close();

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Cannot generate file " + out + " from jar resource " + in, e);
        }
    }

    public File getDataFile(String name) {
        return new File(getDataFolder(), name);
    }

    /**
     * This method will be called when the onEnable() method is called.
     * 
     * @throws Exception
     */
    public abstract void enablePlugin() throws Exception;
}
