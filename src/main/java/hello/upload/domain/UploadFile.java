package hello.upload.domain;

import lombok.Data;

// 업로드 파일 정보 보관
@Data
public class UploadFile {

    private String uploadFileName;
    private String storeFileName;  // uuid같은 랜덤값으로 파일 이름이 겹치지 않게 만들어준다.

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
