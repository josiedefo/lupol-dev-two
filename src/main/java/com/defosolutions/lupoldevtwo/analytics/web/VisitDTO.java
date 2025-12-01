package com.defosolutions.lupoldevtwo.analytics.web;

import java.util.Map;

public record VisitDTO(
  String visitorId,
  String path,
  String referrer,
  String tz,
  String lang,
  String screen,
  Map<String,String> utm
) {}

