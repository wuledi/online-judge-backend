package com.wuledi.ai.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * PDF 生成工具
 *
 * @author wuledi
 */
@Service
public class PDFGenerationTool {

    /**
     * 生成 PDF 文件
     *
     * @param fileName 文件名
     * @param content  内容
     * @return 生成 PDF 文件的信息
     */
    @Tool(description = "Generate a PDF file with given content", returnDirect = false)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = System.getProperty("user.dir") + "/tmp" + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                // 使用类加载器从资源目录加载字体
                InputStream fontStream = getClass().getClassLoader().getResourceAsStream("static/fonts/xinsongti.ttf");
                if (fontStream == null) {
                    throw new IOException("Font file not found in resources");
                }

                // 读取字体数据并创建PdfFont对象
                byte[] fontData = IoUtil.readBytes(fontStream);

                PdfFont font = PdfFontFactory.createFont(fontData,
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                // 使用内置中文字体
//                PdfFont font = PdfFontFactory.createFont();
                document.setFont(font);
                // 创建段落
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
