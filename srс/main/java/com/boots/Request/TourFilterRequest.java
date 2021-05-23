package com.boots.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TourFilterRequest {
    String start;
    String finish;
    String date;
    int count;
}
