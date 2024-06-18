package Study.data_jpa.repository;

import Study.data_jpa.dto.MemberDto;
import Study.data_jpa.entity.Member;
import Study.data_jpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional(readOnly = true)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    @Transactional
    @Rollback(value = false)
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass()); // Spring Data Jpa가 알아서 구현체 만들어서 프록시 객체 주입 넣어줌.

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @Transactional
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    @Transactional
    public void findUser() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);

        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    @Transactional
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String string : usernameList) {
            System.out.println("string = " + string);
        }
    }

    @Test
    @Transactional
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> usernameList = memberRepository.findMemberDto();
        for (MemberDto dto : usernameList) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    @Transactional
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @Transactional
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("dfadsf");
        System.out.println("aaa2 = " + aaa2); // Optional.empty(null 이면)
    }

    @Test
    @Transactional
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username"); // 0페이지에서 3개 가져오면서 username을 내림차순 정렬
        
        //when
        Page<Member> page1 = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toDto = page1.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
//        Slice<Member> page2 = memberRepository.findByAge(age, pageRequest);

        //then
//        List<Member> content = page1.getContent(); // 페이징한 실제 데이터 꺼내기(조회된 데이터)
//        long totalElements = page1.getTotalElements();
//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);

        // Page
        assertThat(page1.getContent().size()).isEqualTo(3); //조회된 데이터
        assertThat(page1.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page1.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page1.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page1.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page1.hasNext()).isTrue(); //다음 페이지가 있는가?

        // Slice
//        assertThat(page2.getContent().size()).isEqualTo(3); //조회된 데이터
//        assertThat(page2.getNumber()).isEqualTo(0); //페이지 번호
//        assertThat(page2.isFirst()).isTrue(); //첫번째 항목인가?
//        assertThat(page2.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        // 벌크 연산을 때리면 영속성 컨텍스트를 무시하고 디비에 바로 쿼리 날려버림.
        // 그래서 @Modifying(clearAutomatically = true) 이걸 안 키면, 영속성 컨텍스트는 40살로 남아있고, 디비엔 41살로 되어 있음.
        System.out.println("member5 = " + member5);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when N + 1
        // select Member 1
//        List<Member> members = memberRepository.findAll(); // member만 select
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
//        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member = " + member); // member 이미 select 쳐놔서 그냥 가져옴.
            System.out.println("member = " + member.getTeam().getName()); // 이때 team 지연 로딩 걸려 있던 거에서 실데이터 값 가지고 옴.
        }
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void queryHint() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
//        Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); // findReadOnlyByUsername() 에서 읽기 전용으로 해놔서 변경 감지 X

        em.flush();
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void lock() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    @Transactional
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

}