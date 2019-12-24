/*
 * Copyright 2019 University of Hildesheim, Software Systems Engineering
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

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.AbstractVariabilityModelExtractor;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * Feature Model extractor for xfm Files.
 *
 * @author Calvin Hansch
 */
public class PsFmExtractor extends AbstractVariabilityModelExtractor {
    private File xfmFile;
    
    @Override
    protected void init(@NonNull Configuration config) throws SetUpException {
        xfmFile = config.getValue(DefaultSettings.VARIABILITY_INPUT_FILE);
    }

    @Override
    protected @Nullable VariabilityModel runOnFile(@NonNull File target) throws ExtractorException {
        XMLParser fm1 = new XMLParser(xfmFile);
        return null;
    }

    @Override
    protected @NonNull String getName() {
        return "PsFmExtractor";
    }

}
