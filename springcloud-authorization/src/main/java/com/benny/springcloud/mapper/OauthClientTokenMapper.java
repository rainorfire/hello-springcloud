package com.benny.springcloud.mapper;

import com.benny.springcloud.model.OauthClientToken;
import com.benny.springcloud.model.OauthClientTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OauthClientTokenMapper {
    long countByExample(OauthClientTokenExample example);

    int deleteByExample(OauthClientTokenExample example);

    int deleteByPrimaryKey(String authenticationId);

    int insert(OauthClientToken record);

    int insertSelective(OauthClientToken record);

    List<OauthClientToken> selectByExampleWithBLOBsWithRowbounds(OauthClientTokenExample example, RowBounds rowBounds);

    List<OauthClientToken> selectByExampleWithBLOBs(OauthClientTokenExample example);

    List<OauthClientToken> selectByExampleWithRowbounds(OauthClientTokenExample example, RowBounds rowBounds);

    List<OauthClientToken> selectByExample(OauthClientTokenExample example);

    OauthClientToken selectByPrimaryKey(String authenticationId);

    int updateByExampleSelective(@Param("record") OauthClientToken record, @Param("example") OauthClientTokenExample example);

    int updateByExampleWithBLOBs(@Param("record") OauthClientToken record, @Param("example") OauthClientTokenExample example);

    int updateByExample(@Param("record") OauthClientToken record, @Param("example") OauthClientTokenExample example);

    int updateByPrimaryKeySelective(OauthClientToken record);

    int updateByPrimaryKeyWithBLOBs(OauthClientToken record);

    int updateByPrimaryKey(OauthClientToken record);
}