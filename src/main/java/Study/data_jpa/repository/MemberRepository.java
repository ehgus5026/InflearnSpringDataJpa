package Study.data_jpa.repository;


import Study.data_jpa.dto.MemberDto;
import Study.data_jpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * ** 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능 **
     * 조회: find...By, read...By, query...By, get...By
     * 예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.
     * COUNT: count...By 반환타입 long
     * EXISTS: exists...By 반환타입 boolean
     * 삭제: delete...By, remove...By 반환타입 long DISTINCT: findDistinct, findMemberDistinctBy LIMIT: findFirst3, findFirst, findTop, findTop3
     * 보통 간단할 때 사용.
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 스프링 데이터 JPA로 NamedQuery 사용

    /**
     * 스프링 데이터 JPA는 선언한 "도메인 클래스 + .(점) + 메서드 이름"으로 Named 쿼리를 찾아서 실행
     * 만약 실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용한다.
     * 필요하면 전략을 변경할 수 있지만 권장하지 않는다.
     */
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 메서드에 JPQL 쿼리 작성
     * @org.springframework.data.jpa.repository.Query 어노테이션을 사용.
     * 실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라 할 수 있음.
     * JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음(매우 큰 장점!)
     * 참고: 실무에서는 메소드 이름으로 쿼리 생성 기능은 파라미터가 증가하면 메서드 이름이 매우 지저분해진다. 따라서 @Query 기능을 자주 사용하게 된다.
     * 복잡한 정적 쿼리는 이렇게 씀. 동적 쿼리는 QueryDsl로.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO로 조회
    @Query("select new Study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩(자주 쓰임)
    // Collection 타입으로 IN 절 지원
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    /**
     * 스프링 데이터 JPA는 유연한 반환 타입 지원
     * 조회 결과가 많거나 없으면?
     * 컬렉션
     *  -결과 없음: 빈 컬렉션 반환
     * 단건 조회
     *  -결과없음: null 반환
     *  -결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생
     * 참고: 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의 Query.getSingleResult() 메서드를 호출한다.
     * 이 메서드를 호출했을 때 조회 결과가 없으면 javax.persistence.NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히 불편하다.
     * 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null 을 반환한다.
     */
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional (데이터가 있을 수도, 없을 수도 있을 때 null을 감싸고 싶으면)

}
