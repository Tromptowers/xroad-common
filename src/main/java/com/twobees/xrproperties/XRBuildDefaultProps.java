/*
 * Copyright (C) 2018 Ben Brand
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class XRBuildDefaultProps {

    /**
     * @author Ben Brand
     *
     * Set a number of default properties using the java Properties library.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            //
            /**************************************
             * NOTE:    change the location of the properties file and copy to correct
             *          location afterwards.
             */
            //
            OutputStream output = new FileOutputStream("/home/brand/tmp/config.properties");
            //
            // property: url to the DAL (data abstraction layer, also called the backend)
            prop.setProperty("dalURL", "http://localhost:8080/xroad-dal/");
            //
            // property; the run level which also drives the debug logging level
            prop.setProperty("runLevel", "99");
            //
            // property; the release level of the application
            prop.setProperty("releaseLevel", "0.1");
            //
            // property; the release cycle for the application, ie production, stage, test, devel, ...)
            prop.setProperty("releaseCycle", "DEV");
            //
            // property; the database root name for this app, assuming one master database but could be multiple
            prop.setProperty("dbRootName", "cman");
            //
            // property; the path to the common XML DB files
            prop.setProperty("xmlDB", "http://localhost:8080/static/");
            //
            // property; the path to the common wrapper classes files
            prop.setProperty("wrapperClass", "com.twobees.xrdbwrapper.");
            //
            // the following are application specific
            prop.setProperty("appName", "Capacity Manager CAPMAN ... a tip of the hat");
            prop.setProperty("dumpFolder", "/home/brand/tmp");
            prop.setProperty("loadFolder", "/home/brand/tmp");
            prop.setProperty("defaultAssetRole", "1");
            prop.setProperty("defaultAssetCost", "100.00");
            //
            // save properties to the project root folder in config.properties
            prop.store(output, null);
            System.out.println(prop);
            output.close();
            //
            // for testing purposes; set them system wide, kind of a playback
            XRSetSystemProps sp = new XRSetSystemProps();

        } catch (IOException io) {
            io.printStackTrace();
        } 
        
    }
}
