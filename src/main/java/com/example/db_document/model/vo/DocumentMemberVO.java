package com.example.db_document.model.vo;
import com.example.db_document.pojo.User;
import lombok.Data;
import java.util.List;

@Data
public class DocumentMemberVO {
    private List<User> members;
}
