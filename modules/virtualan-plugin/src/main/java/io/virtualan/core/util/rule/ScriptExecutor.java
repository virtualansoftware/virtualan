/*
 * Copyright 2020 Virtualan Contributors (https://virtualan.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.virtualan.core.util.rule;

import groovy.lang.*;
import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.MockServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This is Script executor Service .
 *
 * @author  Elan Thangamani
 *
 **/

@Service("scriptExecutor")
public class ScriptExecutor {
    private static final  Logger LOG = LoggerFactory.getLogger(ScriptExecutor.class);
    private final GroovyClassLoader loader;
    private final GroovyShell shell;

    public ScriptExecutor() {
        loader = new GroovyClassLoader(this.getClass().getClassLoader());
        shell = new GroovyShell(loader, new Binding());
    }

    public MockResponse executeScript(MockServiceRequest mockServiceRequest, MockResponse mockResponse) {
        Script script = shell.parse(mockServiceRequest.getRule().toString());
        LOG.debug("Executing {}", mockServiceRequest);
        return (MockResponse) script.invokeMethod("executeScript", new Object[] { mockServiceRequest, mockResponse });
    }
}
