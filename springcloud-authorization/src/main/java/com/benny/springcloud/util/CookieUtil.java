package com.benny.springcloud.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieUtil {
    /**
     * 设置cookie
     * @param response
     * @param name  cookie名字
     * @param value cookie值
     * @param maxAge cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, String path){
        Cookie cookie = new Cookie(name,value);

        if(maxAge>0) {
            cookie.setMaxAge(maxAge);
        }
        if(StringUtils.isNotEmpty(path)){
            cookie.setPath(path);
        }else{
            cookie.setPath("/");
        }
        response.addCookie(cookie);
    }
    /**
     * 根据名字获取cookie
     * @param request
     * @param name cookie名字
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name){
        Map<String, Cookie> cookieMap = ReadCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie;
        }else{
            return null;
        }
    }


    /**
     * 将cookie封装到Map里面
     * @param request
     * @return
     */
    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request){
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if(null!=cookies){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 新增写domain的cookie方法
     * @param request
     * @param response
     * @param name
     * @param value
     * @param maxAge
     * @param path
     * @param domain
     */
    public static void addCookie(HttpServletRequest request, HttpServletResponse response,String name,String value,int maxAge,String path,String domain){
        Cookie cookie = new Cookie(name,value);

        cookie.setMaxAge(maxAge);
        if(StringUtils.isNotEmpty(path)){
            cookie.setPath(path);
        }else{
            cookie.setPath("/");
        }
        if(StringUtils.isNotEmpty(domain)){
            cookie.setDomain(domain);
        }else{
            cookie.setDomain(request.getServerName());
        }
        response.addCookie(cookie);
    }
}
