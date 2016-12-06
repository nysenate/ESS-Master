package gov.nysenate.ess.core.service.ldap;

import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.model.auth.LdapAuthResult;
import gov.nysenate.ess.core.model.auth.LdapAuthStatus;
import gov.nysenate.ess.core.service.auth.EssLdapAuthService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class EssLdapAuthServiceIT extends BaseTests
{
    @Value("${test.ldap.valid.uid}") private String validUid;
    @Value("${test.ldap.valid.dn}") private String validDn;
    @Value("${test.ldap.valid.password}") private String validPassword;

    @Autowired
    private EssLdapAuthService senateLdapService;

    @Test
    public void testAutowiredSucceeds() throws Exception {
        assertNotNull(senateLdapService);
    }


    /** {@link EssLdapAuthService#authenticateUserByUid(String, String)} tests
     * ------------------------------------------------------------------------------------------------------*/

    @Test
    public void testAuthenticateUserByUid_Succeeds() throws Exception {
        LdapAuthResult authResult = senateLdapService.authenticateUserByUid(validUid, validPassword);
        assertNotNull(authResult);
        Assert.assertEquals(LdapAuthStatus.AUTHENTICATED, authResult.getAuthStatus());
        assertEquals(validUid, authResult.getUid());
        assertEquals(validDn.toLowerCase(), authResult.getName().toString().toLowerCase());
    }

    @Test
    public void testAuthenticateUserByUid_EmptyUserPass() throws Exception {
        LdapAuthResult authResult = senateLdapService.authenticateUserByUid(null, "pass");
        assertEquals(LdapAuthStatus.EMPTY_USERNAME, authResult.getAuthStatus());
        authResult = senateLdapService.authenticateUserByUid("user", null);
        assertEquals(LdapAuthStatus.EMPTY_CREDENTIALS, authResult.getAuthStatus());
    }

    @Test
    public void testAuthenticateUserByUid_AuthenticationFailsInvalidPass() throws Exception {
        LdapAuthResult authResult = senateLdapService.authenticateUserByUid(validUid, "invalidPassword");
        assertEquals(LdapAuthStatus.AUTHENTICATION_EXCEPTION, authResult.getAuthStatus());
        assertEquals(validUid, authResult.getUid());
    }

    @Test
    public void testAuthenticateUserByUid_AuthenticationFailsInvalidUser() throws Exception {
        String invalidUser = "gloork";
        LdapAuthResult authResult = senateLdapService.authenticateUserByUid(invalidUser, "invalidPassword");
        assertEquals(LdapAuthStatus.AUTHENTICATION_EXCEPTION, authResult.getAuthStatus());
        assertEquals(invalidUser, authResult.getUid());
    }

    @Test
    public void testGetPerson() throws Exception {

    }
}