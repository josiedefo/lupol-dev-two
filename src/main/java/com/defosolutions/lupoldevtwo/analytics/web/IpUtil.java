package com.defosolutions.lupoldevtwo.analytics.web;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtil {
  private IpUtil() {}
  public static String clientIp(HttpServletRequest req) {
    String[] headers = {"X-Forwarded-For","X-Real-IP","CF-Connecting-IP","True-Client-IP"};
    for (String h : headers) {
      String v = req.getHeader(h);
      if (v != null && !v.isBlank()) return v.split(",")[0].trim();
    }
    return req.getRemoteAddr();
  }
}
