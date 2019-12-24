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
     * Parse the XML File using DOMParser.
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws  
     */
    public void parse() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf  =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf .newDocumentBuilder();
        Document doc = builder.parse(xfmFile);
    }
}
