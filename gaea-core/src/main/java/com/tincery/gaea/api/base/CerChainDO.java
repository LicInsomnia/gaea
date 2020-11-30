package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.bson.Document;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class CerChainDO extends SimpleBaseDO {
    @Id
    private String id;
    private List<Document> subCerChain;
}
