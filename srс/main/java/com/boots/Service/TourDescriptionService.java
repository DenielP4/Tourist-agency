package com.boots.Service;

import com.boots.Entity.TourDescription;
import com.boots.Repository.TourDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TourDescriptionService {
    @Autowired
    TourDescriptionRepository tourDescriptionRepository;

    public void add(TourDescription tourDescription){
        tourDescriptionRepository.save(tourDescription);
    }
    
    public String parse(String url){ //Преобразует ссылку с облачного хранилища в формат, который удобен в чтении. Из ссылки вытягивает 13dCjpWflutbu6-9kRlxFd2BJMQAqAE1D и переписывает в нужный формат.
        return "https://drive.google.com/uc?export=view&id="+url.substring(("https://drive.google.com/file/d/").length(),url.length()-"/view?usp=sharing".length());
    }

    public void delete(TourDescription tourDescription){
        tourDescriptionRepository.delete(tourDescription);
    }
}
