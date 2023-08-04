package org.example;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.awt.font.GlyphMetrics;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet(value = "/time")
public class ThymeleafTestController  extends HttpServlet {
    private TemplateEngine engine;


    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getClass().getClassLoader().getResource("/templates").getPath());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);

    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        session.setMaxInactiveInterval(1 * 60);
        resp.setContentType("text/html");

        String currentTime;

        String timezone = req.getParameter("timezone");

        if (timezone == null) {
            currentTime = ZonedDateTime
                    .now(ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ")) + "UTC";
        } else {
            currentTime = ZonedDateTime
                    .now(ZoneId.of(getCookies(req)))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ")) + timezone;
        }

        Context simpleContext = new Context(req.getLocale(),
                Map.of("time", currentTime));

        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private static String getCookies(HttpServletRequest req) {
        String cookies = req.getHeader("Cookie");
        Map<String, String> result = new HashMap<>();

        if (cookies != null) {
            String[] separateCookies = cookies.split(";");
            for (String pair : separateCookies) {
                String[] keyValue = pair.split("=");
                // result.put(keyValue[0], keyValue[1]);
                if (keyValue[0].contains("lastTimezone")) {
                    result.put("lastTimezone", keyValue[1]);
                } else {
                    result.isEmpty();
                }
            }
        } else {
            result.put("lastTimezone", "UTC");
        }
        return result.get("lastTimezone");
    }

}


