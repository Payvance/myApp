package com.payvance.erp_saas.core.service;

import com.payvance.erp_saas.core.entity.UserAddress;
import com.payvance.erp_saas.core.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
// Service for managing user addresses including upserting address by userId
@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserAddressRepository userAddressRepository;

    /**
     * Upsert address by userId
     */
    @Transactional
    public UserAddress upsertAddress(Long userId, UserAddress address) {
        Optional<UserAddress> existing = userAddressRepository.findByUserId(userId);

        if (existing.isPresent()) {
            UserAddress addr = existing.get();
            copyAddress(address, addr);
            return userAddressRepository.save(addr);
        } else {
            address.setUserId(userId);
            return userAddressRepository.save(address);
        }
    }

    private void copyAddress(UserAddress source, UserAddress target) {
        target.setHouseBuildingNo(source.getHouseBuildingNo());
        target.setHouseBuildingName(source.getHouseBuildingName());
        target.setRoadAreaPlace(source.getRoadAreaPlace());
        target.setLandmark(source.getLandmark());
        target.setVillage(source.getVillage());
        target.setTaluka(source.getTaluka());
        target.setCity(source.getCity());
        target.setDistrict(source.getDistrict());
        target.setState(source.getState());
        target.setPincode(source.getPincode());
        target.setPostOffice(source.getPostOffice());
        target.setCountry(source.getCountry());
    }
}
