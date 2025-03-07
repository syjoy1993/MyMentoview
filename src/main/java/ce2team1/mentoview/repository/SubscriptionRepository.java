package ce2team1.mentoview.repository;

import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {


//    @Query("SELECT s FROM Subscription s WHERE s.user.userId = :userId")
//    List<Subscription> findAllByUserId(@Param("userId") Long userId);
//
//    @Query("SELECT s FROM Subscription s WHERE s.user.userId = :userId AND s.status = 'ACTIVE'")
//    Subscription findActiveSubscriptionByUserId(@Param("userId") Long userId);
//
//    @Query("SELECT s FROM Subscription s WHERE s.portonePaymentId = :paymentId")
//    Subscription findByPortonePaymentId(@Param("userId") String paymentId);
//
//    @Query("SELECT s FROM Subscription s WHERE s.status = 'CANCELED' AND s.nextBillingDate = :today")
//    List<Subscription> findByStatusAndNextBillingDate(@Param("today") LocalDate today);

    List<Subscription> findAllByUser_UserId(Long userId);

    Subscription findByUser_UserIdAndStatus(Long userId, SubscriptionStatus status);

    Subscription findByPortonePaymentId(String paymentId);

    List<Subscription> findByStatusAndNextBillingDate(SubscriptionStatus status, LocalDate nextBillingDate);


}