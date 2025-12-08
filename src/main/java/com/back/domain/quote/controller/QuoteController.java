package com.back.domain.quote.controller;

import com.back.domain.quote.entity.Quote;
import com.back.domain.quote.service.QuoteService;

import java.util.List;
import java.util.Scanner;

public class QuoteController {
    private final Scanner scanner;
    private final QuoteService quoteService;

    public QuoteController(Scanner scanner) {
        this.scanner = scanner;
        this.quoteService = new QuoteService();
    }


    public void actionWrite() {
        System.out.print("명언 : ");
        String content = scanner.nextLine().trim();

        System.out.print("작가 : ");
        String author = scanner.nextLine().trim();

        Quote q = quoteService.write(content, author);

        System.out.println(q.id + "번 명언이 등록되었습니다.");
    }


    public void actionList() {
        List<Quote> list = quoteService.findAll();
        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");
        // 단순히 저장 순서대로 출력 (원하면 역순으로 바꿀 수 있음)
        for (Quote q : list) {
            System.out.println(q.id + " / " + q.author + " / " + q.content);
        }
    }

    public void actionDelete() {
        while (true) {
            System.out.print("삭제할 명언의 번호를 입력) ");
            int num = Integer.parseInt(scanner.nextLine().trim());

            boolean ok = quoteService.delete(num);

            if (ok) {
                System.out.println(num + "번 명언이 삭제되었습니다.");
                break;
            } else {
                System.out.println(num + "번 명언은 존재하지 않습니다.");
            }
        }
    }

    public void actionModify() {
        while (true) {
            System.out.print("수정할 명언의 번호를 입력) ");
            int num = Integer.parseInt(scanner.nextLine().trim());

            // 기존 내용 보여줘야 하니까, service에서 findAll()으로 찾거나,
            // 별도 findById 메서드를 만들어도 됨
            Quote target = quoteService.findAll().stream()
                    .filter(q -> q.id == num)
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                System.out.println(num + "번 명언은 존재하지 않습니다.");
                continue;
            }

            System.out.println("명언(기존) : " + target.content);
            System.out.print("새 명언) ");
            String newContent = scanner.nextLine().trim();

            System.out.println("작가(기존) : " + target.author);
            System.out.print("새 작가) ");
            String newAuthor = scanner.nextLine().trim();

            quoteService.modify(num, newContent, newAuthor);
            System.out.println("수정이 완료되었습니다.");
            break;
        }
    }

    public void actionBuild() {
        quoteService.build();
        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }
}
