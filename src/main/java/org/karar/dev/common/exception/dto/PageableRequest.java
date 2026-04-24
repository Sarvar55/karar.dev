package org.karar.dev.common.exception.dto;

import io.swagger.v3.oas.annotations.Parameter;

public class PageableRequest {

    @Parameter(example = "0")
    public int page;

    @Parameter(example = "5")
    public int size;

    @Parameter(
            description = "Sort format: field,direction",
            example = "createdAt,desc"
    )
    public String sort;
}
