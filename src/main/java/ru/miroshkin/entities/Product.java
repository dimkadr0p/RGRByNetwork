package ru.miroshkin.entities;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Product {
    private String name;
    private Integer price;
    private String url;
    private String photo;
}
