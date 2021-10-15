package by.aliakseishysh.pinfo.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class DataDownloader {

    private static final Logger logger = LoggerFactory.getLogger(DataDownloader.class);
    private static final List<String> responses = Collections.synchronizedList(new LinkedList<>());
    private static BlockingQueue<String> uris;
    private static ExecutorService executorService;
    private static CompletionService<Boolean> completionService;
    private static Bucket bucket;
    private static int overdraft;
    private final AtomicInteger index = new AtomicInteger(0);

    /**
     * Sets default settings for {@code DataDownloader}
     */
    private static void downloaderSetDefault() {
        overdraft = 4;
        int threadCount = 4;
        int requestsPerSecond = 4;
        executorService = Executors.newFixedThreadPool(threadCount);
        completionService = new ExecutorCompletionService<>(executorService);
        bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(overdraft, Refill.greedy(requestsPerSecond, Duration.ofSeconds(1))))
                .build();
    }

    /**
     * Downloads data from provided uris
     *
     * @param requestUris uris to make request
     * @return list with downloaded data
     */
    public List<String> downloadAll(Queue<String> requestUris) {
        uris = new LinkedBlockingQueue<>(requestUris);
        downloaderSetDefault();
        int futuresSize = 0;
        try {
            while (!uris.isEmpty()) {
                if (futuresSize == overdraft) {
                    completionService.take();
                    futuresSize--;
                }
                bucket.asScheduler().consume(1);
                if (!uris.isEmpty()) {
                    String uri = uris.take();
                    completionService.submit(new CallableDownloader(uri));
                    futuresSize++;
                } else {
                    break;
                }
            }
            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.debug("Thread was interrupted: {}", e.getMessage());
            executorService.shutdownNow();
        }
        logger.info("Downloaded: " + responses.size() + "; Requested: " + requestUris.size());
        return responses;
    }

    /**
     * Callable to download info from 1 uri
     */
    private class CallableDownloader implements Callable<Boolean> {
        private int runIndex = 0;
        private final String uri;

        private CallableDownloader(String uri) {
            this.uri = uri;
        }

        @Override
        public Boolean call() {
            boolean result = false;
            int b = index.addAndGet(1);
            logger.info("Started: " + b + ", remaining: " + uris.size());
            String responseResult;
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(uri);
                try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                    HttpEntity responseEntity = response.getEntity();
                    switch (response.getStatusLine().getStatusCode()) {
                        case 200:
                            responseResult = new BufferedReader(new InputStreamReader(responseEntity.getContent()))
                                    .lines().collect(Collectors.joining());
                            logger.info("200: Downloaded " + b);
                            responses.add(responseResult);
                            result = true;
                            break;
                        case 429:
                            if (runIndex < 5) {
                                logger.info(response.getStatusLine() + ": Redownloading " + b);
                                runIndex++;
                                call();
                            } else {
                                logger.info(response.getStatusLine() + ": Can't download, canceling..." + b);
                            }
                            break;
                        default:
                            logger.info(response.getStatusLine() + ": Canceling " + b);
                    }
                    EntityUtils.consume(responseEntity);
                }
            } catch (IOException e) {
                logger.error("Connection problem: {}", e.getMessage());
            }
            return result;
        }
    }

}
