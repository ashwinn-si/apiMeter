package com.quotaGate.usage_service.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Long noOfAllowedRequest;

    @OneToMany(mappedBy = "subscription")
    private List<User> userList = new ArrayList<>();

    public Subscription(String name, Long noOfAllowedRequest){
        this.name = name;
        this.noOfAllowedRequest = noOfAllowedRequest;
    }
}
