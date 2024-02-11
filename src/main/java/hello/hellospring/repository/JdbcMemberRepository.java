package hello.hellospring.repository;
import hello.hellospring.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class JdbcMemberRepository implements MemberRepository {
    private final DataSource dataSource;
    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        //여기서도 getConnetion() 을 쓸 수도 있지만..! 더 좋은 방법이 있는데 DataSourceUtils.를 이용해서 하는게 더 좋습니다.
    }
    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";
        //이렇게 저장하는 sql이 있다. (상수로 빼는 걸 추천)
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //이건 결과를 저장하는 겁니다.
        try {
            conn = getConnection(); //연결 가져오고
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //여기서 sql문 넣고
            //Statement.RETURN_GENERATED_KEYS는 디비 insert 해야 1,2,3.. 이런식으로 id를 얻는데 그때 쓰는거다.

            pstmt.setString(1, member.getName());
            //첫번째 변수 즉 value의 ?와 매칭이 돼서, 이름을 넣습니다.

            pstmt.executeUpdate();
            //이걸 실행할 때 쿼리가 날라갑니다.

            rs = pstmt.getGeneratedKeys();
            //이후 만들어진 키를 받을 수 있습니다. (자동으로 증가하는 ID에 해당)
            if (rs.next()) { //값이 있으면 꺼내면 됩니다.
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) { //여러가기 exception을 던지기 때문에 try catch를 잘 해야만 하고 사용했던 자원들을 잘 release 해줘야합니다.
            throw new IllegalStateException(e);
        } finally {
            //반드시 자원 해제해야만 합니다. (db연결은 외부랑 연결된거니까 특히..! ) 큰 장애 발생도 가능,,,
            close(conn, pstmt, rs);
        }
    }
    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();
            //여기서 보면 조회는 업데이트가 아니라 excuteQuery임을 알 수 있다.
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    @Override
    public List<Member> findAll() {
        String sql = "select * from member";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                members.add(member);
            }
            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void clearStore() {

    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    private Connection getConnection() {//Spring Framework를 쓸 때는 반드시 이 방법으로 가져와야만 합니다.
        return DataSourceUtils.getConnection(dataSource);
    }
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void close(Connection conn) throws SQLException {
        //닫을 때도 꼭 DataSourceUtils로 해야만 합니다.
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}