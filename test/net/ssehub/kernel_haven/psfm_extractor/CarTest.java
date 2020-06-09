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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

/**
 * Test the PsFmExtractor class for the car feature model.
 *
 * @author Calvin Hansch
 * @author El-Sharkawy
 */
@RunWith(Parameterized.class)
public class CarTest extends AbstractModelTest {
    private static final VariabilityModel MODEL = loadModel(new File("testdata/xfm/Car.xfm"));
    
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
    public CarTest(String variableName, String type, int nReferencedVars, int nReferncesToVar, int nestingDepth) {
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
            {"Car", "mandatory", 0, 0, 0},
            // Safety Functions
            {"Car_Safety", "optional", 0, 0, 1},
            {"Car_ABS", "optional", 0, 1, 2},
            {"Car_ESP", "optional", 1, 0, 2},
            // Brakes
            {"Brakes", "mandatory", 0, 0, 1},
            {"BrakeActuation", "mandatory", 0, 0, 2},
            {"Electric", "alternative", 0, 0, 3},
            {"Electrohydraulic", "alternative", 0, 0, 3},
            {"Hydraulisch", "alternative", 0, 0, 3},
            {"Rear", "mandatory", 0, 0, 2},
            {"Disc_rear", "alternative", 0, 0, 3},
            {"Drum_rear", "alternative", 0, 0, 3},
            {"Front", "mandatory", 0, 0, 2},
            {"Disc_front", "alternative", 0, 0, 3},
            {"Drum_front", "alternative", 0, 0, 3},
            // Gear Box
            {"Gearbox", "mandatory", 0, 0, 1},
            {"Automatic", "or", 0, 0, 2},
            {"Manual", "or", 0, 0, 2},
            {"Gears", "mandatory", 0, 0, 2},
            // Engine 
            {"Engine", "mandatory", 0, 0, 1},
            {"Diesel", "alternative", 0, 0, 2},
            {"Gasoline", "alternative", 0, 0, 2},
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
