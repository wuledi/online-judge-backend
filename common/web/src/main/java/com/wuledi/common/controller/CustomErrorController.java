package com.wuledi.common.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 自定义错误处理控制器
 *
 * @author wuledi
 */
@Controller // 控制器
@RequestMapping("/errors/*") // 路径
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PAGE = "errors/index";

    @RequestMapping("404")
    public ModelAndView errorCode404() {
        // 设置跳转路径,该文件路径: src/main/view/templates/errors/index.html
        ModelAndView mav = new ModelAndView(ERROR_PAGE);
        mav.addObject("title", "知行录 | 404");
        mav.addObject("code", 404);
        mav.addObject("message", "页面不存在");
        return mav;
    }

    @RequestMapping("500")
    public ModelAndView errorCode500() {
        // 设置跳转路径,该文件路径: src/main/view/templates/errors/index.html
        ModelAndView mav = new ModelAndView(ERROR_PAGE);
        mav.addObject("title", "知行录 | 500");
        mav.addObject("code", 500);
        mav.addObject("message", "系统错误");
        return mav;
    }

    @RequestMapping("block_handler")
    public ModelAndView blockHandler() {
        // 设置跳转路径,该文件路径: src/main/view/templates/errors/index.html
        ModelAndView mav = new ModelAndView(ERROR_PAGE);
        mav.addObject("title", "知行录 | 限流");
        mav.addObject("code", 429);
        mav.addObject("message", "请求过于频繁，请稍后再试");
        return mav;
    }
}