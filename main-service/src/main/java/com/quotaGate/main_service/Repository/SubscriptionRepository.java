package com.quotaGate.main_service.Repository;

import com.quotaGate.main_service.Domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    public Optional<Subscription> findByName(String name);
    public List<Subscription> findAllByOrderByNoOfAllowedRequest();
}
