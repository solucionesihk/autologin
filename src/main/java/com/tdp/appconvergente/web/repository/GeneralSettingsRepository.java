/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tdp.appconvergente.web.repository;

import com.tdp.appconvergente.web.model.GeneralSettingsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author VASSPERU
 */
@Repository
public interface GeneralSettingsRepository extends JpaRepository<GeneralSettingsBean, Long> {

    GeneralSettingsBean findByGsKey(String gsKey);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update GeneralSettingsBean g set g.gsValue = :statusOne where (g.gsValue = :statusTwo and g.gsKey = :gsKey) or (g.gsValue = :statusTwo and g.gsId = 1)")
    int updateFlagSumDebtsProductsMT(@Param("statusOne") String statusOne, @Param("statusTwo") String statusTwo, @Param("gsKey") String gsKey);
}
