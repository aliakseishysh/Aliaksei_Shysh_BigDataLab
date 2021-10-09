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

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class DataDownloader {

    private final AtomicInteger index = new AtomicInteger(0);

    // TODO replace with slf4j
    public List<String> downloadAll(Queue<String> requestUris) {
        BlockingQueue<String> uris = new LinkedBlockingQueue<>(requestUris);
        List<String> responses = Collections.synchronizedList(new ArrayList<>());
        int overdraft = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(overdraft);
        CompletionService completionService = new ExecutorCompletionService(executorService);
        Refill refill = Refill.greedy(overdraft, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(overdraft, refill);
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();

        int futuresSize = 0;
        while(!uris.isEmpty()) {
            if (futuresSize == overdraft) {
                try {
                    completionService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                futuresSize--;
            }
            try {
                bucket.asScheduler().consume(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!uris.isEmpty()) {
                completionService.submit(new Callable() {
                    @Override
                    public Object call() {
                        try {
                            int b = index.addAndGet(1);
                            System.out.println("Started: " + b + ", remaining: " + uris.size());
                            String url = uris.take();
                            String result;
                            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                                HttpGet httpGet = new HttpGet(url);
                                try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                                    HttpEntity entity1 = response.getEntity();

                                    switch (response.getStatusLine().getStatusCode()) {
                                        case 200:
                                            result = new BufferedReader(new InputStreamReader(entity1.getContent()))
                                                    .lines().collect(Collectors.joining());
                                            System.out.println("200: Downloaded " + b);
                                            responses.add(result);
                                            break;
                                        case 429:
                                            System.out.println(response.getStatusLine() + ": Redownloading " + b);
                                            uris.put(url);
                                            call();
                                            break;
                                        default:
                                            System.out.println(response.getStatusLine() + ": Canceling " + b);
                                            break;
                                    }
                                    EntityUtils.consume(entity1);
                                }
                            } catch (IOException e) {
                                throw new UnsupportedOperationException(); // TODO handle exception
                            }


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
        try {
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded: " + responses.size() + "; Requested: " + requestUris.size());
        return responses;
    }

}
