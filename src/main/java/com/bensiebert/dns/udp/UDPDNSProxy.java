package com.bensiebert.dns.udp;

import com.bensiebert.dns.Configuration;
import com.bensiebert.dns.blocklists.BlocklistManager;
import com.bensiebert.dns.util.DNSQuery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPDNSProxy {

    public UDPDNSProxy() {
        new Thread(this::start).start();
    }

    public void start() {
        try (DatagramSocket socket  = new DatagramSocket(Configuration.LISTEN_PORT)) {
            System.out.println("Proxy DNS UDP Server is running on port " + Configuration.LISTEN_PORT + "/udp");

            while(true) {
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                handleUDPQuery(packet, socket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleUDPQuery(DatagramPacket packet, DatagramSocket socket) throws Exception {
        byte[] query = packet.getData();
        int length = packet.getLength();
        String queriedDomain = DNSQuery.extractDomainFromQuery(query, length);

        if (BlocklistManager.isBlocked(queriedDomain)) {
            byte[] response = DNSQuery.createErrorResponse(query, length);
            InetAddress address = packet.getAddress();
            DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, packet.getPort());
            socket.send(responsePacket);
        } else {
            byte[] response = DNSQuery.forwardQueryToUpstream(query, length);
            InetAddress address = packet.getAddress();
            DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, packet.getPort());
            socket.send(responsePacket);
        }
    }
}
