package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin=new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        Admin admin1=adminRepository1.save(admin);
        return  admin1;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.setName(providerName);
        Admin admin =adminRepository1.findById(adminId).get();
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryname) throws Exception{
            countryname=countryname.toUpperCase();
            boolean flag=true;
            for(CountryName name:CountryName.values()){
                if(name.name().equals(countryname)){
                    flag=false;
                    break;
                }
            }
            if(flag) throw new Exception("Country not found");
        Country country=new Country();
        country.setCountryName(CountryName.valueOf(countryname));
        country.setCode(CountryName.valueOf(countryname).toCode());
        ServiceProvider serviceProvider=serviceProviderRepository1.findById(serviceProviderId).get();
        serviceProvider.getCountryList().add(country);
        country.setServiceProvider(serviceProvider);
        serviceProviderRepository1.save(serviceProvider);
        return  serviceProvider;
    }
}
