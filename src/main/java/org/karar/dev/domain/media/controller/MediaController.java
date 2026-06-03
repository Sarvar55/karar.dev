package org.karar.dev.domain.media.controller;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.common.security.service.SecurityService;
import org.karar.dev.domain.media.dto.MediaResponse;
import org.karar.dev.domain.media.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final SecurityService securityService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/vnd.karar.dev+json;v=1.0")
    public ResponseEntity<BaseResponse<MediaResponse>> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        UUID currentUserId = securityService.getCurrentUserId();

        MediaResponse response = mediaService.uploadMedia(file, folder, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(response));
    }

}

