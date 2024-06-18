package Study.data_jpa.dto;


import Study.data_jpa.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
