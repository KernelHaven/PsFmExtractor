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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.junit.Assert;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.test_utils.TestConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.NullHelpers;
import net.ssehub.kernel_haven.variability_model.HierarchicalVariable;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * Abstract class to define tests on XFM files.
 *
 * @author El-Sharkawy
 * @author Calvin Hansch
 */
public class AbstractModelTest {

    private static final @NonNull File RESOURCE_DIR = new File("testdata/xfm");
    
    /**
     * Load the xfm models from testdata directory.
     * @param testModel The model that the parsing should be tested for.
     * @throws SetUpException 
     * @return Returns the variability model created by the PsFm Extractor.
     */
    static VariabilityModel loadModel(File testModel) {
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
        try {
            extractor.init(NullHelpers.notNull(config));
        } catch (SetUpException e) {
            Assert.fail("Could not init extractor due to" + e.getMessage());
        }
        
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
     * Checks that the specified variable is contained in the given model.
     * @param varModel The model which should contain the specified variable.
     * @param variableName The expected name of the variable.
     * @param type The expected type of the variable
     * @param nReferencedVars The expected number of other variables referenced by the tested variable
     * @param nReferncesToVar The expected number of variables that reference the tested variable
     * @param nestingDepth The expected nesting depth of the variable
     */
    // checkstyle: stop parameter number check
    public void assertVariableInModel(VariabilityModel varModel, String variableName, String type, int nReferencedVars,
        int nReferncesToVar, int nestingDepth) {
    // checkstyle: start parameter number check
        
        HierarchicalVariable variable = (HierarchicalVariable) varModel.getVariableMap().get(variableName);
        
        // Check if variable name is contained in VariabilityModel
        Assert.assertNotNull("Variable not contained in Variability Model", variable);
        Assert.assertEquals("Variable has not the expted name", variableName, variable.getName());
        
        // Check if correct type was parsed for a given variable
        assertEquals("Type missmatch in parsed and expected variable type", type, variable.getType());
        
        int nVarsReferenced = variable.getVariablesUsedInConstraints() != null
            ? variable.getVariablesUsedInConstraints().size() : 0;
        assertEquals("Wrong number of variables referenced in constraints", nReferencedVars, nVarsReferenced);
        
        int nReferences = variable.getUsedInConstraintsOfOtherVariables() != null
            ? variable.getUsedInConstraintsOfOtherVariables().size() : 0;
        assertEquals("Wrong number of variables referenced in constraints", nReferncesToVar, nReferences);
        
        assertEquals("Wrong hierarchical level of tested variable", nestingDepth, variable.getNestingDepth());
    }
}
