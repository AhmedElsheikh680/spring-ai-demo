package com.ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {

    private  ChatClient chatClient;

    public AIController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

//    @GetMapping("/chat/{message}")
//    public String openAIChat(@PathVariable String message) {
//     return client.prompt()
//                .user(message)
//                .call()
//                .content();
////      return  ResponseEntity.ok(response);
//    }
@GetMapping("/chat/{message}")
public ResponseEntity<String> openAIChat(@PathVariable String message) {
    try {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return ResponseEntity.ok(response);
    } catch (NonTransientAiException ex) {
        if (ex.getMessage().contains("insufficient_quota")) {
            return ResponseEntity.status(429).body("Error: You have exceeded your quota. Please check your OpenAI account.");
        }
        return ResponseEntity.status(500).body("Error: Non-recoverable AI error occurred.");
    } catch (Exception ex) {
        return ResponseEntity.status(500).body("Error: Unable to process your request.");
    }


}
@GetMapping("/film/{actor}")
  public ActorsFilms openAIFilm(@PathVariable String actor) {
        return chatClient.prompt()
                .user(u -> u.text("افضل 5 افلام ل {actor}")
                        .param("actor", actor))
                .call()
                .entity(ActorsFilms.class);
    }
    record ActorsFilms(String actor, List<String> movies) {
    }


}
