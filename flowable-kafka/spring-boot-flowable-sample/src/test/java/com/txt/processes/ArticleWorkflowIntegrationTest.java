package com.txt.processes;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest
public class ArticleWorkflowIntegrationTest {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Test
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"})
    void articleApprovalTest() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("author", "test@txt.com");
        variables.put("url", "http://abc.com/dummy");
        runtimeService.startProcessInstanceByKey("articleReview", variables);
        Task task = taskService.createTaskQuery().singleResult();

        Assertions.assertEquals("Review the submitted tutorial", task.getName());
        variables.put("approved", true);
        taskService.complete(task.getId(), variables);
        Assertions.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }
}