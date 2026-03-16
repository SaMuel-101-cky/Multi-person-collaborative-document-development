package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.FolderMapper;
import com.example.db_document.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

@Service
public class FolderService {
    @Autowired
    private FolderMapper folderMapper;

    public FolderService(){
    }

    //parentId 为 null 表示创建在根目录下,前端会维护一个currentFolderId来提供参数
    public Folder createFolder(String name, Long creatorId, Long parentId){
        Assert.hasText(name, "文件夹名称不能为空");
        Assert.notNull(creatorId, "创建者ID不能为空");

        int count = folderMapper.countByNameAndParentId(name.trim(), parentId);
        if (count > 0) {
            throw new IllegalArgumentException("同一目录下已存在同名文件夹");
        }

        Folder folder = new Folder();
        folder.setName(name.trim());
        folder.setCreatorId(creatorId);
        folder.setParentId(parentId);

        folderMapper.insert(folder);
        System.out.println("文件夹创建成功: " + name + "\n创建者ID: "+creatorId+"\n父文件夹ID: " + parentId);
        return folder;
    }

    //软删除
    public void softDeleteFolder(Long folderId){
        Assert.notNull(folderId, "文件夹ID不能为空");
        Folder folder = folderMapper.selectById(folderId);
        if (folder == null) {
            throw new IllegalArgumentException("文件夹不存在");
        }

        // 这里可以添加更多的业务逻辑，比如检查文件夹是否为空等，需要级联删除，甚至要判断权限！
        int rows = folderMapper.softDeleteById(folderId);
        if (rows == 0) {
            throw new BusinessException("删除失败，可能已被删除");
        }

        System.out.println("文件夹删除成功: ID " + folderId);
    }

    public void moveFolder(Long folderId, Long newParentId){
        Assert.notNull(folderId, "文件夹ID不能为空");
        Folder folder = folderMapper.selectById(folderId);
        if (folder == null) {
            throw new IllegalArgumentException("文件夹不存在");
        }

        // 检查新父文件夹是否存在
        if (newParentId != null) {
            Folder newParentFolder = folderMapper.selectById(newParentId);
            if (newParentFolder == null) {
                throw new IllegalArgumentException("目标父文件夹不存在");
            }
        }

        if (Objects.equals(newParentId, folder.getParentId())) {
            throw new IllegalArgumentException("此文件夹已在目标文件夹中");
        }

        // 检查同一目录下是否存在同名文件夹(可以补充逻辑判断，是否要替换什么的)
        int count = folderMapper.countByNameAndParentId(folder.getName(), newParentId);
        if (count > 0) {
            throw new IllegalArgumentException("目标目录下已存在同名文件夹");
        }

        int rows = folderMapper.changeParentId(folderId, newParentId);
        if (rows == 0) {
            throw new BusinessException("移动失败");
        }

        System.out.println("文件夹移动成功: ID " + folderId + " 到新父文件夹ID " + newParentId);
    }

    public Folder getFolderById(Long folderId){
        Assert.notNull(folderId, "文件夹ID不能为空");
        Folder folder = folderMapper.selectById(folderId);
        if (folder == null) {
            throw new IllegalArgumentException("文件夹不存在");
        }
        return folder;
    }
}
