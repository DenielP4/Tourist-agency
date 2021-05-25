package com.boots.Service;

import com.boots.Entity.Tour;
import com.boots.Entity.User;
import com.boots.Repository.TourRepository;
import com.boots.Repository.UserRepository;
import com.boots.Request.EditRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TourService {
    @Autowired
    TourRepository tourRepository;

    @Autowired
    TourDescriptionService tourDescriptionService;

    @Autowired
    UserRepository userRepository;

    @SneakyThrows
    public boolean addTour(Tour tour) {
        Date currentDate = new Date();
        Date twoDate = new SimpleDateFormat("yyyy-MM-dd").parse(tour.getDate());
        if (twoDate.before(currentDate)||tour.getPrice()<=0||tour.getCount()<0){
            return false;
        }
        else {
            tourRepository.save(tour);
            return true;
        }
    }

    public boolean save(Tour tour){
        tourRepository.save(tour);
        return true;
    }

    @SneakyThrows
    public boolean editTour(EditRequest editRequest){
        Optional<Tour> tour = tourRepository.findById(editRequest.getId());
        System.out.println(editRequest.toString());
        boolean editTour=false;
        boolean editDescr=false;
        if(!tour.isPresent()){
            return false;
        }
        if (!editRequest.getStart().equals("")){
            tour.get().setStart(editRequest.getStart());
            editTour=true;
        }
        if (!editRequest.getFinish().equals("")){
            tour.get().setFinish(editRequest.getFinish());
            editTour=true;
        }
        if (!(editRequest.getPrice()<=0)){
            tour.get().setPrice(editRequest.getPrice());
            editTour=true;
        }
        if (!editRequest.getDate().equals("")){
            Date currentDate = new Date();
            Date twoDate = new SimpleDateFormat("yyyy-MM-dd").parse(editRequest.getDate());
            if (twoDate.before(currentDate)){
                return false;
            }
            else {
                tour.get().setDate(editRequest.getDate());
                editTour = true;
            }
        }
        if (!(editRequest.getCount()<0)){
            tour.get().setCount(editRequest.getCount());
            editTour=true;
        }
        if(!editRequest.getImg().equals("")){
            tour.get().getTourDescription().setImg(tourDescriptionService.parse(editRequest.getImg()));
            editDescr=true;
        }
        if(!editRequest.getText().equals("")){
            tour.get().getTourDescription().setText(tourDescriptionService.parse(editRequest.getText()));
            editDescr=true;
        }
        if (editTour) tourRepository.save(tour.get());
        if (editDescr) tourDescriptionService.add(tour.get().getTourDescription());

        return true;
    }

    public Tour findTour(String start, String finish, String date){
        return tourRepository.findByStartAndFinishAndDate(start, finish, date);
    }

    public Tour findById(Long id){
        Optional<Tour> tour = tourRepository.findById(id);
        return tour.get();
    }

    public Tour deleteTour(Long id){
        if (id == null) return null;
        Optional<Tour> tourOP = tourRepository.findById(id);
        if(tourOP.orElse(null)==null){
            return null;
        }
        Tour tour = tourOP.get();
        tourDescriptionService.delete(tour.getTourDescription());//delete dependency with TourDescription

        User[] users = tour.getUsers().toArray(new User[0]);//delete dependency with User
        for (int i = 0; i < users.length; ++i){
            System.out.println(users[i].getId());
            Set<Tour> tours = users[i].getTours();
            tours.remove(tour);
            users[i].setTours(tours);
            userRepository.save(users[i]);
        }

        tourRepository.deleteById(id);
        return tour;
    }

    public List<Tour> getAll(){
        return tourRepository.findAll();
    }
}
