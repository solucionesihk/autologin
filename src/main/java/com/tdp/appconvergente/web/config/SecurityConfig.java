package com.tdp.appconvergente.web.config;

import static com.tdp.appconvergente.web.constants.Constants.ACR_VALUES;
import static com.tdp.appconvergente.web.constants.Constants.APP_NAME;
import static com.tdp.appconvergente.web.constants.Constants.AUTHORIZATION_ID_APP;
import static com.tdp.appconvergente.web.constants.Constants.MAIN_URL;
import static com.tdp.appconvergente.web.constants.Constants.METADATA_PATH_AUTOLOGIN1_DEV;
import com.tdp.appconvergente.web.expose.AuthorizationEndpoint;
import com.tdp.appconvergente.web.util.TechnicalData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${server.ssl.key-store}")
    private String keyStoreFile;

    @Value("${server.ssl.key-alias}")
    private String keyStoreAlias;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    String refreshTokenNovumApp;

    String ipDeviceApp;

    @Autowired
    SAMLUserService samlLUserService;

    @Autowired
    TechnicalData technicalData;

    private String getAuthorizationIdApp() {
        return technicalData.getParameterDB(AUTHORIZATION_ID_APP);
    }

    private String getAcrValuesApp() {
        return technicalData.getParameterDB(ACR_VALUES);
    }

    private String getMetadataPathAutologin() {
        return technicalData.getParameterDB(METADATA_PATH_AUTOLOGIN1_DEV);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http.headers().frameOptions().disable();

        http
                .exceptionHandling()
                .authenticationEntryPoint(samlEntryPoint());
        http
                .csrf()
                .disable();

        FilterChainProxy samlFilter = samlFilter();

        http.addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
                .addFilterAfter(samlFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(samlFilter, CsrfFilter.class);

        http
                .authorizeRequests()
                .antMatchers("/saml**").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers(HttpMethod.GET, "/authorization**").permitAll()
                .antMatchers(HttpMethod.GET, "/appCallBack**").authenticated()
                .antMatchers("/6e7a730a-ab43-11ea-bb37-0242ac130002").authenticated();

        http
                .logout().disable();
    }

    @Bean
    public AuthorizationEndpoint authorizationEndpoint() {
        return new AuthorizationEndpoint();
    }

    @Bean
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        webSSOProfileOptions.setForceAuthN(false);
        return webSSOProfileOptions;
    }

    @Bean
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }

    @Bean
    public MetadataDisplayFilter metadataDisplayFilter() {
        return new MetadataDisplayFilter();
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
        SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler
                = new SavedRequestAwareAuthenticationSuccessHandler();
        successRedirectHandler.setDefaultTargetUrl("/6e7a730a-ab43-11ea-bb37-0242ac130002");
        successRedirectHandler.setAlwaysUseDefaultTargetUrl(true);
        return successRedirectHandler;
    }

    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return samlWebSSOProcessingFilter;
    }

    @Bean
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
        SimpleUrlLogoutSuccessHandler simpleUrlLogoutSuccessHandler
                = new SimpleUrlLogoutSuccessHandler();
        simpleUrlLogoutSuccessHandler.setDefaultTargetUrl("/6e7a730a-ab43-11ea-bb37-0242ac130002");
        simpleUrlLogoutSuccessHandler.setAlwaysUseDefaultTargetUrl(true);
        return simpleUrlLogoutSuccessHandler;
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler logoutHandler
                = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.setClearAuthentication(true);
        return logoutHandler;
    }

    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
        return new SAMLLogoutFilter(successLogoutHandler(),
                new LogoutHandler[]{logoutHandler()},
                new LogoutHandler[]{logoutHandler()});
    }

    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(successLogoutHandler(),
                logoutHandler());
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
        return new MetadataGeneratorFilter(metadataGenerator());
    }

    @Bean
    public MetadataGenerator metadataGenerator() {
        MetadataGenerator metadataGenerator = new MetadataGenerator();
        metadataGenerator.setBindingsSLO(Arrays.asList("Redirect"));
        metadataGenerator.setEntityId(APP_NAME);
        //APP_NAME
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager());
        metadataGenerator.setEntityBaseURL(MAIN_URL);
            metadataGenerator.setWantAssertionSigned(false);
            metadataGenerator.setRequestSigned(false);

        return metadataGenerator;
    }

    @Bean
    public KeyManager keyManager() {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource storeFile = loader
                .getResource(keyStoreFile);
        String storePass = keyStorePassword;
        Map<String, String> passwords = new HashMap<>();
        passwords.put(keyStoreAlias, keyStorePassword);
        String defaultKey = keyStoreAlias;
        return new JKSKeyManager(storeFile, storePass, passwords, defaultKey);
    }

    @Bean
    public ExtendedMetadata extendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSignMetadata(true);
            extendedMetadata.setSignMetadata(false);
        return extendedMetadata;
    }

    @Bean
    public FilterChainProxy samlFilter() throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<>();

        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"),
                metadataDisplayFilter()));

        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
                samlEntryPoint()));

        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
                samlWebSSOProcessingFilter()));

        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
                samlLogoutFilter()));

        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
                samlLogoutProcessingFilter()));

        return new FilterChainProxy(chains);
    }

    @Bean
    public VelocityEngine velocityEngine() {
        return VelocityFactory.getEngine();
    }

    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
        return new StaticBasicParserPool();
    }

    @Bean(name = "parserPoolHolder")
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
        return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
        return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    public HTTPArtifactBinding artifactBinding() {
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        ArtifactResolutionProfileImpl profile = new ArtifactResolutionProfileImpl(client);
        profile.setProcessor(new SAMLProcessorImpl(soapBinding()));
        HTTPArtifactBinding binding = new HTTPArtifactBinding(parserPool(), velocityEngine(), profile);
        return binding;
    }

    @Bean
    public HTTPSOAP11Binding soapBinding() {
        return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPAOS11Binding paosBinding() {
        return new HTTPPAOS11Binding(parserPool());
    }

    @Bean
    public SAMLProcessorImpl processor() {
        Collection<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpRedirectDeflateBinding());
        bindings.add(httpPostBinding());
        bindings.add(artifactBinding());
        bindings.add(soapBinding());
        bindings.add(paosBinding());

        return new SAMLProcessorImpl(bindings);
    }

    @Bean
    public HttpClient httpClient() throws IOException {
        return new HttpClient(multiThreadedHttpConnectionManager());
    }

    @Bean
    public MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager() {
        return new MultiThreadedHttpConnectionManager();
    }

    @Bean
    public static SAMLBootstrap sAMLBootstrap() {
        return new SAMLBootstrap();
    }

    @Bean
    public SAMLDefaultLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    @Bean
    public SAMLContextProviderImpl contextProvider() {
        return new SAMLContextProviderImpl();
    }

    // SAML 2.0 WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        WebSSOProfileConsumerImpl consumerImpl = new WebSSOProfileConsumerImpl();
        consumerImpl.setMaxAuthenticationAge(90 * 24 * 60 * 60);
        consumerImpl.setResponseSkew(15 * 60);
        return consumerImpl;
    }

    // SAML 2.0 Web SSO profile
    @Bean
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl() {

            @Override
            protected AuthnRequest getAuthnRequest(SAMLMessageContext context, WebSSOProfileOptions options, AssertionConsumerService assertionConsumer, SingleSignOnService bindingService) throws SAMLException, MetadataProviderException {
                AuthnRequest authnRequest = super.getAuthnRequest(context, options, assertionConsumer, bindingService);
                authnRequest.setExtensions(buildExtensions());
                return authnRequest;
            }

            private Extensions buildExtensions() {
                Extensions extensions = new ExtensionsBuilder()
                        .buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "Extensions", "saml2p");
                XSAny authenticator = new XSAnyBuilder().buildObject("urn:appconvergente:SAML:2.0:extensions", "Authenticator", "req");
                XSAny authorizationId = new XSAnyBuilder().buildObject("urn:appconvergente:SAML:2.0:extensions", "AuthorizationId", "req");
                XSAny refreshTokenNovum = new XSAnyBuilder().buildObject("urn:appconvergente:SAML:2.0:extensions", "RefreshTokenNovum", "req");
                XSAny acrValues = new XSAnyBuilder().buildObject("urn:appconvergente:SAML:2.0:extensions", "AcrValues", "req");
                XSAny appName = new XSAnyBuilder().buildObject("urn:appconvergente:SAML:2.0:extensions", "ApplicationName", "req");
                appName.setTextContent(APP_NAME);
                //dinamico
                refreshTokenNovumApp = authorizationEndpoint().getRefreshTokenNovumApp();
                ipDeviceApp = authorizationEndpoint().getIpDeviceApp();

                authorizationId.setTextContent(getAuthorizationIdApp());
                acrValues.setTextContent(getAcrValuesApp());
                refreshTokenNovum.setTextContent(refreshTokenNovumApp);

                XSAny ipDevice = new XSAnyBuilder().buildObject("urn:appconvergente:SAML:2.0:extensions", "IpDevice", "req");
                ipDevice.setTextContent(ipDeviceApp);

                extensions.getUnknownXMLObjects().add(appName);
                extensions.getUnknownXMLObjects().add(ipDevice);
                extensions.getUnknownXMLObjects().add(authenticator);
                authenticator.getUnknownXMLObjects().add(authorizationId);
                authenticator.getUnknownXMLObjects().add(acrValues);
                authenticator.getUnknownXMLObjects().add(refreshTokenNovum);

                /*extensions.getUnknownXMLObjects().add(autoLoginCredentials);
            autoLoginCredentials.getUnknownXMLObjects().add(secret);*/
                return extensions;
            }
        };

    }

    // not used but autowired...
    // SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    // not used but autowired...
    // SAML 2.0 Holder-of-Key Web SSO profile
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public SingleLogoutProfile logoutProfile() {
        return new SingleLogoutProfileImpl();
    }

    @Bean
    public ExtendedMetadataDelegate idpMetadata() throws MetadataProviderException, ResourceException {

        ExtendedMetadataDelegate extendedMetadataDelegate;
        Timer backgroundTaskTimer = new Timer(true);
        //flag value 1 (metadata login), value 0 (metadata autologin)

        ResourceBackedMetadataProvider resourceBackedMetadataProvider = new ResourceBackedMetadataProvider(backgroundTaskTimer,
                new ClasspathResource("/" + getMetadataPathAutologin()));

        resourceBackedMetadataProvider.setParserPool(parserPool());

        extendedMetadataDelegate = new ExtendedMetadataDelegate(resourceBackedMetadataProvider, extendedMetadata());
        extendedMetadataDelegate.setMetadataTrustCheck(false);
        extendedMetadataDelegate.setMetadataRequireSignature(false);
        resourceBackedMetadataProvider.initialize();
        
        return extendedMetadataDelegate;
    }

    @Bean
    @Qualifier("metadata")
    public CachingMetadataManager metadata() throws MetadataProviderException, ResourceException {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(idpMetadata());
        return new CachingMetadataManager(providers);
    }

    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
        samlAuthenticationProvider.setForcePrincipalAsString(false);
        return samlAuthenticationProvider;
    }

    @Primary
    public SAMLUserDetailsService userDetail() {
        return samlLUserService;
    }

}
