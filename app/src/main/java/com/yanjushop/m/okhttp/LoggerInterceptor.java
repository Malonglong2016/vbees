package com.yanjushop.m.okhttp;

import com.orhanobut.logger.Logger;
import com.yanjushop.m.utils.L;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * ProgectName：application
 * Creator：long
 * Date：2016/6/30
 */
public class LoggerInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            //===>response log
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            HttpUrl url = clone.request().url();
            int code = clone.code();
            Protocol protocol = clone.protocol();
            String message = clone.message();
            ResponseBody body = clone.body();
            String contentType = "";
            String content = "";
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    contentType = mediaType.toString();
                    if (isText(mediaType)) {
                        String resp = body.string();
                        content =  resp;
                        body = ResponseBody.create(mediaType, resp);
                        Logger.d("url---->%s \n " +
                                "code---->%d \n" +
                                "protocol---->%s \n" +
                                "message---->%s \n" +
                                "contentType---->%s \n" +
                                "content---->%s \n", url.toString(), code, protocol.toString(), message, contentType, content);
                        return response.newBuilder().body(body).build();
                    } else {
                        content = " maybe [file part] , too large too print , ignored!";
                    }
                }
            }

            L.d("url---->%s \n " +
                    "code---->%d \n" +
                    "protocol---->%s \n" +
                    "message---->%s \n" +
                    "contentType---->%s \n" +
                    "content---->%s \n", url.toString(), code, protocol.toString(), message, contentType, content);
        } catch (Exception e) {
            Logger.d(e.getMessage());
//            e.printStackTrace();
        }
        return response;
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
