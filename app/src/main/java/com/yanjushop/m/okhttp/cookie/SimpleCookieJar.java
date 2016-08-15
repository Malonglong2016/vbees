package com.yanjushop.m.okhttp.cookie;

import com.yanjushop.m.utils.L;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public final class SimpleCookieJar implements CookieJar
{
    private final List<Cookie> allCookies = new ArrayList<>();

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        allCookies.addAll(cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url)
    {
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : allCookies)
        {
            L.i("test cookie-------------->>>put header " + cookie);
            if (cookie.matches(url))
            {
                result.add(cookie);
            }
        }
        return result;
    }
}
