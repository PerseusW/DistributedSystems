/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.36).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.ResponseMsg;
import io.swagger.model.SwipeDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CookieValue;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-01-16T06:16:37.918Z[GMT]")
@Validated
public interface SwipeApi {

    @Operation(summary = "", description = "Swipe left or right", tags={ "swipe" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Write successful"),
        
        @ApiResponse(responseCode = "400", description = "Invalid inputs", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMsg.class))),
        
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMsg.class))) })
    @RequestMapping(value = "/swipe/{leftorright}/",
        produces = { "application/json" }, 
        consumes = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<Void> swipe(@Parameter(in = ParameterIn.PATH, description = "Ilike or dislike user", required=true, schema=@Schema()) @PathVariable("leftorright") String leftorright, @Parameter(in = ParameterIn.DEFAULT, description = "response details", required=true, schema=@Schema()) @Valid @RequestBody SwipeDetails body);

}

