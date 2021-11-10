package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }


    /**
     * 파일 저장하는 곳
     * MultipartFile로 내 로컬에 저장한 다음에
     * UploadFile을 반환한다.
     */
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);

    }

    /**
     * createStoreFileName
     * 서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 UUID를 사용해서 충돌하지 않도록 한다.
     */
    private String createStoreFileName(String originalFilename) {
        // 서버에 저장하는 파일명
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);  // 확장자는 가져오는게 편하다(운영할 때)
        String storeFileName = uuid + "." + ext;  // uuid + png를 합쳐서 새로운 uuid 파일명을 만든다.
        return storeFileName;
    }

    /**
     * extractExt
     * 확장자를 별도로 추출해서 서버 내부에서 관리하는 파일명에도 붙여준다.
     * 예를 들어서 고객이 a.png라는 이름으로 업로드 하면 29481fhw1-89w1-02954-01298-129juf9.png 와 같이 저장한다.
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos + 1);
        return ext;
    }

}
