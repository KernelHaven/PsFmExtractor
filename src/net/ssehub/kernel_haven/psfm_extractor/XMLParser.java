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

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.ssehub.kernel_haven.util.Logger;

/**
 * XML Parser that reads an xfm file and creates a DOM from it.
 *
 * @author Calvin Hansch
 */
class XMLParser {
    private static final Logger LOGGER = Logger.get();
    private File xfmFile;
    
    /**
     * Create a new XMLParser.
     * @param xfmFile File to be parsed
     */
    public XMLParser(File xfmFile) {
        this.xfmFile = xfmFile;
    }
    
    /**
     * Returns cm:element Nodes from the xfm File.
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @return NodeList containing cm:element  
     */
    public NodeList getCmElement() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf  =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(xfmFile);
        doc.getDocumentElement();
        NodeList nodeList = doc.getElementsByTagName("cm:element");
        
        return nodeList;
    }
    
    /**
     * Get the cm:name from the element node.
     * @param node Node of which the name shall be returned.
     * @return List with name of cm:element. Returns null when no name was found.
     */
    public String getName(Node node) {
        Element e = (Element) node;
        String name = null;
        
        if (e.hasAttribute("cm:name")) {
            name = e.getAttribute("cm:name");
        } else {
            LOGGER.logError2("Node ", e, "has no attribute cm:name!");
        }
        return name;
    }
    
    /**
     * Get the ps:type from a cm:element.
     * @param node Must be of type cm:element
     * @return Returns the ps:type for given element.
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public String getType(Node node) throws ParserConfigurationException, SAXException, IOException {
        String parentID = this.getParent(node);
        
        if (parentID == null ) {
            return "Root node has no parent!";
        }
        
        Element parent = null;
        
        NodeList nodes = this.getCmElement();
        
        // find parent nodes since there the type of the children is stored
        for (int i = 0; i < nodes.getLength(); i++) {
            Element currElement = (Element) nodes.item(i);
            String currID = currElement.getAttribute("cm:id");
            if (currID.equals(parentID)) {
                parent = currElement;
                break; // we found the parent, no need to keep for running 
            }
        }
        
        NodeList children = null;
        NodeList relations = parent.getElementsByTagName("cm:relations");
        // get the ps:children
        for (int i = 0; i < relations.getLength(); i++) {
            Element currElement = (Element) relations.item(i);
            String currElementClass = currElement.getAttribute("cm:class");
            if (currElementClass.equals("ps:children")) {
                children = currElement.getElementsByTagName("cm:relation");
                break; //we found the children, no need to keep running
            }
        }
        
        String cmType = null;
        
        for (int i = 0; i < children.getLength(); i++) {
            Node currChild = children.item(i);
            NodeList targetList = currChild.getChildNodes();
            /*
             * check if cm:target contains our inital node, only then we know if
             * the current type is correct
             */
            for (int j = 0; j < targetList.getLength(); j++) {
                Node currTarget = targetList.item(j);
                // get the ID which is in cm:target and strip "./" so it will
                // match the node id
                String targetContent = currTarget.getTextContent().substring(2);
                // get the ID of our node which was passed as argument
                String initNodeID = ((Element) node).getAttribute("cm:id");
                if (targetContent.equals(initNodeID)) {
                    cmType = ((Element) currChild).getAttribute("cm:type");
                    break; // we found our node in the targets. No need to continue searching
                }
            }
        }
        
        return (cmType);
    }
    
    /**
     * Get the parent features of given feature.
     * @param node The node of which the parent features should be returned
     * @return The name of the parent feature
     */
    public String getParent(Node node) {   
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
}
