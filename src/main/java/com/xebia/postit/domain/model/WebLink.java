package com.xebia.postit.domain.model;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;
import org.springframework.util.Assert;

import com.xebia.postit.common.util.Buildable;
import com.xebia.postit.common.util.DateTimeHelper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Accessors(chain = true, fluent = true)
@Entity
@ToString
@Table(name = "WEB_LINK")
public class WebLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Version
	@Access(AccessType.FIELD)
	@Getter(value = AccessLevel.NONE)
	@Column(name = "version", nullable = false)
	private long version;

	@Column(name = "URL", updatable = false, nullable = false)
	@URL
	private String url;

	@NotEmpty
	@Column(name = "TITLE", updatable = false, nullable = false)
	private String title;

	@NotEmpty
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "TAGS", joinColumns = @JoinColumn(name = "LINK_ID"))
	@OrderBy
	@Column(name = "TAG")
	private Set<@NotEmpty String> tags = new TreeSet<>();

	@NotNull
	@Column(name = "favCount", nullable = false)
	private long favCount;

	public long markFavourite() {
		return ++this.favCount;
	}

	@NotNull
	@Column(name = "CREATED_ON")
	private ZonedDateTime createdOn;

	@Getter(value = AccessLevel.NONE)
	@NotNull
//	@org.hibernate.annotations.Type(type = "yes_no")
	@Column(name = "ENABLED", length = 1)
	private boolean enabled;

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean disable() {
		if (this.enabled) {
			this.enabled = false;
			return true;
		} else { // Already disabled
			return false;
		}
	}

	public static TitleBuilder of(final String url) {
		return new WebLinkBuilder(url);
	}

	public interface TitleBuilder {
		public TagBuilder title(final String title);
	}

	public interface TagBuilder {
		public Buildable<WebLink> tags(final Set<String> tags);
	}

	public static class WebLinkBuilder implements TitleBuilder, TagBuilder, Buildable<WebLink> {

		private String url;

		private String title;

		private Set<String> tags;

		WebLinkBuilder(final String url) {
			Assert.hasLength(url, "Non null and Non empty url is required");
			this.url = url;
		}

		@Override
		public TagBuilder title(final String title) {
			Assert.hasLength(title, "Non null and Non empty title is required");
			this.title = title;
			return this;
		}

		@Override
		public Buildable<WebLink> tags(final Set<String> tags) {
			Assert.notEmpty(tags, "At leaset one tag is required");
			Assert.isTrue(!tags.contains(null) && !tags.contains(""), "Non null and Non empty tags are required");
			this.tags = tags;
			return this;
		}

		@Override
		public WebLink build() {
			WebLink webLink = new WebLink();
			webLink.url = this.url;
			webLink.title = this.title;
			webLink.tags = this.tags;
			webLink.enabled = true;
			webLink.favCount = 0L;
			webLink.createdOn = DateTimeHelper.nowZonedDateTimeUTC();
			return webLink;
		}
	}
}
