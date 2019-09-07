package edu.cricket.api.cricketscores.rest.service;

import edu.cricket.api.cricketscores.domain.UserAggregate;
import edu.cricket.api.cricketscores.repository.UserRepository;
import edu.cricket.api.cricketscores.rest.response.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public UserInfo getUserInfoByUserNameAndPassword(String userName, String password){

        UserInfo userInfo = new UserInfo();
        Optional<UserAggregate> userAggregateOptional = userRepository.findById(userName);
        if(userAggregateOptional.isPresent() && userAggregateOptional.get().getUserPassword().equals(password)){
            UserAggregate userAggregate = userAggregateOptional.get();
            userInfo.setUserName(userAggregate.getUserName());
            userInfo.setMobileNumber(userAggregate.getMobileNo());
            userInfo.setStatusCode(200);
            userInfo.setStatus("Success");

        }else {
            userInfo.setStatusCode(200);
            userInfo.setStatus("No such user");

        }

        return userInfo;
    }


    public UserInfo registerUser(String userName, String password, String phone){
        UserInfo userInfo = new UserInfo();
        Optional<UserAggregate> userAggregateOptional = userRepository.findById(userName);
        if(! userAggregateOptional.isPresent()){
            UserAggregate userAggregate = new UserAggregate();
            userAggregate.setUserName(userName);
            userAggregate.setMobileNo(phone);
            userAggregate.setUserPassword(password);
            userRepository.save(userAggregate);
            userInfo.setStatusCode(200);
            userInfo.setStatus("Success");
        }else{
            userInfo.setStatusCode(200);
            userInfo.setStatus("Username already exists");
        }
        return userInfo;

    }
}
