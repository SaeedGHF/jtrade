package com.jtradeplatform.saas.ws.controllers;

import com.jtradeplatform.saas.services.ChartService;
import com.jtradeplatform.saas.ws.messages.ChartMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChartController {

    SimpMessagingTemplate simpMessagingTemplate;

    ChartController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    //@MessageMapping("/getChart")
    //public void greeting(ChartMessage message) throws Exception {
    //    ChartService chart = new ChartService(message.getSymbol());
    //    String destination = "/chart/" + message.getSymbol();
    //    simpMessagingTemplate.convertAndSend(destination, chart);
    //}
}
