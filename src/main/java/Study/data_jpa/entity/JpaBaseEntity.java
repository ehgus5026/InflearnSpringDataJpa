package Study.data_jpa.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

// 순수 JPA로 Auditing 설정하기.
// 중복되는 컬럼(속성)만을 갖고 받고 싶을 때 (상속 개념이 아님)
// 이 컬럼들을 상속 받을 클래스에 extends 해주고, 추상 클래스에 @MappedSuperclass 해주면 됨
@MappedSuperclass
@Getter
public abstract class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now; // null이 아니고 값을 넣어놔야 나중에 쿼리 날릴 때가 편해짐.
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }


}
