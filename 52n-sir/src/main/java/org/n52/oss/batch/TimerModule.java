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

package org.n52.oss.batch;

import java.util.HashMap;
import java.util.Map;

import org.n52.sir.util.jobs.impl.TimerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.ServletModule;

/**
 * 
 * @author Daniel
 * 
 */
public class TimerModule extends ServletModule {
    
    private static Logger log = LoggerFactory.getLogger(TimerModule.class);

    @Override
    protected void configureServlets() {
        // super.configureServlets();

        log.debug("Configuring TimerModule");

        Map<String, String> params = new HashMap<>();
        params.put("isDaemon", "true");

        serve("/batch*").with(TimerServlet.class, params);

        // http://code.google.com/p/google-guice/wiki/UntargettedBindings
        bind(TimerServlet.class);
    }

}
