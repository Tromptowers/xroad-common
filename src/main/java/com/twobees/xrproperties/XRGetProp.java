/*
 * Copyright (C) 2018 brand
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

import java.util.Properties;

/**
 *
 * @author Ben Brand
 *
 * This class has a set of methods that will extract the corresponding system
 * wide ... properties from the Tomcat instance (as saved in the web.xml file).
 *
 * Feel free to expand & adapt.
 *
 */
public class XRGetProp {

    String prop;
    Properties p = System.getProperties();

    public String getrunLevel() {
        if (!p.containsKey("runLevel")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("runLevel");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): runLevel cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getreleaseLevel() {
        if (!p.containsKey("releaseLevel")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("releaseLevel");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): releaseLevel cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getreleaseCycle() {
        if (!p.containsKey("releaseCycle")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("releaseCycle");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): releaseCycle cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getdbRootName() {
        if (!p.containsKey("dbRootName")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("dbRootName");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): dbRootName cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getxmlDB() {
        if (!p.containsKey("xmlDB")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("xmlDB");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): xmlDB cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getwrapperClass() {
        if (!p.containsKey("wrapperClass")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("wrapperClass");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): wrapperClass cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getdalURL() {
        if (!p.containsKey("dalURL")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("dalURL");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): dalURL cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getappName() {
        if (!p.containsKey("appName")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("appName");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): appName cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getdumpFolder() {
        if (!p.containsKey("dumpFolder")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("dumpFolder");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): dump folder cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getloadFolder() {
        if (!p.containsKey("loadFolder")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("loadFolder");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): load folder cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getdefaultAssetRole() {
        if (!p.containsKey("defaultAssetRole")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("defaultAssetRole");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): default asset role cannot be determined");
            prop = "";
        }
        return prop;
    }

    public String getdefaultAssetCost() {
        if (!p.containsKey("defaultAssetCost")) {
            XRSetSystemProps sp = new XRSetSystemProps();
        }
        prop = p.getProperty("defaultAssetCost");
        if (prop.isEmpty()) {
            System.err.println("SET UP ERROR (XRGetProp): default asset cost cannot be determined");
            prop = "";
        }
        return prop;
    }
}
