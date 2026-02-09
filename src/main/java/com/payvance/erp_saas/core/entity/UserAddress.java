package com.payvance.erp_saas.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "house_building_no")
    private String houseBuildingNo;

    @Column(name = "house_building_name")
    private String houseBuildingName;

    @Column(name = "road_area_place")
    private String roadAreaPlace;

    private String landmark;
    private String village;
    private String taluka;
    private String city;
    private String district;
    private String state;
    private String pincode;

    @Column(name = "post_office")
    private String postOffice;

    private String country;
}
