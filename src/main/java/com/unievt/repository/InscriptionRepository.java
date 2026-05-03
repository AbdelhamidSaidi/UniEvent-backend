package com.unievt.repository;

import com.unievt.entity.Inscription;
import com.unievt.enums.StatutInscriptionEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByEvenementId(Long evenementId);
    List<Inscription> findByEtudiantId(Long etudiantId);
    List<Inscription> findByEvenementIdAndStatut(Long evenementId, StatutInscriptionEnum statut);
}
