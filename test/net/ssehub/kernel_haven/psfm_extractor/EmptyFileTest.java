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

import static org.junit.Assert.assertFalse;
import java.io.File;
import org.junit.Test;

/**
 * Test whether "empty.file" is present as to prevent IO errors
 * when creating new HashMap for VariabilityVariable.
 * @author Calvin Hansch
 */
public class EmptyFileTest {
    /**
     * Check whether the empty file is present as to prevent IO exceptions.
     */
    @Test
    public void testEmptyFile() {
        File emptyFile = new File("empty.file");
        assert (emptyFile.canRead());
    }
    
    /**
     * Negative check to verify that positive test does not always yield true.
     */
    @Test
    public void testWrongFile() {
        File emptyFile = new File("wrongFile.file");
        assertFalse(emptyFile.canRead());
    }

}
