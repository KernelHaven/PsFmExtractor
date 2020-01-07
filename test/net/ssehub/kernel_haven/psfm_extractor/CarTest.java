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

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Test the PsFmExtractor class for the car feature model.
 *
 * @author Calvin Hansch
 */
public class CarTest {
    private File xfmCar; 
    private List<String> attributeNames;
    private List<String> attributeList;    

    /**
     * Setup Test.
     * @throws FileNotFoundException 
     */
    @Before
    public void initialize() throws FileNotFoundException {        
        this.xfmCar = new File("testdata/xfm/Car.xfm");
        this.attributeNames = new ArrayList<String>();
        this.attributeList = new ArrayList<String>();
        
        Scanner scanner = new Scanner(new File("testdata/xfm/carNameList.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            this.attributeList.add(line);
        }
        scanner.close();
    }
    
    /**
     * Check if all attribute names listed in testdata/xfm/carNameList.txt 
     * are found by the extractor.
     */
    @Test
    public void testCarNames() {
        XMLParser xpCar = new XMLParser(this.xfmCar);
        NodeList nlCar = null;
        
        try {
            nlCar = xpCar.getCmElement();
            
            // loop over every node and check whether one contains the cm:name "or"
            for (int i = 0; i < nlCar.getLength(); i++) {
                Node n = nlCar.item(i);
                Element e = (Element) n;
                this.attributeNames.add(e.getAttribute("cm:name"));
                
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
        
        /*
         * remove all found names from list of known names, if the result is empty
         * we found all
         */
        attributeList.removeAll(attributeNames);
        
        assert (attributeList.isEmpty());
    }

}
