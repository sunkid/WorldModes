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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class MessageUtils {
	public static String convertColor(String original) {
		Pattern p = Pattern.compile("&([a-z0-9])");
		String result = new String(original);
		Matcher m = p.matcher(original);
		
		while (m.find()) {
			result = result.replaceAll(m.group(), ChatColor.getByCode(toInt(m.group(1))).toString());
		}
		
		return result;
	}

	private static int toInt(String string) {
		String s = string.trim();
		if (s.length() > 1)
			return -1;
		try {
			return Integer.parseInt(s, 16);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static void send(CommandSender sender, String message) {
		send(sender, ChatColor.WHITE, message);
	}
	
	public static void send(CommandSender sender, ChatColor color, String message) {
		for (String line : message.split("\n")) {
			sender.sendMessage(color + line);
		}
	}

	public static String colorize(ChatColor color, String string) {
		if (color.equals(ChatColor.WHITE))
			return string;
		return color + string + ChatColor.WHITE;
	}

    public static void broadcast(Server server, String message) {
        for (String line : message.split("\n")) {
            server.broadcastMessage(line);
        }
    }

    public static void broadcast(Server server, ChatColor color, String msg) {
        broadcast(server, colorize(color, msg));
    }
}