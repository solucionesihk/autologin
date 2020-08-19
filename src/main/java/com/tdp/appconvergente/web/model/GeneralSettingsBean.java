/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author VASSPERU
 */
@Entity
@Table(name = "ac_general_settings", schema = "public")
public class GeneralSettingsBean implements Serializable {

    @Id
    @Column(name = "ac_id")
    private Long gsId;

    @Column(name = "ac_key")
    private String gsKey;

    @Column(name = "ac_value")
    private String gsValue;

    public Long getGsId() {
        return gsId;
    }

    public void setGsId(Long gsId) {
        this.gsId = gsId;
    }

    public String getGsKey() {
        return gsKey;
    }

    public void setGsKey(String gsKey) {
        this.gsKey = gsKey;
    }

    public String getGsValue() {
        return gsValue;
    }

    public void setGsValue(String gsValue) {
        this.gsValue = gsValue;
    }
}
