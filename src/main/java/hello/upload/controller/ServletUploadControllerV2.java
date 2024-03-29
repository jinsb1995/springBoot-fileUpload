package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    @Value("${file.dir}")
    private String fileDir;


    @GetMapping("/upload")
    public String newFile() {
        return "uploadForm";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws IOException, ServletException {
        log.info("request == {}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName = {}", itemName);

        Collection<Part> parts = request.getParts();
        log.info("parts = {} ", parts);

        for (Part part : parts) {
            log.info("==== PART ====");
            log.info("name = {}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for(String headerName : headerNames) {
                log.info("header {} : {}", headerName, part.getHeader(headerName));
            }
            // 편의 메서드
            // content-disposition; filename
            // getSubmittedFilename = 파일의 이름 가져오기
            log.info("submittedFilename = {}", part.getSubmittedFileName());
            log.info("size = {}", part.getSize());

            // 데이터 읽기
            InputStream inputStream = part.getInputStream();
            // 스트림을 열어서 파일을 String 형태로 읽는다.
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//            log.info("body = {}", body);

            // 파일에 저장하기 : filename이 있을때
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath = {} ", fullPath);
                // 웹 브라우저에서 톰캣 was 거쳐서 저장된다.
                // part.write는 파일을 저장하게 해준다.
                part.write(fullPath);
            }
        }


        return "uploadForm";
    }
}
