package com.project.autonomous.picture.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Picture {

//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Id
    private String id;

    private String name;

    private String type;

    private String imageUrl;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Picture of(String id, String name, String type, String imageUrl){
        return Picture.builder()
            .id(id)
            .name(name)
            .type(type)
            .imageUrl(imageUrl)
            .build();
    }
}
