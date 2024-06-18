package Study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> { // 직접 id를 만들어 줄 때 쓰는 방법.

//    @Id
//    @GeneratedValue // persist 해야 id 값이 들어감.
//    private Long id;

    @Id // @GeneratedValue 못 쓸때 merge()로 넘어가버림.(기본 자료형이면 빈 값일 때 null로 들어가서 값이 있다고 판단돼서 else{}로 넘어가기 때문)
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    // @GeneratedValue 안 쓰고 직접 id 만들 때, 원랜 getId()도 있지만 @Getter 때문에 생략됨.
    @Override
    public boolean isNew() {
        return createdDate == null; // @CreatedDate에 값이 없으면 새로운 엔티티로 판단
    }

}
