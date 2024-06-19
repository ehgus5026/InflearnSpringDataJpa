package Study.data_jpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username + ' ' + target.age}") // Open Proejections : 단! 이렇게 Spl 문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산한다! 따라서 JPQL SELECT 절 최적화가 안된다.
    String getUsername(); // Closed Projections : 조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection).
}
