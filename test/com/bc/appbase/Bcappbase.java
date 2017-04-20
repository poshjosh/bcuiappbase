package com.bc.appbase;

import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.ComponentModelImpl;
import com.bc.appbase.ui.DateFromUIBuilderImpl;
import com.bc.appbase.ui.DateUIUpdaterImpl;
import com.bc.appbase.ui.builder.UIBuilder;
import com.bc.appcore.TypeProvider;
import com.bc.appbase.ui.builder.impl.EntryUIProviderImpl;
import com.bc.appbase.ui.builder.impl.UIBuilderFromMap;
import com.bc.appcore.util.Selection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import com.bc.appcore.jpa.SelectionContext;
import java.awt.Container;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2017 12:22:15 PM
 */
public class Bcappbase {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        final Map props = new HashMap();
        props.put("Name", "Chinomso Ikwuagwu");
        props.put("Married", "true");
        props.put("Alive", Boolean.TRUE);
        props.put("Confession", "I, Chinomso Ikwuagwu confess that Jesus Christ is my Lord and Saviour. Jesus died to pay the price for sin for me and all mankind and I receive the gift of life from Him. Thank you God of Abraham, Isaac and Jacob!");
        props.put("Date", new Date());
        final Map wife = new HashMap();
        wife.put("Name", "Helen Ikwuagwu");
        wife.put("Married", "true");
        wife.put("Alive", Boolean.TRUE);
        wife.put("Confession", "I, Helen Ikwuagwu confess that Jesus Christ is my Lord and Saviour. Jesus died to pay the price for sin for me and all mankind and I receive the gift of life from Him. Thank you God of Abraham, Isaac and Jacob!");
        wife.put("Date", new Date());
//        wife.put("Husband", props);
        props.put("Wife", wife);
        
        final TypeProvider typeProvider = new TypeProvider(){
                    @Override
                    public Class getType(String name, Object value, Class outputIfNone) {
                        switch(name) {
                            case "Married": return Boolean.class;
                            default: return value == null ? String.class : value.getClass();
                        }
                    }
                };        
        
        final ComponentModel cm = new ComponentModelImpl(null, new SelectionContext(){
                    @Override
                    public Selection[] getSelectionValues(Class entityType) {
                        return new Selection[0];
                    }
                    @Override
                    public String getSelectionColumn(Class entityType, String outputIfNone) {
                        return outputIfNone;
                    }

                }, new DateFromUIBuilderImpl(), new DateUIUpdaterImpl());
        
        final UIBuilder<Map, Container> uiBuilder = new UIBuilderFromMap();
        
        final Container ui = uiBuilder
                .source(props)
                .typeProvider(typeProvider)
                .entryUIProvider(new EntryUIProviderImpl(cm, -1))
                .build();
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(ui);
        frame.pack();
        frame.setVisible(true);
    }

}
