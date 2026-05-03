package com.unievt.repository;

import com.unievt.entity.EvenementIntervenant;
import com.unievt.entity.EvenementIntervenantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvenementIntervenantRepository extends JpaRepository<EvenementIntervenant, EvenementIntervenantId> {

    @Query("select ei from EvenementIntervenant ei where ei.evenement.id = :evenementId")
    List<EvenementIntervenant> findByEvenementId(@Param("evenementId") Long evenementId);

    @Query("select ei from EvenementIntervenant ei where ei.intervenant.id = :intervenantId")
    List<EvenementIntervenant> findByIntervenantId(@Param("intervenantId") Long intervenantId);
}
