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
/*******************************************************************************

by 52 North Initiative for Geospatial Open Source Software GmbH

Contact: Andreas Wytzisk
52 North Initiative for Geospatial Open Source Software GmbH
Martin-Luther-King-Weg 24
48155 Muenster, Germany
info@52north.org

This program is free software; you can redistribute and/or modify it under 
the terms of the GNU General Public License version 2 as published by the 
Free Software Foundation.

This program is distributed WITHOUT ANY WARRANTY; even without the implied
WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program (see gnu-gpl v2.txt). If not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
visit the Free Software Foundation web page, http://www.fsf.org.

Authors: Jan Schulte, Daniel Nüst
 
 ******************************************************************************/

package org.n52.sir.datastructure;

import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.util.Tools;

import de.uniMuenster.swsl.sir.SearchCriteriaDocument.SearchCriteria.Phenomenon;
import de.uniMuenster.swsl.sir.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters;
import de.uniMuenster.swsl.sir.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType;
import de.uniMuenster.swsl.sor.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.Enum;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class SirSearchCriteria_Phenomenon {

    public enum SirMatchingType {
        EQUIVALENT_TYPE, SUB_TYPE, SUPER_TYPE;

        /**
         * 
         * @param schemaMatchingType
         * @return
         * @throws OwsExceptionReport
         */
        public static SirMatchingType getSirMatchingType(MatchingType.Enum schemaMatchingType) throws OwsExceptionReport {
            if (schemaMatchingType.equals(MatchingType.SUPER_TYPE)) {
                return SirMatchingType.SUPER_TYPE;
            }
            else if (schemaMatchingType.equals(MatchingType.EQUIVALENT_TYPE)) {
                return SirMatchingType.EQUIVALENT_TYPE;
            }
            else if (schemaMatchingType.equals(MatchingType.SUB_TYPE)) {
                return SirMatchingType.SUB_TYPE;
            }
            else {
                throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                             "MatchingType",
                                             "Your request was invalid: MatchingType parameter is missing or wrong!");
            }
        }

        /**
         * 
         * @param string
         * @return
         * @throws OwsExceptionReport
         */
        public static SirMatchingType getSirMatchingType(String string) throws OwsExceptionReport {
            if (string.equalsIgnoreCase(SirMatchingType.SUPER_TYPE.toString())) {
                return SirMatchingType.SUPER_TYPE;
            }
            else if (string.equalsIgnoreCase(SirMatchingType.EQUIVALENT_TYPE.toString())) {
                return SirMatchingType.EQUIVALENT_TYPE;
            }
            else if (string.equalsIgnoreCase(SirMatchingType.SUB_TYPE.toString())) {
                return SirMatchingType.SUB_TYPE;
            }
            else {
                throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                             "MatchingType",
                                             "Your request was invalid: MatchingType parameter is missing or wrong!");
            }
        }

        /**
         * 
         * @param sirMatchingType
         * @return
         * @throws OwsExceptionReport
         */
        public static Enum getSorMatchingType(SirMatchingType sirMatchingType) throws OwsExceptionReport {
            if (sirMatchingType.equals(SirMatchingType.SUPER_TYPE)) {
                return de.uniMuenster.swsl.sor.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.SUPER_TYPE;
            }
            else if (sirMatchingType.equals(SirMatchingType.EQUIVALENT_TYPE)) {
                return de.uniMuenster.swsl.sor.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.EQUIVALENT_TYPE;
            }
            else if (sirMatchingType.equals(SirMatchingType.SUB_TYPE)) {
                return de.uniMuenster.swsl.sor.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.SUB_TYPE;
            }

            OwsExceptionReport er = new OwsExceptionReport();
            er.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "MatchingType",
                                 "MatchingType not supported!");
            throw er;
        }

        /**
         * 
         * @return
         * @throws OwsExceptionReport
         */
        public de.uniMuenster.swsl.sir.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.Enum getSchemaMatchingType() throws OwsExceptionReport {
            if (this.equals(SirMatchingType.SUPER_TYPE)) {
                return MatchingType.SUPER_TYPE;
            }
            else if (this.equals(SirMatchingType.EQUIVALENT_TYPE)) {
                return MatchingType.EQUIVALENT_TYPE;
            }
            else if (this.equals(SirMatchingType.SUB_TYPE)) {
                return MatchingType.SUB_TYPE;
            }

            OwsExceptionReport er = new OwsExceptionReport();
            er.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "MatchingType",
                                 "MatchingType not supported!");
            throw er;
        }
    }

    private SirMatchingType matchingType;

    private String phenomenonName;

    private int searchDepth;

    private String sorUrl;

    /**
     * 
     * @param phenomenon
     * @throws OwsExceptionReport
     */
    public SirSearchCriteria_Phenomenon(Phenomenon phenomenon) throws OwsExceptionReport {

        this.phenomenonName = phenomenon.getPhenomenonName();

        if (phenomenon.isSetSORParameters()) {
            SORParameters sorParams = phenomenon.getSORParameters();
            this.sorUrl = sorParams.getSORURL();
            this.searchDepth = sorParams.getSearchDepth();
            this.matchingType = SirMatchingType.getSirMatchingType(sorParams.getMatchingType());
        }
    }

    /**
     * @param phenomenonName
     * 
     */
    public SirSearchCriteria_Phenomenon(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    /**
     * @param phenomenonName
     * @param sorUrl
     * @param matchingType
     * @param searchDepth
     */
    public SirSearchCriteria_Phenomenon(String phenomenonName,
                                        String sorUrl,
                                        SirMatchingType matchingType,
                                        int searchDepth) {
        this.phenomenonName = phenomenonName;
        this.sorUrl = sorUrl;
        this.matchingType = matchingType;
        this.searchDepth = searchDepth;
    }

    /**
     * @return the matchingType
     */
    public SirMatchingType getMatchingType() {
        return this.matchingType;
    }

    /**
     * @return the phenomenonName
     */
    public String getPhenomenonName() {
        return this.phenomenonName;
    }

    /**
     * @return the searchDepth
     */
    public int getSearchDepth() {
        return this.searchDepth;
    }

    /**
     * @return the sorUrl
     */
    public String getSorUrl() {
        return this.sorUrl;
    }

    /**
     * @param matchingType
     *        the matchingType to set
     */
    public void setMatchingType(SirMatchingType matchingType) {
        this.matchingType = matchingType;
    }

    /**
     * @param phenomenonName
     *        the phenomenonName to set
     */
    public void setPhenomenonName(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    /**
     * @param searchDepth
     *        the searchDepth to set
     */
    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    /**
     * @param sorUrl
     *        the sorUrl to set
     */
    public void setSorUrl(String sorUrl) {
        this.sorUrl = sorUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SirSearchCriteria_Phenomenon [phenomenonName: ");
        sb.append(this.phenomenonName);
        sb.append(", SOR parameters: URL = ");
        sb.append(this.sorUrl);
        sb.append(", matching type = ");
        sb.append(this.matchingType);
        sb.append(", search depth = ");
        sb.append(this.searchDepth);
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @return true if all parameters for SOR are given
     */
    public boolean usesSOR() {
        if (this.sorUrl == null || this.matchingType == null)
            return false;
        return Tools.noneEmpty(new String[] {this.sorUrl,
                                             this.matchingType.toString(),
                                             Integer.toString(this.searchDepth)});
    }

}