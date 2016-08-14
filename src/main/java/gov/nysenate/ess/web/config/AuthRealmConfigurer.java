package gov.nysenate.ess.web.config;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * A service that wires all initialized realms into the application's security manager
 */
@Service
public class AuthRealmConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(AuthRealmConfigurer.class);

    @Autowired protected List<Realm> realmList;
    @Autowired protected DefaultWebSecurityManager securityManager;

    @PostConstruct
    public void setUp() {
        securityManager.setRealms(realmList);
    }
}
