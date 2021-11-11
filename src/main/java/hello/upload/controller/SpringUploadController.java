package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "uploadForm";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
                           // ArgumentResolver가 MultipartFile이라는것을 처리? 해준다.
        log.info("request = {} ", request);
        log.info("itemName = {} ", itemName);
        log.info("multipartFile = {} ", file);

        if(!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename();  // 업로드 파일 명
            log.info("파일 저장 fullPath = {}", fullPath);
            file.transferTo(new File(fullPath));    // 파일 저장
         }

        return "uploadForm";
    }
}
