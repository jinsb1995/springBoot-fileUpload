package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        // 파일은 데이터베이스에 저장하는게 아니라 로컬 피시에 저장한다.
        // aws, S3등 저장
        MultipartFile oneFile = form.getAttachFile();
        UploadFile attachFile = fileStore.storeFile(oneFile);

        List<MultipartFile> imageFiles = form.getImageFiles();
        List<UploadFile> storeImageFiles = fileStore.storeFiles(imageFiles);

        // 데이터베이스에 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }


    // 이미지 표출해주는 작업
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        // "file:/C:/../fileFolder/asdasd.png   내부 파일에 접근함
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    // 첨부파일 다운로드
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        // 아무나 다운로드 할 수 있게 하면 안되니까 접근 권한을 확인하는 로직을 먼저 짜야 한다.

        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName = {}", uploadFileName);


        // 한글이 꺠질 수 있으므로 encode작업을 해준다.
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
