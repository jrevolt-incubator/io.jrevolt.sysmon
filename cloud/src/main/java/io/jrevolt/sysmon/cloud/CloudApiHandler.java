package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.ApiException;
import io.jrevolt.sysmon.cloud.model.ApiObject;
import io.jrevolt.sysmon.cloud.model.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
public class CloudApiHandler implements InvocationHandler {

	static private final Logger LOG = LoggerFactory.getLogger(CloudApiHandler.class);

	@Autowired
	CloudCfg cfg;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass().equals(CloudApi.class)) {
			return doRequest(method, args);
		} else {
			return method.invoke(this, method, args);
		}
	}

	///

	ApiObject doRequest(Method method, Object[] args) {
		UriBuilder uri = UriBuilder.fromUri(cfg.getBaseUrl());
		uri.queryParam("command", method.getName());
		doArgs(uri, method, args);
		uri = sign(uri);
		WebTarget target = ClientBuilder.newClient().target(uri.build());
		Invocation inv = target.request().accept(MediaType.APPLICATION_JSON_TYPE).buildGet();

		long started = System.currentTimeMillis();
		Response response = inv.invoke();
		String s = response.readEntity(String.class);
		Duration duration = Duration.between(Instant.ofEpochMilli(started), Instant.now());
		LOG.debug("Response: {} {} {}", response.getStatus(), duration, target.getUri());

		String responsename = method.getReturnType().getSimpleName().toLowerCase();
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		JsonObject jresp = json.getAsJsonObject(responsename);
		JsonObject jerr = json.getAsJsonObject("errorresponse");
		if (jresp == null && jerr != null) {
			ErrorResponse error = new Gson().fromJson(jerr, ErrorResponse.class);
			throw new ApiException(error);
		}
		Object result = new Gson().fromJson(jresp, method.getReturnType());
		return (ApiObject) result;
	}

	UriBuilder doArgs(UriBuilder uri, Method m, Object[] args) {
		if (args == null || args.length==0) { return uri; }
		Parameter[] params = m.getParameters();
		for (int i=0; i<args.length; i++) {
			Parameter p = params[i];
			Object value = args[i];
			if (value == null) { continue; }
			uri.queryParam(p.getName(), value);
		}
		return uri;
	}


	UriBuilder sign(UriBuilder uri) {
		uri = uri
				.queryParam("apikey", cfg.getApiKey())
				.queryParam("response", "json");
		MultivaluedMap<String, String> params = getQueryParams(uri.build().getQuery());
		uri.replaceQuery(null);
		for (String s : new TreeSet<>(params.keySet())) {
			List<String> list = params.get(s);
			uri.queryParam(s, list.toArray());
		}
		String signature = sign(cfg.getSecretKey(), uri.build().getQuery().toLowerCase());
		uri = uri.queryParam("signature", signature);

		return uri;
	}

	String sign(String key, String data) {
		try {
			String SHA1 = "HmacSHA1";
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), SHA1);
			Mac mac = Mac.getInstance(SHA1);
			mac.init(keyspec);
			byte[] bytes = mac.doFinal(data.getBytes());
			String encoded = Base64.getEncoder().encodeToString(bytes);
			return encoded;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	MultivaluedMap<String, String> getQueryParams(String query) {
		try {
			MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
			for (String param : query.split("&")) {
				String[] pair = param.split("=");
				String key = URLDecoder.decode(pair[0], "UTF-8");
				String value = "";
				if (pair.length > 1) {
					value = URLDecoder.decode(pair[1], "UTF-8");
				}
				if ("".equals(key) && pair.length == 1) {
					continue;
				}
				params.add(key, value);
			}
			return params;
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
