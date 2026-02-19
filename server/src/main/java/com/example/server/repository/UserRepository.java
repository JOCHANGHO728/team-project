import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 회원가입 (userdb 추가)
    public int insertUser(UserDto user) {
        String sql = "INSERT INTO userdb (login_id, password, name, p_number) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getLoginId(), user.getPassword(), user.getName(), user.getPNumber());
    }

    // 2. 회원탈퇴 (userdb 삭제)
    public int deleteUser(Long id) {
        String sql = "DELETE FROM userdb WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}