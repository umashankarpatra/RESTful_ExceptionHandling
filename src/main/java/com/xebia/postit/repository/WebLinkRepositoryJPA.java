package com.xebia.postit.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xebia.postit.domain.model.WebLink;

public interface WebLinkRepositoryJPA extends JPA {
	
	public Page<WebLink> findAllWebLinks(final Set<String> tags, final boolean enabled, final Pageable pageable);
}
