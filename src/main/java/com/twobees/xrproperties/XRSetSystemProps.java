/*
* Copyright (C) 2014-2025 Ben Brand
  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.twobees.xrproperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class XRSetSystemProps {

    public XRSetSystemProps() {

        Properties prop = new Properties();

        try {
            //
            // the config.properties file must be contained in the src/main/resources
            // folder of the common lib.
            // the inputstream of a class in the common lib is used to force
            // java to find it.
            //
            InputStream inputStream = XRGetProp.class.getClassLoader().getResourceAsStream("config.properties");
            //
            // load a properties file
            prop.load(inputStream);
            //
            // save the key application properties into the application space
            //
            Properties p = System.getProperties();
            p.setProperty("dalURL", prop.getProperty("dalURL"));
            p.setProperty("runLevel", prop.getProperty("runLevel"));
            Integer runLevel = Integer.valueOf(p.getProperty("runLevel")); // easier to determine run level than string
            p.setProperty("releaseLevel", prop.getProperty("releaseLevel"));
            p.setProperty("releaseCycle", prop.getProperty("releaseCycle"));
            p.setProperty("dbRootName", prop.getProperty("dbRootName"));
            p.setProperty("xmlDB", prop.getProperty("xmlDB"));
            p.setProperty("wrapperClass", prop.getProperty("wrapperClass"));
            p.setProperty("appName", prop.getProperty("appName"));
            p.setProperty("dumpFolder", prop.getProperty("dumpFolder"));
            p.setProperty("loadFolder", prop.getProperty("loadFolder"));
            p.setProperty("defaultAssetRole", prop.getProperty("defaultAssetRole"));
            p.setProperty("defaultAssetCost", prop.getProperty("defaultAssetCost"));
            //
            if (runLevel > 50) {
                System.out.println("Property: DAL URL, value            = " + p.getProperty("dalURL"));
                System.out.println("Property: RUN LEVEL, value          = " + p.getProperty("runLevel"));
                System.out.println("Property: RELEASE LEVEL, value      = " + p.getProperty("releaseLevel"));
                System.out.println("Property: RELEASE CYCLE, value      = " + p.getProperty("releaseCycle"));
                System.out.println("Property: DB ROOT NAME, value       = " + p.getProperty("dbRootName"));
                System.out.println("Property: XML DB, value             = " + p.getProperty("xmlDB"));
                System.out.println("Property: WRAPPER CLASS, value      = " + p.getProperty("wrapperClass"));
                System.out.println("Property: zkTheme, value            = " + p.getProperty("ZKTheme"));
                System.out.println("Property: appName, value            = " + p.getProperty("appName"));
                System.out.println("Property: dump data folder, value   = " + p.getProperty("dumpFolder"));
                System.out.println("Property: load data folder, value   = " + p.getProperty("loadFolder"));
                System.out.println("Property: default asset role, value = " + p.getProperty("defaultAssetRole"));
                System.out.println("Property: default asset cost, value = " + p.getProperty("defaultAssetCost"));
            }
        } catch (IOException ex) {
            System.out.println("\nXRSetSystemProps: config.properties file not found!!\n");
            ex.printStackTrace();
        } 
    }
}
