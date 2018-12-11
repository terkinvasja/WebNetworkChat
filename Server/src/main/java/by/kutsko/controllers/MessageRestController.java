package by.kutsko.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageRestController {

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("/test")
    public void test() {

    }
}
