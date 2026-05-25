package org.karar.dev.common.notification.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record EmailTemplateModel(
       Map<String, Object> variables
) {}