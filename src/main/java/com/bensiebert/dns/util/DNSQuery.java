package com.bensiebert.dns.util;

import com.bensiebert.dns.Configuration;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSQuery {

    public static String extractDomainFromQuery(byte[] query, int length) {
        if(length < 12) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        int position = 12; // Skip header

        while(position < length) {
            int labelLength = query[position] & 0xFF;
            if (labelLength == 0) {
                break;
            }

            position++;
            if (position + labelLength > length) {
                return ""; // Invalid query
            }
            String label = new String(query, position, labelLength);
            if (result.length() > 0) {
                result.append(".");
            }
            result.append(label);
            position += labelLength;

            if (position >= length) {
                break; // End of query
            }
        }

        if (result.length() > 0) {
            return result.toString();
        }

        return "";
    }

    public static byte[] createErrorResponse(byte[] query, int length) {
        byte[] res = new byte[length];
        System.arraycopy(query, 0, res, 0, length);

        res[2] = (byte) 0x81;
        res[3] = (byte) 0x80;
        res[5] = (byte) 0x00;
        res[6] = (byte) 0x00;
        res[7] = (byte) 0x01;

        return res;
    }

    public static byte[] forwardQueryToUpstream(byte[] query, int length) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress upstreamAddress = InetAddress.getByName(Configuration.UPSTREAM_DNS);

            DatagramPacket packet = new DatagramPacket(query, length, upstreamAddress, 53);
            socket.send(packet);

            byte[] responseBuffer = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            return responsePacket.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
