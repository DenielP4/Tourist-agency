package com.boots.Repository;

import com.boots.Entity.TourDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourDescriptionRepository extends JpaRepository<TourDescription,Long> {
}
