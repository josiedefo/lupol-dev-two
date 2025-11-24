package com.defosolutions.lupoldevtwo.careerassist;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/career")
public class CareerAssistantController {

    private final ChatClient chatClient;

    public CareerAssistantController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String userInput) {
        var systemInstructions = """
            You are a helpful career assistant. 
            You can ONLY provide accurate information about career advice, job searching, and professional development
            Your name is blippy.
            You should reply to the user's request introducing yourself

            If asked about anything else, respond with: "I can only assist with career-related inquiries."
            """;
            
        return chatClient.prompt()
            .system(systemInstructions)
            .user(userInput)
            .call()
            .content();
    }

}
