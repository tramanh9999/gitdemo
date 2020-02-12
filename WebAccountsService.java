package com.example.microserviceDemo.services.web;

import com.netflix.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.security.auth.login.AccountNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.cloud.client.ServiceInstance;

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

@Service

public class WebAccountsService {

    @Autowired
    @LoadBalanced
    protected RestTemplate restTemplate;
    protected String serviceUrl;
    protected Logger logger = Logger.getLogger(WebAccountsService.class.getName());

    public WebAccountsService(String serviceUrl) {
        this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
    }

    @PostConstruct
    public void demoOnly() {
        logger.warning("The RestTemplate requests factory is" + restTemplate.getRequestFactory().getClass());

    }

    public Account findByNumber(String number) {
        logger.info("findByNumber() invoked: " + number);

        ServiceInstance chooseService = loadBalancer.choose("accounts-service");
        logger.info("URI of accountservice: ");
        chooseService.getUri();
        logger.info("Host of accountService:" + chooseService.getHost());
        serviceUrl = "http://" + chooseService.getHost();
        return restTemplate.getForObject(serviceUrl + "/accounts/{number}", Account.class, number);
    }

    public List<Account> byOwnerContains(String name) {
        logger.info("byOwnerContains() invoked: for " + name);
        Account[] accounts = null;
        try {
            accounts = restTemplate.getForObject(serviceUrl + "/accounts/owner/{name}", Account[].class, name);

        } catch (HttpClientErrorException err) {

        }
        if (accounts == null || accounts.length == 0) {
            return null;
        }
        return Arrays.asList(accounts);

    }

    @Autowired
    LoadBalancerClient loadBalancer;

    public Account getByNumber(String accountNumber) throws AccountNotFoundException {

        Account account = restTemplate.getForObject(serviceUrl + "/accounts/{number}", Account.class, accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(accountNumber);

        } else {
            return account;
        }
    }
    public Account[] all() {

        Account[] accounts = null;
        try {
            accounts = restTemplate.getForObject(serviceUrl + "/accounts/all", Account[].class);

        } catch (Exception ee) {

        }
        return accounts;
    }
}


//todo : create new microservice  called monster and connect to new db.
