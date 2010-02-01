package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class ContributionSubject implements Serializable {

    private int id;
    private String subject;
    private Contribution contribution;

    private static Logger logger = Logger.getLogger( ContributionSubject.class.getName() );


    public ContributionSubject() {

    }

    public static List<Subject> getCurrentLevels() {
        return Arrays.asList( Subject.ASTRONOMY, Subject.BIOLOGY, Subject.CHEMISTRY, Subject.EARTH_SCIENCE, Subject.MATHEMATICS, Subject.PHYSICS, Subject.OTHER );
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject( String subject ) {
        this.subject = subject;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }
}