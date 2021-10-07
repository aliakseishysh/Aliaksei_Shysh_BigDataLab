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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DataDownloader {

    private static volatile AtomicInteger index = new AtomicInteger(0);
    // TODO replace with slf4j
    public static List<String> downloadAll(Queue<String> requestUris) throws InterruptedException {
        BlockingQueue<String> uris = new LinkedBlockingQueue<>(requestUris);
        List<String> responses = Collections.synchronizedList(new ArrayList<>());
        int overdraft = 7;
        ExecutorService executorService = Executors.newFixedThreadPool(overdraft);
        CompletionService completionService = new ExecutorCompletionService(executorService);
        Refill refill = Refill.greedy(overdraft, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(overdraft, refill);
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();

        int futuresSize = 0;
        while(!uris.isEmpty()) {
            if (futuresSize == overdraft) {
                completionService.take();
                futuresSize--;
            }
            bucket.asScheduler().consume(1);
            if (!uris.isEmpty()) {
                completionService.submit(new Callable() {
                    @Override
                    public Object call() {
                        try {
                            int b = index.addAndGet(1);
                            System.out.println("Started: " + b + ", size: " + uris.size());
                            String temp = DataDownloader.downloadData(uris.take());
                            System.out.println("Downloaded " + b + " : " + temp.substring(0, 100));
                            responses.add(temp);
                        } catch (InterruptedException e) {
                            // TODO handle thread interruption
                        }
                        return null;
                    }
                });
                futuresSize++;
            } else {
                break;
            }

        }
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        return responses;
    }

    private static String downloadData(String url) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
                HttpEntity entity1 = response1.getEntity();
                // System.out.println(response1.getStatusLine().getStatusCode() + " " + response1.getStatusLine().getReasonPhrase());
                // do something useful with the response body
                // and ensure it is fully consumed
                String result = new BufferedReader(new InputStreamReader(entity1.getContent())).lines().collect(Collectors.joining());
                EntityUtils.consume(entity1);
                return result;
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException(); // TODO handle exception
        }
    }

}
