// 2. 스프링 빈에 직접 등록 (@Component | Repository | Service 없이)

package hello.hellospring;

import hello.hellospring.aop.TimeTraceAop;
import hello.hellospring.repository.*;
import hello.hellospring.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private final MemberRepository memberRepository;

    //@Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // JPA
    /*private EntityManager em;
    @Autowired
    public SpringConfig(EntityManager em) {
        this.em = em;
    }*/

    // JPA 쓸거니까 안 쓴다.
    /*private DataSource dataSource;
    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }*/

    @Bean   // bean에 등록하겠다.
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

    // 인터페이스를 통해 다형성!!
    // 스프링이 이걸 굉장히 편리하게 되도록 스프링 컨테이너가 이걸 지원해주는 것!
    // 그리고 dependency injection이라는 것 덕분에 그걸 편리하게 해주는 것
    /*@Bean
    public MemberRepository memberRepository() {
        //return new MemoryMemberRepository();
        //return new JdbcMemberRepository(dataSource);
        //return new JdbcTemplateMemberRepository(dataSource);
        return new JpaMemberRepository(em);
    }*/

    /*@Bean
    public TimeTraceAop timeTraceAop() {
        return new TimeTraceAop();
    }*/
}
