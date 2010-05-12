package edu.colorado.phet.website.authentication.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class EditProfilePanel extends PhetPanel {

    private Model errorModel;

    private static final String ERROR_SEPARATOR = "<br/>";

    private static Logger logger = Logger.getLogger( EditProfilePanel.class.getName() );

    public EditProfilePanel( String id, PageContext context ) {
        this( id, context, PhetSession.get().getUser() );
    }

    public EditProfilePanel( String id, PageContext context, PhetUser user ) {
        super( id, context );

        errorModel = new Model( "" );
        add( new RawLabel( "profile-errors", errorModel ) );

        add( new EditProfileForm( "edit-profile-form", user ) );

        if ( PhetSession.get().getUser().getId() == user.getId() ) {
            Label signal = new Label( "edit-self-profile", "" );
            add( signal );
            signal.setRenderBodyOnly( true );
        }
        else {
            add( new InvisibleComponent( "edit-self-profile" ) );
        }
    }

    private final class EditProfileForm extends Form {

        private int userId;
        private int currentUserId;

        private TextField name;
        private TextField organization;
        private DropDownChoice description;
        private TextField jobTitle;

        private TextField address1;
        private TextField address2;
        private TextField city;
        private TextField state;
        private TextField country;
        private TextField zipcode;

        private TextField phone1;
        private TextField phone2;
        private TextField fax;

        private CheckBox receiveEmail;
        private CheckBox receiveWebsiteNotifications;
        private CheckBox teamMember;

        private final ValueMap properties = new ValueMap();

        public EditProfileForm( String id, final PhetUser user ) {
            super( id );

            userId = user.getId();

            PhetUser currentUser = PhetSession.get().getUser();

            properties.add( "name", user.getName() );
            properties.add( "organization", user.getOrganization() );
            properties.add( "description", user.getDescription() );
            properties.add( "jobTitle", user.getJobTitle() );
            properties.add( "address1", user.getAddress1() );
            properties.add( "address2", user.getAddress2() );
            properties.add( "city", user.getCity() );
            properties.add( "state", user.getState() );
            properties.add( "country", user.getCountry() );
            properties.add( "zipcode", user.getZipcode() );
            properties.add( "phone1", user.getPhone1() );
            properties.add( "phone2", user.getPhone2() );
            properties.add( "fax", user.getFax() );

            add( name = new StringTextField( "name", new PropertyModel( properties, "name" ) ) );
            add( organization = new StringTextField( "organization", new PropertyModel( properties, "organization" ) ) );
            add( description = new DropDownChoice( "description", new PropertyModel( properties, "description" ), PhetUser.getDescriptionOptions() ) );
            add( jobTitle = new StringTextField( "jobTitle", new PropertyModel( properties, "jobTitle" ) ) );
            add( address1 = new StringTextField( "address1", new PropertyModel( properties, "address1" ) ) );
            add( address2 = new StringTextField( "address2", new PropertyModel( properties, "address2" ) ) );
            add( city = new StringTextField( "city", new PropertyModel( properties, "city" ) ) );
            add( state = new StringTextField( "state", new PropertyModel( properties, "state" ) ) );
            add( country = new StringTextField( "country", new PropertyModel( properties, "country" ) ) );
            add( zipcode = new StringTextField( "zipcode", new PropertyModel( properties, "zipcode" ) ) );
            add( phone1 = new StringTextField( "phone1", new PropertyModel( properties, "phone1" ) ) );
            add( phone2 = new StringTextField( "phone2", new PropertyModel( properties, "phone2" ) ) );
            add( fax = new StringTextField( "fax", new PropertyModel( properties, "fax" ) ) );
            add( receiveEmail = new CheckBox( "receiveEmail", new Model( new Boolean( user.isReceiveEmail() ) ) ) );
            add( receiveWebsiteNotifications = new CheckBox( "receiveWebsiteNotifications", new Model( new Boolean( user.isReceiveWebsiteNotifications() ) ) ) );
            add( teamMember = new CheckBox( "phetTeamMember", new Model( new Boolean( user.isTeamMember() ) ) ) );
            if ( currentUser.isTeamMember() ) {
                Label label = new Label( "rwn-phet", "" );
                add( label );
                // make it effectively invisible
                label.setRenderBodyOnly( true );
            }
            else {
                add( new InvisibleComponent( "rwn-phet" ) );
                add( new InvisibleComponent( "phetTeamMember" ) );
            }

        }

        @Override
        protected void onSubmit() {
            boolean error = false;
            String errorString = "";
            String err = null;

            String nom = name.getModelObjectAsString();
            String desc = description.getModelObjectAsString();

            if ( nom == null || nom.length() == 0 ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.user", this, "Please fill in the name field" );
            }

            if ( desc == null || desc.length() == 0 ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.description", this, "Please pick a description" );
            }

            if ( !error ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        PhetUser user = (PhetUser) session.load( PhetUser.class, userId );
                        user.setName( name.getModelObjectAsString() );
                        user.setOrganization( organization.getModelObjectAsString() );
                        user.setDescription( description.getModelObjectAsString() );
                        user.setJobTitle( jobTitle.getModelObjectAsString() );
                        user.setAddress1( address1.getModelObjectAsString() );
                        user.setAddress2( address2.getModelObjectAsString() );
                        user.setCity( city.getModelObjectAsString() );
                        user.setState( state.getModelObjectAsString() );
                        user.setCountry( country.getModelObjectAsString() );
                        user.setZipcode( zipcode.getModelObjectAsString() );
                        user.setPhone1( phone1.getModelObjectAsString() );
                        user.setPhone2( phone2.getModelObjectAsString() );
                        user.setFax( fax.getModelObjectAsString() );
                        user.setReceiveEmail( (Boolean) receiveEmail.getModelObject() );
                        if ( PhetSession.get().getUser().isTeamMember() ) {
                            user.setReceiveWebsiteNotifications( (Boolean) receiveWebsiteNotifications.getModelObject() );
                            user.setTeamMember( (Boolean) teamMember.getModelObject() );
                        }
                        session.update( user );
                        return true;
                    }
                } );

                int currentUserId = PhetSession.get().getUser().getId();
                if ( success && currentUserId == PhetSession.get().getUser().getId() ) {
                    // synchronize the user data for the session instance
                    PhetUser user = PhetSession.get().getUser();
                    user.setName( name.getModelObjectAsString() );
                    user.setOrganization( organization.getModelObjectAsString() );
                    user.setDescription( description.getModelObjectAsString() );
                    user.setJobTitle( jobTitle.getModelObjectAsString() );
                    user.setAddress1( address1.getModelObjectAsString() );
                    user.setAddress2( address2.getModelObjectAsString() );
                    user.setCity( city.getModelObjectAsString() );
                    user.setState( state.getModelObjectAsString() );
                    user.setCountry( country.getModelObjectAsString() );
                    user.setZipcode( zipcode.getModelObjectAsString() );
                    user.setPhone1( phone1.getModelObjectAsString() );
                    user.setPhone2( phone2.getModelObjectAsString() );
                    user.setFax( fax.getModelObjectAsString() );
                    user.setReceiveEmail( (Boolean) receiveEmail.getModelObject() );
                    if ( PhetSession.get().getUser().isTeamMember() ) {
                        user.setReceiveWebsiteNotifications( (Boolean) receiveWebsiteNotifications.getModelObject() );
                        user.setTeamMember( (Boolean) teamMember.getModelObject() );
                    }
                }
                error = !success;
            }
            if ( error ) {
                logger.error( "Error editing profile" );
                logger.error( "Reason: " + errorString );
                errorString = getPhetLocalizer().getString( "validation.user.problems", this, "Please fix the following problems with the form:" ) + "<br/>" + errorString;
                errorModel.setObject( errorString );
            }
            else {
                errorModel.setObject( "" );
            }
        }
    }
}
