package com.bensiebert.dns;

import com.bensiebert.dns.tcp.TCPDNSProxy;
import com.bensiebert.dns.udp.UDPDNSProxy;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ProxyDNSServer {

    public ProxyDNSServer() {
        new UDPDNSProxy();
        new TCPDNSProxy();
    }

}
