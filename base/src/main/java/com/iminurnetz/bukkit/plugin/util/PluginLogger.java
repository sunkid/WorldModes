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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;

public class PluginLogger {
	
	private BukkitPlugin plugin;
	private Server server;
	private String prefix = "";
	private Logger logger;

	private boolean inited = false;
	
	public PluginLogger(BukkitPlugin plugin) {
		this.plugin = plugin;
	}
	
	private void init() {
		server = plugin.getServer();
		prefix = plugin.getFullMessagePrefix();

		if (server != null)
			logger = server.getLogger();
		
		if (logger != null) {
			inited = true;
		} else {
			logger = Logger.getLogger("Minecraft");
		}
	}
	
	public void log(Level level, String msg) {	
		if (!inited) {
			init();
		}
		logger.log(level, prefix + msg);
	}

	public void log(Level level, String msg, Exception e) {
		if (!inited) {
			init();
		}
		logger.log(level, prefix + msg, e);
	}

	public void log(String msg) {
		log(Level.INFO, msg);
	}

	public Logger getLogger() {
		return logger;
	}

}
