package com.google.devrel.training.conference.spi;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.devrel.training.conference.Constants;
import com.google.devrel.training.conference.domain.Profile;
import com.google.devrel.training.conference.form.ProfileForm;
import com.google.devrel.training.conference.form.ProfileForm.TeeShirtSize;
import com.googlecode.objectify.Key;

/**
 * Defines conference APIs.
 */
@Api(name = "conference", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
        Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID }, description = "API for the Conference Central Backend application.")
public class ConferenceApi {

    /*
     * Get the display name from the user's email. For example, if the email is
     * lemoncake@example.com, then the display name becomes "lemoncake."
     */
    private static String extractDefaultDisplayNameFromEmail(String email) {
        return email == null ? null : email.substring(0, email.indexOf("@"));
    }

    /**
     * Creates or updates a Profile object associated with the given user
     * object.
     *
     * @param user
     *            A User object injected by the cloud endpoints.
     * @param profileForm
     *            A ProfileForm object sent from the client form.
     * @return Profile object just created.
     * @throws UnauthorizedException
     *             when the User object is null.
     */

    // Declare this method as a method available externally through Endpoints
    @ApiMethod(name = "saveProfile", path = "profile", httpMethod = HttpMethod.POST)
    // The request that invokes this method should provide data that
    // conforms to the fields defined in ProfileForm

   
    public Profile saveProfile(User user, ProfileForm pf) throws UnauthorizedException {
    	if(user == null)
    		throw new UnauthorizedException("Authorization is required");

        String userId = null;
        userId = user.getUserId();
        String mainEmail = null;
        mainEmail = user.getEmail();
        String displayName ;
        if(pf.getDisplayName()!= null)
        	displayName = pf.getDisplayName();
        else 
        	displayName = extractDefaultDisplayNameFromEmail(mainEmail);
        TeeShirtSize teeShirtSize;
        if(pf.getTeeShirtSize()!= null)
        	teeShirtSize = pf.getTeeShirtSize();
        else
        	teeShirtSize = TeeShirtSize.NOT_SPECIFIED;

        
        //Profile profile = new Profile(userId, displayName, mainEmail, teeShirtSize);*/
        
        Profile profile = getProfile(user);
        if (profile == null)
        profile = new Profile(userId, displayName, mainEmail, teeShirtSize);
        else
        profile.update(displayName,teeShirtSize);

        // TODO 3 (In Lesson 3)
        // Save the Profile entity in the datastore
        //Key<Profile> key = Key.create(Profile.class, userId);
         ofy().save().entity(profile).now();
        // Return the profile
        return profile;
    }

    /**
     * Returns a Profile object associated with the given user object. The cloud
     * endpoints system automatically inject the User object.
     *
     * @param user
     *            A User object injected by the cloud endpoints.
     * @return Profile object.
     * @throws UnauthorizedException
     *             when the User object is null.
     */
    @ApiMethod(name = "getProfile", path = "profile", httpMethod = HttpMethod.GET)
    public Profile getProfile(final User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        // TODO
        // load the Profile Entity
        String userId = user.getUserId(); // TODO
        Key<Profile> key = Key.create(Profile.class, userId); // TODO
        Profile profile = ofy().load().key(key).now(); // TODO load the Profile entity
        return profile;
    }
}
