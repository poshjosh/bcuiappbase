package com.bc.appbase;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2017 12:22:15 PM
 */
class LocalClass{
    
}
public class Bcappbase {

    private static final Logger LOGGER = Logger.getLogger(Bcappbase.class.getName());
    
    public class InnerClass extends JTable{
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {  
        
        final Supplier<String> msgSup = () -> {
System.out.println("-------------- ABC ");            
            final String msg = "1" + "2" + "3";
System.out.println("-------------- DEF ");            
            return msg;
        };
        
        LOGGER.log(Level.FINE, msgSup);
        
        if(true) {
            return;
        }
        
        final JTable table = new JTable() {
            
        };
        
        print(table.getClass());
        
        print(Bcappbase.class);
        
        print(LocalClass.class);
        
        print(InnerClass.class);
        
        final Path path = Paths.get(System.getProperty("user.home"), "folder", "file.extension");
System.out.println(path.toString()) ;       
System.out.println(path.toUri());
System.out.println(path.toUri().toURL());
    }
    
    public static void print(Class cls) {
        System.out.println("Class name: "+cls.getName()+", super: "+cls.getSuperclass().getName()+
                ", enclosing: "+cls.getEnclosingClass()+ ", anon: "+cls.isAnonymousClass()+
                ", local: "+cls.isLocalClass()+", member: "+cls.isMemberClass()+
                ", synthetic: "+cls.isSynthetic()+", primitive: "+cls.isPrimitive());
    }

}
