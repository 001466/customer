create table orders
		(
		   id                   bigint not null,
		   type                 varchar(512),
		   custom_name          varchar(512),
		   custom_mobile        varchar(64),
		   custom_email         varchar(512),
		   custom_wechat        varchar(512),
		   custom_qq            varchar(512),
		   custom_content       varchar(512),
		   product_id           varchar(512),
		   product_branch       varchar(512),
		   product_material     varchar(512),
		   deliver_adderss      varchar(512),
		   deliver_status       int comment '0未发货1已发货-1已退货',
		   deliver_express      varchar(512),
		   deliver_express_id   varchar(64),
		   createby             bigint,
		   createtime           datetime,
		   createdate           date,
		   createip             varchar(64),
		   updateby             bigint,
		   updatetime           datetime,
		   updateip             varchar(64),
		   converted            int comment '0未转化1已转化',
		   primary key (id)
		)
		ENGINE=InnoDB DEFAULT CHARSET=utf8;
		