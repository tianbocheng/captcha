package com.teleinfo.captcha.repository;

import com.teleinfo.captcha.common.CommonRepository;
import com.teleinfo.captcha.pojo.Client;

public interface ClientRepository extends CommonRepository<Client, Long> {

	Client findByClientId(String clientId);

}
