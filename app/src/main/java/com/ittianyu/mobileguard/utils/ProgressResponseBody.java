package com.ittianyu.mobileguard.utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by yu.
 * okhttp3 intercept
 */
public class ProgressResponseBody extends ResponseBody {

    public interface ProgressListener {
        void onPreExecute(long contentLength);
        void update(long totalBytes, boolean done);
        void onFailure(Call call, IOException e);
    }

    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody,
                                ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        if (progressListener != null) {
            progressListener.onPreExecute(contentLength());
        }
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytes = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytes += bytesRead != -1 ? bytesRead : 0;
                if (null != progressListener) {
                    progressListener.update(totalBytes, bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
