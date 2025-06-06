package com.bensiebert.dns;

public class Configuration {

    public static final String UPSTREAM_DNS = System.getProperty("dns.upstream", "1.1.1.1");
    public static final Integer LISTEN_PORT = Integer.parseInt(System.getProperty("dns.port", "53"));
    public static final String BLOCKLISTS = System.getProperty("dns.blocklists", "/opt/dns");
}
