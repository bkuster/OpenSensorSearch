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
/**
 * author Yakoub
 */
package org.n52.sir.IT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.Util;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument;

public class SearchSensorIT {

    private static Client c = null;
    
    @BeforeClass
    public static void setUpClient() throws MalformedURLException {
        c  = new Client(Util.getServiceURIforIT());
    }
	
	public void searchSensor(String file, String description) throws Exception {
		System.out.println(description);
		File f = new File((ClassLoader.getSystemResource(file).getFile()));
		SearchSensorRequestDocument doc = SearchSensorRequestDocument.Factory
				.parse(f);

		XmlObject response = null;

		response = c.xSendPostRequest(doc);
		// parse and validate response
		SearchSensorResponseDocument resp_doc = SearchSensorResponseDocument.Factory
				.parse(response.getDomNode());
		// validate the respo_doc

		assertTrue("Invalid  Sensor Response", resp_doc.validate());

		int send = doc.getSearchSensorRequest().getSensorIdentificationArray().length;
		int response_count = resp_doc.getSearchSensorResponse()
				.sizeOfSearchResultElementArray();
		assertEquals(send, response_count);

	}

	@Test
	public void searchSensorbyIDInSIR() throws Exception {
		searchSensor("Requests/SearchSensor_bySensorIDInSIR.xml",
				"Search Sensor by ID in SIR");

	}

	@Test
	public void searchSensorByDescription() throws Exception {

		searchSensor("Requests/SearchSensor_byServiceDescription.xml",
				"Search Sensor by service Description");

	}

}
