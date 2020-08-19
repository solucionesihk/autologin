/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.constants;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author eduar
 */
public class Constants {

    public static final String MAIN_URL = StringUtils.defaultIfBlank(
            System.getenv("MAIN_URL"),
            "https://ms-ne-appconvergente-saml-qa.mybluemix.net"
    );

    public static final String APP_NAME = "AppConvergente";

    public static final String DATA_SEPARATOR = ";";
    public static final String RESPONSE = "response";
    public static final String STATUS = "status";

    public static final String REFRESH_TOKEN_NOVUM = "refreshTokenNovum";
    public static final String IP_DEVICE = "ipDevice";
    public static final String COOKIES_NAME = "AppConvergenteCookie";

    public static final String AUTH_CODE = "authCode";

    public static final String FLAG_METADATA = "FLAG_METADATA";
    public static final String LOGIN_VALUE = "1";
    public static final String AUTOLOGIN_VALUE = "0";

    public static final String METADATA_URL_LOGIN = "METADATA_URL_LOGIN";
    public static final String METADATA_PATH_AUTOLOGIN = "METADATA_PATH_AUTOLOGIN";
    public static final String METADATA_PATH_AUTOLOGIN_DEV = "METADATA_PATH_AUTOLOGIN_DEV";
    public static final String METADATA_PATH_AUTOLOGIN1_DEV = "METADATA_PATH_AUTOLOGIN1_DEV";
    public static final String AUTHORIZATION_ID_APP = "AUTHORIZATION_ID_APP";
    public static final String ACR_VALUES = "ACR_VALUES";

}
