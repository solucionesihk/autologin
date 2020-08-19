package com.tdp.appconvergente.web.config;

import com.tdp.appconvergente.web.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SAMLUserService implements SAMLUserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAMLUserService.class);

    @Override
    public User loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        LOGGER.info("LOADING USER BASED ON SAML AUTHENTICATION OBJECT......{}", credential.toString());
        User u = new User();
        u.setIssuer(credential.getAuthenticationAssertion().getIssuer().getValue());
        u.setNameId(credential.getNameID().getValue());
        String acr = credential.getAuthenticationAssertion().getAttributeStatements().get(0).getAttributes().get(0).getName();
        String amr = credential.getAuthenticationAssertion().getAttributeStatements().get(0).getAttributes().get(1).getName();
        u.setAcr(credential.getAttributeAsString(acr));
        u.setAmr(credential.getAttributeAsString(amr));
        LOGGER.info("Issuer                  : {}", u.getIssuer());
        LOGGER.info("NameId                  : {}", u.getNameId());
        LOGGER.info("ACR                     : {}", u.getAcr());
        LOGGER.info("AMR                     : {}", u.getAmr());
        LOGGER.info(u.toString());
        return u;
    }
}
