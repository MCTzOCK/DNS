package com.bensiebert.dns.tcp;

import com.bensiebert.dns.Configuration;
import com.bensiebert.dns.blocklists.BlocklistManager;
import com.bensiebert.dns.util.DNSQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPDNSProxy {

    public TCPDNSProxy() {
        new Thread(this::start).start();
    }

    public void start() {
        try (ServerSocket tcpSocket = new ServerSocket(Configuration.LISTEN_PORT)) {
            System.out.println("Proxy DNS TCP Server is running on port " + Configuration.LISTEN_PORT + "/tcp");
            while (true) {
                try (Socket clientSocket = tcpSocket.accept()) {
                    handleTCPQuery(clientSocket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTCPQuery(Socket clientSocket) throws Exception {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        byte[] lengthBytes = new byte[2];
        inputStream.read(lengthBytes);
        int length = ((lengthBytes[0] & 0xFF) << 8) | (lengthBytes[1] & 0xFF);

        byte[] query = new byte[length];
        inputStream.read(query);

        String queriedDomain = DNSQuery.extractDomainFromQuery(query, length);

        if (BlocklistManager.isBlocked(queriedDomain)) {
            byte[] response = DNSQuery.createErrorResponse(query, length);
            outputStream.write(response);
        } else {
            byte[] response = DNSQuery.forwardQueryToUpstream(query, length);
            outputStream.write(response);
        }

        clientSocket.close();
    }

}
