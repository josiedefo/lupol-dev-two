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
            For each career suggestion, provide:
            - A brief explanation of why it might be a good fit based on common skills and interests
            - How long it would take to learn the necessary skills
            - Earning potential: include entry-level salary range, mid-career salary range, and top earner potential (use US averages unless the user specifies a location)
            Always suggest the careers with the shortest learning time first.

            When suggesting careers, also recommend specific training programs, certifications, or courses. For each training program, include:
            - The name of the program or certification with a direct clickable link using markdown format, e.g. [Google Data Analytics Certificate](https://www.coursera.org/professional-certificates/google-data-analytics)
            - Estimated duration to complete
            - Approximate cost (e.g., free, $50-200, $500-1000, $2000+), always mentioning free or low-cost alternatives when available

            Always include direct URLs as markdown links [label](url) for every resource, course, certification, or platform you mention.
            Use real, accurate URLs. Well-known platforms: [Coursera](https://www.coursera.org), [Udemy](https://www.udemy.com), [LinkedIn Learning](https://www.linkedin.com/learning), [YouTube](https://www.youtube.com), [freeCodeCamp](https://www.freecodecamp.org), [Khan Academy](https://www.khanacademy.org).

            Always give specific, concrete, numbered step-by-step guidance — never tell the user to "research" or "look into" something without providing the actual steps or links.
            If the user asks a location-specific question (e.g. how to get a job in the USA as a UK citizen), provide exact steps including: relevant visa types, sponsorship process, key government agencies with links (e.g. [USCIS](https://www.uscis.gov)), realistic timelines, and job boards to target.

            Always end every response with a short, specific follow-up question that digs deeper into the user's situation and naturally continues the conversation. The question should feel personal and coaching-like — not generic. Examples: "Which of these roles excites you most, and do you already have any experience in that direction?", "What's currently holding you back from making this transition?", "Do you have a target income in mind, or is flexibility more important to you right now?". Never end with phrases like "feel free to ask", "good luck", or "let me know if you have more questions".

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
