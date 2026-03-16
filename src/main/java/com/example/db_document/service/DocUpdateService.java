package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocUpdateMapper;
import com.example.db_document.model.dto.DocUpdateCreateRequest;
import com.example.db_document.model.dto.DocUpdateUpdateRequest;
import com.example.db_document.pojo.DocUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class DocUpdateService {
    @Autowired
    private DocUpdateMapper docUpdateMapper;

    public DocUpdateService() {
    }

    @Transactional(rollbackFor = Exception.class)
    public DocUpdate createDocUpdate(DocUpdateCreateRequest req) {
        Assert.notNull(req, "更新请求不能为空");
        Assert.notNull(req.getDocumentId(), "文档ID不能为空");
        Assert.notNull(req.getVectorClock(), "vectorClock不能为空");
        Assert.notNull(req.getUpdateData(), "updateData不能为空");

        if (req.getParentUpdateId() == null) {
            DocUpdate latest = docUpdateMapper.selectLatestByDocumentId(req.getDocumentId());
            if (latest != null) {
                req.setParentUpdateId(latest.getId());
            }
        }

        DocUpdate docUpdate = new DocUpdate();
        docUpdate.setDocumentId(req.getDocumentId());
        docUpdate.setVectorClock(req.getVectorClock());
        docUpdate.setUpdateData(req.getUpdateData());
        docUpdate.setIsSnapshot(req.getIsSnapshot());
        docUpdate.setParentUpdateId(req.getParentUpdateId());

        int rows = docUpdateMapper.insert(docUpdate);
        if (rows == 0) {
            throw new BusinessException("新增更新失败");
        }
        return docUpdateMapper.selectById(docUpdate.getId());
    }

    public DocUpdate getByDocumentIdAndVectorClock(Long documentId, String vectorClock) {
        Assert.notNull(documentId, "文档ID不能为空");
        Assert.notNull(vectorClock, "vectorClock不能为空");

        DocUpdate docUpdate = docUpdateMapper.selectByDocumentIdAndVectorClock(documentId, vectorClock);
        if (docUpdate == null) {
            throw new IllegalArgumentException("未找到对应的更新记录");
        }
        return docUpdate;
    }

    public List<DocUpdate> listByDocumentId(Long documentId) {
        Assert.notNull(documentId, "文档ID不能为空");
        return docUpdateMapper.selectByDocumentId(documentId);
    }

    public List<DocUpdate> listChildren(Long documentId, Long parentUpdateId) {
        Assert.notNull(documentId, "文档ID不能为空");
        Assert.notNull(parentUpdateId, "parentUpdateId不能为空");
        return docUpdateMapper.selectChildrenByDocumentIdAndParentUpdateId(documentId, parentUpdateId);
    }

    @Transactional(rollbackFor = Exception.class)
    public DocUpdate updateDocUpdate(DocUpdateUpdateRequest req) {
        Assert.notNull(req, "更新请求不能为空");
        Assert.notNull(req.getId(), "更新ID不能为空");

        DocUpdate existing = docUpdateMapper.selectById(req.getId());
        if (existing == null) {
            throw new IllegalArgumentException("更新记录不存在");
        }

        if (req.getVectorClock() == null
                && req.getUpdateData() == null
                && req.getIsSnapshot() == null
                && req.getParentUpdateId() == null) {
            throw new IllegalArgumentException("更新参数不能为空");
        }

        DocUpdate entity = new DocUpdate();
        entity.setId(req.getId());
        entity.setVectorClock(req.getVectorClock());
        entity.setUpdateData(req.getUpdateData());
        entity.setIsSnapshot(req.getIsSnapshot());
        entity.setParentUpdateId(req.getParentUpdateId());

        int rows = docUpdateMapper.updateDynamic(entity);
        if (rows == 0) {
            throw new BusinessException("更新失败");
        }
        return docUpdateMapper.selectById(req.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Assert.notNull(id, "更新ID不能为空");

        int rows = docUpdateMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException("删除失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByDocumentId(Long documentId) {
        Assert.notNull(documentId, "文档ID不能为空");
        docUpdateMapper.deleteByDocumentId(documentId);
    }
}
