/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.util;

import com.tdp.appconvergente.web.model.GeneralSettingsBean;
import com.tdp.appconvergente.web.repository.GeneralSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author VASSPERU
 */
@Service
public class TechnicalData {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalData.class);

    @Autowired
    GeneralSettingsRepository generalSettingsRepository;

    public String getParameterDB(String parameter) {
        LOGGER.info("¡Obtencion de datos tecnicos DB!");
        LOGGER.info("Input Parameter: {}", parameter);
        GeneralSettingsBean bean = generalSettingsRepository.findByGsKey(parameter);
        LOGGER.info("Output Parameter: {}", bean.getGsValue());
        return bean.getGsValue();
    }
}
