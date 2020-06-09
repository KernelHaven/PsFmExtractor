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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.ExtractorException;
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
    protected @Nullable VariabilityModel runOnFile(@NonNull File target) throws ExtractorException {
        XMLParser fm;
        try {
            fm = new XMLParser(xfmFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ExtractorException(e);
        }
        
        //Empty because there is no constraint file
        File constraintFile = new File("empty.file");
        
        //create Map to store VariabilityVariable
        Map<@NonNull String, VariabilityVariable> variables = new HashMap<>();
        Map<HierarchicalVariable, Element> elementMap =  new HashMap<HierarchicalVariable, Element>();
        Map<String, HierarchicalVariable> idMap = new HashMap<>();
        
        // Creates Variability variables with name and type, but no relations/hierarchies
        NodeList nodeList = fm.getCmElement();          
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            String name = fm.getName(node);
            if (null != name) {
                HierarchicalVariable var = new HierarchicalVariable(name, fm.getType(node));
                variables.put(name, var);
                Element currElement = (Element) node;
                
                /*
                 *  save corresponding name and id for current node so we don't
                 *  need to iterate over complete array later
                 */
                idMap.put(currElement.getAttribute("cm:id"), var);
               
                /*
                 *  save element id with corresponding Node so we
                 *  don't need to iterate over complete array when finding the
                 *  parents
                 */
                elementMap.put(var, currElement);
            }
        }
        
        // Set hierarchy: This must be done from up to down
        computeHierarchy(fm, variables, elementMap, idMap);
        
        // Compute relations
        computeRelations(fm, variables, elementMap, idMap);
        
        VariabilityModel result = new VariabilityModel(constraintFile, variables);
        result.getDescriptor().addAttribute(Attribute.HIERARCHICAL);
        result.getDescriptor().addAttribute(Attribute.CONSTRAINT_USAGE);
        
        return result;
    }

    /**
     * Computes the part of each {@link VariabilityVariable} of the {@link VariabilityModel}.
     * @param fm The XML parser used to parse the XFM file.
     * @param variables The map of variables created for the {@link VariabilityModel} (will be changed as side effect)
     * @param elementMap A map containing of <tt>(variability variable, XML node)</tt>
     * @param idMap A map containing of <tt>(XML ID, variability variable)</tt>
     */
    private void computeHierarchy(XMLParser fm, Map<@NonNull String, VariabilityVariable> variables,
        Map<HierarchicalVariable, Element> elementMap, Map<String, HierarchicalVariable> idMap) {
        
        // Setting hierarchy must be done from up to down -> Sort elements to do this in correct order
        List<VariabilityVariable> vars = new ArrayList<>(variables.values());
        Comparator<VariabilityVariable> byLevel = new Comparator<VariabilityVariable>() {
            @Override
            public int compare(VariabilityVariable o1, VariabilityVariable o2) {
                int level1 = fm.getNestingLevel(elementMap.get(o1));
                int level2 = fm.getNestingLevel(elementMap.get(o2));
                return Integer.compare(level1, level2);
            }
        };
        Collections.sort(vars, byLevel);
        
        // Set the parents
        for (VariabilityVariable var : vars) {
            Node varAsNode = elementMap.get(var);
            String parentID = fm.getParent(varAsNode);
            
            if (null != parentID) {
                HierarchicalVariable parent = idMap.get(parentID);
                ((HierarchicalVariable) var).setParent(parent);                  
            }
        }
    }
    
    /**
     * Computes relations between features created through constraints.
     * @param fm The XML parser used to parse the XFM file.
     * @param variables The map of variables created for the {@link VariabilityModel} (will be changed as side effect)
     * @param elementMap A map containing of <tt>(variability variable, XML node)</tt>
     * @param idMap A map containing of <tt>(XML ID, variability variable)</tt>
     */
    private void computeRelations(XMLParser fm, Map<@NonNull String, VariabilityVariable> variables,
        Map<HierarchicalVariable, Element> elementMap, Map<String, HierarchicalVariable> idMap) {
        
        for (VariabilityVariable variable : variables.values()) {
            Node varAsNode = elementMap.get(variable);
            List<String> ids = fm.getReferencedVariables(varAsNode);
            if (!ids.isEmpty()) {
                Set<VariabilityVariable> referencedVars = variable.getVariablesUsedInConstraints();
                if (null == referencedVars) {
                    referencedVars = new HashSet<>();
                }
                for (String id : ids) {
                    VariabilityVariable referencedVar = idMap.get(id);
                    referencedVars.add(referencedVar);
                    // Handle reverse relation
                    Set<VariabilityVariable> referenceToVars = referencedVar.getUsedInConstraintsOfOtherVariables();
                    if (null == referenceToVars) {
                        referenceToVars = new HashSet<>();
                    }
                    referenceToVars.add(variable);
                    referencedVar.setUsedInConstraintsOfOtherVariables(referenceToVars);
                }
                variable.setVariablesUsedInConstraints(referencedVars);
            }
        }
    }
    
    @Override
    protected @NonNull String getName() {
        return "PsFmExtractor";
    }
}
