package com.defosolutions.lupoldevtwo.careerassist;

import java.util.logging.Logger;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/career")
public class CareerAssistantController {

    private final ChatClient chatClient;
    private static final Logger log = Logger.getLogger(CareerAssistantController.class.getName());

    public CareerAssistantController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
                .build())
            .build();;
    }

    @PostMapping("/chat")
    public String chat(@RequestParam(defaultValue = "What career can I do if I like to dance a lot?") String userInput) {
        log.info("Received user input: "+userInput);
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
