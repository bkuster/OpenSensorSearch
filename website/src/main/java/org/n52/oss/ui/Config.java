/**
 * ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.oss.ui;

public class Config {

    public static final String BASE_URL = "http://gsoc.dev.52north.org:8093";

    private String ossService = "http://localhost:8080/oss-service";

    private String sirEndpoint = this.ossService + "/sir";

    private String apiEndpoint = this.ossService + "/api/v1";

    public String getSirEndpoint() {
        return this.sirEndpoint;
    }

    public void setSirEndpoint(String sirEndpoint) {
        this.sirEndpoint = sirEndpoint;
    }

    public String getApiEndpoint() {
        return this.apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
}
