package com.dianrong.common.uniauth.cas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dianrong.common.uniauth.common.bean.dto.TenancyDto;
import com.dianrong.common.uniauth.common.bean.request.TenancyParam;
import com.dianrong.common.uniauth.common.client.UniClientFacade;
import com.dianrong.common.uniauth.common.cons.AppConstants;
import com.dianrong.common.uniauth.common.exp.UniauthCommonException;

/**
 * . 租户处理相关的service
 * 
 * @author wanglin
 */
@Service("tenancyService")
public class TenancyService extends BaseService {
    @Autowired
    private UniClientFacade uniClientFacade;

    /**
     * . 默认使用的tenancy
     */
    private volatile TenancyDto defaultTenancy;

    private Object lock = new Object();

    public List<TenancyDto> getAllTenancies() {
        TenancyParam param = new TenancyParam();
        param.setStatus(AppConstants.STATUS_ENABLED);
        return uniClientFacade.getTenancyResource().searchTenancy(param).getData();
    }

    /**
     * . 通过tenancyCode 获取对应的tenancy(可用的)
     * 
     * @param tenancyCode 查询的tenancyCode
     * @return tenancy or null
     */
    public TenancyDto getTenancyByCode(String tenancyCode) {
        TenancyParam param = new TenancyParam();
        param.setCode(tenancyCode);
        return uniClientFacade.getTenancyResource().queryEnableTenancyByCode(param).getData();
    }

    /**
     * 获取uniauth默认的租户的信息, 即点融网
     * @return 默认的TenancyDto
     */
    public TenancyDto getDefaultTenancy() {
        if (this.defaultTenancy == null) {
            synchronized (lock) {
                if (this.defaultTenancy == null) {
                    this.defaultTenancy = uniClientFacade.getTenancyResource().queryDefaultTenancy().getData();
                }
            }
        }
        return this.defaultTenancy;
    }

    public String getDefaultTenancyCode() {
        TenancyDto temp = getDefaultTenancy();
        if (temp != null) {
            return temp.getCode();
        }
        throw new UniauthCommonException("default Tenancy can not be null");
    }

    public boolean isDefaultTenancy(String tenancyCode) {
        if (!StringUtils.hasText(tenancyCode)) {
            return false;
        }
        TenancyDto temp = getDefaultTenancy();
        if (temp != null) {
            return tenancyCode.trim().equalsIgnoreCase(temp.getCode());
        }
        return false;
    }
}
