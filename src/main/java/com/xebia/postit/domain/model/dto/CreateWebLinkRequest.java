package com.xebia.postit.domain.model.dto;

import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.URL;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@ApiModel(value = "createWebLinkRequest", description = "Request to create a Web Link resource")
public class CreateWebLinkRequest {

	@ApiModelProperty(value = "Website URL", required = true, example = "https://www.google.com/", position = 1)
	@NotEmpty
	@URL
	private String url;

	@ApiModelProperty(value = "Website Title", required = true, example = "Spring Data JPA", position = 2)
	@NotEmpty
	private String title;

	@ApiModelProperty(value = "Website tags", allowEmptyValue = false, required = true, position = 3)
	@NotEmpty
	private Set<@NotEmpty String> tags = new TreeSet<>();

//	@JsonCreator
//	public static CreateWebLinkRequest of(final @JsonProperty("url") String url,
//			final @JsonProperty("title") String title, final @JsonProperty("tags") Set<String> tags) {
//		return new CreateWebLinkRequest(url, title, tags);
//	}
}
