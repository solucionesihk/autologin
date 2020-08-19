/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.util;

import javax.servlet.http.Cookie;

/**
 *
 * @author VASSPERU
 */
public class CookiesUtils {

    public static Cookie verifyCookie(String nombre, Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(nombre)) {
                return cookie;
            }
        }
        return null;
    }
}
