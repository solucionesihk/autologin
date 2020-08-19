/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@ComponentScans(value = {
    @ComponentScan({"com.tdp.appconvergente.web.user", "com.tdp.appconvergente.web.model",
        "ccom.tdp.appconvergente.web.expose", "com.tdp.appconvergente.web.repository", "com.tdp.appconvergente.web.util"})})
public class ConfigDataBase {

    @Autowired
    private Environment env;

    private static final Log LOGGER = LogFactory.getLog(ConfigDataBase.class);

    @Primary
    @Bean(name = "dataSource", destroyMethod = "close")
    public DataSource dataSource() throws CertificateException, FileNotFoundException, IOException {
        HikariConfig config = new HikariConfig();

        createCertificate();

        String springProfilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
        LOGGER.info("SPRING_PROFILES_ACTIVE: " + springProfilesActive);
        if (springProfilesActive != null) {

            // Cloud
            LOGGER.info("TDP_DB_DRIVER: " + System.getenv("TDP_DB_DRIVER"));
            config.setDriverClassName(System.getenv("TDP_DB_DRIVER"));

            LOGGER.info("TDP_DB_URL: " + System.getenv("TDP_DB_URL"));
            config.setJdbcUrl(System.getenv("TDP_DB_URL"));
            LOGGER.info("TDP_DB_USR: " + System.getenv("TDP_DB_USR"));
            config.setUsername(System.getenv("TDP_DB_USR"));
            LOGGER.info("TDP_DB_PW: " + System.getenv("TDP_DB_PW"));
            config.setPassword(System.getenv("TDP_DB_PW"));
            LOGGER.info("TDP_DB_MIN_POOL: " + System.getenv("TDP_DB_MIN_POOL"));
            config.setMinimumIdle(Integer.parseInt(System.getenv("TDP_DB_MIN_POOL")));
            LOGGER.info("TDP_DB_MAX_POOL: " + System.getenv("TDP_DB_MAX_POOL"));
            config.setMaximumPoolSize(Integer.parseInt(System.getenv("TDP_DB_MAX_POOL")));
            LOGGER.info("FIN DE LA CONF DATASOURCE");
        } else {
            //Properties
            LOGGER.info("PROPERTY_DRIVER: " + env.getProperty("spring.datasource.driver-class-name"));
            config.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            LOGGER.info("PROPERTY_USERNAME: " + env.getProperty("spring.datasource.username"));
            config.setUsername(env.getProperty("spring.datasource.username"));
            LOGGER.info("PROPERTY_PASSWORD: " + env.getProperty("spring.datasource.password"));
            config.setPassword(env.getProperty("spring.datasource.password"));
            LOGGER.info("PROPERTY_URL: " + env.getProperty("spring.datasource.url"));
            config.setJdbcUrl(env.getProperty("spring.datasource.url"));
            config.setMinimumIdle(Integer.parseInt("2"));
            config.setMaximumPoolSize(Integer.parseInt("3"));
        }

        config.setConnectionTestQuery("SELECT current_timestamp");
        config.addDataSourceProperty("sslmode", "verify-full");
        config.addDataSourceProperty("sslrootcert", "root.crt");

        config.setConnectionTimeout(Integer.parseInt("20000"));
        config.setIdleTimeout(Integer.parseInt(("30000")));

        return new HikariDataSource(config);
    }

    @SuppressWarnings({"resource"})
    private static void createCertificate() throws CertificateException, FileNotFoundException, IOException {
        String certB64 = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUREekNDQWZlZ0F3SUJBZ0lKQU5FSDU4eTIva3pITUEwR0NTcUdTSWIzRFFFQkN3VUFNQjR4SERBYUJnTlYKQkFNTUUwbENUU0JEYkc5MVpDQkVZWFJoWW1GelpYTXdIaGNOTVRnd05qSTFNVFF5T1RBd1doY05Namd3TmpJeQpNVFF5T1RBd1dqQWVNUnd3R2dZRFZRUUREQk5KUWswZ1EyeHZkV1FnUkdGMFlXSmhjMlZ6TUlJQklqQU5CZ2txCmhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBOGxwYVFHemNGZEdxZU1sbXFqZmZNUHBJUWhxcGQ4cUoKUHIzYklrclhKYlRjSko5dUlja1NVY0NqdzRaL3JTZzhublQxM1NDY09sKzF0bys3a2RNaVU4cU9XS2ljZVlaNQp5K3laWWZDa0dhaVpWZmF6UUJtNDV6QnRGV3YrQUIvOGhmQ1RkTkY3Vlk0c3BhQTNvQkUyYVM3T0FOTlNSWlNLCnB3eTI0SVVnVWNJTEpXK21jdlc4MFZ4K0dYUmZEOVl0dDZQUkpnQmhZdVVCcGd6dm5nbUNNR0JuK2wyS05pU2YKd2VvdllEQ0Q2Vm5nbDIrNlc5UUZBRnRXWFdnRjNpRFFENW5sL240bXJpcE1TWDZVRy9uNjY1N3U3VERkZ2t2QQoxZUtJMkZMellLcG9LQmU1cmNuck03bkhnTmMvbkNkRXM1SmVjSGIxZEh2MVFmUG02cHpJeHdJREFRQUJvMUF3ClRqQWRCZ05WSFE0RUZnUVVLMytYWm8xd3lLcytERW9ZWGJIcnV3U3BYamd3SHdZRFZSMGpCQmd3Rm9BVUszK1gKWm8xd3lLcytERW9ZWGJIcnV3U3BYamd3REFZRFZSMFRCQVV3QXdFQi96QU5CZ2txaGtpRzl3MEJBUXNGQUFPQwpBUUVBSmY1ZHZselVwcWFpeDI2cUpFdXFGRzBJUDU3UVFJNVRDUko2WHQvc3VwUkhvNjNlRHZLdzh6Ujd0bFdRCmxWNVAwTjJ4d3VTbDlacUFKdDcvay8zWmVCK25Zd1BveU8zS3ZLdkFUdW5SdmxQQm40RldWWGVhUHNHKzdmaFMKcXNlam1reW9uWXc3N0hSekdPekpINFpnOFVONm1mcGJhV1NzeWFFeHZxa25DcDlTb1RRUDNENjdBeldxYjF6WQpkb3FxZ0dJWjJueENrcDUvRlh4Ri9UTWI1NXZ0ZVRRd2ZnQnk2MGpWVmtiRjdlVk9XQ3YwS2FOSFBGNWhycWJOCmkrM1hqSjcvcGVGM3hNdlRNb3kzNURjVDNFMlplU1Zqb3VaczE1Tzkwa0kzazJkYVMyT0hKQUJXMHZTajRuTHoKK1BRenAvQjljUW1PTzhkQ2UwNDlRM29hVUE9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCgo=";
        byte encodedCert[] = Base64.getDecoder().decode(certB64);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(encodedCert);

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(inputStream);

        String content = Base64.getEncoder().encodeToString(cert.getEncoded());

        FileOutputStream fos = new FileOutputStream("root.crt");
        fos.write("-----BEGIN CERTIFICATE-----\n".getBytes());
        fos.write(content.getBytes());
        fos.write("\n".getBytes());
        fos.write("-----END CERTIFICATE-----".getBytes());
    }

}
