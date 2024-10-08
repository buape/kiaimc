package com.buape.kiaimc.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import com.google.gson.Gson;

public class RequestQueueManager {
	private final HttpClient client;
	private final Logger logger;
	private final String token;
	private final String baseUrl;
	private final ScheduledExecutorService executorService;
	private final Queue<QueuedRequest> requestQueue = new LinkedList<>();
	private final Boolean debug;
	private final Gson gson;

	public RequestQueueManager(Logger logger, String token, Boolean debug, String baseUrl) {
		this.client = HttpClient.newHttpClient();
		this.logger = logger;
		this.token = token;
		this.debug = debug;
		this.baseUrl = baseUrl;
		this.executorService = Executors.newSingleThreadScheduledExecutor();
		this.gson = new Gson();
	}

	public CompletableFuture<String> queueRequest(String endpoint, String method, HashMap<String, Object> requestBody) {
		CompletableFuture<String> future = new CompletableFuture<>();
		requestQueue.add(new QueuedRequest(endpoint, method, requestBody, future));
		executeNextRequest();
		return future;
	}

	private void executeNextRequest() {
		if (!requestQueue.isEmpty()) {
			QueuedRequest queuedRequest = requestQueue.poll();
			executorService.submit(() -> {
				try {
					HttpResponse<String> response = sendRequest(queuedRequest);
					handleResponse(response, queuedRequest);
				} catch (IOException | InterruptedException e) {
					queuedRequest.future.completeExceptionally(e);
					e.printStackTrace();
				}
			});
		}
	}

	private HttpResponse<String> sendRequest(QueuedRequest queuedRequest) throws IOException, InterruptedException {
		String jsonBody = gson.toJson(queuedRequest.requestBody);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(this.baseUrl + queuedRequest.endpoint))
				.header("Authorization", this.token)
				.header("Content-Type", "application/json")
				.method(queuedRequest.method, HttpRequest.BodyPublishers.ofString(jsonBody))
				.build();

		debug(String.format("Sending request to %s with body %s", request.uri(), jsonBody));

		return this.client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private void handleResponse(HttpResponse<String> response, QueuedRequest queuedRequest) {
		if (response.statusCode() == 401 || response.statusCode() == 403) {
			this.logger.warning(
					"Your Kiai API token is not authorized, please use the /application authorize command from Kiai in Discord to authorize your bot.");
			queuedRequest.future.completeExceptionally(new RuntimeException("Unauthorized"));
			return;
		}

		if (response.statusCode() == 429) {
			this.logger.warning("Kiai rate limit hit. Retrying in 5 seconds...");
			requestQueue.add(queuedRequest);
			executorService.schedule(this::executeNextRequest, 5, TimeUnit.SECONDS);
			return;
		}

		debug("Response with data: " + response.body() + " and status code " + response.statusCode());
		queuedRequest.future.complete(response.body());
		executeNextRequest();
	}

	private void debug(String message) {
		if (this.debug) {
			this.logger.info(message);
		}
	}

	private static class QueuedRequest {
		private String endpoint;
		private String method;
		private HashMap<String, Object> requestBody;
		private CompletableFuture<String> future;

		private QueuedRequest(String endpoint, String method, HashMap<String, Object> requestBody,
				CompletableFuture<String> future) {
			this.endpoint = endpoint;
			this.method = method;
			this.requestBody = requestBody;
			this.future = future;
		}
	}
}