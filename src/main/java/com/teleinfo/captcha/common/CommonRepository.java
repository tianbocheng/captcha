package com.teleinfo.captcha.common;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CommonRepository<E, ID extends Serializable>
		extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {

}
