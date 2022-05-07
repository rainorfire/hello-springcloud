package com.benny.springcloud.mapper;

import com.benny.springcloud.model.OauthApprovals;
import com.benny.springcloud.model.OauthApprovalsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OauthApprovalsMapper {
    long countByExample(OauthApprovalsExample example);

    int deleteByExample(OauthApprovalsExample example);

    int insert(OauthApprovals record);

    int insertSelective(OauthApprovals record);

    List<OauthApprovals> selectByExampleWithRowbounds(OauthApprovalsExample example, RowBounds rowBounds);

    List<OauthApprovals> selectByExample(OauthApprovalsExample example);

    int updateByExampleSelective(@Param("record") OauthApprovals record, @Param("example") OauthApprovalsExample example);

    int updateByExample(@Param("record") OauthApprovals record, @Param("example") OauthApprovalsExample example);
}