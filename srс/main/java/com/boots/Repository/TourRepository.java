package com.boots.Repository;

import com.boots.Entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour,Long> {
    Tour findByStartAndFinishAndDate(String start,String finish, String date);
}
