/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 *
 * @author VASSPERU
 */
public class Utils {

    public static LocalDateTime localDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime zonedDateTimeAmericaLima() {
        return LocalDateTime.now(ZoneId.of("America/Lima"));
    }

    public static LocalDateTime zonedDateTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
