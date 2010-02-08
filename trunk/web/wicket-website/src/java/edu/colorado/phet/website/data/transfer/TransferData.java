package edu.colorado.phet.website.data.transfer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionComment;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class TransferData {

    private static Logger logger = Logger.getLogger( TransferData.class.getName() );

    public static void transfer( Session session, final ServletContext servletContext ) {

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {

                final List<Object> newObs = new LinkedList<Object>();

                final Map<Integer, PhetUser> userIdMap = new HashMap<Integer, PhetUser>();
                final Map<Integer, Contribution> contributionIdMap = new HashMap<Integer, Contribution>();
                boolean sqlSuccess;

                // because our old data has many many holes!
                final PhetUser anonymous = new PhetUser();
                anonymous.setEmail( "anonymous@unknown.com" );
                anonymous.setPassword( "nothing will hash to this" );
                anonymous.setReceiveEmail( false );
                newObs.add( anonymous );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contributor", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        PhetUser user = new PhetUser();

                        userIdMap.put( result.getInt( "contributor_id" ), user );
                        newObs.add( user );
                        user.setEmail( result.getString( "contributor_email" ) );
                        user.setPassword( result.getString( "contributor_password" ) );
                        user.setTeamMember( result.getBoolean( "contributor_is_team_member" ) );
                        user.setName( result.getString( "contributor_name" ) );
                        user.setOrganization( result.getString( "contributor_organization" ) );
                        user.setAddress1( result.getString( "contributor_address" ) );
                        user.setAddress2( result.getString( "contributor_office" ) );
                        user.setCity( result.getString( "contributor_city" ) );
                        user.setState( result.getString( "contributor_state" ) );
                        user.setCountry( result.getString( "contributor_country" ) );
                        user.setZipcode( result.getString( "contributor_postal_code" ) );
                        user.setPhone1( result.getString( "contributor_primary_phone" ) );
                        user.setPhone2( result.getString( "contributor_secondary_phone" ) );
                        user.setFax( result.getString( "contributor_fax" ) );
                        user.setReceiveEmail( result.getBoolean( "contributor_receive_email" ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) {
                    return sqlSuccess;
                }

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        Contribution contribution = new Contribution();

                        contributionIdMap.put( result.getInt( "contribution_id" ), contribution );
                        newObs.add( contribution );
                        PhetUser user = userIdMap.get( result.getInt( "contributor_id" ) );
                        if ( user == null ) {
                            logger.warn( "contribution with non-existant user: contribution id: " + result.getInt( "contribution_id" ) + " and contributor " + result.getInt( "contributor_id" ) );
                            user = anonymous;
                        }
                        contribution.setPhetUser( user );
                        contribution.setTitle( result.getString( "contribution_title" ) );
                        contribution.setAuthors( result.getString( "contribution_authors" ) );
                        contribution.setKeywords( result.getString( "contribution_keywords" ) );
                        contribution.setApproved( result.getBoolean( "contribution_approved" ) );
                        contribution.setDescription( result.getString( "contribution_desc" ) );
                        contribution.setDuration( result.getInt( "contribution_duration" ) );
                        contribution.setAnswersIncluded( result.getBoolean( "contribution_answers_included" ) );
                        contribution.setContactEmail( result.getString( "contribution_contact_email" ) );
                        contribution.setAuthorOrganization( result.getString( "contribution_authors_organization" ) );
                        contribution.setDateCreated( result.getDate( "contribution_date_created" ) );
                        contribution.setDateUpdated( result.getDate( "contribution_date_updated" ) );
                        contribution.setFromPhet( result.getBoolean( "contribution_from_phet" ) );
                        contribution.setGoldStar( result.getBoolean( "contribution_is_gold_star" ) );

                        String standards = result.getString( "contribution_standards_compliance" );

                        contribution.setStandardK4A( hasStandard( standards, 1 ) );
                        contribution.setStandard58A( hasStandard( standards, 2 ) );
                        contribution.setStandard912A( hasStandard( standards, 3 ) );

                        contribution.setStandardK4B( hasStandard( standards, 4 ) );
                        contribution.setStandard58B( hasStandard( standards, 5 ) );
                        contribution.setStandard912B( hasStandard( standards, 6 ) );

                        contribution.setStandardK4C( hasStandard( standards, 7 ) );
                        contribution.setStandard58C( hasStandard( standards, 8 ) );
                        contribution.setStandard912C( hasStandard( standards, 9 ) );

                        contribution.setStandardK4D( hasStandard( standards, 10 ) );
                        contribution.setStandard58D( hasStandard( standards, 11 ) );
                        contribution.setStandard912D( hasStandard( standards, 12 ) );

                        contribution.setStandardK4E( hasStandard( standards, 13 ) );
                        contribution.setStandard58E( hasStandard( standards, 14 ) );
                        contribution.setStandard912E( hasStandard( standards, 15 ) );

                        contribution.setStandardK4F( hasStandard( standards, 16 ) );
                        contribution.setStandard58F( hasStandard( standards, 17 ) );
                        contribution.setStandard912F( hasStandard( standards, 18 ) );

                        contribution.setStandardK4G( hasStandard( standards, 19 ) );
                        contribution.setStandard58G( hasStandard( standards, 20 ) );
                        contribution.setStandard912G( hasStandard( standards, 21 ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) {
                    return sqlSuccess;
                }

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution_comment", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        ContributionComment comment = new ContributionComment();

                        newObs.add( comment );
                        PhetUser user = userIdMap.get( result.getInt( "contributor_id" ) );
                        Contribution contribution = contributionIdMap.get( result.getInt( "contribution_id" ) );
                        contribution.addComment( comment );
                        comment.setPhetUser( user );
                        comment.setText( result.getString( "contribution_comment_text" ) );
                        comment.setDateCreated( result.getDate( "contribution_comment_created" ) );
                        comment.setDateUpdated( result.getDate( "contribution_comment_updated" ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) {
                    return sqlSuccess;
                }

                List currentContributions = session.createQuery( "select c from Contribution as c" ).list();
                for ( Object o : currentContributions ) {
                    session.delete( o );
                }

                List currentUsers = session.createQuery( "select u from PhetUser as u" ).list();
                for ( Object o : currentUsers ) {
                    PhetUser user = (PhetUser) o;
                    Set obs = new HashSet( user.getTranslations() );
                    for ( Object o1 : obs ) {
                        Translation translation = (Translation) o1;
                        translation.removeUser( user );
                        session.update( translation );
                    }
                    session.delete( user );
                }

                for ( Object ob : newObs ) {
                    session.save( ob );
                }


                return sqlSuccess;
            }
        } );

    }

    private static boolean hasStandard( String str, int standard ) {
        return str.indexOf( "checkbox_standards_" + standard ) >= 0;
    }
}
