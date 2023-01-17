package io.swagger.api;

import io.swagger.model.ResponseMsg;
import io.swagger.model.SwipeDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-01-16T06:16:37.918Z[GMT]")
@RestController
public class SwipeApiController implements SwipeApi {

    private static final Logger log = LoggerFactory.getLogger(SwipeApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private static final Set<String> VALID = Set.of("left", "right");

    @org.springframework.beans.factory.annotation.Autowired
    public SwipeApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> swipe(@Parameter(in = ParameterIn.PATH, description = "Ilike or dislike user", required=true, schema=@Schema()) @PathVariable("leftorright") String leftorright,@Parameter(in = ParameterIn.DEFAULT, description = "response details", required=true, schema=@Schema()) @Valid @RequestBody SwipeDetails body) {
        // Simple validation.
        if (!VALID.contains(leftorright) || body == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

}
