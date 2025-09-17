/*
 * Copyright (C) 2018 - 2025 brand
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/**
 *
 * Author: Ben Brand
 * Release: R2
 * Date: 21 aug 2025
 *
 * The XRPortal class is used to access the back end. It performs a number of
 * checks based on the XML file to make sure that the calling application has
 * set up the portal access correctly.
 *
 * R2 Notes
 * Attempted to use the HttpResponse/Client classes as defines for JDK 11+
 * ... but these do not work for a Tomcat server, these classes are for
 * ... webservers only. All attempts were backed out and we are back to the R1
 * ... release version.
 *
 */
package com.twobees.xrportal;

import com.twobees.xrdb.XRParser;
import com.twobees.xrproperties.XRGetProp;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XRPortal {

    //
    private String serverURL = "";
    private Integer runLevel = 0;
    private String xmlDB = "";
    //

    private String tableName = "";
    //
    private ObjectInputStream servletIn = null;
    private String urlencode = "UTF-8";
    //
    private List<Object> xmlParms;
    private StringBuffer xmlData = new StringBuffer();
    // private StringBuffer buildJava = new StringBuffer();

    public XRPortal(String t) throws URISyntaxException {
        //
        //
        XRGetProp xrparm = new XRGetProp();
        serverURL = xrparm.getdalURL();
        runLevel = Integer.valueOf(xrparm.getrunLevel());
        xmlDB = xrparm.getxmlDB();
        //
        tableName = t;
        //
        try {
            //
            // read the XML file into a StringBuilder class
            //
            String xmlFile = xmlDB + tableName + ".xml";
            if (runLevel > 90) {
                System.out.println("XP02 "
                        + System.currentTimeMillis()
                        + " using XML file "
                        + xmlFile);
            }
            URL xmlCon = new URI(xmlFile).toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(xmlCon.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                xmlData.append(inputLine);
            }
            in.close();
            //
            if (runLevel > 90) {
                System.out.println("XP03 "
                        + System.currentTimeMillis()
                        + " read XML file "
                        + tableName
                        + ".xml, "
                        + xmlData.length()
                        + " chars");
            }
            //
            // XML file is now in the stringbuilder, parse out the parameters;
            //
            Document parseThis = new SAXBuilder().build(new StringReader(xmlData.toString()));
            XRParser xrp = new XRParser(parseThis, "");
            xmlParms = xrp.getParameters();
            if (runLevel > 90) {
                System.out.println("XP04 "
                        + System.currentTimeMillis()
                        + " parsed parameters from XML file "
                        + xmlParms
                );
            }
            in.close();
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(XRPortal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList getData(List parmList) throws URISyntaxException, InterruptedException {
        //
        // The user wants some data from the database; this method will:
        //      * build a servlet URL using the retrieved parameter list
        //      * fire a "get" request to the "get data" servlet in the back end (XRDBGet)
        //      * receive and return the encapsulted data set to the orginator of this request
        //
        // Important is the list of parameters passed to the servlet; this defines what subset of data the user wants to retrieve.
        // The list passed to this method maps 1:1 to the parameter section of the xml file (the first section) and
        // is used to build the URL to the XRDataReader servlet.
        //
        // NOTE v2:
        //      All changed to reflect the Java.21 HttpRequest builder method.
        //
        ArrayList rowList = new ArrayList();
        try {
            //
            // test if we passed the currect number of parameters as required by the XML file
            //
            if (parmList.size() != xmlParms.size()) {
                Logger.getLogger(XRPortal.class.getName()).log(Level.SEVERE,
                        "*** Number of parameters passed to the portal are not the the same as required by the XML DB file");
                return rowList;
            }
            //
            // the first parameter may not be a null and must be a string
            //
            if (parmList.get(0) == null) {
                Logger.getLogger(XRPortal.class.getName()).log(Level.SEVERE,
                        "*** The first parameter passed on cannot have a null value");
                return rowList;
            }
            if (parmList.get(0).getClass() != String.class) {
                Logger.getLogger(XRPortal.class.getName()).log(Level.SEVERE,
                        "*** The first parameter passed on must be a string");
                return rowList;
            }
            //
            // 1. new builder, add the URI
            HttpRequest.Builder getBuilder = HttpRequest.newBuilder()
                    .uri(new URI(serverURL + "XRDBGet"));
            //
            // 2. build the header, TABLE=....
            getBuilder.header("TABLE", tableName);
            //
            //
            // 3. add the parameter pairs as defined in the XML base file
            // some error trapping is going on as well as a date conversion (tested?)
            //
            for (int i = 0; i < xmlParms.size(); ++i) {
                // first part of the parameter, the ID
                //
                // the data part of the parameter needs some looking at
                //  1.  If null then trap this, preventing a execution error
                //  2.  A date needs some tweaking, ie comvert to a Long
                //  3.  Finally, use what was given 1:1 when the portal was declared
                //
                String p2 = "";
                if (parmList.get(i) == null) {
                    p2 = "NULL";
                } else if (parmList.get(i).getClass() == java.sql.Date.class) {
                    java.sql.Date d = (java.sql.Date) parmList.get(i);
                    p2 = d.getTime() + ""; // force to string
                } else {
                    p2 = parmList.get(i).toString();
                }
                getBuilder.setHeader(xmlParms.get(i).toString(), p2);
            }
            //
            // builder has been defined, now use it to build the request to back end DB servlet XRDBGet
            //
            // first grab a client object
            HttpClient getClient = HttpClient.newHttpClient();
            // finalize it
            HttpRequest getData = getBuilder
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            if (runLevel == 99) { // logit
                System.out.println("XP05 "
                        + System.currentTimeMillis()
                        + " using GET "
                        + getData.toString());
            }
            //  now get whatever is passed back
            HttpResponse<InputStream> getResponse = getClient.send(getData, BodyHandlers.ofInputStream());
            // do we need this?
            if (getResponse == null) {
                System.out.println("XP06 "
                        + System.currentTimeMillis()
                        + " SEVERE, null list returned");
            }
            // convert the response body to the ObjectInputStream
            // we need a BodySubscriber
            InputStream is = (InputStream) getResponse.body();
            ObjectInputStream getIt = new ObjectInputStream(is);
            // get the object that was sent by the servlet
            rowList = (ArrayList) getIt.readObject();
            // all done
            getClient.close();
            //
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(XRPortal.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (runLevel > 90) {
                System.out.println("XP07 "
                        + System.currentTimeMillis()
                        + " getList finished, servlet returned "
                        + rowList.size()
                        + " rows");
            }
        }
        return rowList;
    }

    public ArrayList setData(Object wrapper, String userOption) { // userOption is optional, just a help field to pass something to the backend
        ArrayList callBack = new ArrayList();
        String returnCode = "";
        String responseCode = "";

        try {
            //
            // 1. new builder, add the URI
            HttpRequest.Builder setBuilder = HttpRequest.newBuilder()
                    .uri(new URI(serverURL + "XRDBSet"));
            //
            // 2. build the header, TABLE=....
            setBuilder.header("TABLE", tableName);
            setBuilder.header("USER-OPTION", userOption);
            //
            if (runLevel == 99) {
                System.out.println("XP08 "
                        + System.currentTimeMillis()
                        + " building set request for "
                        + tableName);
            }
            //
            // Convert wrapper to byte[]
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
                ois.writeObject(wrapper);
            }
            // connect
            HttpClient client = HttpClient.newBuilder().build();
            // finalize request, add the bobypublisher
            HttpRequest setRow = setBuilder
                    .header("Content-Type", "text/plain;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(boas.toByteArray()))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            if (runLevel == 99) { // logit
                System.out.println("XP09 "
                        + System.currentTimeMillis()
                        + " posting updated row (SET) ");
            }
            // do it, trap the response
            HttpResponse<?> response = client.send(setRow, BodyHandlers.discarding());
            if (runLevel == 99) {
                System.out.println("XP10 Servlet response: "
                        + response.toString());
            }
            // cleaning up
            client.close();
        } catch (IOException ex) {
            System.getLogger(XRPortal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (URISyntaxException | InterruptedException ex) {
            System.getLogger(XRPortal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        callBack.add(returnCode);
        callBack.add(responseCode);
        return callBack;
    }

    public ArrayList insertRow(Object wrapper, String userOption) { // userOption is optional, just a help field to pass something to the backend
        ArrayList callBack = new ArrayList();
        String returnCode = "";
        String responseCode = "";

        try {
            //
            // 1. new builder, add the URI
            HttpRequest.Builder insertBuilder = HttpRequest.newBuilder()
                    .uri(new URI(serverURL + "XRDBInsert"));
            //
            // 2. build the header, TABLE=....
            insertBuilder.header("TABLE", tableName);
            insertBuilder.header("USER-OPTION", userOption);
            //
            if (runLevel == 99) {
                System.out.println("XP11 "
                        + System.currentTimeMillis()
                        + " building insert row request for "
                        + tableName);
            }
            //
            // Convert wrapper to byte[]
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
                ois.writeObject(wrapper);
            }
            // connect
            HttpClient client = HttpClient.newBuilder().build();
            // finalize request, add the bobypublisher
            HttpRequest insertRow = insertBuilder
                    .header("Content-Type", "text/plain;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(boas.toByteArray()))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            if (runLevel == 99) { // logit
                System.out.println("XP12 "
                        + System.currentTimeMillis()
                        + " inserting row (INSERT) ");
            }
            // do it, trap the response
            HttpResponse<?> response = client.send(insertRow, BodyHandlers.discarding());
            if (runLevel == 99) {
                System.out.println("XP13 Servlet response: "
                        + response.toString());
            }
            // cleaning up
            client.close();
        } catch (IOException | URISyntaxException | InterruptedException ex) {
            System.getLogger(XRPortal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        callBack.add(returnCode);
        callBack.add(responseCode);
        return callBack;
    }

    public ArrayList deleteRow(Object wrapper, String userOption) { // userOption is optional, just a help field to pass something to the backend
        ArrayList callBack = new ArrayList();
        String returnCode = "";
        String responseCode = "";

        try {
            //
            // 1. new builder, add the URI
            HttpRequest.Builder deleteBuilder = HttpRequest.newBuilder()
                    .uri(new URI(serverURL + "XRDBDelete"));
            //
            // 2. build the header, TABLE=....
            deleteBuilder.header("TABLE", tableName);
            deleteBuilder.header("USER-OPTION", userOption);
            //
            if (runLevel == 99) {
                System.out.println("XP14 "
                        + System.currentTimeMillis()
                        + " deleting a row in "
                        + tableName);
            }
            //
            // Convert wrapper to byte[]
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
                ois.writeObject(wrapper);
            }
            // connect
            HttpClient client = HttpClient.newBuilder().build();
            // finalize request, add the bobypublisher
            HttpRequest deleteRow = deleteBuilder
                    .header("Content-Type", "text/plain;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(boas.toByteArray()))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            if (runLevel == 99) { // logit
                System.out.println("XP15 "
                        + System.currentTimeMillis()
                        + " deleting a row ");
            }
            // do it, trap the response
            HttpResponse<?> response = client.send(deleteRow, BodyHandlers.discarding());
            if (runLevel == 99) {
                System.out.println("XP16 Servlet response: "
                        + response.toString());
            }
            // cleaning up
            client.close();
        } catch (IOException | URISyntaxException | InterruptedException ex) {
            System.getLogger(XRPortal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        callBack.add(returnCode);
        callBack.add(responseCode);
        return callBack;
    }
}
