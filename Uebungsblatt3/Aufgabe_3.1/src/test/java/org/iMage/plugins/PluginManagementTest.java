package org.iMage.plugins;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PluginManagementTest {

    List<PluginForJmjrst> testList;

    @Before
    public void setUp() throws Exception {
        testList = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {
        testList = null;
    }

    //This test should be ignored after plugins are generated and loaded to this project.
    @Ignore
    @Test
    public void getPluginsEmpty() {
        for (PluginForJmjrst plugin : PluginManagement.getPlugins()) {
            testList.add(plugin);
        }

        assertEquals(0, testList.size());
    }
}