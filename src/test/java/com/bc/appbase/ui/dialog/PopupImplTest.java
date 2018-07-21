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
package com.bc.appbase.ui.dialog;

import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import java.awt.Component;
import java.util.Collections;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josh
 */
public class PopupImplTest {
    
    private final Component parentComponent = null;
    
    private final SearchResultsNotFoundException hasUserMessage;
    
    public PopupImplTest() {
        this.hasUserMessage =
            new SearchResultsNotFoundException(
                    "TEST MESSAGE FOR "+SearchResultsNotFoundException.class.getName());
        this.hasUserMessage.fillInStackTrace();
    }
    
    @BeforeClass
    public static void setUpClass() { }
    @AfterClass
    public static void tearDownClass() { }
    @Before
    public void setUp() { }
    @After
    public void tearDown() { }

    /**
     * Test of showErrorMessage method, of class PopupImpl.
     */
    @Test
    public void testShowErrorMessage() {
        System.out.println("showErrorMessage");
        final Throwable t = this.getThrowable();
        final Object description = "TEST ERROR MESSAGE DESCRIPTION";
        final PopupImpl instance = this.getInstance();
        instance.showErrorMessage(t, description);
    }

    /**
     * Test of showSuccessMessage method, of class PopupImpl.
     */
    @Test
    public void testShowSuccessMessage() {
        System.out.println("showSuccessMessage");
        final Object message = "TEST SUCCESS MESSAGE";
        final PopupImpl instance = this.getInstance();
        instance.showSuccessMessage(message);
    }

    /**
     * Test of getUserMessage method, of class PopupImpl.
     */
    @Test
    public void testGetUserMessage() {
        System.out.println("getUserMessage");
        final Throwable t = this.getThrowable();
        final String outputIfNone = null;
        final boolean firstNotLast = false;
        final PopupImpl instance = this.getInstance();
        final String expResult = this.hasUserMessage.getUserMessage();
        System.out.println("Expected userMessage: "+expResult);
        final String result = instance.getUserMessage(t, outputIfNone, firstNotLast);
        System.out.println("userMessage: "+result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParentComponent method, of class PopupImpl.
     */
    @Test
    public void testGetParentComponent() {
        System.out.println("getParentComponent");
        final PopupImpl instance = this.getInstance();
        final Component expResult = parentComponent;
        final Component result = instance.getParentComponent();
        assertEquals(expResult, result);
    }
    
    public PopupImpl getInstance() {
        final Map<Object, PopupImpl.OptionAction> successOptions = Collections.singletonMap("OK", null);
        final Map<Object, PopupImpl.OptionAction> errorOptions = new SimpleErrorOptions();
        final String subject = "Application Error Report from: " + this.getClass().getName();
        errorOptions.put("Send Report", new SendReportAction("smtp.googlemail.com", 465, "posh.bc@gmail.com", "uuid-391120", subject));
//        errorOptions.put("Send Report", new SendReportAction("smtp.mail.yahoo.com", 465, "chinomsoikwuagwu@yahoo.com", "3realtins", subject));
        return new PopupImpl(parentComponent, successOptions, errorOptions);
    }
    
    private Throwable getThrowable() {
        final Throwable t = new Throwable("TEST EXCEPTION", hasUserMessage);
        t.fillInStackTrace();
        return t;
    }
}
