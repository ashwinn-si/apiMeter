package com.quotaGate.main_service.Config;

import com.quotaGate.main_service.Enums.LOG_TYPE;
import com.quotaGate.main_service.Utils.AppLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Seeder implements CommandLineRunner {

    @Autowired
    private SubscriptionSeeder subscriptionSeeder;

    @Override
    public void run(String... args) throws Exception {
        AppLogger.log(LOG_TYPE.INFO, "SEEDER", "Starting to Seed Subscription");
        subscriptionSeeder.seed();
        AppLogger.log(LOG_TYPE.INFO, "SEEDER", "End of Seed Subscription");
    }
}
