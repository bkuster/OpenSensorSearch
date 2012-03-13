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
package org.n52.sir.client;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.util.XmlTools;

import de.uniMuenster.swsl.sir.DeleteSensorInfoRequestDocument;
import de.uniMuenster.swsl.sir.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest;
import de.uniMuenster.swsl.sir.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted;
import de.uniMuenster.swsl.sir.SensorIdentificationDocument.SensorIdentification;
import de.uniMuenster.swsl.sir.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class DeleteSensorInfoBean extends AbstractBean {

    private String deleteRefSensorID = "";

    private String deleteRefType = "";

    private String deleteRefURL = "";

    private boolean deleteSensor = false;

    private String sensorIDinSIR = "";

    private String serviceSpecificSensorID = "";

    private String serviceType = "";

    private String serviceURL = "";

    @Override
    public void buildRequest() {
        DeleteSensorInfoRequestDocument requestDoc = DeleteSensorInfoRequestDocument.Factory.newInstance(XmlTools.xmlOptionsForNamespaces());
        DeleteSensorInfoRequest request = requestDoc.addNewDeleteSensorInfoRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        // InfoToBeInserted
        InfoToBeDeleted infoToBeDeleted = request.addNewInfoToBeDeleted();
        SensorIdentification sensorIdentification = infoToBeDeleted.addNewSensorIdentification();

        if ( !this.sensorIDinSIR.isEmpty()) {
            sensorIdentification.setSensorIDInSIR(this.sensorIDinSIR);
        }

        // ServiceReference
        else if ( !this.serviceType.isEmpty() && !this.serviceType.isEmpty()
                && !this.serviceSpecificSensorID.isEmpty()) {
            ServiceReference serviceRef = sensorIdentification.addNewServiceReference();
            serviceRef.setServiceURL(this.serviceURL);
            serviceRef.setServiceType(this.serviceType);
            serviceRef.setServiceSpecificSensorID(this.serviceSpecificSensorID);
        }

        else {
            this.requestString = "Some kind of sensor identification is required!";
        }

        // delete sensor
        if (this.deleteSensor) {
            infoToBeDeleted.setDeleteSensor(this.deleteSensor);
        }
        // delete service reference
        else if ( !this.sensorIDinSIR.isEmpty()) {
            if ( !this.deleteRefURL.isEmpty() && !this.deleteRefType .isEmpty() && !this.deleteRefSensorID.isEmpty()) {
                ServiceReference serviceRef = infoToBeDeleted.addNewServiceInfo().addNewServiceReference();
                serviceRef.setServiceURL(this.deleteRefURL);
                serviceRef.setServiceType(this.deleteRefType);
                serviceRef.setServiceSpecificSensorID(this.deleteRefSensorID);
            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
    }

    /**
     * @return the deleteRefSensorID
     */
    public String getDeleteRefSensorID() {
        return this.deleteRefSensorID;
    }

    /**
     * @return the deleteRefType
     */
    public String getDeleteRefType() {
        return this.deleteRefType;
    }

    /**
     * @return the deleteRefURL
     */
    public String getDeleteRefURL() {
        return this.deleteRefURL;
    }

    /**
     * @return the sensorIDinSIR
     */
    public String getSensorIDinSIR() {
        return this.sensorIDinSIR;
    }

    /**
     * @return the serviceInfosServiceSpecificSensorID
     */
    public String getServiceSpecificSensorID() {
        return this.serviceSpecificSensorID;
    }

    /**
     * @return the serviceInfosServiceType
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * @return the serviceInfosServiceURL
     */
    public String getServiceURL() {
        return this.serviceURL;
    }

    /**
     * @return the deleteSensor
     */
    public boolean isDeleteSensor() {
        return this.deleteSensor;
    }

    /**
     * @param deleteRefSensorID the deleteRefSensorID to set
     */
    public void setDeleteRefSensorID(String deleteRefSensorID) {
        this.deleteRefSensorID = deleteRefSensorID;
    }

    /**
     * @param deleteRefType the deleteRefType to set
     */
    public void setDeleteRefType(String deleteRefType) {
        this.deleteRefType = deleteRefType;
    }

    /**
     * @param deleteRefURL the deleteRefURL to set
     */
    public void setDeleteRefURL(String deleteRefURL) {
        this.deleteRefURL = deleteRefURL;
    }

    /**
     * @param deleteSensor the deleteSensor to set
     */
    public void setDeleteSensor(boolean deleteSensor) {
        this.deleteSensor = deleteSensor;
    }

    /**
     * @param sensorIDinSIR
     *        the sensorIDinSIR to set
     */
    public void setSensorIDinSIR(String sensorIDinSIR) {
        this.sensorIDinSIR = sensorIDinSIR;
    }

    /**
     * @param serviceInfosServiceSpecificSensorID
     *        the serviceInfosServiceSpecificSensorID to set
     */
    public void setServiceSpecificSensorID(String serviceInfosServiceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceInfosServiceSpecificSensorID;
    }

    /**
     * @param serviceInfosServiceType
     *        the serviceInfosServiceType to set
     */
    public void setServiceType(String serviceInfosServiceType) {
        this.serviceType = serviceInfosServiceType;
    }

    /**
     * @param serviceInfosServiceURL
     *        the serviceInfosServiceURL to set
     */
    public void setServiceURL(String serviceInfosServiceURL) {
        this.serviceURL = serviceInfosServiceURL;
    }

}
