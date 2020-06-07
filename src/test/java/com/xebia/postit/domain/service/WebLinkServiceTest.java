package com.xebia.postit.domain.service;

import java.net.MalformedURLException;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Sets;
import com.xebia.postit.SpringProfiles;
import com.xebia.postit.domain.model.WebLink;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = SpringProfiles.DEVELOPMENT)
@Commit
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class WebLinkServiceTest {

	@Autowired
	private WebLinkService webLinkService;

	@Test
	void testCreateWebLink() throws MalformedURLException {
		WebLink webLink = webLinkService.createWebLink("https://www.baeldung.com/jpa-criteria-api-in-expressions",
				"JPA in Clause", Sets.newHashSet("JPA", "Criteria in"));
		System.out.println("New WebLink Object created in DB -->" + webLink);
	}

}
