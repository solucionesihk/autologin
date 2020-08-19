/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.expose;

import static com.tdp.appconvergente.web.constants.Constants.AUTOLOGIN_VALUE;
import static com.tdp.appconvergente.web.constants.Constants.FLAG_METADATA;
import static com.tdp.appconvergente.web.constants.Constants.LOGIN_VALUE;
import com.tdp.appconvergente.web.exception.NotFoundCodeException;
import com.tdp.appconvergente.web.repository.GeneralSettingsRepository;
import com.tdp.appconvergente.web.util.TechnicalData;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author VASSPERU
 */
@RestController
public class ContigenceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContigenceEndpoint.class);

    @Autowired
    GeneralSettingsRepository generalSettingsRepository;

    @Autowired
    TechnicalData technicalData;

    @SuppressWarnings("rawtypes")
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/update-flag/{value}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateFlag(@PathVariable(value = "value") String value) throws NotFoundCodeException {
        LOGGER.info("Input data");
        LOGGER.info("Value to change: {}", value);
        String flagValue = technicalData.getParameterDB(FLAG_METADATA);
        if (StringUtils.equals(flagValue, value)) {
            throw new NotFoundCodeException("El estado actual del FLAG, es el mismo que est\u00e1 ingresando.");
        }
        if (StringUtils.equals(value, AUTOLOGIN_VALUE) || StringUtils.equals(value, LOGIN_VALUE)) {
            int status = generalSettingsRepository.updateFlagSumDebtsProductsMT(value, flagValue, FLAG_METADATA);
            if (status == 1) {
                return ResponseEntity.ok("Flag actualizado correctamente de " + flagValue + " a " + value);
            }
        }
        throw new NotFoundCodeException("El valor a ingresar es 0 o 1");
    }
}
