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
 * @author Yakoub
 */

package org.n52.oss.IT.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByBoundingBoxTest {

    private static SolrConnection c;

    @BeforeClass
    public static void prepare() {
        c = new SolrConnection("http://localhost:8983/solr", 2000);
    }

    @Before
    public void insertSensor() throws XmlException, IOException, OwsExceptionReport {
        /*
         * Insert testSensor for search
         */
    	String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SensorMLDecoder d = new SensorMLDecoder();
        SirSensor sensor = d.decode(doc);

        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO(c);
        dao.insertSensor(sensor);
    }

    @Test
    public void searchByBoundingBox() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(c);
        /*
         * Prepare the list of keywords
         */
        SirBoundingBox box = new SirBoundingBox(2,1,4,3);
        Collection<SirSearchResultElement> results = searchDAO.searchSensorByBoundingBox(box);

        assertNotNull(results);
        assertEquals(results.size(), 1);

        Iterator<SirSearchResultElement> iter = results.iterator();
        SirSearchResultElement result = iter.next();
        // SensorML is stored in the sensor description value
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) result.getSensorDescription();
        assertNotNull(description);
    //    assertTrue("urn:ogc:object:feature:testsensor".equals(description.getId()));
    }

    /*
     * Searches for a sensor but not in the range covered , should return 0
     */
    @Test
    public void searchByLocationNotInRange() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(c);
        SirBoundingBox box = new SirBoundingBox(5,6,7,8);
        
        Collection<SirSearchResultElement> results = searchDAO.searchSensorByBoundingBox(box);

        assertNotNull(results);
        assertEquals(results.size(), 0);

    }

    // FIXME let the delete delete only by the given id not all
    @After
    public void deleteSensor() throws SolrServerException, IOException{
        c.deleteSensor("");
    }
}
