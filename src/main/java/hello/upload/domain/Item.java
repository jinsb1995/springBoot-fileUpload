package hello.upload.domain;

import lombok.Data;

import java.util.List;

// 데이터베이스에 저장되는 곳
@Data
public class Item {

    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;

}
