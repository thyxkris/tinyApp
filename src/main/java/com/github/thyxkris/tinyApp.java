package com.github.thyxkris;


import spark.Request;
import spark.Response;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.threadPool;

public class tinyApp {

    private static List<String> infoList = new ArrayList<>();
    private static Boolean isHealthy = true;

    public static void main(String[] args) {

        int maxThreads = 32;
        int minThreads = 2;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        get("/", tinyApp::greeting);
        get("/cpu", tinyApp::cpu);
        get("/ram", tinyApp::ram);
        get("/ram/info", tinyApp::ramInfo);
        get("/ram/clean", tinyApp::ramClean);
        get("/health", tinyApp::health);
        get("/health/flip", tinyApp::flipHealth);
        get("/details", tinyApp::details);
    }

    private static String greeting(Request request, Response response) throws UnknownHostException {
        StringBuilder sb = new StringBuilder();
        String myHostname = "";
        try {
            myHostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            myHostname = e.getMessage().substring(0,e.getMessage().indexOf(":"));
        }
        sb.append("Hello/Hallo/Salut/NiHao By:").append(myHostname).append("<br/>");
        String sourceIP = request.ip();
        sb.append("Receive Request From: ").append(sourceIP).append("<br/>");
        return sb.toString();
    }


    private static String cpu(Request request, Response response) {
        long currentTime = System.currentTimeMillis();
        BigInteger computation = fib(10000);
        return "fib function took " + (System.currentTimeMillis() - currentTime) + " milliseconds";
    }


    private static String ram(Request request, Response response) {
        try {
            String myBigString = new String(new char[10000000]);
            infoList.add(myBigString);
            return ramInfo(request, response);
        } catch (OutOfMemoryError e) {
            return "OutOfMemory Exception!";
        }
    }

    private static String ramInfo(Request request, Response response) {
        return getRamInfo();
    }

    private static String ramClean(Request request, Response response) {
        infoList.clear();
        return ramInfo(request, response);
    }


    private static String health(Request request, Response response) {
        if (isHealthy) {
            return "Healthy!";
        } else {
            response.status(401);
            return "Not healthy";
        }
    }

    private static String flipHealth(Request request, Response response) {
        isHealthy = !isHealthy;
        return health(request, response);
    }


    private static String details(Request request, Response response) throws UnknownHostException {
        StringBuilder sb = new StringBuilder();
        String myHostname = "";
        try {
            myHostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            myHostname = e.getMessage().substring(0,e.getMessage().indexOf(":"));
        }
        sb.append("Hello/Hallo/Salut/NiHao By:").append(myHostname).append("<br/>");
        String sourceIP = request.ip();
        sb.append("Receive Request From: ").append(sourceIP).append("<br/>");
        sb.append("Request Headers are: ").append("<br/>");
        request.headers().forEach(header -> sb.append(header).append(": ").append(request.headers(header)).append("<br/>"));
        return sb.toString();
    }

    // Fibonacci sequence to perform some long computations
    private static BigInteger fib(long nth) {
        nth = nth - 1;
        long count = 0;
        BigInteger first = BigInteger.ZERO;
        BigInteger second = BigInteger.ONE;

        BigInteger third = null;
        while (count < nth) {
            third = new BigInteger(first.add(second).toString());
            first = new BigInteger(second.toString());
            second = new BigInteger(third.toString());
            count++;
        }

        return third;
    }


    // get some RAM statistics
    private static String getRamInfo() {

        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: ").append(format.format(freeMemory / 1024)).append("<br/>");
        sb.append("allocated memory: ").append(format.format(allocatedMemory / 1024)).append("<br/>");
        sb.append("max memory: ").append(format.format(maxMemory / 1024)).append("<br/>");
        sb.append("total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)).append("<br/>");
        return sb.toString();
    }

}
