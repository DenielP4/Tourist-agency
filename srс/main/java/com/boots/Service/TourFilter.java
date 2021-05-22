package com.boots.Service;

import com.boots.Entity.Tour;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class TourFilter {
    @PersistenceContext
    private EntityManager em;
    public List<Tour> filterTour(String start, String finish, String date, int count) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tour> cq = cb.createQuery(Tour.class);
        Root<Tour> tour = cq.from(Tour.class);

        Predicate predicateForStart = cb.like(tour.get("start"), start);
        Predicate predicateForFinish = cb.like(tour.get("finish"), finish);
        Predicate predicateForDate = null;
        Predicate predicateForCount = null;
        if(!date.equals("")){
            predicateForDate = cb.like(tour.get("date"), date);
        }
        if(count!=0) {
            predicateForCount = cb.ge(tour.get("count"), count);
        }
        Predicate predicateFinal = null;
        if(!date.equals("")&&count!=0) predicateFinal = cb.and(predicateForStart, predicateForFinish, predicateForDate, predicateForCount);
        else if(!date.equals("")) predicateFinal = cb.and(predicateForStart, predicateForFinish, predicateForDate);
        else if(count!=0) predicateFinal = cb.and(predicateForStart, predicateForFinish, predicateForCount);
        else predicateFinal = cb.and(predicateForStart, predicateForFinish);
        cq.where(predicateFinal);
        return em.createQuery(cq).getResultList();
    }
}
