package com.lucasdev.stereodroid;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.StereomoodApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class StereodroidOAuth {
	private static String consumerKey = "ddde975646671ba3057d9efce21b40a904cc8bf71";
	private static String consumerSecret = "51628acfe02993d78291f77b013d6350";
	private static String authorizeUrl = "http://www.stereomood.com/api/oauth/authenticate?oauth_token=";
	private Token requestToken,accessToken = null;
	private static StereodroidOAuth instance = null;
	
	public static StereodroidOAuth getInstance(){
		if(instance == null){
			instance = new StereodroidOAuth();
		}
		return instance;
	}
	
	private OAuthService service = new ServiceBuilder()
			.provider(StereomoodApi.class).apiKey(consumerKey)
			.apiSecret(consumerSecret).callback(Stereodroid.CALLBACK_URL)
			.build();
	
	
	public Token doValidate(String pin) {
		try {
			Verifier verifier = new Verifier(pin);
			accessToken = service.getAccessToken(requestToken, verifier);
		} catch (Exception e) {
		}
		return accessToken;
	}
	
	public Response doRequest(String url) {
		OAuthRequest request = new OAuthRequest(Verb.GET,url);
		service.signRequest(accessToken, request);
		return request.send();
	}
	
	public String getAuthorizeUrl(){
		return authorizeUrl + getRequestToken().getToken();
	}
	
	public Token getRequestToken(){
		if(requestToken == null){
			requestToken = service.getRequestToken();
		}
		return requestToken;
	}
	
	public void setAccessToken(String userToken, String tokenSecret){
		accessToken = new Token(userToken, tokenSecret);
	}
}
