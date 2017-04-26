package com.github.sinedsem.infgres.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.ServerReportRequest;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebService {

    private static final int DAY_IN_SECONDS = 86_400;

    private static final ObjectMapper mapper = new ObjectMapper();

    private volatile long reportRequestStartTime = 0;
    private volatile long reportRequestEndTime = 0;

    //    private String host = "192.168.1.250";
    private String host = "localhost";
    private List<DatamineEntity> generatedData;

    private final CloseableHttpClient httpClient;
    private ExecutorService requestExecutor = Executors.newFixedThreadPool(10);


    @Autowired
    public WebService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }


    public void generate() {

        generatedData = new ArrayList<>();

        List<UUID> nodes = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            nodes.add(UUID.randomUUID());
        }

        for (UUID nodeId : nodes) {

            Random random = new Random();

            List<DatamineEntity> list = new ArrayList<>(10000);

            int interval = 43_200;
            int days = 400;

            long endTime = System.currentTimeMillis() / 1000;
            long startTime = endTime - days * DAY_IN_SECONDS;
            while (startTime < endTime) {
                long nextEndTime = startTime + interval * (random.nextInt(90) + 1);
                DiskStatus diskStatus = new DiskStatus();
                diskStatus.setStartTime(startTime);
                diskStatus.setEndTime(nextEndTime);
                diskStatus.setTotalSpace(50_000_000);
                diskStatus.setUsedSpace(random.nextInt(50_000_000));
                diskStatus.setNodeId(nodeId);
                list.add(diskStatus);
                startTime = nextEndTime;
            }

            generatedData.addAll(expandCompressedEntities(interval, list));
        }
    }


    private List<DatamineEntity> expandCompressedEntities(long interval, List<DatamineEntity> list) {
        List<DatamineEntity> result = new ArrayList<>(list.size() * 100);

        for (DatamineEntity entity : list) {
            long realEndTime = entity.getEndTime();
            long startTime = entity.getStartTime();

            while (startTime + interval <= realEndTime) {
                DatamineEntity diskStatus = new DiskStatus((DiskStatus) entity);
                diskStatus.setEndTime(startTime + interval * 2);
                diskStatus.setStartTime(startTime);

                result.add(diskStatus);
                startTime = startTime + interval;
            }
        }
        return result;
    }

    public void pushGeneratedData(int batchSize) {
        resetStopwatch();

        System.out.println("pushing " + generatedData.size());

        List<Callable<Void>> tasks = new ArrayList<>(generatedData.size() / batchSize);

        for (int i = 0; i < generatedData.size(); i += batchSize) {
            List<DatamineEntity> batch = new ArrayList<>(batchSize);
            for (int j = 0; j < batchSize && i + j < generatedData.size(); j++) {
                batch.add(generatedData.get(i + j));
            }

            tasks.add(() -> {
                pushAll(batch);
                return null;
            });

        }

        try {
            requestExecutor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void pushAll(Collection<? extends DatamineEntity> entities) {

        AgentReport agentReport = new AgentReport();
//        agentReport.setId(null);

        agentReport.getEntities().addAll(entities);

        HttpPost request = new HttpPost("http://" + host + ":9010/listener/report");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(objectToJson(agentReport), "utf-8"));

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetStopwatch() {
        System.out.println("resetting stopwatch");

        HttpGet request = new HttpGet("http://" + host + ":9010/listener/resetStopwatch");
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDb(boolean influx) {
        System.out.println("set db influx " + influx);

        HttpGet request = new HttpGet("http://" + host + ":9010/listener/setDb?influx=" + influx);
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getDuration() {
        HttpGet request = new HttpGet("http://" + host + ":9010/listener/time");
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            String s = new BasicResponseHandler().handleResponse(response);
            response.close();
            return Long.parseLong(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public synchronized byte[] getReport(ServerReportRequest reportRequest) {

        String url = "http://" + host + ":9010/reporter/report";

        CloseableHttpResponse response = null;
        reportRequestStartTime = System.nanoTime();
        reportRequestEndTime = -1;
        try {
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(objectToJson(reportRequest), "utf-8"));
            request.setHeader("Content-Type", "application/json");
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    byte[] result = IOUtils.toByteArray(response.getEntity().getContent());
                    reportRequestEndTime = System.nanoTime();
                    response.close();
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new byte[0];
    }

    public synchronized long getReportDuration() {
        if (reportRequestEndTime == -1) {
            return -1;
        }
        return reportRequestEndTime - reportRequestStartTime;
    }

    private static String objectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Wrong object passed to ObjectMapper - can not convert to JSON", e);
        }
    }

    public byte[] loadNodes() {
        String url = "http://" + host + ":9010/reporter/nodes";

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            try {
                byte[] result = IOUtils.toByteArray(response.getEntity().getContent());
                response.close();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    public byte[] loadGroups() {
        String url = "http://" + host + ":9010/reporter/groups";

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            try {
                byte[] result = IOUtils.toByteArray(response.getEntity().getContent());
                response.close();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    public boolean clearDbs(boolean full) {
        System.out.println("clearing dbs, full=" + full);

        HttpGet request = new HttpGet("http://" + host + ":9010/listener/clearDbs?full=" + full);
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            response.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
