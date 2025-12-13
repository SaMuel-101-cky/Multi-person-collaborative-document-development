package com.example.db_document.servcie;

import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.UserMapper;
import com.example.db_document.model.vo.SharedContentVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharedService {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private UserMapper userMapper;

    public SharedService(){
    }

    public SharedContentVO getSharedDocuments(Long userId) {
        SharedContentVO vo = new SharedContentVO();
        User user = userMapper.selectById(userId);

        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }

        List<Document> sharedDocs = documentMapper.selectSharedDocuments(userId);
        vo.setDocuments(sharedDocs);
        return vo;
    }
}
