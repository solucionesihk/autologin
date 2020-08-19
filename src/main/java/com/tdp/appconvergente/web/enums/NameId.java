/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author VASSPERU
 */
public enum NameId {
    DNI("DNI"),
    CEX("C"),
    MSISDN("MSISDN");

    public static NameId getByValue(String NameId) {
        for (NameId v : values()) {
            if (v.value.equalsIgnoreCase(NameId)) {
                return v;
            }
        }
        return null;
    }

    private final String value;

    private static final Map<String, NameId> lookup = new HashMap<>();

    static {
        for (NameId type : NameId.values()) {
            lookup.put(type.getValue(), type);
        }
    }

    private NameId(String s) {
        value = s;
    }

    public String getValue() {
        return value;
    }

    public static Set<String> asSet() {
        return lookup.keySet();
    }

    public static boolean isNameId(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return lookup.containsKey(value.toLowerCase());
    }
}
