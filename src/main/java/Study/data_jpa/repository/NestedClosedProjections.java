package Study.data_jpa.repository;

/**
 * 중첩 구조 처리
 * **주의**
 * 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
 * 프로젝션 대상이 ROOT가 아니면
 *  -LEFT OUTER JOIN 처리.
 *  -모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
 *
 * **정리**
 *  -프로젝션 대상이 root 엔티티면 유용하다.
 *  -프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다! (member는 딱 username만 검색하지만 team은 다 끌어와서 조회함.)
 *  -실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
 *  -실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자.
 */
public interface NestedClosedProjections {

    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}
