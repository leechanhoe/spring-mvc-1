package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller // 뷰 이름이 반환이된다 - 뷰리졸버찾음
@Slf4j // = private final Logger log = LoggerFactory.getLogger(getClass()); 롬복이 자동으로 해줌
@RestController // 스트링이 그대로 반환
public class LogTestController {
    //private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        System.out.println("name = " + name);

        log.trace("trace log={}", name);
        log.debug("debug log={}", name); // 개발 서버에서 보는 디버그 정보
        log.info(" info log={}", name); // 중요한 정보
        log.warn(" warn log={}", name); // 경고
        log.error("error log={}", name); // 에러

        return "ok";
    }
}
