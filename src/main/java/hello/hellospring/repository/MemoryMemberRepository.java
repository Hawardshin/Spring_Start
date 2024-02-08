package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository{
    private  static Map<Long,Member> store = new HashMap<>();//실무에서는 다른 해쉬맵써야함
    private  static long sequence =0L; // 실무에서는 동시성 문제 해결위해 어텀 롱? 으로 해야함
    @Override
    public Member save(Member member) {
         member.setId(++sequence);
         store.put(member.getId(),member);
         return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        //자바의 람다 사용
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();

    }

    @Override
    public List<Member> findAll() {//자바 실무할 때 List 많이 사용합니다.
        return new ArrayList<>(store.values());
    }

    public void clearStore(){
        store.clear();
    }
}
