/*
 * Copyright 2020 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.psfm_extractor;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.NullHelpers;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * Test the PsFmExtractor class for the car feature model.
 *
 * @author Calvin Hansch
 */
public class CarTest {
    private static final @NonNull File RESOURCE_DIR = new File("testdata/tmp_res");
    
    /**
     * Creates the temporary resource dir.
     */
    @Before
    public void createTmpRes() {
        RESOURCE_DIR.mkdir();
    }
    
    /**
     * Deletes the temporary resource directory.
     * 
     * @throws IOException If deleting fails.
     */
    @After
    public void deleteTmpRes() throws IOException {
        Util.deleteFolder(RESOURCE_DIR);
    }
    
    /**
     * Load the xfm models from testdata directory.
     * @param testModel The model that the parsing should be tested for.
     * @throws SetUpException 
     * @return Returns the variability model created by the PsFm Extractor.
     */
    private VariabilityModel loadModel(File testModel) throws SetUpException {
        Assert.assertTrue("Model for testing does not exist: " + testModel.getAbsolutePath(), testModel.exists());
        
        // Create a configuration for testing
        Properties props = new Properties();
        props.setProperty("resource_dir", RESOURCE_DIR.getPath());
        props.setProperty(DefaultSettings.VARIABILITY_INPUT_FILE.getKey(), testModel.getPath());
        
        TestConfiguration config = null;
        try {
            config = new TestConfiguration(props);
        } catch (SetUpException e) {
            Assert.fail("Could not create configuration for testing with XFM=" + testModel.getAbsolutePath());
        }
        
        // Run extractor
        PsFmExtractor extractor = new PsFmExtractor();
        extractor.init(NullHelpers.notNull(config));
        return  extractor.runOnFile(testModel);
    }
    
    /** 
     * Test whether the complex "car" model is parsed correctly.
     * @throws SetUpException 
     */
    @Test
    public void testOR() throws SetUpException {
        VariabilityModel varModel = loadModel(new File("testdata/xfm/Car.xfm"));
        Map<String, VariabilityVariable> varMap = varModel.getVariableMap();
        Scanner scanner;
        try {
            scanner = new Scanner(new File("testdata/xfm/carList.csv"));
            //Read ";" separated csv and split each line into variable name and type.
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] var = line.split(";");
                String varName = var[0];
                String varType = var[1];
                
                // check if variable name is contained in VariabilityModel
                assertTrue("Variable not contained in Variability Model",
                        varMap.containsKey(varName));
                
                //check if correct type was parsed for a given variable
                assertEquals("Type missmatch in parsed and expected variable type",
                        varType, varMap.get(varName).getType());
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
