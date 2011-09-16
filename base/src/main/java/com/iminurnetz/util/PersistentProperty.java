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

package com.iminurnetz.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class PersistentProperty {
	private Properties properties;
	private String fileName;
	private URL resource;
	private boolean fromURL;

	public static final DateFormat dateFormat = DateFormat.getInstance();

	public PersistentProperty(File dataFolder, String fileName)
			throws FileNotFoundException, IOException {
		this(dataFolder, fileName, true);
	}

	public PersistentProperty(File dataFolder, String fileName,
			boolean createIfNotExists) throws FileNotFoundException,
			IOException {
		this(dataFolder.getAbsolutePath()
				+ System.getProperty("file.separator") + fileName,
				createIfNotExists);
	}

	public PersistentProperty(String fileName) throws FileNotFoundException,
			IOException {
		this(fileName, true);
	}

	public PersistentProperty(String fileName, boolean createIfNotExists)
			throws FileNotFoundException, IOException {

		fromURL = false;
		this.fileName = fileName;
		File f = new File(fileName);
		if (f.exists())
			load();
		else if (createIfNotExists)
			save();
		else
			throw new FileNotFoundException("File not found: " + fileName);
	}

	public PersistentProperty(URL resource) throws FileNotFoundException, IOException {
		fromURL = true;
		this.resource = resource;
		load();
	}

	public void load() throws FileNotFoundException, IOException {
		if (properties == null)
			properties = new Properties();
		if (fromURL)
			properties.load(resource.openStream());
		else
			properties.load(new FileInputStream(fileName));
	}

	public void save() throws FileNotFoundException, IOException {
		if (fromURL)
			return;

		properties.store(new FileOutputStream(fileName), "Properties File");
	}

	public Map<String, String> getMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(
				fromURL ? new InputStreamReader(resource.openStream())
						: new FileReader(fileName));
		String line;
		while ((line = reader.readLine()) != null) {
			if ((line.trim().length() == 0) || (line.charAt(0) == '#')) {
				continue;
			}
			int delimPosition = line.indexOf('=');
			String key = line.substring(0, delimPosition).trim();
			String value = line.substring(delimPosition + 1).trim();
			map.put(key, value);
		}
		reader.close();
		return map;
	}

	public void removeKey(String key) {
		this.properties.remove(key);
	}

	public boolean keyExists(String key) {
		return this.properties.containsKey(key);
	}

	public String getString(String key) {
		if (this.properties.containsKey(key)) {
			return this.properties.getProperty(key);
		}

		return "";
	}

	public String getString(String key, String value) {
		if (this.properties.containsKey(key)) {
			return this.properties.getProperty(key);
		}
		setString(key, value);
		return value;
	}

	public void setString(String key, String value) {
		this.properties.setProperty(key, value);
	}

	public int getInt(String key) {
		if (this.properties.containsKey(key)) {
			return Integer.parseInt(this.properties.getProperty(key));
		}

		return 0;
	}

	public int getInt(String key, int value) {
		if (this.properties.containsKey(key)) {
			return Integer.parseInt(this.properties.getProperty(key));
		}

		setInt(key, value);
		return value;
	}

	public void setInt(String key, int value) {
		this.properties.setProperty(key, String.valueOf(value));
	}

	public double getDouble(String key) {
		if (this.properties.containsKey(key)) {
			return Double.parseDouble(this.properties.getProperty(key));
		}

		return 0.0D;
	}

	public double getDouble(String key, double value) {
		if (this.properties.containsKey(key)) {
			return Double.parseDouble(this.properties.getProperty(key));
		}

		setDouble(key, value);
		return value;
	}

	public void setDouble(String key, double value) {
		this.properties.setProperty(key, String.valueOf(value));
	}

	public long getLong(String key) {
		if (this.properties.containsKey(key)) {
			return Long.parseLong(this.properties.getProperty(key));
		}

		return 0L;
	}

	public long getLong(String key, long value) {
		if (this.properties.containsKey(key)) {
			return Long.parseLong(this.properties.getProperty(key));
		}

		setLong(key, value);
		return value;
	}

	public void setLong(String key, long value) {
		this.properties.setProperty(key, String.valueOf(value));
	}

	public boolean getBoolean(String key) {
		if (this.properties.containsKey(key)) {
			return Boolean.parseBoolean(this.properties.getProperty(key));
		}

		return false;
	}

	public boolean getBoolean(String key, boolean value) {
		if (this.properties.containsKey(key)) {
			return Boolean.parseBoolean(this.properties.getProperty(key));
		}

		setBoolean(key, value);
		return value;
	}

	public void setBoolean(String key, boolean value) {
		this.properties.setProperty(key, String.valueOf(value));
	}

	public Date getDate(String key, Date value) {

		if (this.properties.containsKey(key)) {
			try {
				return dateFormat.parse(this.properties.getProperty(key));
			} catch (ParseException e) {
				/* ignored */
			}
		}

		setDate(key, value);
		return value;

	}

	public void setDate(String key, Date value) {
		this.properties.setProperty(key, dateFormat.format(value));
	}
}