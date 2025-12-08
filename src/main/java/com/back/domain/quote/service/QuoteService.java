package com.back.domain.quote.service;

import com.back.domain.quote.entity.Quote;
import com.back.domain.quote.repository.QuoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final List<Quote> databox = new ArrayList<>();
    private int lastId;

    public QuoteService() {
        this.quoteRepository = new QuoteRepository();

        // 파일에서 기존 명언 + lastId 불러오기
        this.databox.addAll(quoteRepository.findAll());
        this.lastId = quoteRepository.loadLastId();
    }

    public Quote write(String content, String author) {
        int id = ++lastId;

        Quote q = new Quote(id, content, author);
        databox.add(q);

        quoteRepository.save(q);
        quoteRepository.saveLastId(lastId);

        return q;
    }

    public List<Quote> findAll() {
        return databox;
    }

    public boolean delete(int id) {
        Optional<Quote> opt = databox.stream()
                .filter(q -> q.id == id)
                .findFirst();

        if (opt.isEmpty()) return false;

        databox.remove(opt.get());
        quoteRepository.deleteById(id);

        return true;
    }

    public boolean modify(int id, String newContent, String newAuthor) {
        Optional<Quote> opt = databox.stream()
                .filter(q -> q.id == id)
                .findFirst();

        if (opt.isEmpty()) return false;

        Quote target = opt.get();
        target.content = newContent;
        target.author = newAuthor;

        // 파일도 덮어쓰기
        quoteRepository.save(target);

        return true;
    }

    public void build() {
        quoteRepository.buildDataJson(databox);
    }
}
