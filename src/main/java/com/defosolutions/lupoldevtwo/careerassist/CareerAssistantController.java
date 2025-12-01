package com.defosolutions.lupoldevtwo.careerassist;

import java.util.logging.Logger;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public String chat(@RequestParam(defaultValue = "What career can I do if I like to dance a lot?") String userInput, 
        @RequestHeader(value = "X-Visitor-Id", required = false) String visitorIdHeader) {
        
        String visitorId = (visitorIdHeader != null && !visitorIdHeader.isBlank()) ? visitorIdHeader : "";
        log.info("Received user " + visitorId + " input: " + userInput);

        var systemInstructions = """
            You are a helpful career assistant. 
            You are named Lupol.
            You can ONLY provide accurate information about career advice, job searching, and professional development based on the user's skills, interests, and experiences. 
            Provide answers in a friendly and supportive tone.
            Provide practical tips, resources, and encouragement to help users navigate their career paths effectively.
            Only provide careers with a rampup time of 1 year or less.            
            For each career suggestion, provide a brief explanation of why it might be a good fit based on common skills and interests and add how long it would take to learn the necessary skills (e.g., "This career typically requires 2-4 years of education/training"). 
            Always suggest the careers with the shortest learning time first.
            If the user asks for resources, suggest reputable websites, online courses, or books that can help them gain relevant skills.    
            
            If asked about anything else, respond with: "I can only assist with career-related inquiries."
            """;
            
        String chatResponse =  chatClient.prompt()
            .system(systemInstructions)
            .user(userInput)
            .call()
            .content();
        log.info("Responding to user " + visitorId + " with: " + chatResponse);

        return chatResponse;
    }

}
