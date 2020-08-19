/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author eduar
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundCodeException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotFoundCodeException(String message) {
        super(message);
    }
}
