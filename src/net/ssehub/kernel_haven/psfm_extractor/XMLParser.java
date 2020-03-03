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

/**
 * XML Parser that reads an xfm file and creates a DOM from it.
 *
 * @author Calvin Hansch
 */
class XMLParser {
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
            System.err.print("Node has no attribute cm:name!");
        }
        return name;
    }
    
    /**
     * Get the ps:type from a cm:element.
     * @param node Must be of type cm:element
     * @return Returns the ps:type for given element.
     */
    public String getType(Node node) {
        //get the child nodes from cm:element, should be cm:relations
        NodeList cNodes = node.getChildNodes();
        //get the child nodes from cm:relations, should be cm:relation
        Node relation = cNodes.item(1).getChildNodes().item(1);
        
        //retrieve attribute cm:type
        Element e = (Element) relation;
        return (e.getAttribute("cm:type"));
    }
    
    /**
     * Get the parent features of given feature.
     * @param node The node of which the parent features should be returned
     * @return The name of the parent feature
     */
    public String getParent(Node node) {
        String parent = null;        
        Element element = null;
        
        if (node.getNodeType() == 1) {
            element = (Element) node;
        } else {
            System.err.println("Not a valid node!");
        }
        
        NodeList relation = element.getElementsByTagName("cm:relation");
        
        // for every element of type cm:relation find those children that have type ps:parent
        for (int i = 0; i < relation.getLength(); i++) {
            Element currElement = (Element) relation.item(i);
            if (currElement.getAttribute("cm:type").equals("ps:parent")) {
                currElement.getElementsByTagName("cm:target");
            }
        }
        
        return parent;
    }
}
