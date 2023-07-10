package com.opentool.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opentool.dashboard.mapper.LoginLogMapper;
import com.opentool.dashboard.domain.entity.LoginLog;
import com.opentool.dashboard.service.ILoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 登录日志服务
 *
 * @author ZenSheep
 * @date 2023/07/09
 */
@Service
public class LoginLogService extends ServiceImpl<LoginLogMapper, LoginLog>  implements ILoginLogService {
        @Autowired
        private LoginLogMapper loginLogMapper;

        @Override
        public List<LoginLog> getLoginLogList() {
                return loginLogMapper.selectList(null);
        }
}
