/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author VASSPERU
 */
@Entity
@Table(name = "ac_auth_code", schema = "public")
public class AuthCodeBean implements Serializable {

    @Id
    @Column(name = "ac_authcode_id")
    @SequenceGenerator(name = "ac_auth_code_seq", sequenceName = "ac_auth_code_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ac_auth_code_seq")
    private Long authCodeId;

    @Column(name = "ac_authcode_key")
    private String authCodeKey;

    @Column(name = "ac_authcode_type")
    private Integer authCodeType;

    @Column(name = "ac_create_date")
    private LocalDateTime createDate;

    public Long getAuthCodeId() {
        return authCodeId;
    }

    public void setAuthCodeId(Long authCodeId) {
        this.authCodeId = authCodeId;
    }

    public String getAuthCodeKey() {
        return authCodeKey;
    }

    public void setAuthCodeKey(String authCodeKey) {
        this.authCodeKey = authCodeKey;
    }

    public Integer getAuthCodeType() {
        return authCodeType;
    }

    public void setAuthCodeType(Integer authCodeType) {
        this.authCodeType = authCodeType;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

}
