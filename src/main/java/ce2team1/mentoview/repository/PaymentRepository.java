package ce2team1.mentoview.repository;

import ce2team1.mentoview.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}