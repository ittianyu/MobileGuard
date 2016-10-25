package com.ittianyu.mobileguard.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by yu.
 * File utils. Need OkIo.jar
 * Save file by inputstream or String
 */
public class FileUtils {
    /**
     * save the all content in inputstream to file when success to save.
     * Whether success, it will close the inputstream.
     * throw NullPointerException if in or file is null.
     * @param file the path of file
     * @param in the content inputstream
     * @throws IOException if failed to save
     */
    public static void saveFileWithStream(File file, InputStream in) throws IOException {
        // check the arguments
        if(null == in) {
            throw new NullPointerException("in can't be null");
        }
        if(null == file) {
            in.close();
            throw new NullPointerException("file can't be null");
        }
        // save file
        // sink
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        // source
        BufferedSource source = Okio.buffer(Okio.source(in));
        try{
            // write to sink
            source.readAll(sink);
        }catch (IOException e) {
            throw new IOException(e);
        } finally {
            // close
            source.close();
            sink.close();
            in.close();
            System.out.println("saveFileWithStream: stream closed");
        }
    }


    /**
     * save the all content to file when success to save.
     * throw NullPointerException if file is null.
     * throw IllegalArgumentException if data is empty
     * @param file the path of file
     * @param data the content
     * @throws IOException if failed to save
     */
    public static void saveFileWithString(File file, String data) throws IOException {
        // check the arguments
        if(TextUtils.isEmpty(data)) {
            throw new IllegalArgumentException("data can't be empty");
        }
        if(null == file) {
            throw new NullPointerException("file can't be null");
        }
        // save file

        // sink
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        try{
            // write to sink
            sink.writeUtf8(data);
        }catch (IOException e) {
            throw new IOException(e);
        } finally {
            // close
            sink.close();
            System.out.println("saveFileWithStream: stream closed");
        }
    }

}
