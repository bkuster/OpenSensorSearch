/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.datastructure;

import java.util.Collection;
import java.util.Date;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirSearchResultElement {

    private Date lastUpdate;

    private SirSensorDescription sensorDescription;

    private String sensorIdInSir;

    private Collection<SirServiceReference> serviceReferences;

    public SirSearchResultElement() {
        // empty constructor for deserialization
    }

    /**
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * @return the sensorDescription
     */
    public SirSensorDescription getSensorDescription() {
        return this.sensorDescription;
    }

    /**
     * @return the sensorIdInSir
     */
    public String getSensorIdInSir() {
        return this.sensorIdInSir;
    }

    /**
     * @return the serviceDescriptions
     */
    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    /**
     * @param lastUpdate
     *        the lastUpdate to set
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(SirSensorDescription sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @param sensorIdInSir
     *        the sensorIdInSir to set
     */
    public void setSensorIdInSir(String sensorIdInSir) {
        this.sensorIdInSir = sensorIdInSir;
    }

    /**
     * @param serviceDescriptions
     *        the serviceDescriptions to set
     */
    public void setServiceReferences(Collection<SirServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SearchResultElement: ");
        sb.append(", SensorID in SIR: " + this.sensorIdInSir);
        sb.append(", ServiceDescription: " + this.serviceReferences);
        sb.append(", Last update: " + this.lastUpdate);
        return sb.toString();
    }
}
