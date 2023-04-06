package com.txt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.txt.domain.Approval;
import com.txt.domain.Article;
import com.txt.service.ArticleWorkflowService;

@RestController
public class ArticleWorkflowController {

    @Autowired
    private ArticleWorkflowService service;

    @PostMapping("/submit")
    public void submit(@RequestBody Article article) {
        service.startProcess(article);
    }

    @GetMapping("/tasks")
    public List<Article> getTasks(@RequestParam String assignee) {
        return service.getTasks(assignee);
    }

    @PostMapping("/review")
    public void review(@RequestBody Approval approval) {
        service.submitReview(approval);
    }

}
