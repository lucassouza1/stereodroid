package org.scribe.builder.api;

public class StereomoodApi extends DefaultApi10a {

	@Override
	protected String getRequestTokenEndpoint() {
		return "http://www.stereomood.com/api/oauth/request_token";
	}

	@Override
	protected String getAccessTokenEndpoint() {
		return "http://www.stereomood.com/api/oauth/access_token";
	}

}
