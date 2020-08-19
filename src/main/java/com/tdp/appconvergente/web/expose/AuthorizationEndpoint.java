/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.expose;

import com.fasterxml.jackson.core.JsonProcessingException;
import static com.tdp.appconvergente.web.constants.Constants.AUTH_CODE;
import static com.tdp.appconvergente.web.constants.Constants.DATA_SEPARATOR;
import static com.tdp.appconvergente.web.constants.Constants.IP_DEVICE;
import static com.tdp.appconvergente.web.constants.Constants.REFRESH_TOKEN_NOVUM;
import com.tdp.appconvergente.web.enums.NameId;
import com.tdp.appconvergente.web.exception.NotFoundException;
import com.tdp.appconvergente.web.model.AuthCodeBean;
import com.tdp.appconvergente.web.repository.AuthCodeRepository;
import com.tdp.appconvergente.web.user.User;
import static com.tdp.appconvergente.web.util.Utils.zonedDateTimeAmericaLima;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Herbert
 */
@Controller
public class AuthorizationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationEndpoint.class);

    @Autowired
    AuthCodeRepository authCodeRepository;

    int codeTypeValue = -1;

    public Integer getCodeTypeValue() {
        return codeTypeValue;
    }

    public void setCodeTypeValue(Integer codeTypeValue) {
        this.codeTypeValue = codeTypeValue;
    }

    private String refreshTokenNovumApp;

    private String ipDeviceApp;

    public String getRefreshTokenNovumApp() {
        return refreshTokenNovumApp;
    }

    public void setRefreshTokenNovumApp(String refreshTokenNovumApp) {
        this.refreshTokenNovumApp = refreshTokenNovumApp;
    }

    public String getIpDeviceApp() {
        return ipDeviceApp;
    }

    public void setIpDeviceApp(String ipDeviceApp) {
        this.ipDeviceApp = ipDeviceApp;
    }

    @SuppressWarnings("rawtypes")
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/authorization")
    public ResponseEntity<String> getAuthorizationData(
            @RequestParam(value = REFRESH_TOKEN_NOVUM, required = true) String refreshTokenNovum,
            @RequestParam(value = IP_DEVICE, required = true) String ipDevice) throws NotFoundException {
        if (refreshTokenNovum.isEmpty() || ipDevice.isEmpty()) {
            throw new NotFoundException("Ingrese los valores de " + REFRESH_TOKEN_NOVUM + " e " + IP_DEVICE);
        }
        HttpHeaders headers = new HttpHeaders();
        setRefreshTokenNovumApp(refreshTokenNovum);
        setIpDeviceApp(ipDevice);
        headers.add("Location", "/6e7a730a-ab43-11ea-bb37-0242ac130002");
        LOGGER.info("Redirigido a login correctamente");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @SuppressWarnings("rawtypes")
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/appCallBack")
    public String appCallBack(@RequestParam(value = AUTH_CODE, required = true) String authCodeValue, Model model) throws NotFoundException {
        if (getCodeTypeValue() < 0) {
            throw new NotFoundException("Error extrayendo el codeTypeValue: " + getCodeTypeValue());
        }
        model.addAttribute(AUTH_CODE, authCodeValue);
        model.addAttribute("codeTypeValue", getCodeTypeValue());
        return "redirectappcallback";
    }

    @SuppressWarnings("rawtypes")
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/6e7a730a-ab43-11ea-bb37-0242ac130002")
    public ResponseEntity<?> resp(ExpiringUsernameAuthenticationToken userToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        AuthCodeBean authCodeBean;
        Integer codeType = 2;
        try {
            User user = (User) userToken.getPrincipal();
            String issuer = user.getIssuer();
            String nameId = user.getNameId();
            String acr = user.getAcr();
            String amr = user.getAmr();
            StringBuilder authCodeEncode = new StringBuilder();
            authCodeEncode.append(nameId);
            authCodeEncode.append(DATA_SEPARATOR);
            authCodeEncode.append(amr);
            authCodeEncode.append(DATA_SEPARATOR);
            authCodeEncode.append(acr);
            authCodeEncode.append(DATA_SEPARATOR);
            authCodeEncode.append(issuer);
            String encodedAuthCode = Base64.getEncoder().encodeToString(authCodeEncode.toString().getBytes());
//            authCodeBean = authCodeRepository.findByAuthCodeKey(encodedAuthCode);
            if (StringUtils.startsWith(nameId, NameId.DNI.getValue())) {
                codeType = 0;
            }
            if (StringUtils.startsWith(nameId, NameId.CEX.getValue())) {
                codeType = 1;
            }
//            if (authCodeBean == null) {
                authCodeBean = new AuthCodeBean();
                authCodeBean.setAuthCodeKey(encodedAuthCode);
                authCodeBean.setAuthCodeType(codeType);
                authCodeBean.setCreateDate(zonedDateTimeAmericaLima());
                authCodeRepository.save(authCodeBean);
                LOGGER.info("AuthCode: {} guardado correctamente!", encodedAuthCode);
//            }
            setCodeTypeValue(codeType);
            headers.add("Location", "/appCallBack?" + AUTH_CODE + "=" + encodedAuthCode);
            LOGGER.info("Redirigido a appCallBack correctamente");
            LOGGER.info("Encoded Auth Code: {} ", encodedAuthCode);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (UsernameNotFoundException ex) {
            LOGGER.error("User does not exists", ex, ex.getMessage());
            return new ResponseEntity<>("User does not exists", HttpStatus.UNAUTHORIZED);
        }
    }

}
