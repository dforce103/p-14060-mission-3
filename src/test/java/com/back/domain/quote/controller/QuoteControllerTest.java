package com.back.domain.quote.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.back.App;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class QuoteControllerTest {
    // 헬퍼 메서드
    private String runAppWithInput(String input) {
        // 기존 표준 입출력 백업
        InputStream defaultIn = System.in;
        PrintStream defaultOut = System.out;

        // 테스트용 입출력 준비
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        PrintStream printOut = new PrintStream(testOut);

        System.setIn(testIn);
        System.setOut(printOut);

        try {
            new App().run();   // 원본 App.run() 그대로 사용
        } catch (Exception e) {
            // 입력이 끝나서 nextLine()에서 예외 나면 여기로 옴
            // 콘솔 앱 테스트에서는 그냥 무시해도 됨
        } finally {
            // 표준 입출력 원복
            System.setIn(defaultIn);
            System.setOut(defaultOut);
        }

        return testOut.toString();
    }

    @Test
    @DisplayName("목록 - 헤더가 출력된다")
    void t2_목록_헤더() {
        String out = runAppWithInput("""
                목록
                종료
                """);

        assertThat(out)
                .contains("번호 / 작가 / 명언")
                .contains("----------------------");
    }

    @Test
    @DisplayName("삭제 - 존재하지 않는 번호를 삭제하면 에러 메시지를 출력한다")
    void t4_삭제_없는번호() {
        String out = runAppWithInput("""
                삭제
                0
                종료
                """);

        assertThat(out)
                .contains("삭제할 명언의 번호를 입력")     // "입력) "까지 정확히 안 맞아도 앞부분만 체크
                .contains("0번 명언은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("수정 - 존재하지 않는 번호를 수정하면 에러 메시지를 출력한다")
    void t5_수정_없는번호() {
        String out = runAppWithInput("""
                수정
                0
                종료
                """);

        assertThat(out)
                .contains("수정할 명언의 번호를 입력")
                .contains("0번 명언은 존재하지 않습니다.");
    }


}
