package com.dianrong.common.uniauth.server.ldap.ipa.dao;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import javax.naming.Name;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Repository;

import com.dianrong.common.uniauth.common.util.Assert;
import com.dianrong.common.uniauth.server.ldap.ipa.entity.User;
import com.dianrong.common.uniauth.server.ldap.ipa.support.IPAConstants;
import com.dianrong.common.uniauth.server.ldap.ipa.support.IPAUtil;

@Repository
public class UserDao {

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * 调用IPA数据服务,进行身份认证
     * 
     * @param account 账号
     * @param password 密码
     * @throws AuthenticationException 账号和密码不匹配
     * @throws EmptyResultDataAccessException 账号不存在
     */
    public void authenticate(String account, String password) {
        ldapTemplate.authenticate(query().base(IPAConstants.USER_BASE).where("uid").is(account), password);
    }

    /**
     * 根据uid获取用户的基本信息
     * 
     * @param uid 不能为空
     */
    public User getUserByAccount(String uid) {
        Assert.notNull(uid);
        Name dn = IPAUtil.buildDn(IPAConstants.USER_BASE, "uid", uid);
        return ldapTemplate.findByDn(dn, User.class);
    }
}
