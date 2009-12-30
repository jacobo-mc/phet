package edu.colorado.phet.website.templates;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;

public class StaticPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( StaticPage.class.getName() );

    public StaticPage( PageParameters parameters ) {
        super( parameters );

        try {
            String path = parameters.getString( "path" );

            Class panelClass = panelMap.get( path );

            Constructor ctor = panelClass.getConstructor( String.class, PageContext.class );
            Method meth = panelClass.getMethod( "getKey" );
            String key = (String) meth.invoke( null );

            PhetPanel panel = (PhetPanel) ctor.newInstance( "panel", getPageContext() );

            addTitle( new ResourceModel( key + ".title" ) );
            initializeLocation( getNavMenu().getLocationByKey( key ) );
            add( panel );
        }
        catch( RuntimeException e ) {
            e.printStackTrace();
        }
        catch( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }

    }

    public static Map<String, Class> panelMap = new HashMap<String, Class>();

    private static boolean addedToMapper = false;

    public static void addPanel( Class panelClass ) {
        if ( addedToMapper ) {
            logger.error( "Attempt to add static page after mappings have been completed" );
            throw new RuntimeException( "Attempt to add static page after mappings have been completed" );
        }
        try {
            Method meth = panelClass.getMethod( "getUrl" );
            String url = (String) meth.invoke( null );
            panelMap.put( url, panelClass );
        }
        catch( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        for ( String url : panelMap.keySet() ) {
            mapper.addMap( "^" + url + "$", StaticPage.class );
        }
        addedToMapper = true;
    }
}