package Study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 엔티티는 JPA 스펙상 기본 생성자가 있어야 함.
@ToString(of = {"id", "username", "age"})
//@NamedQuery(    // 실무에서 거의 안씀.
//        name="Member.findByUsername", // 아무렇게 줘도 됨, 관례상 이렇게 줬음.
//        query="select m from Member m where m.username = :username")
//@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.members.add(this);
    }

    // @Setter 말고 이런 식으로
    /*public void changeUsername(String username) {
        this.username = username;
    }*/


}
