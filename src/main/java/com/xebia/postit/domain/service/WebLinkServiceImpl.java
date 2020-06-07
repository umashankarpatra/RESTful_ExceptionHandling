package com.xebia.postit.domain.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Status;

import com.xebia.postit.common.exception.PostItException;
import com.xebia.postit.common.exception.PostItException.PostItExceptionType;
import com.xebia.postit.domain.model.WebLink;
import com.xebia.postit.domain.model.dto.WebLinkResponse;
import com.xebia.postit.repository.WebLinkRepository;

@Service
public class WebLinkServiceImpl implements WebLinkService {

	@Autowired
	private WebLinkRepository webLinkRepository;

	@Transactional
	@Override
	public WebLink createWebLink(final String url, final String title, final Set<String> tags) {
		try {
			URL remoteUrl = new URL(url);
			URLConnection urlConnection = remoteUrl.openConnection();
			urlConnection.connect();
		} catch (MalformedURLException e) {
			throw new PostItException(PostItExceptionType.MALFORMED_URL, Status.BAD_REQUEST, url);
		} catch (IOException e) {
			throw new PostItException(PostItExceptionType.URL_HOST_NOT_AVALABLE, Status.SERVICE_UNAVAILABLE, url);
		}
		if(this.webLinkRepository.countEnabledWebLinksByUrl(url) > 0) {
			throw new PostItException(PostItExceptionType.DUPLICATE_URL, Status.BAD_REQUEST);
		}
		WebLink webLink = WebLink.of(url).title(title).tags(tags).build();
		return this.webLinkRepository.save(webLink);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<WebLink> getWebLink(final long linkId) {
		return this.webLinkRepository.findById(linkId);
	}

	@Transactional
	@Override
	public boolean markFavouriteWebLink(final long linkId) {
		WebLink webLink = this.webLinkRepository.findByIdAndEnabled(linkId, true).orElseThrow(
				() -> new PostItException(PostItExceptionType.ACTIVE_RECORD_BY_ID_NOT_FOUND, Status.NOT_FOUND, linkId));
		webLink.markFavourite();
		return true;
	}

	@Transactional
	@Override
	public boolean deActivateWebLink(final long linkId) {
		WebLink webLink = this.webLinkRepository.findByIdAndEnabled(linkId, true).orElseThrow(
				() -> new PostItException(PostItExceptionType.ACTIVE_RECORD_BY_ID_NOT_FOUND, Status.NOT_FOUND, linkId));
		return webLink.disable();
	}

	@Transactional(readOnly = true)
	@Override
	public Page<WebLinkResponse> getAllWebLinks(final Set<String> tags, final boolean enabled,
			final Pageable pageable) {
		Page<WebLink> webLinks = this.webLinkRepository.findAllWebLinks(tags, enabled, pageable);

		Function<List<WebLink>, List<WebLinkResponse>> entityToDtotransformer = (List<WebLink> source) -> source
				.stream().map(WebLinkResponse::of).collect(Collectors.toList());

		return PageableExecutionUtils.getPage(entityToDtotransformer.apply(webLinks.getContent()), pageable,
				webLinks::getTotalElements);
	}
}
