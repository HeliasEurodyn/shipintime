package com.enershare.rest;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class GZipHelper {

    public static boolean looksLikeGzip(byte[] data) {
        // GZIP magic numbers: 0x1f, 0x8b
        return data != null && data.length >= 2
                && (data[0] == (byte) 0x1f) && (data[1] == (byte) 0x8b);
    }

    public static byte[] gunzip(byte[] data) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = gis.read(buf)) != -1) out.write(buf, 0, r);
            return out.toByteArray();
        }
    }

    public static byte[] inflate(byte[] data) throws IOException {
        try (InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(data));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = iis.read(buf)) != -1) out.write(buf, 0, r);
            return out.toByteArray();
        }
    }


    public static String decompressGzip(byte[] compressed, String charset) {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
             InputStreamReader reader = new InputStreamReader(gis, Charset.forName(charset));
             BufferedReader in = new BufferedReader(reader)) {

            StringBuilder outStr = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                outStr.append(line);
            }
            return outStr.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress GZIP response", e);
        }
    }

}
