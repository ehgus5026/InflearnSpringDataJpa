package Study.data_jpa.repository;

import Study.data_jpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 사용자 정의 리포지토리 구현 (보통 핵심 비즈니스 로직 리포지토리와 화면에 보여주기 위한 리포지토리랑 쪼갬.)
 * 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
 * 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
 * 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
 *  -JPA 직접 사용(EntityManager)
 *  -스프링 JDBC Template 사용 MyBatis 사용
 *  -데이터베이스 커넥션 직접 사용 등등...
 *  -Querydsl 사용
 * 참고: 실무에서는 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때 사용자 정의 리포지토리 기능 자주 사용
 * 참고: 항상 사용자 정의 리포지토리가 필요한 것은 아니다.
 * 그냥 임의의 리포지토리를 만들어도 된다. 예를들어 MemberQueryRepository를 인터페이스가 아닌 클래스로 만들고
 * 스프링 빈으로 등록해서 그냥 직접 사용해도 된다. 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다.
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }



}
