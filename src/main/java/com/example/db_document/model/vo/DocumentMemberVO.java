package com.example.db_document.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class DocumentMemberVO {
    private List<UserVO> members;
}
