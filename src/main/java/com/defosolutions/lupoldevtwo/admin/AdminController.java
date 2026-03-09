package com.defosolutions.lupoldevtwo.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = Logger.getLogger(AdminController.class.getName());

    private static final Pattern USER_INPUT_PATTERN =
        Pattern.compile("Received user (.+?) input: (.+)", Pattern.DOTALL);
    private static final Pattern ASSISTANT_REPLY_PATTERN =
        Pattern.compile("Responding to user (.+?) with: (.+)", Pattern.DOTALL);

    @Value("${admin.token}")
    private String adminToken;

    @Value("${cloudwatch.log.group}")
    private String logGroup;

    @Value("${cloudwatch.region:us-east-1}")
    private String awsRegion;

    @GetMapping("/conversations")
    public ResponseEntity<?> conversations(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @RequestParam(defaultValue = "24") int hours) {

        if (token == null || !adminToken.equals(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or missing admin token"));
        }

        try {
            CloudWatchLogsClient client = CloudWatchLogsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

            Instant startTime = Instant.now().minus(hours, ChronoUnit.HOURS);

            List<FilteredLogEvent> events = new ArrayList<>();
            String nextToken = null;

            do {
                FilterLogEventsRequest.Builder requestBuilder = FilterLogEventsRequest.builder()
                    .logGroupName(logGroup)
                    .filterPattern("?\"Received user\" ?\"Responding to user\"")
                    .startTime(startTime.toEpochMilli())
                    .limit(500);
                if (nextToken != null) requestBuilder.nextToken(nextToken);

                var response = client.filterLogEvents(requestBuilder.build());
                events.addAll(response.events());
                nextToken = response.nextToken();
            } while (nextToken != null && events.size() < 2000);

            client.close();

            // Parse and group by visitorId
            Map<String, List<Map<String, Object>>> byVisitor = new LinkedHashMap<>();

            for (FilteredLogEvent event : events) {
                String msg = event.message();
                String visitorId = null;
                String role = null;
                String text = null;

                Matcher userMatcher = USER_INPUT_PATTERN.matcher(msg);
                Matcher assistantMatcher = ASSISTANT_REPLY_PATTERN.matcher(msg);

                if (userMatcher.find()) {
                    visitorId = userMatcher.group(1).trim();
                    role = "user";
                    text = userMatcher.group(2).trim().replace("\\n", "\n");
                } else if (assistantMatcher.find()) {
                    visitorId = assistantMatcher.group(1).trim();
                    role = "assistant";
                    text = assistantMatcher.group(2).trim().replace("\\n", "\n");
                }

                if (visitorId == null || visitorId.isBlank()) continue;

                byVisitor.computeIfAbsent(visitorId, k -> new ArrayList<>())
                    .add(Map.of(
                        "role", role,
                        "text", text,
                        "timestamp", event.timestamp()
                    ));
            }

            // Build response list sorted by most recently active first
            List<Map<String, Object>> result = byVisitor.entrySet().stream()
                .map(e -> {
                    List<Map<String, Object>> messages = e.getValue();
                    long lastActive = messages.stream()
                        .mapToLong(m -> (Long) m.get("timestamp"))
                        .max().orElse(0);
                    return Map.<String, Object>of(
                        "visitorId", e.getKey(),
                        "messages", messages,
                        "lastActive", lastActive,
                        "messageCount", messages.size()
                    );
                })
                .sorted(Comparator.comparingLong(m -> -((Long) m.get("lastActive"))))
                .toList();

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.warning("CloudWatch fetch failed: " + e.getMessage());
            return ResponseEntity.status(503).body(Map.of(
                "error", "CloudWatch unavailable: " + e.getMessage(),
                "hint", "Ensure the App Runner instance role has logs:FilterLogEvents permission and CLOUDWATCH_LOG_GROUP is set correctly."
            ));
        }
    }
}
