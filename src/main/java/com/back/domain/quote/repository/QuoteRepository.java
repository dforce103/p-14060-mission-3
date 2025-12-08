package com.back.domain.quote.repository;

import com.back.domain.quote.entity.Quote;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

public class QuoteRepository {
    // 파일 저장 위치: 프로젝트 루트 기준 db/quote
    private static final String DB_DIR = "db/quote";

    // ────────────────────── DB 관련 유틸 메서드들 ──────────────────────

    // db/quote 디렉토리 생성
    private static void initDbDir() {
        try {
            Files.createDirectories(Paths.get(DB_DIR));
        } catch (IOException e) {
            throw new RuntimeException("DB 폴더 생성 실패: " + DB_DIR, e);
        }
    }

    // lastId.txt 읽어서 "다음에 사용할 id" 리턴
    public static int loadLastId() {
        Path lastIdPath = Paths.get(DB_DIR, "lastId.txt");

        if (!Files.exists(lastIdPath)) {
            return 1; // 첫 실행이면 1번부터 시작
        }

        try {
            String txt = Files.readString(lastIdPath).trim();
            if (txt.isEmpty()) return 1;
            int lastId = Integer.parseInt(txt);
            return lastId + 1;      // 마지막 id 다음 번호
        } catch (IOException e) {
            throw new RuntimeException("lastId 읽기 실패", e);
        }
    }

    // 현재까지 사용한 마지막 id를 lastId.txt에 저장
    public static void saveLastId(int id) {
        Path lastIdPath = Paths.get(DB_DIR, "lastId.txt");
        try {
            Files.writeString(lastIdPath, String.valueOf(id));
        } catch (IOException e) {
            throw new RuntimeException("lastId 저장 실패", e);
        }
    }

    // Quote 하나를 {id}.json 으로 저장
    private static void saveQuoteToFile(Quote q) {
        Path path = Paths.get(DB_DIR, q.id + ".json");

        String json = "{\n" +
                "  \"id\": " + q.id + ",\n" +
                "  \"content\": \"" + q.content + "\",\n" +
                "  \"author\": \"" + q.author + "\"\n" +
                "}";

        try {
            Files.writeString(path, json);
        } catch (IOException e) {
            throw new RuntimeException(q.id + "번 명언 저장 실패", e);
        }
    }

    // {id}.json 파일 삭제
    private static void deleteQuoteFile(int id) {
        Path path = Paths.get(DB_DIR, id + ".json");
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(id + "번 명언 파일 삭제 실패", e);
        }
    }

    // db/quote 폴더의 모든 json을 읽어서 databox에 채우기
    private static void loadQuotes(List<Quote> databox) {
        Path dir = Paths.get(DB_DIR);

        if (!Files.exists(dir)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
            for (Path path : stream) {
                List<String> lines = Files.readAllLines(path);
                Quote q = parseQuoteFromJson(lines);
                if (q != null) {
                    databox.add(q);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("명언 불러오기 실패", e);
        }
    }

    // json 파일 내용을 Quote 객체로 파싱
    private static Quote parseQuoteFromJson(List<String> lines) {
        int id = 0;
        String content = "";
        String author = "";

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("\"id\"")) {
                id = Integer.parseInt(line.replaceAll("[^0-9]", ""));
            } else if (line.startsWith("\"content\"")) {
                // "content": "문장", 형태에서 값만 추출
                content = line.split(":", 2)[1].trim();
                content = content.replaceFirst("^\"", "")
                        .replaceFirst("\",$", "")
                        .replace("\\\"", "\"");
            } else if (line.startsWith("\"author\"")) {
                author = line.split(":", 2)[1].trim();
                author = author.replaceFirst("^\"", "")
                        .replaceFirst("\"$", "")
                        .replace("\\\"", "\"");
            }
        }

        if (id == 0) return null;
        return new Quote(id, content, author);
    }

    // 전체 명언 목록(databox)을 하나로 모아서 프로젝트 루트에 data.json 생성
    public static void buildDataJson(List<Quote> databox) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < databox.size(); i++) {
            Quote q = databox.get(i);

            sb.append("  {\n");
            sb.append("    \"id\": ").append(q.id).append(",\n");
            sb.append("    \"content\": \"").append(q.content.replace("\"", "\\\"")).append("\",\n");
            sb.append("    \"author\": \"").append(q.author.replace("\"", "\\\"")).append("\"\n");
            sb.append("  }");

            if (i < databox.size() - 1) {
                sb.append(",\n");   // 마지막이 아니면 콤마
            } else {
                sb.append("\n");
            }
        }

        sb.append("]");

        Path path = Paths.get("data.json"); // 🔑 프로젝트 루트에 data.json 생성

        try {
            Files.writeString(path, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("data.json 생성 실패", e);
        }
    }

    public Collection<? extends Quote> findAll() {
        return List.of();
    }

    public void save(Quote q) {
    }

    public void deleteById(int id) {
    }
}
