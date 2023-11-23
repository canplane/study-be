package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {

    // findByName이라 하면, 뒤에 있는 Name으로 아래와 같은 jpql 짜줌
    // select m from Member m where m.name = ?
    // -> 80%는 인터페이스만 짜도 개발이 다 된 것!
    @Override
    Optional<Member> findByName(String name);
}
