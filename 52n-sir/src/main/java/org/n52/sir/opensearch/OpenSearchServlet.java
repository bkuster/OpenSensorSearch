/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.sir.opensearch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirSearchSensorResponse;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 */
@Singleton
public class OpenSearchServlet extends HttpServlet {

    private static final long serialVersionUID = -3235081570449109985L;

    @SuppressWarnings("unused")
    private static final String INIT_PARAM_CONFIG_FILE = "configFile";

    @SuppressWarnings("unused")
    private static final String INIT_PARAM_DBCONFIG_FILE = "dbConfigFile";

    private static Logger log = LoggerFactory.getLogger(OpenSearchServlet.class);

    private OpenSearchConfigurator configurator;

    private RequestDismantler dismantler;

    private HashMap<String, IOpenSearchListener> listeners;

    private SearchSensorListener sensorSearcher;

    public OpenSearchServlet() {
        super();

        log.info("NEW " + this);
    }

    @Override
    public void destroy() {
        log.info("destroy() called...");

        super.destroy();

        // do nothing...

        log.info("done destroy()");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // FIXME Daniel: the open search functionality must be extracted to a testable class, preferably with
        // an event bus or a listener model.

        if (log.isDebugEnabled())
            log.debug(" ****** (GET) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());

        String acceptHeader = req.getHeader("accept");
        log.trace("Accept header for 'accept': " + acceptHeader);
        String httpAccept = req.getParameter(OpenSearchConstants.ACCEPT_PARAMETER);
        log.trace("Accept header for " + OpenSearchConstants.ACCEPT_PARAMETER + ": " + httpAccept);

        String searchText = req.getParameter(OpenSearchConstants.QUERY_PARAMETER);

        Map<String, String> map = req.getParameterMap();

        Set<String> keys = map.keySet();

        // redirect if httpAccept is missing
        if (httpAccept == null || httpAccept.isEmpty()) {
            redirectMissingHttpAccept(req, resp);
            return;
        }
        if (httpAccept.contains(" "))
            httpAccept = httpAccept.replace(" ", "+");

        // must be set before getWriter() is called.
        resp.setCharacterEncoding(SirConfigurator.getInstance().getCharacterEncoding());

        try (PrintWriter writer = resp.getWriter();) {

            Collection<SirSearchResultElement> searchResult = null;

            // handle missing query parameter, can be the case if just using geo extension...
            if (searchText == null || searchText.isEmpty()) {
                searchResult = new ArrayList<>();
                searchText = "";
                log.debug("No search text given.");
            }

            /*
             * Geo Extension: http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2
             */
            SirBoundingBox boundingBox = null;
            /*
             * if (this.dismantler.requestContainsGeoParameters(req)) { boundingBox =
             * this.dismantler.getBoundingBox(req);
             * log.info("Geo extension used: bounding box {} from query {} (source: {})", new Object[]
             * {boundingBox, req.getQueryString(), req.getRemoteAddr()}); } else
             * log.info("Searching with query {} (source: {})", new Object[] {req.getQueryString(),
             * req.getRemoteAddr()});
             */
            if (keys.contains(OpenSearchConstants.BOX_PARAM)) {
                String bbox = req.getParameter(OpenSearchConstants.BOX_PARAM);
                String[] s = bbox.split(",");
                boundingBox = new SirBoundingBox(Double.parseDouble(s[2]),
                                                 Double.parseDouble(s[1]),
                                                 Double.parseDouble(s[0]),
                                                 Double.parseDouble(s[3]));
                log.debug("Geo extension used: {}", boundingBox);
            }

            String lat = null;
            String lng = null;
            String radius = null;
            if (keys.contains(OpenSearchConstants.LAT_PARAM) && keys.contains(OpenSearchConstants.LON_PARAM)
                    && keys.contains(OpenSearchConstants.RADIUS_PARAM)) {
                lat = req.getParameter(OpenSearchConstants.LAT_PARAM);
                lng = req.getParameter(OpenSearchConstants.LON_PARAM);
                radius = req.getParameter(OpenSearchConstants.RADIUS_PARAM);
            }

            /*
             * Time extension: http://www.opensearch.org/Specifications/OpenSearch/Extensions/Time/1.0/Draft_1
             */
            String start = null;
            String end = null;
            /*
             * if (this.dismantler.requestContainsTime(req)) { Calendar[] startEnd =
             * this.dismantler.getStartEnd(req); start = startEnd[0]; end = startEnd[1];
             * log.debug("Time extension used: {} - {}", start, end); }
             */
            if (keys.contains(OpenSearchConstants.TIME_START_PARAMETER)) {
                // contains a temporal query
                log.debug(req.getParameter(OpenSearchConstants.TIME_START_PARAMETER));
                start = req.getParameter(OpenSearchConstants.TIME_START_PARAMETER);
                end = req.getParameter(OpenSearchConstants.TIME_END_PARAMETER);
                log.debug("Time extension used: {} - {}", start, end);
            }

            // create search criteria
            SirSearchCriteria searchCriteria = new SirSearchCriteria();
            if ( !searchText.isEmpty()) {
                ArrayList<String> searchTexts = new ArrayList<>();
                searchTexts.add(searchText);
                searchCriteria.setSearchText(searchTexts);
            }

            if (boundingBox != null)
                searchCriteria.setBoundingBox(boundingBox);

            if (start != null && end != null) {
                searchCriteria.setDtend(end);
                searchCriteria.setDtstart(start);
            }
            if (lat != null && lng != null && radius != null) {
                searchCriteria.setLat(lat);
                searchCriteria.setLng(lng);
                searchCriteria.setRadius(radius);
            }

            // create search request
            SirSearchSensorRequest searchRequest = new SirSearchSensorRequest();
            searchRequest.setSimpleResponse(true);
            searchRequest.setVersion(SirConstants.SERVICE_VERSION_0_3_1);
            searchRequest.setSearchCriteria(searchCriteria);

            ISirResponse response = this.sensorSearcher.receiveRequest(searchRequest);

            if (response instanceof SirSearchSensorResponse) {
                SirSearchSensorResponse sssr = (SirSearchSensorResponse) response;
                searchResult = sssr.getSearchResultElements();
            }
            else if (response instanceof ExceptionResponse) {
                ExceptionResponse er = (ExceptionResponse) response;
                String s = new String(er.getByteArray());
                writer.print(s);
            }
            else {
                log.error("Unhandled response: {}", response);
                writer.print(response.toString());
            }

            if (this.listeners.containsKey(httpAccept)) {
                IOpenSearchListener l = this.listeners.get(httpAccept);

                l.createResponse(req, resp, searchResult, writer, searchText);
            }
            else {
                log.error("Could not create response as for {}, not supported.", httpAccept);

                OwsExceptionReport report = new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                                                   OpenSearchConstants.ACCEPT_PARAMETER,
                                                                   "Unsupported output format '" + httpAccept + "'.");
                report.getDocument().save(writer, XmlTools.xmlOptionsForNamespaces());
            }

        }
        catch (Exception e) {
            log.error("Unhandled exception in doGet: ", e);
        }

        resp.flushBuffer(); // commits the response

        log.debug(" *** (GET) Done.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() throws ServletException {
        super.init();

        this.configurator = new OpenSearchConfigurator();

        try {
            this.sensorSearcher = new SearchSensorListener();
            this.sensorSearcher.setEncodeURLs(false);
        }
        catch (OwsExceptionReport e) {
            log.error("Could not create SearchSensorListener.", e);
            throw new ServletException(e);
        }

        this.dismantler = new RequestDismantler();

        this.listeners = new HashMap<>();

        // TODO move listener configuration to config file
        IOpenSearchListener jsonListener = new JsonListener(this.configurator);
        this.listeners.put(jsonListener.getMimeType(), jsonListener);
        IOpenSearchListener htmlListener = new HtmlListener(this.configurator);
        this.listeners.put(htmlListener.getMimeType(), htmlListener);
        IOpenSearchListener xmlListener = new XmlListener(this.configurator);
        this.listeners.put(xmlListener.getMimeType(), xmlListener);
        IOpenSearchListener rssListener = new RssListener(this.configurator);
        this.listeners.put(rssListener.getMimeType(), rssListener);
        IOpenSearchListener atomListener = new AtomListener(this.configurator);
        this.listeners.put(atomListener.getMimeType(), atomListener);
        IOpenSearchListener kmlListener = new KmlListener(this.configurator);
        this.listeners.put(kmlListener.getMimeType(), kmlListener);

        // get ServletContext
        // ServletContext context = getServletContext();
        // String basepath = context.getRealPath("/");

        // get configFile as Inputstream
        // InputStream configStream = context.getResourceAsStream(getInitParameter(INIT_PARAM_CONFIG_FILE));
        // if (configStream == null) {
        // throw new UnavailableException("could not open the config file");
        // }
        //
        // // get dbconfigFile as Inputstream
        // InputStream dbConfigStream =
        // context.getResourceAsStream(getInitParameter(INIT_PARAM_DBCONFIG_FILE));
        // if (dbConfigStream == null) {
        // throw new UnavailableException("could not open the database config file");
        // }
    }

    private void redirectMissingHttpAccept(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(this.configurator.getFullOpenSearchPath());
        sb.append("?");

        Enumeration< ? > params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String s = (String) params.nextElement();
            sb.append(s);
            sb.append("=");
            String[] parameterValues = req.getParameterValues(s);
            for (String sVal : parameterValues) {
                sb.append(sVal);
                sb.append(",");
            }

            sb.replace(sb.length() - 1, sb.length(), "&");
        }

        sb.append(OpenSearchConstants.ACCEPT_PARAMETER);
        sb.append("=");
        sb.append(OpenSearchConstants.X_DEFAULT_MIME_TYPE);
        log.debug("Redirecting to {}", sb.toString());
        resp.sendRedirect(sb.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OpenSearchSIR [");
        if (this.configurator != null)
            sb.append(this.configurator.getOpenSearchPath());
        sb.append("]");
        return sb.toString();
    }
}