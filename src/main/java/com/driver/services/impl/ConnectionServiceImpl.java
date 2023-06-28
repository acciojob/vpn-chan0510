package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
      User user=userRepository2.findById(userId).get();
      if(user.isConnected())throw  new Exception("Already connected");
      if(user.getCountry().getCountryName().name().equals(countryName)) return  user;
      List<Country> list=new ArrayList<>();
      for(ServiceProvider serviceProvider: user.getServiceProviderList()){
          for(Country country:serviceProvider.getCountryList()){
              if(country.getCountryName().name().equals(countryName)){
                  list.add(country);
                  break;
              }
          }
      }
      if(list.size()==0)throw new Exception("Unable to connect");
        Collections.sort(list,(a,b)->{
            return a.getId()-b.getId();
        });
        ServiceProvider serviceProvider1=new ServiceProvider();
        for(ServiceProvider serviceProvider: user.getServiceProviderList()){
            boolean flag=false;
            for(Country country : serviceProvider.getCountryList()){
                if(country.equals(list.get(0))){
                    serviceProvider1=serviceProvider;
                    flag=true;
                    break;
                }
            }
            if(flag)break;
        }
        user.setConnected(true);
        String code=list.get(0).getCode()+"."+serviceProvider1.getId()+"."+user.getId();
        user.setMaskedIp(code);
        Connection connection=new Connection();
        connection.setServiceProvider(serviceProvider1);
        connection.setUser(user);
        user.getConnectionList().add(connection);
        serviceProvider1.getConnectionList().add(connection);
        serviceProviderRepository2.save(serviceProvider1);
        userRepository2.save(user);
        connectionRepository2.save(connection);
        return  user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user=userRepository2.findById(userId).get();
        if(user.isConnected()==false)throw  new Exception("Already disconnected");
        user.setConnected(false);
        user.setMaskedIp(null);
        for(Connection connection:user.getConnectionList()){
            if(connection.getUser().getId()==userId){
                user.getConnectionList().remove(user.getConnectionList().indexOf(connection));
                break;
            }
        }
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User senderUser=userRepository2.findById(senderId).get();
        User reciverUser=userRepository2.findById(receiverId).get();
        if(reciverUser.isConnected()){
            if(senderUser.isConnected()){
                String senderCode= senderUser.getMaskedIp().substring(0,3);
                String reciverCode=reciverUser.getMaskedIp().substring(0,3);
                if(senderCode.equals(reciverCode))return  senderUser;

                try{
                    senderUser=disconnect(senderId);
                    String name="";
                    for(CountryName countryName:CountryName.values()){
                        if(countryName.toCode().equals(reciverCode)){
                            name=countryName.name();
                            break;
                        }
                    }
                    senderUser=connect(senderId,name);
                    return  senderUser;
                }catch(Exception e){
                    throw new Exception("Cannot establish communication");
                }
            }
            else{
                String reciverCode=reciverUser.getMaskedIp().substring(0,3);
                if(senderUser.getOriginalCountry().getCode().equals(reciverCode))return  senderUser;

                try{
                    String name="";
                    for(CountryName countryName:CountryName.values()){
                        if(countryName.toCode().equals(reciverCode)){
                            name=countryName.name();
                            break;
                        }
                    }
                    senderUser=connect(senderId,name);
                    return  senderUser;
                }catch(Exception e){
                    throw new Exception("Cannot establish communication");
                }
            }
        }
        else{
            String reciverCode=reciverUser.getOriginalIp().substring(0,3);
            if(senderUser.isConnected()){
                String senderCode= senderUser.getMaskedIp().substring(0,3);
                if(senderCode.equals(reciverCode))return  senderUser;

                try{
                    senderUser=disconnect(senderId);
                    String name="";
                    for(CountryName countryName:CountryName.values()){
                        if(countryName.toCode().equals(reciverCode)){
                            name=countryName.name();
                            break;
                        }
                    }
                    senderUser=connect(senderId,name);
                    return  senderUser;
                }catch(Exception e){
                    throw new Exception("Cannot establish communication");
                }
            }
            else{
                if(senderUser.getOriginalCountry().getCode().equals(reciverCode))return  senderUser;

                try{
                    String name="";
                    for(CountryName countryName:CountryName.values()){
                        if(countryName.toCode().equals(reciverCode)){
                            name=countryName.name();
                            break;
                        }
                    }
                    senderUser=connect(senderId,name);
                    return  senderUser;
                }catch(Exception e){
                    throw new Exception("Cannot establish communication");
                }
            }

        }
    }
}
