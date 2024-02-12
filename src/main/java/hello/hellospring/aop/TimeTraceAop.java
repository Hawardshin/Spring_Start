package hello.hellospring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

//aop는 Aspect 꼭 적어줘야함
//위에 어노테이션 @Component로 써도 되지만 Spring Bean에 등록하는 걸 더 선호하긴 한다.
@Aspect
public class TimeTraceAop {
    @Around("execution(* hello.hellospring..*(..)) && !target(hello.hellospring.SpringConfig)")
    public Object exectue(ProceedingJoinPoint joinPoint) throws Throwable{
        //joinPoint 에 다양한 인자가 있으니 활용해서 보면 된다.
        //중간에 인터셉트도 가능
        long start = System.currentTimeMillis();
        System.out.println("START: " + joinPoint.toString());

        try{
            //이렇게 하면 다음 메서드로 넘어갑니다.
            return joinPoint.proceed();
        }finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms");

        }
    }
}
