package com.boots.Controller;

import com.boots.Entity.Tour;
import com.boots.Entity.TourDescription;
import com.boots.Request.DeleteRequest;
import com.boots.Request.EditRequest;
import com.boots.Request.TourCreateRequest;
import com.boots.Request.TourFilterRequest;
import com.boots.Service.TourDescriptionService;
import com.boots.Service.TourFilter;
import com.boots.Service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@Controller
public class AdminTourController {
    @Autowired
    TourService tourService;

    @Autowired
    TourDescriptionService tourDescriptionService;

    @Autowired
    TourFilter tourFilter;

    @GetMapping("/admin/add")
    public String initAdd(Model model) {
        model.addAttribute("tourForm", new TourCreateRequest());
        return "create";
    }

    @PostMapping("/admin/add")
    public String addTour(@ModelAttribute("tourForm") @Valid TourCreateRequest tourForm, BindingResult bindingResult, Model model) throws ParseException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tourError", "Убвунгве убвумумвге осас");
            return "create";
        }

        Tour tour = new Tour();
        tour.setStart(tourForm.getStart());
        tour.setFinish(tourForm.getFinish());
        tour.setPrice(tourForm.getPrice());
        tour.setDate(tourForm.getDate());
        tour.setCount(tourForm.getCount());

        TourDescription tourDescription = new TourDescription();
        tourDescription.setImg(tourDescriptionService.parse(tourForm.getImg()));
        tourDescription.setText(tourDescriptionService.parse(tourForm.getText()));

        tourDescriptionService.add(tourDescription);
        tour.setTourDescription(tourDescription);

        if (!tourService.addTour(tour)){
            model.addAttribute("tourError", "Некорректный ввод данных");
            tourDescriptionService.delete(tourDescription);
            return "create";
        }
        else {
            tourDescription.setTour(tour);
            tourDescriptionService.add(tourDescription);
            return "redirect:/admin/add";
        }
    }

    @GetMapping("/admin/find")
    public String initFind(Model model){
        model.addAttribute("tourFilterForm",new TourFilterRequest());
        model.addAttribute("FilteredTours",tourService.getAll());
        return "findTour";
    }

    @PostMapping("/admin/find")
    public String findTour(@ModelAttribute("tourFilterForm") @Valid TourFilterRequest tourFilterForm,
                           Long tourId,
                           @RequestParam String action,
                           BindingResult bindingResult,
                           Model model){
        if(action.equals("filter")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("filterError", "Убвунгве убвумумвге осас");
                return "findTour";
            }
            else if(tourFilterForm.getStart().equals("")||tourFilterForm.getFinish().equals("")){
                model.addAttribute("filterError","Укажите место отправки и место назначения");
                return "findTour";
            }
            model.addAttribute("FilteredTours", tourFilter.filterTour(tourFilterForm.getStart(), tourFilterForm.getFinish(), tourFilterForm.getDate(), tourFilterForm.getCount()));
            return "findTour";
        }
        else{
            return "redirect:/admin/find/view/"+tourId;
        }
    }

    @GetMapping("/admin/find/view/{tourId}")
    public String viewTour(@PathVariable Long tourId, Model model){
        Tour tour = tourService.findById(tourId);
        if(tour!=null){
            model.addAttribute("users",tour.getUsers());
            return "userWithTour";
        }
        else{
            return "redirect:/admin/find";
        }
    }


    @GetMapping("/admin/edit")
    public String initEdit(Model model) {
        model.addAttribute("editForm", new EditRequest());
        return "editTour";
    }

    @PostMapping("/admin/edit")
    public String editTour(@ModelAttribute("editForm") @Valid EditRequest editForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editError", "Убвунгве убвумумвге осас");
            return "editTour";
        }
        if (!tourService.editTour(editForm)){
            model.addAttribute("editError","Некорректный ввод данных");
        }
        else{
            model.addAttribute("editError","Запись изменена");
        }
        return "editTour";
    }

    @GetMapping("/admin/delete")
    public String initDel(Model model) {
        model.addAttribute("deleteForm", new DeleteRequest());
        return "deleteTour";
    }

    @PostMapping("/admin/delete")
    public String deleteTour(@ModelAttribute("deleteForm") @Valid DeleteRequest deleteForm, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("deleteError", "Not found");
            return "deleteTour";
        }
        Tour tour = tourService.deleteTour(deleteForm.id);
        if (tour!=null){
            model.addAttribute("deletedTour","ID = "+tour.getId()
                    +" From = " +tour.getStart()
                    +" To = " +tour.getFinish()
                    +" Date = " +tour.getDate()
                    +" Price = " +tour.getPrice()
                    +" Count = " +tour.getCount());
        }
        else model.addAttribute("deletedTour","Not found");
        return "deleteTour";
    }


}
