// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.reids.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MonthlyReportFilter {
    private ArrayList<String> sims;
    private ArrayList<String> allList;

    public MonthlyReportFilter( File trunk ) {
        ArrayList<String> categories = getPredefinedCategories();

        sims = new ArrayList<String>();
        File[] simRoots = new File[]{new File( trunk, "simulations-java\\simulations" ), new File( trunk, "simulations-flash\\simulations" ), new File( trunk, "simulations-flex\\simulations" )};
        for ( File simRoot : simRoots ) {
            for ( File dir : simRoot.listFiles() ) {
                sims.add( dir.getName() );
            }
        }
        allList = new ArrayList<String>();
        allList.addAll( categories );
        allList.addAll( sims );
        allList.add( "molecules-and-light" );
        allList.add( "isotopes-and-atomic-mass" );
        allList.add( "balloons-and-static-electricity" );
        allList.add( "states-of-matter-basics" );
        allList.add( "sponsorship" );
        allList.add( "solutions" );
        allList.add( "licensing" );
        allList.add( "ti" );
        allList.add( "Sim data collection/processing" );

        allList.add( "concentration" );
        allList.add( "forces-and-motion-basics" );
        allList.add( "example-sim" );
        allList.add( "energy-skate-park-basics" );
        allList.add( "balance-and-torque" );
        allList.add( "forces-and-motion" );
        allList.add( "fractions-intro" );
        allList.add( "ipad" );

        allList.add( "acid-base-solutions-study" );
        allList.add( "energy-skate-park-basics-study" );
        allList.add( "reflection-and-refaction" );
        allList.add( "gravity-force-lab" );

        //As of 3/5/2012, we are supposed to put all sim research study support in this category
        allList.add( "sim-data-analysis-research-support" );

        allList.add( "leave:sick:family" );
        allList.add( "leave:sick:personal" );
        allList.add( "leave:sick" );
        allList.add( "leave:vacation" );
    }

    public ArrayList<String> getAllCategories() {
        return removeDuplicates( allList );//Have to remove duplicates or there can be overcounting
    }

    private ArrayList<String> removeDuplicates( ArrayList<String> allList ) {
        ArrayList<String> x = new ArrayList<String>();
        for ( String s : allList ) {
            if ( !x.contains( s ) ) { x.add( s ); }
        }
        return x;
    }

    public static ArrayList<String> getPredefinedCategories() {
        String categoryString =
                "Administrative/Documentation\n" +            //includes meetings
                "Build Process\n" +
                "Build Process (Java)\n" +
                "Common Code\n" +
                "Common Code (HTML5)\n" +
                "Conferences, Workshops and Booths\n" +
                "Customer Support\n" +
                "Environment/Maintenance/Tools\n" +
                "Fort\n" +
                "HTML5\n" +
                "Installer\n" +
                "Interviewing\n" +
                "KSU Translation Credits\n" +
                "Licensing\n" +
                "Miscellaneous\n" +
                "New Sim Investigations\n" +
                "OVSD\n" +
                "Research, Surveys, Interviews & Observations\n" +
                "Scenery\n" +
                "Kite\n" +
                "Scenery-phet\n" +
                "Joist\n" +
                "Sim-sharing\n" +
                "Sun\n" +
                "Translations\n" +
                "Unfuddle\n" +
                "Website";

        ArrayList<String> categories = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer( categoryString, "\n" );
        while ( st.hasMoreElements() ) {
            categories.add( st.nextToken() );
        }
        return categories;
    }

    public TimesheetModel filter( TimesheetModel selection ) {
        ArrayList<Entry> mapped = new ArrayList<Entry>();
        for ( int i = 0; i < selection.getEntryCount(); i++ ) {
            mapped.add( map( selection.getEntry( i ) ) );
        }
        TimesheetModel timesheetModel = new TimesheetModel();
        for ( Entry entry : mapped ) {
            timesheetModel.addEntry( entry );
        }
        return timesheetModel;
    }

    private Entry map( Entry entry ) {
        return new Entry( entry.getStartSeconds(), entry.getEndSeconds(), mapCategory( entry.getCategory() ), entry.getNotes(), entry.isReport(), entry.isRunning() );
    }

    private String mapCategory( String category ) {
        //if it appears in the list and only differs by casing, whitespace or hyphenation, then use it
        for ( String elm : allList ) {
            if ( matchesCategory( category, elm ) ) { return elm; }
        }

        //didn't match
        if ( category.equalsIgnoreCase( "administrative" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equalsIgnoreCase( "documentation" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "cck" ) ) { return mapCategory( "circuit-construction-kit" ); }
        if ( category.equals( "timesheet" ) ) { return mapCategory( "administrative" ); }
        if ( category.equals( "mailing lists" ) ) { return mapCategory( "administrative" ); }
        if ( category.equals( "mailing-lists" ) ) { return mapCategory( "administrative" ); }
        if ( category.equals( "phet-meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "phet meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "misc" ) ) { return mapCategory( "miscellaneous" ); }
        if ( category.equals( "record-and-playback" ) ) { return mapCategory( "common code" ); }
        if ( category.equals( "unfuddle-notifier" ) ) { return mapCategory( "unfuddle" ); }
        if ( category.equals( "botany" ) ) { return mapCategory( "misc" ); }
        if ( category.equals( "phetcommon" ) ) { return mapCategory( "common code" ); }
        if ( category.equals( "maintenance" ) ) { return mapCategory( "misc" ); }
        if ( category.equals( "ide" ) ) { return mapCategory( "maintenance" ); }
        if ( category.equals( "logging" ) ) { return mapCategory( "phetcommon" ); }
        if ( category.equals( "translation-utility" ) ) { return mapCategory( "Translations" ); }
        if ( category.equals( "android" ) ) { return mapCategory( "Miscellaneous" ); }
        if ( category.equals( "wicket-deploy" ) ) { return mapCategory( "website" ); }
        if ( category.equals( "wicket-site" ) ) { return mapCategory( "website" ); }
        if ( category.equals( "support" ) ) { return mapCategory( "phet help" ); }
        if ( category.equals( "git" ) ) { return mapCategory( "environment" ); }
        if ( category.equals( "sbt" ) ) { return mapCategory( "build process" ); }
        if ( category.equals( "newsletter" ) ) { return mapCategory( "website" ); }
        if ( category.equals( "conference" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "flashcommon" ) ) { return mapCategory( "Flash_Flash Common" ); }
        if ( category.equals( "flash-common" ) ) { return mapCategory( "Flash_Flash Common" ); }
        if ( category.equals( "mobile" ) ) { return mapCategory( "Miscellaneous" ); }
        if ( category.equals( "build-tools" ) ) { return mapCategory( "Build Process" ); }
        if ( category.equals( "meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "buoyancy" ) ) { return mapCategory( "density-and-buoyancy" ); }
        if ( category.equals( "simsharing" ) ) { return mapCategory( "Sim-sharing" ); }
        if ( category.equals( "blog" ) ) { return mapCategory( "Website" ); }
        if ( category.equals( "sun-and-planet" ) ) { return mapCategory( "gravity-and-orbits" ); }
        if ( category.equals( "design-patterns" ) ) { return mapCategory( "Common Code" ); }
        if ( category.equals( "email" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "meetings" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "ksu-translation-credits" ) ) { return mapCategory( "KSU Translation Credits" ); }
        if ( category.equals( "piccolo" ) ) { return mapCategory( "piccolo-phet" ); }
        if ( category.equals( "piccolo-phet" ) ) { return mapCategory( "Common Code" ); }
        if ( category.equals( "physics-meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "isotopes" ) ) { return mapCategory( "isotopes-and-atomic-mass" ); }
        if ( category.equals( "phet-help" ) ) { return mapCategory( "Customer Support" ); }
        if ( category.equals( "phethelp" ) ) { return mapCategory( "Customer Support" ); }
        if ( category.equals( "ksu-credits" ) ) { return mapCategory( "KSU Translation Credits" ); }
        if ( category.equals( "environment" ) ) { return mapCategory( "Environment/Maintenance/Tools" ); }
        if ( category.equals( "dna" ) ) { return mapCategory( "New Sim Investigations" ); }
        if ( category.equals( "interview" ) ) { return mapCategory( "Interviewing" ); }
        if ( category.equals( "ksu" ) ) { return mapCategory( "KSU Translation Credits" ); }
        if ( category.equals( "scala" ) ) { return mapCategory( "Miscellaneous" ); }
        if ( category.equals( "spongelab" ) ) { return mapCategory( "Customer Support" ); }
        if ( category.equals( "javafx" ) ) { return mapCategory( "Miscellaneous" ); }
        if ( category.equals( "developer-meeting" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "sponsor" ) ) { return mapCategory( "sponsorship" ); }
        if ( category.equals( "tablet investigation" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "tablet-investigation" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "sim-event-data-collection" ) ) { return mapCategory( "Sim data collection/processing" ); }
        if ( category.equals( "sim-event-collection" ) ) { return mapCategory( "Sim data collection/processing" ); }
        if ( category.equals( "server" ) ) { return mapCategory( "misc" ); }
        if ( category.equals( "gas-properties" ) ) { return mapCategory( "ideal-gas" ); }
        if ( category.equals( "radiating-charge" ) ) { return mapCategory( "radiating-charges" ); }
        if ( category.equals( "ipad-mass-springs-prototype" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "energy-skate-park-html5" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "per-meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "concentration-caat" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "energy-skate-park-easeljs" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "energy-skate-park-basics-kineticjs" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "concentration-html5" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "sim-event-data-collection-analysis" ) ) { return mapCategory( "Sim data collection/processing" ); }
        if ( category.equals( "energy-skate-park-fabricjs" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "energy-skate-park-basics-processing" ) ) { return mapCategory( "ipad" ); }
        if ( category.equals( "accessibility" ) ) { return mapCategory( "scenery" ); }
        if ( category.equals( "i18n" ) ) { return mapCategory( "html5" ); }
        if ( category.equals( "riaw" ) ) { return mapCategory( "resistance-in-a-wire" ); }
        if ( category.equals( "base" ) ) { return mapCategory( "balloons-and-static-electricity" ); }
        if ( category.equals( "famb" ) ) { return mapCategory( "forces-and-motion-basics" ); }
        if ( category.equals( "jt" ) ) { return mapCategory( "john-travoltage" ); }
        if ( category.equals( "svg" ) ) { return mapCategory( "scenery" ); }
        if ( category.equals( "fpaf" ) ) { return mapCategory( "fluid-pressure-and-flow" ); }
        if ( category.equals( "john-travoltage" ) ) { return mapCategory( "travoltage" ); }
        if ( category.equals( "mll" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "logo" ) ) { return mapCategory( "misc" ); }
        if ( category.equals( "grunt" ) ) { return mapCategory( "Build Process" ); }
        if ( category.equals( "chipper" ) ) { return mapCategory( "Build Process" ); }
        if ( category.equals( "admin" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "woas" ) ) { return mapCategory( "Wave on a String" ); }
        if ( category.equals( "gfl" ) ) { return mapCategory( "Gravity Force Lab" ); }
        if ( category.equals( "baa" ) ) { return mapCategory( "build-an-atom" ); }
        if ( category.equals( "dot" ) ) { return mapCategory( "Common Code (HTML5)" ); }
        if ( category.equals( "html" ) ) { return mapCategory( "Common Code (HTML5)" ); }
        if ( category.equals( "java-u25-redeploys" ) ) { return mapCategory( "Build Process (Java)" ); }

        //[unknown: espb, unknown: vibe, unknown: concentration, unknown: smorgasbord, unknown: mobius]
        if ( category.equals( "espb" ) ) { return mapCategory( "energy-skate-park-basics" ); }
        if ( category.equals( "vibe" ) ) { return mapCategory( "Common Code (HTML5)" ); }
        if ( category.equals( "smorgasbord" ) ) { return mapCategory( "Common Code (HTML5)" ); }
        if ( category.equals( "mobius" ) ) { return mapCategory( "Common Code (HTML5)" ); }

        if ( category.equals( category.toLowerCase() ) ) {
            System.out.println( "No match found for the category: " + category );
            return "unknown: " + category;
        }
        else {
            return mapCategory( category.toLowerCase() );
        }
    }

    private boolean matchesCategory( String documented, String standardized ) {
        String a = documented.replaceAll( " ", "" ).replaceAll( "-", "" ).trim();
        String b = standardized.replaceAll( " ", "" ).replaceAll( "-", "" ).trim();
        return a.equalsIgnoreCase( b );
    }
}
