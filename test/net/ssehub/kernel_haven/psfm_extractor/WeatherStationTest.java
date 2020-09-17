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
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * Tests the PsFmExtractor for the atomic feature models.
 *
 * @author Sascha El-Sharkawy
 */
@RunWith(Parameterized.class)
public class WeatherStationTest extends AbstractModelTest {
    private static final VariabilityModel MODEL = loadModel(new File("testdata/xfm/WeatherStation.xfm"));
    
    private String variableName;
    private String type;
    private int nReferencedVars;
    private int nReferncesToVar;
    private int nestingDepth;

    /**
     * Constructor used by {@link #data()} method.
     * @param variableName The expected name of the variable.
     * @param type The expected type of the variable
     * @param nReferencedVars The expected number of other variables referenced by the tested variable
     * @param nReferncesToVar The expected number of variables that reference the tested variable
     * @param nestingDepth The expected nesting depth of the variable
     */
    public WeatherStationTest(String variableName, String type, int nReferencedVars, int nReferncesToVar,
        int nestingDepth) {
        
        this.variableName = variableName;
        this.type = type;
        this.nReferencedVars = nReferencedVars;
        this.nReferncesToVar = nReferncesToVar;
        this.nestingDepth = nestingDepth;
    }
    
    /**
     * Provides the test data to be tested.
     * @return Each lines represents one test
     */
    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // The root
            {"WeatherStation", "mandatory", 0, 0, 0},
            // Sensors
            {"Sensors"    , "mandatory", 0, 0, 1},
            {"Temperature", "or"       , 0, 1, 2},
            {"Color"      , "optional" , 0, 0, 3},
            {"WindSpeed"  , "or"       , 0, 1, 2},
            {"AirPressure", "or"       , 0, 0, 2},
            // Languages
            {"Languages", "mandatory"  , 0, 0, 1},
            {"English"  , "alternative", 0, 0, 2},
            {"German"   , "alternative", 0, 0, 2},
            // Warnings
            {"Warnings", "optional", 0, 0, 1},
            {"Gale"    , "or"      , 1, 0, 2},
            {"Heat"    , "or"      , 1, 0, 2},
        });
    }
    
    /**
     * Performs the test.
     */
    @Test
    public void test() {
        assertVariableInModel(MODEL, variableName, type, nReferencedVars, nReferncesToVar, nestingDepth);
    }
}
