package com.cgcl.yangdang.controller;

import com.cgcl.yangdang.netty.ServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author Liu Cong
 * @since 2019-04-04
 */
@Slf4j
@Controller
public class HomeController {
    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/clearData")
    @ResponseBody
    public void clearData(HttpServletResponse response) throws IOException {
        ServerHandler.clearLineMap();
        response.sendRedirect("/");
    }
}
