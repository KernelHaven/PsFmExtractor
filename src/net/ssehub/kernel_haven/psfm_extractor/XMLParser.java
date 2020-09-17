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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * XML Parser that reads an xfm file and creates a DOM from it.
 *
 * @author Calvin Hansch
 * @author El-Sharkawy
 */
class XMLParser {
    
    private static final String NAME_ATTRIBUTE = "cm:name";
    private static final String ID_ATTRIBUTE = "cm:id";
    private static final String CLASS_ATTRIBUTE = "cm:class";
    
    private static final String RELATIONS_TAG = "cm:relations";
    private static final String RELATION_TAG = "cm:relation";
    
    private static final Logger LOGGER = Logger.get();
    private Document doc;
    private Map<String, Element> nodes;
    private Map<Node, Integer> hierarchies;
    
    /**
     * Create a new XMLParser.
     * @param xfmFile File to be parsed
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any parse errors occur.
     * @throws ParserConfigurationException If a DocumentBuilder
     *      cannot be created which satisfies the configuration requested.
     */
    protected XMLParser(File xfmFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        doc = builder.parse(xfmFile);
        doc.getDocumentElement();
        nodes = new HashMap<>();
        hierarchies = new HashMap<>();
        
        parse();
    }
    
    /**
     * Parsed information of the XML file once so that important information can be reused.
     */
    private void parse() {
        NodeList nodeList = doc.getElementsByTagName("cm:element");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element) nodeList.item(i);
            nodes.put(node.getAttribute(ID_ATTRIBUTE), node);
        }
        
        for (Element node : nodes.values()) {
            hierarchies.put(node, computeNestingLevel(node));
        }
    }
    
    /**
     * Returns the nesting level of the given node.
     * @param node A feature of the feature model.
     * @return It hierarchy beginning at 0.
     */
    public int getNestingLevel(Node node) {
        return hierarchies.get(node);
    }
    
    /**
     * Returns <tt>cm:element</tt> Nodes from the xfm File.
     * @return The NodeList containing all features
     */
    protected NodeList getCmElement() {
        return doc.getElementsByTagName("cm:element");
    }
    
    /**
     * Get the cm:name from the element node.
     * @param node Node of which the name shall be returned.
     * @return List with name of cm:element. Returns null when no name was found.
     */
    protected String getName(Node node) {
        Element e = (Element) node;
        String name = null;
        
        if (e.hasAttribute(NAME_ATTRIBUTE)) {
            name = e.getAttribute(NAME_ATTRIBUTE);
        } else {
            LOGGER.logError2("Node ", e, "has no attribute cm:name!");
        }
        return name;
    }
    
    /**
     * Get the ps:type from a cm:element.
     * @param node Must be of type cm:element
     * @return Returns the ps:type for given element.
     */
    @NonNull
    protected String getType(Node node) {
        String parentID = this.getParent(node);
        
        if (parentID == null ) {
            // Root is always mandatory
            return "mandatory";
        }
        
        Element parent = null;
        NodeList nodes = this.getCmElement();
        
        // find parent nodes since there the type of the children is stored
        for (int i = 0; i < nodes.getLength(); i++) {
            Element currElement = (Element) nodes.item(i);
            String currID = currElement.getAttribute(ID_ATTRIBUTE);
            if (currID.equals(parentID)) {
                parent = currElement;
                break; // we found the parent, no need to keep for running 
            }
        }
        
        NodeList children = parent != null ? getReferencedElements(parent, "ps:children") : null;
        String cmType = null;

        // May only be null if parent is null, but this was already handled at begin of method
        if (null != children) {
            for (int i = 0; i < children.getLength(); i++) {
                Node currChild = children.item(i);
                NodeList targetList = currChild.getChildNodes();
                /*
                 * check if cm:target contains our initial node, only then we know if
                 * the current type is correct
                 */
                for (int j = 0; j < targetList.getLength(); j++) {
                    Node currTarget = targetList.item(j);
                    // get the ID which is in cm:target and strip "./" so it will
                    // match the node id
                    String targetContent = currTarget.getTextContent().substring(2);
                    // get the ID of our node which was passed as argument
                    String initNodeID = ((Element) node).getAttribute(ID_ATTRIBUTE);
                    if (targetContent.equals(initNodeID)) {
                        cmType = ((Element) currChild).getAttribute("cm:type");
                        // omit "ps:" from type to not conflict with MetricHaven config files
                        cmType = cmType.substring(3);
                        break; // we found our node in the targets. No need to continue searching
                    }
                }
            }
        }
        
        if (null == cmType) {
            cmType = "UNKNOWN";
            Logger.get().logError2("Could no determine feature type of ", node);
        }
        
        return cmType;
    }
    
    /**
     * Returns the {@link NodeList} of related elements of the specified relation class.
     * @param node A feature holding potentially references to other features.
     * @param classType The relation type, one of <tt>ps:dependencies</tt> or <tt>ps:children</tt>.
     *      Must not be <tt>null</tt>.
     * @return The related elements, maybe <tt>null</tt>.
     */
    private NodeList getReferencedElements(Node node, String classType) {
        NodeList relations = ((Element) node).getElementsByTagName(RELATIONS_TAG);
        NodeList children = null;
        
        for (int i = 0; i < relations.getLength() && null == children; i++) {
            Element currElement = (Element) relations.item(i);
            if (classType.equals(currElement.getAttribute(CLASS_ATTRIBUTE))) {
                children = currElement.getElementsByTagName(RELATION_TAG);
            }
        }
        
        return children;
    }
    
    /**
     * Returns all variables which are referenced by a <tt>requires</tt> constraint.
     * @param node A feature node.
     * @return The list of IDs referenced through requires constraint.
     */
    public List<String> getReferencedVariables(Node node) {
        List<String> referencedVariables = new ArrayList<>();
        NodeList children = getReferencedElements(node, "ps:dependencies");
        if (null != children) {
            for (int i = 0; i < children.getLength(); i++) {
                Element currChild = (Element) children.item(i);
                String relationType = currChild.getAttribute("cm:type");
                
                if (null != relationType && "ps:requires".equals(relationType)) {
                    NodeList targetList = currChild.getChildNodes();
                    for (int j = 0; j < targetList.getLength(); j++) {
                        Node target = targetList.item(j);
                        String id = target.getTextContent().trim();
                        if (!id.isEmpty()) {
                            // Relation is between IDs is separated by a slash: /
                            id = id.split("/")[1];
                            referencedVariables.add(id);
                        }
                    }
                }
            }
        }
        
        return referencedVariables;
    }
    
    /**
     * Get the parent features of given feature.
     * @param node The node of which the parent features should be returned
     * @return The ID of the parent feature or <tt>null</tt> if the node has no parent, i.e., is the root
     */
    String getParent(Node node) {   
        String parentID = null;
        Element element = null;
        
        if (node.getNodeType() == 1) {
            element = (Element) node;
        } else {
            LOGGER.logError2("Node ", node, "is not of type element!");
        }
        
        NodeList relation = element.getElementsByTagName("cm:relation");
        Node target = null;
        
        // for every element of type cm:relation find those children that have type ps:parent
        for (int i = 0; i < relation.getLength(); i++) {
            Element currElement = (Element) relation.item(i);
            
            if (currElement.getAttribute("cm:type").equals("ps:parent")) {
                // get the <cm:target> attribute which holds the parents id
                target = currElement.getChildNodes().item(1);
            }
        }

        if (target != null) {
            parentID = target.getTextContent();
            // Strip ./ from id String
            parentID = parentID.substring(2);
        }
        
        return parentID;
    }
    
    /**
     * Computes the nesting level / hierarchy of a given node.
     * @param node A feature of the feature model
     * @return Its nesting level beginning at 0.
     */
    private int computeNestingLevel(Node node) {
        int level = 0;
        String parentID = getParent(node);
        if (null != parentID) {
            level = 1 + computeNestingLevel(nodes.get(parentID));
        }
        
        return level;
    }
}
