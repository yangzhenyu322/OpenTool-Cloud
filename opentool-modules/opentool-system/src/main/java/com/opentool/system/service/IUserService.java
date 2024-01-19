package com.opentool.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opentool.common.core.domain.R;
import com.opentool.system.api.domain.SysUser;
import com.opentool.system.domain.dto.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:39
 */
public interface IUserService extends IService<SysUser> {
    List<SysUser> queryUserList();

    List<Map<String, Object>> getSelectMaps(String startDate, String endDate);

    SysUser findUserByUserName(String username);

    R<?> registerUser(SysUser userInfo, String codeRequestId, String inputCode);

    R<?> checkPhoneNumberRegistered(String phoneNumber);

    R<?> checkUsernameAndPhoneConsistent(String username, String phoneNumber);

    R<?> updateUserPassword(UserInfo userInfo);
}
