package Study.data_jpa.repository;

/**
 * **클래스 기반 Projection**
 * 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능
 */
public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) { // 생성자의 파라미터 명을 따라서 분석해서 매칭.
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

}
