package com.xebia.postit.controller;

import static com.xebia.postit.common.i18n.MessageProvider.getMessage;
import static com.xebia.postit.common.i18n.PostItMessageCode.*;
import static com.xebia.postit.common.util.rest.HeaderUtil.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xebia.postit.common.util.rest.PaginatedResource;
import com.xebia.postit.common.util.rest.PaginatedResourceAssembler;
import com.xebia.postit.domain.model.WebLink;
import com.xebia.postit.domain.model.dto.CreateWebLinkRequest;
import com.xebia.postit.domain.model.dto.WebLinkResponse;
import com.xebia.postit.domain.service.WebLinkService;
import com.xebia.postit.infra.config.ApplicationProperties;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "PostIt", description = "<b>APIs</b>", tags = "Post It")
@RestController
@RequestMapping(ApplicationProperties.POST_IT_API)
@Validated
public class WebLinkController {
	
	@Autowired
	private WebLinkService webLinkService;

	@Autowired
	private PaginatedResourceAssembler<WebLinkResponse> pagedResourcesAssembler;

	@PostMapping("/web-links")
	// @formatter:off
	@ApiOperation(nickname = "create-web-link", 
			value = "Creates a Web Link resource", 
		    notes = "", 
		    response = Void.class
	)
	@ApiResponses(value = {
				@ApiResponse(response = Void.class, code = 201, message = "One or more records created successfully"),
				@ApiResponse(code = 400, message = "Could not create records for supplied input") 
			}
	)
	public ResponseEntity<Void> createVaRs(
			@RequestBody @Valid @NotNull final CreateWebLinkRequest createWebLinkRequest) {
	// @formatter:on
		WebLink webLink = this.webLinkService.createWebLink(createWebLinkRequest.getUrl(), createWebLinkRequest.getTitle(),
				createWebLinkRequest.getTags());
	
		return ResponseEntity.created(linkTo(methodOn(WebLinkController.class).getWebLinkById(webLink.id())).toUri())
				.headers(addSuccess(getMessage(WEB_LINK_CEATED))).build();
	}
	
    @GetMapping("/web-links/{id}")
    // @formatter:off
	@ApiOperation(nickname = "get-web-link-by-id", 
	    consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, 
	    value = "Gets a Web Link resource by ID", 
	    response = WebLinkResponse.class, 
	    notes = ""
    )
	public ResponseEntity<WebLinkResponse> getWebLinkById(
			@ApiParam(value = "Web Link id", example = "132", required = true) 
			@PathVariable(name = "id", required = true) @NotNull final Long id) {
		// @formatter:on
        return this.webLinkService.getWebLink(id)
                .map(WebLinkResponse::of).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().headers(addError(getMessage(RECORD_NOT_FOUND))).build());
    }

    @PutMapping("/web-links/{id}/de-activate")
    // @formatter:off
	@ApiOperation(nickname = "get-web-link-by-id", 
	    consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, 
	    value = "Deactivate a Web Link resource by ID", 
	    response = Void.class, 
	    notes = ""
    )
	public ResponseEntity<Void> deActivateWebLinkById(
			@ApiParam(value = "Web Link id", example = "132", required = true) 
			@PathVariable(name = "id", required = true) @NotNull final Long id) {
		// @formatter:on
    	this.webLinkService.deActivateWebLink(id);
    	return ResponseEntity.ok().headers(addSuccess(getMessage(WEB_LINK_DE_ACTIVATED))).build();
    }

    @PutMapping("/web-links/{id}/mark-favourite")
    // @formatter:off
	@ApiOperation(nickname = "get-web-link-by-id", 
	    consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, 
	    value = "Deactivate a Web Link resource by ID", 
	    response = Void.class, 
	    notes = ""
    )
	public ResponseEntity<Void> markFavouriteWebLinkById(
			@ApiParam(value = "Web Link id", example = "132", required = true) 
			@PathVariable(name = "id", required = true) @NotNull final Long id) {
		// @formatter:on
    	this.webLinkService.markFavouriteWebLink(id);
    	return ResponseEntity.ok().headers(addSuccess(getMessage(WEB_LINK_MARKED_FAVOURITE))).build();
    }

	@GetMapping("/web-links")
	// @formatter:off
	@ApiOperation(nickname = "get-web-link-page", consumes = APPLICATION_JSON_VALUE, 
		produces = APPLICATION_JSON_VALUE, 
		value = "Gets a page of Web links matching the selection filters", 
		notes = "", response = PaginatedResource.class
	)
	public ResponseEntity<PaginatedResource<WebLinkResponse>> getAllAlbums(
			@ApiParam(name = "tags", value = "Tag names", example = "Java", allowMultiple = true, allowEmptyValue = false) 
			@RequestParam(value = "tags", required = false) final Set<@NotEmpty String> tags, 
			@ApiParam(name = "enabled", value = "Enabled or Disabled", allowableValues = "true,false", example = "true") 
			@RequestParam(value = "enabled", required = true) @NotNull final Boolean enabled,
			@ApiIgnore @PageableDefault(page = 0, size = 2, sort = {
					"title" }, direction = Sort.Direction.ASC) final Pageable pageable) {
		// @formatter:on

		Page<WebLinkResponse> webLinks = this.webLinkService.getAllWebLinks(tags, enabled, pageable);

		if (webLinks.isEmpty()) {
			return ResponseEntity.notFound().headers(addError(getMessage(RECORDS_NOT_FOUND))).build();
		} else {
			return ResponseEntity.ok(this.pagedResourcesAssembler.assemble(webLinks));
		}
	}
}
