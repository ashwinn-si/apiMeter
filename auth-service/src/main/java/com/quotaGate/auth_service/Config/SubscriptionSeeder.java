package com.quotaGate.auth_service.Config;

import com.quotaGate.auth_service.Domain.Subscription;
import com.quotaGate.auth_service.Enums.LOG_TYPE;
import com.quotaGate.auth_service.Repository.SubscriptionRepository;
import com.quotaGate.auth_service.Utils.AppLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SubscriptionSeeder {
    private HashMap<String, Subscription> subscriptionHashMap;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    SubscriptionSeeder(){
        subscriptionHashMap = new HashMap<>();
        loadData();
    }

    public void seed(){
        for(String subscriptionName: subscriptionHashMap.keySet()){
            if(subscriptionRepository.findByName(subscriptionName).isEmpty()){
                AppLogger.log(LOG_TYPE.INFO, "Subscription Service", subscriptionName + " seeded");
                subscriptionRepository.save(subscriptionHashMap.get(subscriptionName));
                continue;
            }
            AppLogger.log(LOG_TYPE.INFO, "Subscription Service", subscriptionName + " already exists");
        }
    }

    private void loadData(){
        subscriptionHashMap.put("NORMAL", new Subscription("NORMAL", 10L));
        subscriptionHashMap.put("PREMIUM", new Subscription("PREMIUM", 20L));
        subscriptionHashMap.put("ULTRA", new Subscription("ULTRA", 50L));
    }
}
