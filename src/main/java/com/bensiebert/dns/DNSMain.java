package com.bensiebert.dns;

import com.bensiebert.dns.blocklists.BlocklistManager;

import java.io.IOException;

public class DNSMain {

    public static void main(String[] args) throws IOException {
        BlocklistManager.loadBlocklists();
        ProxyDNSServer server = new ProxyDNSServer();
    }
}
