package com.wuledi.common.banner;

import com.github.lalyos.jfiglet.FigletFont;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * 自定义Banner
 *
 * @author wuledi
 */
@Slf4j
public class SpringBootBanner implements Banner {

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        // 自定义ASCII艺术Banner
        try {
            String asciiArt = FigletFont.convertOneLine("wuledi");
            System.out.println(asciiArt);
            out.println(" :: https://wuledi.com ::      (v1.0.0)    \n");
        } catch (Exception e) {
           log.error("生成Banner失败", e);
        }
    }
}