package com.benny.springcloud.mapper;

import com.benny.springcloud.model.OauthCode;
import com.benny.springcloud.model.OauthCodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OauthCodeMapper {
    long countByExample(OauthCodeExample example);

    int deleteByExample(OauthCodeExample example);

    int insert(OauthCode record);

    int insertSelective(OauthCode record);

    List<OauthCode> selectByExampleWithBLOBsWithRowbounds(OauthCodeExample example, RowBounds rowBounds);

    List<OauthCode> selectByExampleWithBLOBs(OauthCodeExample example);

    List<OauthCode> selectByExampleWithRowbounds(OauthCodeExample example, RowBounds rowBounds);

    List<OauthCode> selectByExample(OauthCodeExample example);

    int updateByExampleSelective(@Param("record") OauthCode record, @Param("example") OauthCodeExample example);

    int updateByExampleWithBLOBs(@Param("record") OauthCode record, @Param("example") OauthCodeExample example);

    int updateByExample(@Param("record") OauthCode record, @Param("example") OauthCodeExample example);
}