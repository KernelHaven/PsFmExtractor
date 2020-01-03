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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Tests the PsFmExtractor for the atomic feature models.
 *
 * @author Calvin Hansch
 */
public class AtomicTests {
    private File xfmOr;
    private File xfmAlternative;
    private File xfmMandatory;
    private File xfmOptional;
    
    /**
     * Initialize test.
     */
    @Before
    public void init() {
        this.xfmOr = new File("testdata/xfm/or.xfm");
        this.xfmAlternative = new File("testdata/xfm/alternative.xfm");
        this.xfmMandatory = new File("testdata/xfm/mandatory.xfm");
        this.xfmOptional = new File("testdata/xfm/optional.xfm");
    }
    
    /**
     * Test if the or-xfm contains an element with cm:name "or" to ensure that
     * the parser correctly parses the xfm file.
     */
    @Test
    public void testOrName() {
        XMLParser xpOr = new XMLParser(this.xfmOr);
        NodeList nlOr = null;
        Boolean containsOr = false;
        
        try {
            nlOr = xpOr.getCmElement();
            
            // loop over every node and check whether one contains the cm:name "or"
            for (int i = 0; i < nlOr.getLength(); i++) {
                Node n = nlOr.item(i);
                Element e = (Element) n;
                if (e.getAttribute("cm:name").contentEquals("or")) {
                    containsOr = true;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(containsOr);
    }
    
    /**
     * Test if the or-xfm does not contain an element with cm:name "alternative" to ensure that
     * the positive test does not always yield true.
     */
    @Test
    public void testOrNameNegative() {
        XMLParser xpOr = new XMLParser(this.xfmOr);
        NodeList nlOr = null;
        Boolean containsOr = false;
        
        try {
            nlOr = xpOr.getCmElement();
            
            /** 
            * loop over every node and check whether one contains the cm:name "alternative", 
            * this is expected to be NOT true.
            **/
            for (int i = 0; i < nlOr.getLength(); i++) {
                Node n = nlOr.item(i);
                Element e = (Element) n;
                if (e.getAttribute("cm:name").contentEquals("alternative")) {
                    containsOr = true;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        
        assertFalse(containsOr);
    }
    
    /**
     * Test the feature model with the "alternative" feature.
     */
    @Test
    public void testAlternativeName() {
        XMLParser xpAlt = new XMLParser(this.xfmAlternative);
        NodeList nlAlt = null;
        Boolean containsAlt = false;
        
        try {
            nlAlt = xpAlt.getCmElement();
            
            // loop over every node and check whether one contains the cm:name "alternative"
            for (int i = 0; i < nlAlt.getLength(); i++) {
                Node n = nlAlt.item(i);
                Element e = (Element) n;                
                if (e.getAttribute("cm:name").contentEquals("alternative")) {
                    containsAlt = true;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(containsAlt);
    }
    
    /**
     * Test the feature model with the "mandatory" feature.
     */
    @Test
    public void testMandatoryName() {
        XMLParser xpMan = new XMLParser(this.xfmMandatory);
        NodeList nlMan = null;
        Boolean containsMan = false;
        
        try {
            nlMan = xpMan.getCmElement();
            
            // loop over every node and check whether one contains the cm:name "alternative"
            for (int i = 0; i < nlMan.getLength(); i++) {
                Node n = nlMan.item(i);
                Element e = (Element) n;                
                if (e.getAttribute("cm:name").contentEquals("mandatory")) {
                    containsMan = true;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(containsMan);
    }
    
    /**
     * Test the feature model with the "optional" feature.
     */
    @Test
    public void testOptionalName() {
        XMLParser xpOpt = new XMLParser(this.xfmOptional);
        NodeList nlOpt = null;
        Boolean containsOpt = false;
        
        try {
            nlOpt = xpOpt.getCmElement();
            
            // loop over every node and check whether one contains the cm:name "alternative"
            for (int i = 0; i < nlOpt.getLength(); i++) {
                Node n = nlOpt.item(i);
                Element e = (Element) n;                
                if (e.getAttribute("cm:name").contentEquals("optional")) {
                    containsOpt = true;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(containsOpt);
    }

}
