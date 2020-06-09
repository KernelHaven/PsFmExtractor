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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.*;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.NullHelpers;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * Tests the PsFmExtractor for the atomic feature models.
 *
 * @author Calvin Hansch
 */
public class AtomicTests {
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
        VariabilityModel result = null;
        try {
            result = extractor.runOnFile(testModel);
        } catch (ExtractorException e) {
            Assert.fail("Could not init extractor due to" + e.getMessage());
        }
        Assert.assertNotNull(result);
        
        return result;
    }
    
    /** 
     * Test whether atomic "or" model is parsed correctly.
     * @throws SetUpException 
     */
    @Test
    public void testOR() throws SetUpException {
        VariabilityModel varModel = loadModel(new File("testdata/xfm/testOr.xfm"));
        VariabilityVariable var = varModel.getVariableMap().get("or");
        Assert.assertNotNull("Tested variable not found.", var);
        Assert.assertEquals("Tested variable is not of extected type", "or", var.getType().toLowerCase());
    }
    
    /** 
     * Run a negative test to ensure that testOr() does not always evaluate true.
     * @throws SetUpException 
     */
    @Test
    public void negativeTestOR() throws SetUpException {
        VariabilityModel varModel = loadModel(new File("testdata/xfm/testAlternative.xfm"));
        VariabilityVariable var = varModel.getVariableMap().get("alternative");
        Assert.assertNotNull("Tested variable not found.", var);
        Assert.assertNotEquals("Tested variable is not of extected type", "or", var.getType().toLowerCase());
    }
    
    /** 
     * Test whether atomic "alternative" model is parsed correctly.
     * @throws SetUpException 
     */
    @Test
    public void testAlternative() throws SetUpException {
        VariabilityModel varModel = loadModel(new File("testdata/xfm/testAlternative.xfm"));
        VariabilityVariable var = varModel.getVariableMap().get("alternative");
        Assert.assertNotNull("Tested variable not found.", var);
        Assert.assertEquals("Tested variable is not of extected type", "alternative", var.getType().toLowerCase());
    }
    
    /** 
     * Test whether atomic "mandatory" model is parsed correctly.
     * @throws SetUpException 
     */
    @Test
    public void testMandatory() throws SetUpException {
        VariabilityModel varModel = loadModel(new File("testdata/xfm/testMandatory.xfm"));
        VariabilityVariable var = varModel.getVariableMap().get("mandatory");
        Assert.assertNotNull("Tested variable not found.", var);
        Assert.assertEquals("Tested variable is not of extected type", "mandatory", var.getType().toLowerCase());
    }
    
    /** 
     * Test whether atomic "Optional" model is parsed correctly.
     * @throws SetUpException 
     */
    @Test
    public void testOptional() throws SetUpException {
        VariabilityModel varModel = loadModel(new File("testdata/xfm/testOptional.xfm"));
        VariabilityVariable var = varModel.getVariableMap().get("optional");
        Assert.assertNotNull("Tested variable not found.", var);
        Assert.assertEquals("Tested variable is not of extected type", "optional", var.getType().toLowerCase());
    }
}
