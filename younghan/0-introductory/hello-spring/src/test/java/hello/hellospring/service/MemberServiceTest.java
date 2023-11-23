// 단위 테스트: 순수하게 java 코드로 하면서 최소한의 단위로 하는 거
// 스프링 컨테이너와 db까지 연동하고 그런 걸 통합 테스트라 표현함.
// 가급적이면 순수한 유닛 테스트가 좋은 테스트일 확률 높음
// 어쩔 수 없이 테스트 시에 스프링까지 올려야 하고 그런 통합 테스트 경우도 있겠지만 그런 경우는 좋은 테스트가 아닐 확률이 높음

package hello.hellospring.service;

import hello.hellospring.domain.Member;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import hello.hellospring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.*;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
        // repo를 외부에서 넣어주는 것: 의존관계 설정: dependency injection (DI)
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("hello");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

        /*try {
            memberService.join(member2);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.123123");
        }*/

        // then
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}