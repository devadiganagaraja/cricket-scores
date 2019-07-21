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

        UserAggregate userAggregate1 = new UserAggregate();
        userAggregate1.setUserName("nagaraj");
        userAggregate1.setMobileNo("9035969892");
        userAggregate1.setUserPassword("123");
        userRepository.save(userAggregate1);

        UserAggregate userAggregate2 = new UserAggregate();
        userAggregate2.setUserName("abhijith");
        userAggregate2.setMobileNo("9986326165");
        userAggregate2.setUserPassword("123");
        userRepository.save(userAggregate2);

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
}
