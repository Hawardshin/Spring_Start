package hello.hellospring.domain;

import jakarta.persistence.*;

@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//시스템이 정하는 아이디
//    @Column(name = "username") //colume이 name이 아니라 username이면 이렇게 바꿀 수 있다.
    private String name;//고객이 회원가입할 때 적는 이름

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
