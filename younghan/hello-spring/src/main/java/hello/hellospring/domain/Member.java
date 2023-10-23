// with JPA

package hello.hellospring.domain;

import javax.persistence.*;

@Entity // 이제 JPA가 관리하는 entity가 된다
public class Member {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // DB가 아이디 자동 생성해주는(auto_increment) 그런 느낌의 방식을 아이덴티티 전략이라 함
    private Long id;

    //@Column(name = "username")
    private String name;

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
