package Study.data_jpa.dto;


import Study.data_jpa.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC) // MemberRepository에서 동적 네이티브 쿼리 테스트 하려고(428줄)
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    // 엔티티는 DTO를 바라보면 안되지만, DTO는 엔티티를 바라봐도 됨.
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.teamName = member.getTeam().getName();
    }

}
