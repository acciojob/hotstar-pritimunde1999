package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        int finalAmount =0;

        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            finalAmount = 500 + 200*subscriptionEntryDto.getNoOfScreensRequired();
        }
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
        {
            finalAmount = 800 + 250*subscriptionEntryDto.getNoOfScreensRequired();
        }
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE))
        {
            finalAmount = 1000 + 350*subscriptionEntryDto.getNoOfScreensRequired();
        }
        subscription.setTotalAmountPaid(finalAmount);
        user.setSubscription(subscription);
        userRepository.save(user);
        return finalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        int fareNeedToPay =0;
        if(user.getSubscription().equals(SubscriptionType.ELITE))
        {
            throw new Exception("Already the best Subscription");
        }

        if(user.getSubscription().equals(SubscriptionType.PRO)) {
            fareNeedToPay= (1000+ 350*user.getSubscription().getNoOfScreensSubscribed()) - user.getSubscription().getTotalAmountPaid();
        }
        if(user.getSubscription().equals(SubscriptionType.BASIC)) {
            fareNeedToPay= (800+ 250*user.getSubscription().getNoOfScreensSubscribed()) - user.getSubscription().getTotalAmountPaid();
        }
       return fareNeedToPay;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptions = subscriptionRepository.findAll();

        int totalRevenue = 0;
        for(Subscription subscription :subscriptions)
        {
            totalRevenue+=subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
