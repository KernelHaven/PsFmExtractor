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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;
import net.ssehub.kernel_haven.variability_model.AbstractVariabilityModelExtractor;
import net.ssehub.kernel_haven.variability_model.HierarchicalVariable;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;
import net.ssehub.kernel_haven.variability_model.VariabilityModelDescriptor.Attribute;
import net.ssehub.kernel_haven.variability_model.VariabilityVariable;

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
    protected @Nullable VariabilityModel runOnFile(@NonNull File target) {
        XMLParser fm1 = new XMLParser(xfmFile);
        
        //Empty because there is no constraint file
        File constraintFile = new File("empty.file");
        
        //create Map to store VariabilityVariable
        Map<@NonNull String, VariabilityVariable> variables = new HashMap<>();
        
        NodeList nodeList = null;
        try {
            nodeList = fm1.getCmElement();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            variables.put(constraintFile.toString(), 
                    new HierarchicalVariable(fm1.getName(nodeList.item(i)), fm1.getType(nodeList.item(i))));
        }
        
        VariabilityModel result = new VariabilityModel(constraintFile, variables);
        result.getDescriptor().addAttribute(Attribute.HIERARCHICAL);
        return result;
    }

    @Override
    protected @NonNull String getName() {
        return "PsFmExtractor";
    }
}
