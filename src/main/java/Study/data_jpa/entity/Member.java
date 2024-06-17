package Study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 엔티티는 JPA 스펙상 기본 생성자가 있어야 함.
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    public Member(String username) {
        this.username = username;
    }

    // @Setter 말고 이런 식으로
    /*public void changeUsername(String username) {
        this.username = username;
    }*/


}
