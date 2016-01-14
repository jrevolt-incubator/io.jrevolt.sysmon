package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.ApiException;
import io.jrevolt.sysmon.cloud.model.ApiObject;
import io.jrevolt.sysmon.cloud.model.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
		MultivaluedMap<String,String> params = new MultivaluedHashMap<>();
		doArgs(params, method, args);
		String data = sign(params);

		boolean isCacheable = cfg.isUseCache() && method.getAnnotation(Cached.class) != null;
		boolean isDryRun = cfg.isDryRun() && method.getAnnotation(DryRun.class) != null;

		File cache = new File(urlencode(params.getFirst("signature") + ".json"));
		String s = null;
		if (isCacheable && cache.exists()) {
			try {
				s = new String(Files.readAllBytes(cache.toPath()), StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if(isDryRun) {
			try {
				LOG.debug("Dry-Run: {} {}", method.getName(), params);
				return (ApiObject) method.getReturnType().newInstance();
			} catch (Throwable /* InstantiationException | IllegalAccessException*/ e) {
				LOG.error("Error creating dry-run response", e);
				return null;
			}

		} else {
			WebTarget target = ClientBuilder.newClient().target(uri.build());
			Invocation inv = target.request()
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.buildPost(Entity.entity(data, "application/x-www-form-urlencoded"));

			LOG.debug("Calling: {} {}", method.getName(), params);
			long started = System.currentTimeMillis();
			Response response = inv.invoke();
			s = response.readEntity(String.class);
			Duration duration = Duration.between(Instant.ofEpochMilli(started), Instant.now());
			LOG.debug("Response: {} {} {} : {}", method.getName(), response.getStatus(), duration,
						 StringUtils.abbreviate(s, cfg.getLogMaxResponseLength()));

			if (isCacheable) {
				try {
					Files.write(cache.toPath(), s.getBytes(StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
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
		} catch (Exception e) {
			throw new UnsupportedOperationException(s, e);
		}
	}

	UriBuilder doArgs(UriBuilder uri, MultivaluedMap<String, String> params, Method m, Object[] args) {
		uri.queryParam("command", m.getName());
		if (args == null || args.length==0) { return uri; }
		Parameter[] methodParams = m.getParameters();
		for (int i=0; i<args.length; i++) {
			Parameter p = methodParams[i];
			Object value = args[i];
			if (value == null) { continue; }
//			uri.queryParam(p.getName(), value);
			params.add(p.getName(), value.toString());
		}
		return uri;
	}


	void doArgs(MultivaluedMap<String, String> params, Method m, Object[] args) {
		params.putSingle("command", m.getName());
		if (args == null) { return; }
		Parameter[] methodParams = m.getParameters();
		for (int i=0; i<args.length; i++) {
			Parameter p = methodParams[i];
			Object value = args[i];
			if (value == null) { continue; }
			if (value instanceof Collection) {
				((Collection<?>) value).stream().forEach(v->params.add(p.getName(), v.toString()));
			} else {
				params.add(p.getName(), value.toString());
			}
		}
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

	String sign(MultivaluedMap<String, String> params) {
		params.putSingle("apikey", cfg.getApiKey());
		params.putSingle("response", "json");
		StringBuilder unsigned = new StringBuilder();
		params.keySet().stream().sorted().forEach(pname->{
			List<String> list = params.get(pname);
			String value = list.size() == 1 ? list.get(0) : null /*FIXME*/;
			if (unsigned.length()>0) { unsigned.append("&"); }
			unsigned.append(pname).append("=").append(urlencode(value));
		});
		String signature = sign(cfg.getSecretKey(), unsigned.toString().toLowerCase());
		params.putSingle("signature", signature);
		return String.format("%s&signature=%s", unsigned, urlencode(signature));
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

	String urlencode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
