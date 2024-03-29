/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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

package org.jboss.resteasy.microprofile.test.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.microprofile.test.config.resource.MicroProfileConfigFilter;
import org.jboss.resteasy.microprofile.test.config.resource.MicroProfileConfigResource;
import org.jboss.resteasy.microprofile.test.config.resource.TestConfigApplication;
import org.jboss.resteasy.microprofile.test.util.MicroProfileConfigSystemPropertySetupTask;
import org.jboss.resteasy.microprofile.test.util.TestEnvironment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2131
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(MicroProfileConfigSystemPropertySetupTask.class)
public class MicroProfileConfigServletContextListenerTest {

    static Client client;

    @ArquillianResource
    private URL url;

    @Deployment
    public static Archive<?> deploy() {
        return TestEnvironment.createWar(MicroProfileConfigServletContextListenerTest.class)
                .addClasses(TestConfigApplication.class, MicroProfileConfigFilter.class, MicroProfileConfigResource.class)
                .setWebXML(MicroProfileConfigServletContextListenerTest.class.getPackage(), "web_servlet_context_listener.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private URI generateURL(String path) throws URISyntaxException {
        return TestEnvironment.generateUri(url, path);
    }

    /**
     * @tpTestDetails Verify system variables are accessible and have highest priority; get Config programmatically.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testSystemProgrammatic() throws Exception {
        Response response = client.target(generateURL("/system/prog")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("system-system", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Verify system variables are accessible and have highest priority; get Config by injection.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testSystemInject() throws Exception {
        Response response = client.target(generateURL("/system/inject")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("system-system", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Verify web.xml servlet init params are accessible and have higher priority than filter params and context
     *                params; get Config programmatically.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testInitProgrammatic() throws Exception {
        Response response = client.target(generateURL("/init/prog")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("init-init", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Verify web.xml servlet init params are accessible and have higher priority than filter params and context
     *                params; get Config by injection.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testInitInject() throws Exception {
        Response response = client.target(generateURL("/init/inject")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("init-init", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Verify web.xml context params are accessible; get Config programmatically.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testContextProgrammatic() throws Exception {
        Response response = client.target(generateURL("/context/prog")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("context-context", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Verify web.xml context params are accessible; get Config by injection.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testContextInject() throws Exception {
        Response response = client.target(generateURL("/context/inject")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("context-context", response.readEntity(String.class));
    }
}
