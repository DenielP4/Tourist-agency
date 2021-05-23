package com.boots.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class EditRequest {
    Long id;
    String start;
    String finish;
    String Date;
    int price;
    int count;
    String img;
    String text;

    public EditRequest() {
    }
}
