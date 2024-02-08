package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;

import java.util.Optional;

public class MemberService {
    private final MemberRepository memberRepository = new MemoryMemberRepository();


    public long join(Member member){
        //같은 이름이 있는 중복회원 X
        /*1. optional을 바로 반환하는 방법 별로 권장 X*/
        Optional<Member> result = memberRepository.findByName(member.getName());
//      Member mem = result.get(); //orElseGet 함수도 사용가능
        result.ifPresent(m ->{//optional이기 때문에 할 수 있는 것
            throw new IllegalStateException("이미 존재하는 회원입니다");
        } );
        /*2. 한번에 하는 방법*/
        memberRepository.findByName(member.getName())
                .ifPresent(m->{
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
        memberRepository.save(member);
        return member.getId();
    }
}
