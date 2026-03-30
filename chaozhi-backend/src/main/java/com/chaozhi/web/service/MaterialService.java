package com.chaozhi.web.service;

import com.chaozhi.web.dto.MaterialDTO;
import com.chaozhi.web.query.MaterialQuery;
import com.chaozhi.web.vo.MaterialVO;
import com.chaozhi.web.vo.PageVO;

import java.util.List;

public interface MaterialService {

    PageVO<MaterialVO> page(MaterialQuery query);

    List<MaterialVO> listEnabled();

    void create(MaterialDTO dto);

    void update(MaterialDTO dto);

    void delete(Long id);
}
