package Study.data_jpa.repository;


import Study.data_jpa.dto.MemberDto;
import Study.data_jpa.entity.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 매우 중요 !!!
 * save() 메서드
 * 새로운 엔티티면 저장(persist), 새로운 엔티티가 아니면 병합(merge) -> DB에 있는 데이터를 가져와서 save한 데이터로 바꿔치기해서 넣기.(merge는 업데이트, 세이브할 때 쓰지 말자. 변경은 엔티티의 값을 변경해서 변경 감지가 일어나서 바뀌는 거로 쓰자.)
 * 새로운 엔티티를 구별하는 방법
 *  -새로운 엔티티를 판단하는 기본 전략
 *  -식별자가 객체일 때 `null` 로 판단
 *  -식별자가 자바 기본 타입일 때 0 으로 판단
 *  -Persistable 인터페이스를 구현해서 판단 로직 변경 가능
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
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
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional (데이터가 있을 수도, 없을 수도 있을 때 null을 감싸고 싶으면 -> Optional.empty)

    /**
     * 페이징과 정렬 파라미터
     * org.springframework.data.domain.Sort : 정렬 기능
     * org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
     *
     * 특별한 반환 타입
     * org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
     * org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit +1 조회)
     * List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
     *
     * 페이징과 정렬 사용 예제
     * Page<Member> findByUsername(String name, Pageable pageable); // count 쿼리 사용
     * Slice<Member> findByUsername(String name, Pageable pageable); // count 쿼리 사용 안함
     * List<Member> findByUsername(String name, Pageable pageable); // count 쿼리 사용 안함
     * List<Member> findByUsername(String name, Sort sort);
     */
    // 반환 타입에 따라 getTotalCount를 날릴 지 안 날릴 지 결정함.
//    Page<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByAge(int age, Pageable pageable); // Slice (count X) 추가로 limit +1을 조회한다. 그래서 다음 페이지 여부 확인(최근 모바일 리스트 생각해보면 됨)

    // count 쿼리를 다음과 같이 분리할 수 있음.
    // (이건 복잡한 sql에서 사용, 데이터는 left join, 카운트는 left join 안해도 됨)
    // 실무에서 매우 중요!!!, 참고: 전체 count 쿼리는 매우 무겁다.
    // 스프링 부트 3 이상을 사용하면 하이버네이트 6이 적용된다.
    // 이 경우 하이버네이트 6에서 의미없는 left join을 최적화 해버린다. 따라서
    // 다음을 실행하면 SQL이 LEFT JOIN을 하지 않는 것으로 보인다. (@Query(value = "select m from Member m left join m.team t")
    /**
     * 하이버네이트 6은 이런 경우 왜 left join을 제거하는 최적화를 할까? 실행한 JPQL을 보면 left join을 사용하고 있다.
     * select m from Member m left join m.team t
     * Member 와 Team 을 조인을 하지만 사실 이 쿼리를 Team 을 전혀 사용하지 않는다. select 절이나, where 절에서
     * 사용하지 않는 다는 뜻이다. 그렇다면 이 JPQL은 사실상 다음과 같다.
     * select m from Member m
     * left join 이기 때문에 왼쪽에 있는 member 자체를 다 조회한다는 뜻이 된다.
     * 만약 select 나, where 에 team 의 조건이 들어간다면 정상적인 join 문이 보인다. JPA는 이 경우 최적화를 해서 join 없이 해당 내용만으로 SQL을 만든다.
     * 여기서 만약 Member 와 Team 을 하나의 SQL로 한번에 조회하고 싶으시다면 JPA가 제공하는 fetch join 을 사용 해야한다.
     * select m from Member m left join fetch m.team t 이 경우에도 SQL에서 join문은 정상 수행된다.
     */
    @Query(value = "select m from Member m",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트 초기화: @Modifying(clearAutomatically = true)
     * (이 옵션의 기본값은 false)
     * 이 옵션 없이 회원을 findById 로 다시 조회하면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수 있다.
     * 만약 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화 하자.
     * save, update는 디비로 먼저 쿼리 쳐줌.
     */
    @Modifying(clearAutomatically = true) // 이걸 해줘야 JPA의 executeUpdate() 같은 기능을 해줌. 안 해주면 resultList나 singleResult 이런걸 호출함.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * Entity Graph
     * 페치 조인 하려면 JPQL을 작성해야 하는데 Spring Data JPA 에서는 편리하게 할 수 있음.
     */
    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) // JPQL 안 쓰고 페치 조인 쓰기
    List<Member> findAll();

    // JPQL + 엔티티그래프
    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraph();

    // 메서드 이름으로 쿼리에서 특히 편리하다.
//    @EntityGraph("Member.all") // NamedEntityGraph
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * JPA Hint
     * JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
     * forCounting : 반환 타입으로 Page 인터페이스를 적용하면
     * 추가로 호출하는 페이징을 위한 count 쿼리도 쿼리 힌트 적용(기본값 true)
     */
    /*@QueryHints(value = { @QueryHint(name = "org.hibernate.readOnly",
            value = "true")},
            forCounting = true)
    Page<Member> findByUsername(String name, Pageable pageable);*/

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * LOCK
     * select for update
     * 실시간 트래픽이 많은 서비스에서 가급적이면 lock을 걸면 안됨.
     * 실시간 트래픽이 많지 않고, 중요한 돈을 맞추는 이런 경우는 좋을 수도.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);


}

