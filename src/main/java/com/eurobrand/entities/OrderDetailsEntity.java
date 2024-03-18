package com.eurobrand.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "orderDetails")
@Getter
@Setter
public class OrderDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double totalPrice;
    private String note;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String city;
    private String postalCode;
    private String address;
}
