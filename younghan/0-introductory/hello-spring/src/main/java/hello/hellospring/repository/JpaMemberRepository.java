package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository {

    // JPA는 EntityManager라는 걸로 모든 게 동작을 한다.
    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        // Gradle 통해 JPA 라이브러리 받았으니, 스프링 (부트)가 자동으로 EntityManager라는 걸 생성해줌.
        // 현재 DB랑 다 연결해줌. 만들어진 걸 우리는 인젝션 받으면 된다.
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    // PK 아니면 조회하는데 jpql 사용해야함
    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        // Member m: Member as m
        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();
        return result;
    }
}
