package com.boots.Service;

import com.boots.Entity.Tour;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Service
@RequiredArgsConstructor
@ManagedResource(objectName = "MyMBeans:category=MBeans,name=SchedulerService")
public class SchedulerService {
    @Autowired
    TourService tourService;
    @Autowired
    TourDescriptionService tourDescriptionService;

    @Scheduled(cron = "0 0/30 * * * *")
    @ManagedOperation(description = "Deleting unused tours with an expired date")
    public void deleteTour() throws ParseException {
        List<Tour> tours = tourService.getAll();
        Date currentDate = new Date();
        Date twoDate;

        for (Tour t : tours) {
            twoDate = new SimpleDateFormat("yyyy-MM-dd").parse(t.getDate());
            System.out.println(currentDate+"\n"+twoDate);
            if (twoDate.before(currentDate)){
                if(t.getUsers()!=null){
                    t.setCount(0);
                    tourService.save(t);
                }
                else {
                    tourDescriptionService.delete(t.getTourDescription());
                    tourService.deleteTour(t.getId());
                }
            }
        }
    }
}
