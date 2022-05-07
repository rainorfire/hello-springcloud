package com.benny.springcloud.mapper;

import com.benny.springcloud.model.OauthClientDetails;
import com.benny.springcloud.model.OauthClientDetailsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OauthClientDetailsMapper {
    long countByExample(OauthClientDetailsExample example);

    int deleteByExample(OauthClientDetailsExample example);

    int deleteByPrimaryKey(String clientId);

    int insert(OauthClientDetails record);

    int insertSelective(OauthClientDetails record);

    List<OauthClientDetails> selectByExampleWithRowbounds(OauthClientDetailsExample example, RowBounds rowBounds);

    List<OauthClientDetails> selectByExample(OauthClientDetailsExample example);

    OauthClientDetails selectByPrimaryKey(String clientId);

    int updateByExampleSelective(@Param("record") OauthClientDetails record, @Param("example") OauthClientDetailsExample example);

    int updateByExample(@Param("record") OauthClientDetails record, @Param("example") OauthClientDetailsExample example);

    int updateByPrimaryKeySelective(OauthClientDetails record);

    int updateByPrimaryKey(OauthClientDetails record);
}