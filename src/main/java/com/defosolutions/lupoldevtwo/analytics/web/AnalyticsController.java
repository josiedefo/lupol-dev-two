package com.defosolutions.lupoldevtwo.analytics.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
  private static final Logger ANALYTICS_LOG = Logger.getLogger(AnalyticsController.class.getName());

  @PostMapping("/visit")
  public ResponseEntity<Void> visit(@RequestBody(required=false) VisitDTO dto, HttpServletRequest req) {
    if (dto == null || dto.visitorId() == null || dto.visitorId().isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    // Build a flat map for structured one-line JSON log
    Map<String, Object> dtoToJson = new HashMap<>();
    dtoToJson.put("type", "visit");
    dtoToJson.put("ts", Instant.now().toString());
    dtoToJson.put("visitorId", dto.visitorId());
    dtoToJson.put("ip", IpUtil.clientIp(req));
    dtoToJson.put("ua", nullSafe(req.getHeader("User-Agent")));
    dtoToJson.put("path", nullSafe(dto.path()));
    dtoToJson.put("referrer", nullSafe(dto.referrer()));
    dtoToJson.put("tz", nullSafe(dto.tz()));
    dtoToJson.put("lang", nullSafe(dto.lang()));
    dtoToJson.put("screen", nullSafe(dto.screen()));
    if (dto.utm()!=null) dtoToJson.put("utm", dto.utm()); // nested map is fine in JSON

    // Log as JSON (pattern encoder formats it); fallback to toString if not configured
    ANALYTICS_LOG.info("Received analytics visit: " + toJson(dtoToJson));

    return ResponseEntity.accepted().build();
  }

  private static String nullSafe(String s){ return s == null ? "" : s; }

  // tiny JSON builder to avoid extra deps
  private static String toJson(Map<String, ?> m) {
    StringBuilder sb = new StringBuilder(256).append('{');
    boolean first = true;
    for (var entry : m.entrySet()) {
      if (!first) sb.append(',');
      first = false;
      sb.append('"').append(esc(entry.getKey())).append("\":");
      Object v = entry.getValue();
      if (v == null) sb.append("null");
      else if (v instanceof Number || v instanceof Boolean) sb.append(v);
      else if (v instanceof Map<?,?> sub) {
        sb.append('{');
        boolean f2 = true;
        for (var e2 : sub.entrySet()) {
          if (!f2) sb.append(',');
          f2 = false;
          sb.append('"').append(esc(String.valueOf(e2.getKey()))).append("\":");
          Object vv = e2.getValue();
          if (vv == null) sb.append("null");
          else sb.append('"').append(esc(String.valueOf(vv))).append('"');
        }
        sb.append('}');
      } else sb.append('"').append(esc(String.valueOf(v))).append('"');
    }
    return sb.append('}').toString();
  }
  private static String esc(String s) {
    return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
  }
}
