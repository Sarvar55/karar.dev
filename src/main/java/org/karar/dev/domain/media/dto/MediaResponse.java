package org.karar.dev.domain.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
    private UUID id;
    private String url;          // Presigned URL for temporary access
    private String filename;
    private String contentType;
    private Long size;
}
