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


import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class DataDownloader {

    private static final Logger logger = LoggerFactory.getLogger(DataDownloader.class);
    private final AtomicInteger index = new AtomicInteger(0);
    private static BlockingQueue<String> uris;
    private static final List<String> responses = Collections.synchronizedList(new ArrayList<>());
    private static ExecutorService executorService;
    private static CompletionService<Boolean> completionService;
    private static Bucket bucket;
    private static int overdraft;

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
                    completionService.submit(new CallableDownloader());
                    futuresSize++;
                } else {
                    break;
                }
            }
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.debug("Thread was interrupted: {}", e.getMessage());
            executorService.shutdownNow();
        }
        logger.info("Downloaded: " + responses.size() + "; Requested: " + requestUris.size());
        return responses;
    }

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
     * Callable to download info from 1 uri
     */
    private class CallableDownloader implements Callable<Boolean> {
        private int runIndex = 0;

        @Override
        public Boolean call() {
            boolean result = false;
            try {
                int b = index.addAndGet(1);
                logger.info("Started: " + b + ", remaining: " + uris.size());
                String url = uris.take();
                String responseResult;
                try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                    HttpGet httpGet = new HttpGet(url);
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
                                    uris.put(url);
                                    runIndex++;
                                    call();
                                } else {
                                    logger.info(response.getStatusLine() + ": Can't download, canceling..." + b);
                                }
                                break;
                            default:
                                logger.info(response.getStatusLine() + ": Canceling " + b);
                                break;
                        }
                        EntityUtils.consume(responseEntity);
                    }
                } catch (IOException e) {
                    logger.error("Connection problem: {}", e.getMessage());
                }
            } catch (InterruptedException e) {
                logger.debug("Thread was interrupted: {}", e.getMessage());
            }
            return result;
        }
    }

}
