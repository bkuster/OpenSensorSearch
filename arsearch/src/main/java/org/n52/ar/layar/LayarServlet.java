/**
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.ar.layar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.ar.SirCallbackServlet;
import org.n52.ar.SirPOI;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * 
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
public class LayarServlet extends SirCallbackServlet {

	private static Logger log = LoggerFactory.getLogger(LayarServlet.class);

	private static final String REQUEST_FORMAT = "application/json";

	/**
     * 
     */
	private static final long serialVersionUID = 3747559074882272526L;

	private JsonFactory factory;

	private String layerName = "opensensorsearch";

	public LayarServlet() {
		log.debug("NEW {}", this);
	}

	@Override
	protected SirPOI createPOI(SearchResultElement sre) {
		Hotspot h = new Hotspot();
		// poi.actions
		// poi.alt = calculate altitude?`
		// poi.attribution
		h.description = sre.getSensorDescription().getText();
		// poi.distance = calculate?
        h.id = sre.getSensorId();
		// poi.imageURL
		double[] latLon = sre.getSensorDescription().getBoundingBox()
				.getCenter();
		h.lat = latLon[0];
		h.lon = latLon[1];
		ArrayList<ServiceReference> references = (ArrayList<ServiceReference>) sre
				.getServiceReferences();
		h.title = references.get(0).getServiceSpecificSensorId(); 

		return h; 
	}

	/**
	 * http://layar.com/documentation/browser/api/getpois-request/
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userAgent = request.getHeader("User-agent");
		log.debug("User-agent={}", userAgent);

		/*
		 * mandatory:
		 */
		String userId = request.getParameter("userId");
		String version = request.getParameter("version");
		String countryCode = request.getParameter("countryCode");
		String lang = request.getParameter("lang");

		String action = request.getParameter("action");
		if (action == null) {
			// response.sendError(HttpServletResponse.SC_BAD_REQUEST,
			// "Missing parameter 'action'");
			// return;
			log.debug("No action given.");
		}

		String latParam = request.getParameter("lat");
		if (latParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'lat'");
			return;
		}

		String lonParam = request.getParameter("lon");
		if (lonParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'lon'");
			return;
		}

		String radiusParam = request.getParameter("radius");
		if (radiusParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'radius'");
			return;
		}

		/*
		 * optional:
		 */
		String accurary = request.getParameter("accuracy");
		String pageKey = request.getParameter("pageKey");
		// String oauth_consumer_key =
		// request.getParameter("oauth_consumer_key");
		// String oauth_signature_method =
		// request.getParameter("oauth_signature_method");
		String radiolist = request.getParameter("RADIOLIST");
		String searchbox = request.getParameter("SEARCHBOX");
		String custom_slider = request.getParameter("CUSTOM_SLIDER");
		String checkboxlist = request.getParameter("CHECKBOXLIST");
		String localCountryCode = request.getParameter("localCountryCode");
		String alt = request.getParameter("alt"); // current altitude
		String recognizedReferenceImage = request
				.getParameter("recognizedReferenceImage");

		// convert parameters
		float lat = Float.parseFloat(latParam);
		float lon = Float.parseFloat(lonParam);
		double[] center = new double[] { lat, lon };
		float radius = Float.parseFloat(radiusParam);

		// get nearby sensors
		LayarResponse layarResponse = new LayarResponse();
		layarResponse.layer = request.getParameter("layerName");
		if (layarResponse.layer == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter 'layerName'");
			return;
		}

		response.setContentType("application/json; charset=utf-8");
		JsonGenerator generator = this.factory.createJsonGenerator(response
				.getWriter());
		generator.useDefaultPrettyPrinter();

		// query SIR
		try {
			Collection<SirPOI> pois = querySIR(center, radius, REQUEST_FORMAT);

			for (SirPOI sirPOI : pois) {
				if (sirPOI instanceof Hotspot) {
					Hotspot h = (Hotspot) sirPOI;
					layarResponse.hotspots.add(h);
				}
			}
		} catch (Exception e) {
			log.error("Error querying SIR.", e);
			layarResponse.errorCode = LayarResponse.CODE_ERROR;
			layarResponse.errorString = e.toString() + ": " + e.getMessage();
			layarResponse.showMessage = "Server side exception!";
		}

		// write response
		layarResponse.toJSON(generator);

		// clean up
		generator.close();
		response.flushBuffer();
	}

	public String getLayerName() {
		return this.layerName;
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		this.factory = new JsonFactory();

		log.info("Initialized " + this);
	}
}