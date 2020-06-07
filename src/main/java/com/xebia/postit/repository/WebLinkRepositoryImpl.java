package com.xebia.postit.repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.xebia.postit.domain.model.WebLink;
import com.xebia.postit.domain.model.WebLink_;

@Repository
public class WebLinkRepositoryImpl extends AbstractJPA implements WebLinkRepositoryJPA {

	@Override
	public Page<WebLink> findAllWebLinks(final Set<String> tags, final boolean enabled, final Pageable pageable) {
		long totalRecords = findWebLinksCount(tags, enabled, pageable);
		if (totalRecords == 0) {
			return new PageImpl<>(Collections.emptyList(), pageable, totalRecords);
		} else {
			CriteriaBuilder criteriaBuilder = criteriaBuilder();
			CriteriaQuery<WebLink> query = criteriaQuery(WebLink.class);
			Root<WebLink> root = query.from(WebLink.class);

			if (tags != null && !tags.isEmpty()) {
				query.where(root.get(WebLink_.tags.getName()).in(tags),
						criteriaBuilder.equal(root.get(WebLink_.enabled.getName()), true));
			} else {
				query.where(criteriaBuilder.equal(root.get(WebLink_.enabled.getName()), true));
			}

			query.select(root);
			query.distinct(true);
			query.orderBy(criteriaBuilder.desc(root.get(WebLink_.favCount.getName())),
					criteriaBuilder.desc(root.get(WebLink_.createdOn.getName())));
			TypedQuery<WebLink> typedQuery = typedQuery(query);

			typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			typedQuery.setMaxResults(pageable.getPageSize());
			List<WebLink> results = getUnmodifiableResultList(typedQuery);
			return new PageImpl<>(results, pageable, totalRecords);
		}
	}

	private long findWebLinksCount(final Set<String> tags, final boolean enabled, final Pageable pageable) {
		CriteriaBuilder criteriaBuilder = criteriaBuilder();
		CriteriaQuery<Long> query = criteriaQuery(Long.class);
		Root<WebLink> root = query.from(WebLink.class);

		if (tags != null && !tags.isEmpty()) {
			query.where(root.get(WebLink_.tags.getName()).in(tags),
					criteriaBuilder.equal(root.get(WebLink_.enabled.getName()), true));
		} else {
			query.where(criteriaBuilder.equal(root.get(WebLink_.enabled.getName()), true));
		}

		query.select(criteriaBuilder.countDistinct(root));
		return typedQuery(query).getSingleResult().longValue();
	}
}
