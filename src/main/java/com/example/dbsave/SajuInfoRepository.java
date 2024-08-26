package com.example.dbsave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SajuInfoRepository extends JpaRepository<SajuDb, Long> {

}
