package com.teleinfo.captcha.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "captcha_client", indexes = { @Index(unique = true, columnList = "client_id") })
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "bigint(20) COMMENT '主键ID'")
	protected Long id;

	@Column(name = "create_date", updatable = false, columnDefinition = "datetime(0) COMMENT '创建时间'")
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	protected Date createDate;

	@Column(name = "update_date", columnDefinition = "datetime(0) COMMENT '修改时间'")
	protected Date updateDate;

	@Column(name = "client_id", columnDefinition = "varchar(50) COMMENT '客户端ID'")
	private String clientId;

	@Column(name = "client_security", columnDefinition = "varchar(50) COMMENT '客户端签名'")
	private String clientSecurity;

	@PreUpdate
	public void preUpdate() {
		this.setUpdateDate(new Date());
	}

}
