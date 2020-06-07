package com.xebia.postit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xebia.postit.domain.model.WebLink;

public interface WebLinkRepository extends JpaRepository<WebLink, Long>, WebLinkRepositoryJPA {

	public Optional<WebLink> findByIdAndEnabled(final long id, final boolean enabled);

	@Query("SELECT COUNT(w) FROM WebLink w WHERE w.enabled = true and w.url=:url")
	public long countEnabledWebLinksByUrl(@Param("url") final String url);
}
