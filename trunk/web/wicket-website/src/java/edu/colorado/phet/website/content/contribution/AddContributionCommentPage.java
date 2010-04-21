package edu.colorado.phet.website.content.contribution;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Page meant solely for handling submitted forms for adding comments. If the user isn't logged in, they are redirected
 * to the sign in page with the correct destination specified to return (so it will save their comment)
 */
public class AddContributionCommentPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( AddContributionCommentPage.class.getName() );

    private int contributionId;
    private Contribution contribution;

    public AddContributionCommentPage( PageParameters parameters ) {
        super( parameters );

        contributionId = parameters.getInt( "contribution_id" );
        String text = parameters.getString( "text" );

        if ( !PhetSession.get().isSignedIn() ) {
            setResponsePage( new RedirectPage( SignInPage.getLinker( getLinker( contributionId, text ).getDefaultRawUrl() ).getDefaultRawUrl() ) );
            return;
        }

        PhetUser user = PhetSession.get().getUser();

        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                contribution = (Contribution) session.createQuery( "select c from Contribution as c where c.id = :id" ).setInteger( "id", contributionId ).uniqueResult();
                return true;
            }
        } );

        if ( !success ) {
            logger.info( "unknown contribution of id " + contributionId );
            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
        }

        logger.debug( "adding comment to contribution " + contribution.getId() + " with user " + user.getEmail() + " and text " + text );

        // redirect back to contribution page
        setResponsePage( new RedirectPage( ContributionPage.getLinker( contributionId ).getDefaultRawUrl() ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/submit-comment$", AddContributionCommentPage.class, new String[]{} );
    }

    public static RawLinkable getLinker( final int contributionId, final String text ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                try {
                    return "contributions/submit-comment?contribution_id=" + contributionId + "&text=" + URLEncoder.encode( text, "UTF-8" );
                }
                catch( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        };
    }

    public static RawLinkable getLinker( Contribution contribution, String text ) {
        return getLinker( contribution.getId(), text );
    }

    public static RawLinkable getBaseLinker() {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "contributions/submit-comment";
            }
        };
    }

}