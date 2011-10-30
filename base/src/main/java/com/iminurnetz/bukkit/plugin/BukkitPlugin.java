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
import java.io.FileNotFoundException;
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

    public static final String REPOSITORY = "https://raw.github.com/sunkid/@project@/master/release/";

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
            if (serverVersion > 0 && (serverVersion < getMinimumServerVersion() || serverVersion > getMaximumServerVersion())) {
                throw new UnsupportedServerVersionException("This plugin only supports server versions " + getMinimumServerVersion() + " to " + getMaximumServerVersion());
            }

            if (serverVersion > 0) {
                log("Server version compatibility check succeeded");
            }

            PluginManager pm = getServer().getPluginManager();
            if (pm.getPlugin(BASE_BUKKIT_PLUGIN) == null) {
                updateAndLoadBaseBukkitPlugin();
            }

            enablePlugin();

            if (!getName().equals(BASE_BUKKIT_PLUGIN)) {
                Configuration config = getConfig();
                if (!config.getBoolean("settings.disable-stats", false)) {
                    postUsage();
                }

                if (!config.getBoolean("settings.disable-updates", false)) {
                    File jarFile = new File(getDataFolder().getParentFile(), getName() + ".jar");
                    try {
                        checkAndUpdateJarFile(jarFile, true);
                    } catch (Exception e) {
                        log(Level.SEVERE, "Cannot check for or install latest version", e);
                    }
                }
            }

        } catch (Exception e) {
            log("Error enabling! ABORTED", e);
            this.setEnabled(false);
        }
    }

    public void postUsage() {
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
        File basePluginJarFile = new File(getDataFolder(), BASE_BUKKIT_PLUGIN + ".jar");
        try {
            checkAndUpdateJarFile(basePluginJarFile, false);
        } catch (IOException e) {
            log(Level.SEVERE, "Cannot check remote or local version of BaseBukkitPlugin!", e);
            if (!basePluginJarFile.exists()) {
                throw new FileNotFoundException("Install BaseBukkitPlugin manually into " + basePluginJarFile.getAbsolutePath());
            } else {
                log(Level.SEVERE, "Attempting installation from local jar");
            }
        }

        PluginManager pm = getServer().getPluginManager();
        Plugin plugin = pm.loadPlugin(basePluginJarFile);
        pm.enablePlugin(plugin);
    }

    private void checkAndUpdateJarFile(File jarFile, boolean isPlugin) throws IOException {
        boolean checkServer = checkTimeStamp(isPlugin);

        String installedVersion = getPluginVersionFromJar(jarFile);
        if (installedVersion.equals("0")) {
            checkServer = true;
        }

        if (checkServer) {
            String name = jarFile.getName().replace(".jar", "");

            log("Checking for lastest version of " + name);
            if (isPlugin) {
                log("Automatic updates can be disabled by setting 'settings.disable-updates' to false in config.yml");
            }

            String latestVersion = getLatestVersionFromRepository(name);

            if (!latestVersion.equals(installedVersion)) {
                log("Latest version " + latestVersion + " is newer than the installed version!");

                // not sure this is necessary, as SimplePluginManager seems to
                // move files automatically
                // at least it'll create the folder
                if (isPlugin) {
                    jarFile = new File(getServer().getUpdateFolderFile(), jarFile.getName());
                    if (!jarFile.getParentFile().exists()) {
                        jarFile.getParentFile().mkdir();
                    } else {
                        String updateVersion = getPluginVersionFromJar(jarFile);
                        if (updateVersion.equals(getVersion())) {
                            log("The latest version has already been downloaded to " + jarFile.getAbsolutePath());
                            return;
                        }
                    }
                }

                URL jarUrl = new URL(getRepository(name) + jarFile.getName());
                DownloadUtils.download(logger, jarUrl, jarFile);
                log("The latest version was downloaded to " + jarFile.getAbsolutePath());
                if (isPlugin) {
                    log("The update will automatically be installed upon the next server restart!");
                }
            }

            if (isPlugin) {
                try {
                    if (!getTimeStampFile().setLastModified((new Date()).getTime())) {
                        throw new Exception("Setting last modified time stamp did not succeed!");
                    }
                } catch (Exception e) {
                    log(Level.SEVERE, "Cannot update time stamp", e);
                }
            }
        }
    }

    private boolean checkTimeStamp(boolean create) {
        return checkTimeStamp(24, create);
    }

    private boolean checkTimeStamp(int interval, boolean create) {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File timeStamp = getTimeStampFile();
        boolean checkServer = true;
        if (!timeStamp.exists() && create) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(timeStamp));
                out.write("remove this file to force the version checks for BaseBukkitPlugin and " + getName() + "\n");
                out.close();
            } catch (IOException e) {
                log(Level.SEVERE, "Cannot create time stamp", e);
                return true;
            }
        } else if (create) {
            Date lastModified = new Date(timeStamp.lastModified());
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.HOUR, -interval);

            checkServer = lastModified.before(yesterday.getTime());
        }
        return checkServer;
    }

    private String getPluginVersionFromJar(File pluginJarFile) {
        String pluginVersion = "0";

        if (pluginJarFile.exists()) {
            try {
                JarFile jarFile = new JarFile(pluginJarFile);
                JarEntry entry = jarFile.getJarEntry("plugin.yml");
                InputStream stream = jarFile.getInputStream(entry);

                PluginDescriptionFile desc = new PluginDescriptionFile(stream);
                pluginVersion = desc.getVersion();
                stream.close();
                jarFile.close();
            } catch (Exception e) {
                log(Level.SEVERE, "Cannot check plugin version for " + pluginJarFile.getName(), e);
            }
        }

        return pluginVersion;
    }

    public static String getLatestVersionFromRepository(String project) throws IOException {
        URL versionUrl = new URL(getRepository(project) + "version.txt");
        String latestVersion = DownloadUtils.readURL(versionUrl);
        return latestVersion.trim();
    }

    private static String getRepository(String project) {
        return REPOSITORY.replace("@project@", project);
    }

    private File getTimeStampFile() {
        return new File(getDataFolder(), "lastVersionCheck.txt");
    }

    protected int getMinimumServerVersion() {
        return MIN_SERVER_VERSION;
    }

    protected int getMaximumServerVersion() {
        return MAX_SERVER_VERSION;
    }

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
