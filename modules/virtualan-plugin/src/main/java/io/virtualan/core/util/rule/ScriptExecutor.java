package io.virtualan.core.util.rule;

import groovy.lang.*;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.MockServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("scriptExecutor")
public class ScriptExecutor {
    private final static Logger LOG = LoggerFactory.getLogger(ScriptExecutor.class);
    private final GroovyClassLoader loader;
    private final GroovyShell shell;

    public ScriptExecutor() {
        loader = new GroovyClassLoader(this.getClass().getClassLoader());
        shell = new GroovyShell(loader, new Binding());
    }


    public MockResponse executeScript(MockServiceRequest mockServiceRequest, MockResponse mockResponse, String scriptText) throws IOException {
        Script script = shell.parse(scriptText);
        LOG.info("Executing {}", mockServiceRequest);
        MockResponse result = (MockResponse) script.invokeMethod("executeScript", new Object[] { mockServiceRequest, mockResponse });
        return result;
    }


    public static void main(String[] args) throws InstantiationException, IllegalAccessException,
      ResourceException, ScriptException, IOException, javax.script.ScriptException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        LOG.info("Example mock Groovy scripts integration with Java.: ");
        MockServiceRequest mockServiceRequest = new MockServiceRequest();
        mockServiceRequest.setInput("Elan");
        MockResponse mockResponse = new MockResponse();
        System.out.println(scriptExecutor.executeScript( mockServiceRequest, mockResponse,"def executeScript(mockServiceRequest) {mockServiceRequest.getInput()}"));
    }
}
