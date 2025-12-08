package com.back;

import com.back.domain.quote.controller.QuoteController;
import com.back.domain.quote.entity.Quote;
import com.back.domain.system.controller.SystemController;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    Scanner sc = new Scanner(System.in);
    public void run() {

        SystemController systemController = new SystemController();
        QuoteController quoteController =  new QuoteController(sc);

        System.out.println("== 명언 앱 ==");

        while (true) {
            System.out.print("명령) ");
            String command = sc.nextLine().trim();

            switch (command) {
                case "종료":
                    systemController.actionExit();
                    return;

                case "등록":
                    quoteController.actionWrite();
                    break;

                case "목록":
                    quoteController.actionList();
                    break;

                case "삭제":
                    quoteController.actionDelete();
                    break;

                case "수정":
                    quoteController.actionModify();
                    break;

                case "빌드":
                    quoteController.actionBuild();
                    break;

            }
        }
    }
}
