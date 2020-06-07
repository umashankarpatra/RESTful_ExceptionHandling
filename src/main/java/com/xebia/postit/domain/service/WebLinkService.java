package com.xebia.postit.domain.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xebia.postit.domain.model.WebLink;
import com.xebia.postit.domain.model.dto.WebLinkResponse;

public interface WebLinkService {

	public WebLink createWebLink(final String url, final String title, final Set<String> tags);

	public Optional<WebLink> getWebLink(final long linkId);

	public boolean markFavouriteWebLink(final long linkId);

	public boolean deActivateWebLink(final long linkId);

	public Page<WebLinkResponse> getAllWebLinks(final Set<String> tags, final boolean enabled, final Pageable pageable);
}
