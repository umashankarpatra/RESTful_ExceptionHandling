package com.xebia.postit.domain.model.dto;

import java.util.Set;

import com.xebia.postit.common.util.DateTimeHelper;
import com.xebia.postit.domain.model.WebLink;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ApiModel(value = "webLinkResponse", description = "Web Link resource")
public class WebLinkResponse {

	@ApiModelProperty(value = "Website resource ID", required = true, example = "234", position = 1)
	private long id;

	@ApiModelProperty(value = "Website URL", required = true, example = "https://www.google.com/", position = 2)
	private String url;

	@ApiModelProperty(value = "Website Title", required = true, example = "Spring Data JPA", position = 3)
	private String title;

	@ApiModelProperty(value = "Website tags", allowEmptyValue = false, dataType = "Set", required = true, example = "Java", position = 4)
	private Set<String> tags;

	@ApiModelProperty(value = "Favourite count of Website", required = true, example = "13", position = 5)
	private long favCount;

	@ApiModelProperty(value = "Record creation timestamp", required = true, example = "27-05-2020 19:30:11", position = 6)
	private String createdOn;

	public static WebLinkResponse of(final WebLink webLink) {
		return new WebLinkResponse(webLink.id(), webLink.url(), webLink.title(), webLink.tags(), webLink.favCount(),
				DateTimeHelper.formatZonedDateTime(webLink.createdOn()));
	}
}
