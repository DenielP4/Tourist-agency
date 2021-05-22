package com.boots.Controller;

import com.boots.Entity.Tour;
import com.boots.Entity.User;
import com.boots.Repository.UserRepository;
import com.boots.Request.ByRequest;
import com.boots.Request.EditRequest;
import com.boots.Request.TourFilterRequest;
import com.boots.Service.EmailService;
import com.boots.Service.TourFilter;
import com.boots.Service.TourService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

@Controller
public class ClientController {
    @Autowired
    TourService tourService;
    @Autowired
    TourFilter tourFilter;
    //@Autowired
    //EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;

    @GetMapping("/booking")
    public String initBooking(Model model) {
        model.addAttribute("tourFilterForm", new TourFilterRequest());
        model.addAttribute("FilteredTours",tourService.getAll());
        return "booking";
    }

    @PostMapping("/booking")
    public String findTours(@ModelAttribute("tourFilterForm") @Valid TourFilterRequest tourFilterForm,
                            Long tourId,
                            @RequestParam String action,
                            BindingResult bindingResult,
                            Model model){
        if(action.equals("filter")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("filterError", "Убвунгве убвумумвге осас");
                return "booking";
            }
            else if(tourFilterForm.getStart().equals("")||tourFilterForm.getFinish().equals("")){
                model.addAttribute("filterError","Укажите место отправки и место назначения");
                return "booking";
            }
            model.addAttribute("FilteredTours", tourFilter.filterTour(tourFilterForm.getStart(), tourFilterForm.getFinish(), tourFilterForm.getDate(), tourFilterForm.getCount()));
            return "booking";
        }
        else if(action.equals("break")){
            model.addAttribute("FilteredTours",tourService.getAll());
            return "booking";
        }
        else{
            return "redirect:/booking/view/"+tourId;
        }
    }

    @SneakyThrows
    @GetMapping("/booking/view/{tourId}")
    public String viewTour(@PathVariable Long tourId, Model model){
        model.addAttribute("ByForm",new ByRequest());
        Tour tourFromDb = tourService.findById(tourId);
        if (tourFromDb!=null) {
            model.addAttribute("image", tourFromDb.getTourDescription().getImg());

            String characteristics = "Откуда: " + tourFromDb.getStart()
                    + "\nКуда: " + tourFromDb.getFinish()
                    + "\nЦена: " + tourFromDb.getPrice()
                    + "\nДата: " + tourFromDb.getDate();
            model.addAttribute("characteristics", characteristics);
            URL text = new URL(tourFromDb.getTourDescription().getText());
            String inputLine, res = "";
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(text.openStream()));
                while ((inputLine = in.readLine()) != null) {
                    res += inputLine;
                }
                in.close();
            } catch (IOException e) {
                System.out.println("Текстовик поломався(");
            }
            model.addAttribute("description", res);
            return "viewingTour";
        }
        else return "redirect:/booking";
    }

    @SneakyThrows
    @PostMapping("/booking/view/{tourId}")
    public String byTour(@PathVariable Long tourId,
                         @ModelAttribute("ByForm") @Valid ByRequest byRequest,
                         Model model,
                         @AuthenticationPrincipal User user){
        Tour tourFromDb = tourService.findById(tourId);
        if (tourFromDb!=null) {
            if (byRequest.getMinusCount()<=tourFromDb.getCount()&&byRequest.getMinusCount()>0) {


                tourService.editTour(new EditRequest(tourFromDb.getId(), "", "", "", 0, tourFromDb.getCount() - byRequest.getMinusCount(), "", ""));
//            emailService.send(user.getEmail(),"Количество приобретенных вами путевок: "
//                    + byRequest.getMinusCount()
//                    +"\nОписание путевки: \nИз: " + tourFromDb.getStart()
//                    + "\nВ: " + tourFromDb.getFinish()
//                    + "\nДата: " + tourFromDb.getDate()
//                    + "\nОбщая стоимость: " + tourFromDb.getPrice()*byRequest.getMinusCount() + " рублей");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
                boolean exist = false;
                for (Tour t : userRepository.findById(user.getId()).get().getTours()) {
                    if (t.getId().equals(tourFromDb.getId())) exist = true;
                    System.out.println(t.getId());
                }
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!"+exist);
                if (!exist) {
                    Set<Tour> tourSet = user.getTours();
                    tourSet.add(tourFromDb);
                    for (Tour t : tourSet) {
                        System.out.println(t.getId());
                    }
                    user.setTours(tourSet);
                    userRepository.save(user);
                }
                return "redirect:/booking";
            }
            else {
                model.addAttribute("image",tourFromDb.getTourDescription().getImg());
                String characteristics = "Откуда: " + tourFromDb.getStart()
                        + "\nКуда: " + tourFromDb.getFinish()
                        + "\nЦена: " + tourFromDb.getPrice()
                        + "\nДата: " + tourFromDb.getDate();
                model.addAttribute("characteristics", characteristics);
                URL text = new URL(tourFromDb.getTourDescription().getText());
                String inputLine, res = "";
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(text.openStream()));
                    while ((inputLine = in.readLine()) != null) {
                        res += inputLine;
                    }
                    in.close();
                } catch (IOException e) {
                    System.out.println("Текстовик поломався(");
                }
                model.addAttribute("description", res);
                model.addAttribute("ByError", "Введено некорректное число путевок");
                return "viewingTour";
            }
        }
        else return "redirect:/booking";
    }
//    @PostMapping("/booking")
//    public String findTours(@ModelAttribute("tourFilterForm") @Valid TourFilterRequest tourFilterForm,
//                            @ModelAttribute("ByForm") @Valid ByRequest byRequest,
//                            Long tourId,
//                            @RequestParam String action,
//                            BindingResult bindingResult,
//                            Model model) {
//        System.out.println(byRequest.getMinusCount());
//        if(action.equals("filter")) {
//            if (bindingResult.hasErrors()) {
//                model.addAttribute("filterError", "Убвунгве убвумумвге осас");
//                return "booking";
//            }
//            else if(tourFilterForm.getStart().equals("")||tourFilterForm.getFinish().equals("")){
//                model.addAttribute("filterError","Укажите место отправки и место назначения");
//                return "booking";
//            }
//            model.addAttribute("FilteredTours", tourFilter.filterTour(tourFilterForm.getStart(), tourFilterForm.getFinish(), tourFilterForm.getDate(), tourFilterForm.getCount()));
//            return "booking";
//        }
//        else{
//            return "forward:/booking/view";
//        }
//    }
//
//    @PostMapping("/booking/view")
//    public String viewTour(@ModelAttribute("tourFilterForm") @Valid TourFilterRequest tourFilterForm,
//                           @ModelAttribute("ByForm") @Valid ByRequest byRequest,
//                           Long tourId,
//                           @RequestParam String action,
//                           BindingResult bindingResult,
//                           Model model,
//                           @AuthenticationPrincipal User user) throws IOException {
//        System.out.println(byRequest.getMinusCount());
//        if (action.equals("view")) {
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            tour = tourService.findById(tourId);
//            System.out.println(tour.getId()+"\n"+   tour.getTourDescription().getImg()+"\n"+tour.getTourDescription().getText());
//            model.addAttribute("image", tour.getTourDescription().getImg());
//            URL text = new URL(tour.getTourDescription().getText());
//            String inputLine, res = "";
//            try {
//                BufferedReader in = new BufferedReader(new InputStreamReader(text.openStream()));
//
//                while ((inputLine = in.readLine()) != null) {
//                    res += inputLine;
//                }
//                in.close();
//            }
//            catch (IOException e){
//                System.out.println("Текстовик поломався(");
//            }
//
//            String characteristics = "Откуда: " + tour.getStart() + "\nКуда: " + tour.getFinish() + "\nЦена: " + tour.getPrice() + "\nДата: " + tour.getDate();
//            model.addAttribute("characteristics", characteristics);
//            model.addAttribute("description", res);
//            System.out.println(tourId);
//            return "viewingTour";
//        }
//        else{
//            if (byRequest.getMinusCount()<=tour.getCount()&&byRequest.getMinusCount()>0) {
//                tourService.editTour(new EditRequest(tour.getId(), "", "", "", 0, tour.getCount() - byRequest.getMinusCount(), "", ""));
//                //emailService.send(user.getEmail(),"Количество приобретенных вами путевок: "
//                //        +byRequest.getMinusCount()
//                //        +"\nОписание путевки: \nИз: "+tour.getStart()+"\nВ: "+tour.getFinish()+"\nДата: "+tour.getDate()+"\nОбщая стоимость: "+tour.getPrice()*byRequest.getMinusCount()+" рублей");
//                System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
//                boolean exist=false;
//                for (Tour t :userRepository.findById(user.getId()).get().getTours()) {
//                    if (t.getId().equals(tour.getId())) exist=true;
//                    System.out.println(t.getId());
//                }
//                System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
//                if(!exist) {
//                    Set<Tour> tourSet = user.getTours();
//                    tourSet.add(tour);
//                    user.setTours(tourSet);
//                    userRepository.save(user);
//                }
//                return "redirect:/booking";
//            }
//            else {
//                model.addAttribute("ByError", "ты когда-нибудь получал чипалах со скоростью света?");
//                return "viewingTour";
//            }
//        }
//    }
//
//    @GetMapping("/booking/view")
//    public String initView(Model model){
//        model.addAttribute("ByForm",new ByRequest());
//        return "viewingTour";
//    }
}
