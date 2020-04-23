package com.airhacks;


import javax.naming.*;
import java.io.PrintWriter;


public class MainTest {

    public static void main(String[] args) throws NamingException {

//        Hashtable env = new Hashtable();
//        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");

        Context ctx = new InitialContext();

        final NamingEnumeration<Binding> bindingNamingEnumeration = ctx.listBindings("jms");

        while ( bindingNamingEnumeration.hasMore()) {

            System.out.println(bindingNamingEnumeration.next());
        }
        System.out.printf("Done");
    }

    public void printJndiContextAsHtmlList(PrintWriter writer, Context ctx, String name )
    {
        writer.println( "<ul>" );
        try {
            NamingEnumeration<Binding> en = ctx.listBindings( "" );
            while( en != null && en.hasMoreElements() ) {
                Binding binding = en.next();
                String name2 = name + (( name.length() > 0 ) ? "/" : "") + binding.getName();
                writer.println( "<li><u>" + name2 + "</u>: " + binding.getClassName() + "</li>" );
                if( binding.getObject() instanceof Context) {
                    printJndiContextAsHtmlList( writer, (Context) binding.getObject(), name2 );
                }
            }
        } catch( NamingException ex ) {
            // Normalerweise zu ignorieren
        }
        writer.println( "</ul>" );
    }
}