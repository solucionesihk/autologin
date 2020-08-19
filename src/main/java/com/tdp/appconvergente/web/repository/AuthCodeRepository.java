/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.repository;

import com.tdp.appconvergente.web.model.AuthCodeBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author VASSPERU
 */
@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCodeBean, Integer> {

    AuthCodeBean findByAuthCodeKey(String authCodeKey);
}
