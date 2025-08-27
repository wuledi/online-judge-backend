package com.wuledi.metasearch.datasource.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.common.constant.SearchConstant;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.JsonConverter;
import com.wuledi.metasearch.datasource.DataSource;
import com.wuledi.metasearch.model.dto.PictureDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 *
 */
@Service
public class PictureDataSource implements DataSource<PictureDTO> {
    /**
     * 图片搜索
     *
     * @param keyword 搜索关键词
     * @param pageNumber 页码
     * @param pageSize   每页大小
     * @return 图片列表
     */
    @Override
    public Page<PictureDTO> doSearch(String keyword, Long pageNumber, Long pageSize) {
        Long current = (pageNumber - 1) * pageSize;
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", keyword, current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }
        Elements elements = doc.select(".iuscp.isv");
        List<PictureDTO> images = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（murl）
            String m = element.select(".iusc").getFirst().attr("m");
            Map<String, Object> map = JsonConverter.jsonToMap(m);

            String murl = (String) map.get("murl");
            // 取标题
            String title = element.select(".inflnk").getFirst().attr("aria-label");
            PictureDTO image = new PictureDTO();
            image.setName(title);
            image.setUrl(murl);
            images.add(image);
            if (images.size() >= pageSize) {
                break;
            }
        }

        Page<PictureDTO> imagePage = new Page<>(pageNumber, pageSize, images.size());
        imagePage.setRecords(images);
        return imagePage;
    }

    /**
     * 获取数据源类型
     *
     * @return 数据源类型
     */
    @Override
    public String getType() {
        return SearchConstant.PICTURE;
    }
}
