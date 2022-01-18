package com.seedak.shopifychallenge.repository;

import com.seedak.shopifychallenge.model.ImageSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageSetRepository extends JpaRepository<ImageSet, Integer> {

}
