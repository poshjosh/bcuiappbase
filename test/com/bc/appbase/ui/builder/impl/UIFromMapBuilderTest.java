/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bc.appbase.ui.builder.impl;

import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.ComponentModelImpl;
import com.bc.appbase.ui.DateFromUIBuilderImpl;
import com.bc.appbase.ui.DateUIUpdaterImpl;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.typeprovider.MemberValueTypeProvider;
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.appcore.typeprovider.TypeProviderImpl;
import com.bc.appcore.util.SelectionValues;
import java.awt.Container;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh
 */
public class UIFromMapBuilderTest {
    
    public UIFromMapBuilderTest() {}
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    /**
     * Test of build method, of class UIBuilderFromEntityMap.
     */
    @Test
    public void testBuild() throws Exception {
        System.out.println("build");
        final Map source = new HashMap();
        source.put("Name", "Chinomso Ikwuagwu");
        source.put("Married", "true");
        source.put("Alive", Boolean.TRUE);
        source.put("Confession", "I, Chinomso Ikwuagwu confess that Jesus Christ is my Lord and Saviour. Jesus died to pay the price for sin for me and all mankind and I receive the gift of life from Him. Thank you God of Abraham, Isaac and Jacob!");
        source.put("Date", new Date());
        final Map wife = new HashMap();
        wife.put("Name", "Helen Ikwuagwu");
        wife.put("Married", "true");
        wife.put("Alive", Boolean.TRUE);
        wife.put("Confession", "I, Helen Ikwuagwu confess that Jesus Christ is my Lord and Saviour. Jesus died to pay the price for sin for me and all mankind and I receive the gift of life from Him. Thank you God of Abraham, Isaac and Jacob!");
        wife.put("Date", new Date());
//        wife.put("Husband", props);
        source.put("Wife", wife);
        
        final MemberValueTypeProvider memberTypeProvider = new MemberValueTypeProvider(){
            @Override
            public Class getType(Class parentType, String name, Object value, Class outputIfNone) {
                switch(name) {
                    case "Married": return Boolean.class;
                    default: return super.getType(parentType, name, value, outputIfNone);
                }
            }
        };
        
        final TypeProvider typeProvider = new TypeProviderImpl(Collections.EMPTY_SET, memberTypeProvider);
        
        final ComponentModel cm = new ComponentModelImpl(
                SelectionValues.from(Collections.emptySet()), new DateFromUIBuilderImpl(), new DateUIUpdaterImpl());
        
        final UIBuilderFromEntityMap uiBuilder = new UIBuilderFromEntityMap();
        
        final Container ui = uiBuilder
                .selectionContext(SelectionContext.NO_OP)
                .sourceData(source)
                .typeProvider(typeProvider)
                .entryUIProvider(new FormEntryComponentModelImpl(cm, -1, ThirdComponentProvider.PROVIDE_NONE))
                .build();
        
        final int selection = JOptionPane.showConfirmDialog(
                null, ui, "The Title", JOptionPane.OK_CANCEL_OPTION);
        
System.out.println(selection);        
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(ui);
//        frame.pack();
//        frame.setVisible(true);
    }
    
}
