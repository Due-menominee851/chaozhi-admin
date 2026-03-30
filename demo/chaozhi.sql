 CREATE TABLE t_material (                                                                                             
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '物料名称',                                             
    code        VARCHAR(100) NOT NULL COMMENT '物料编码',
    spec        VARCHAR(200) DEFAULT NULL COMMENT '规格型号',                                                           
    unit        VARCHAR(50)  DEFAULT NULL COMMENT '单位',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ENABLE' COMMENT '状态: ENABLE/DISABLE',                                  
    sort        INT          DEFAULT 0 COMMENT '排序',
    remark      TEXT         DEFAULT NULL COMMENT '备注',                                                               
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',                                                           
    UNIQUE KEY uk_code (code)            
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料表';   
	
	
	                                                                                                      
  CREATE TABLE t_purchase_order (                                                                                       
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no       VARCHAR(50)    NOT NULL COMMENT '采购单号',                                                          
    supplier_name  VARCHAR(200)   NOT NULL COMMENT '供应商名称',
    order_date     DATE           NOT NULL COMMENT '下单日期',                                                          
    status         VARCHAR(20)    NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
    total_amount   DECIMAL(12,2)  DEFAULT 0 COMMENT '总金额',                                                           
    remark         TEXT           DEFAULT NULL COMMENT '备注',
    create_time    DATETIME       DEFAULT NULL COMMENT '创建时间',                                                      
    update_time    DATETIME       DEFAULT NULL COMMENT '更新时间',
    UNIQUE KEY uk_order_no (order_no)                                                                                   
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单';                                                           
                                                                                                      
  CREATE TABLE t_purchase_order_item (                                                                                  
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id       BIGINT         NOT NULL COMMENT '采购订单ID',                                                        
    material_id    BIGINT         NOT NULL COMMENT '物料ID',
    material_code  VARCHAR(100)   NOT NULL COMMENT '物料编码',                                                          
    material_name  VARCHAR(100)   NOT NULL COMMENT '物料名称',
    spec           VARCHAR(200)   DEFAULT NULL COMMENT '规格型号',                                                      
    unit           VARCHAR(50)    DEFAULT NULL COMMENT '单位',                                                          
    quantity       DECIMAL(12,4)  NOT NULL COMMENT '采购数量',                                        
    price          DECIMAL(12,4)  NOT NULL COMMENT '采购单价',                                                          
    amount         DECIMAL(12,2)  DEFAULT 0 COMMENT '金额',
    remark         VARCHAR(500)   DEFAULT NULL COMMENT '备注',                                                          
    create_time    DATETIME       DEFAULT NULL COMMENT '创建时间',
    update_time    DATETIME       DEFAULT NULL COMMENT '更新时间',                                                      
    INDEX idx_order_id (order_id)                                                                                       
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细';      
	
	  -- 入库单主表                                                                                                         
  CREATE TABLE t_stock_in_order (                                                                                       
    id          BIGINT       NOT NULL AUTO_INCREMENT,                                                                   
    in_no       VARCHAR(64)  NOT NULL COMMENT '入库单号',                                                               
    source_order_no VARCHAR(64) NOT NULL COMMENT '来源采购单号',                                                        
    warehouse_name  VARCHAR(255) NOT NULL COMMENT '入库仓库',                                                           
    operator_name   VARCHAR(255) COMMENT '经办人',                                                                      
    in_date     DATE         NOT NULL COMMENT '入库日期',                                                               
    status      VARCHAR(32)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PENDING/COMPLETED/CANCELLED',                
    remark      TEXT         COMMENT '备注',                                                                            
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,                                                                 
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,                                     
    PRIMARY KEY (id),                                                                                                   
    UNIQUE KEY uk_in_no (in_no)                                                                                         
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单主表';            
                                                                                                                        
  -- 入库单明细表                      
  CREATE TABLE t_stock_in_order_item (                                                                                  
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    order_id       BIGINT       NOT NULL COMMENT '入库单ID',                                                            
    source_item_id BIGINT       COMMENT '来源采购订单明细ID',
    material_id    BIGINT       NOT NULL COMMENT '物料ID',                                                              
    material_code  VARCHAR(64)  NOT NULL COMMENT '物料编码',                                                            
    material_name  VARCHAR(255) NOT NULL COMMENT '物料名称',                                                            
    spec           VARCHAR(255) COMMENT '规格型号',                                                                     
    unit           VARCHAR(64)  COMMENT '单位',                                                       
    order_qty      DECIMAL(18,2) COMMENT '采购数量',                                                                    
    in_qty         DECIMAL(18,2) NOT NULL COMMENT '本次入库数量',                                                       
    qualified_qty  DECIMAL(18,2) COMMENT '合格数量',                                                                    
    remark         VARCHAR(255) COMMENT '明细备注',                                                                     
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP,                                            
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,                                  
    PRIMARY KEY (id),                                                                                                   
    KEY idx_order_id (order_id),                                                                      
    KEY idx_source_item_id (source_item_id)                                                                             
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单明细表';                            
                                                                                                      
  -- 库存主表                                                                                                           
  CREATE TABLE t_inventory (           
    id              BIGINT       NOT NULL AUTO_INCREMENT,                                             
    material_id     BIGINT       NOT NULL COMMENT '物料ID',                                                             
    material_code   VARCHAR(64)  NOT NULL COMMENT '物料编码',
    material_name   VARCHAR(255) NOT NULL COMMENT '物料名称',                                                           
    spec            VARCHAR(255) COMMENT '规格型号',                                                                    
    unit            VARCHAR(64)  COMMENT '单位',                                                      
    warehouse_code  VARCHAR(64)  COMMENT '仓库编码（暂保留，与仓库主数据打通后补充）',                                  
    warehouse_name  VARCHAR(255) NOT NULL COMMENT '仓库名称',                                                           
    current_stock   DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '当前库存',                              
    available_stock DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '可用库存',                                                
    locked_stock    DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '锁定库存',
    safety_stock    DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '安全库存',                                                
    warning_status  VARCHAR(32)  NOT NULL DEFAULT 'NORMAL' COMMENT '预警状态：NORMAL/LOW/ZERO',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,                                                             
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,                                 
    PRIMARY KEY (id),                                                                                                   
    UNIQUE KEY uk_material_warehouse (material_id, warehouse_name)                                                      
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存主表';              
                                                                                                                        
  -- 库存流水表                          
  CREATE TABLE t_inventory_log (                                                                                        
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    material_id   BIGINT       NOT NULL COMMENT '物料ID',                                                               
    material_code VARCHAR(64)  NOT NULL COMMENT '物料编码',
    material_name VARCHAR(255) NOT NULL COMMENT '物料名称',                                                             
    warehouse_name VARCHAR(255) NOT NULL COMMENT '仓库名称',                                                            
    change_type   VARCHAR(64)  NOT NULL COMMENT '变动类型：PURCHASE_IN / SALE_OUT 等',                
    change_qty    DECIMAL(18,2) NOT NULL COMMENT '变动数量',                                                            
    before_qty    DECIMAL(18,2) NOT NULL COMMENT '变动前库存',                                                          
    after_qty     DECIMAL(18,2) NOT NULL COMMENT '变动后库存',                                                          
    ref_order_no  VARCHAR(64)  COMMENT '关联单据号',                                                                    
    remark        VARCHAR(255) COMMENT '备注',                                                                          
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,                                                               
    PRIMARY KEY (id),                                                                                                   
    KEY idx_material_id (material_id),                                                                
    KEY idx_ref_order_no (ref_order_no)                                                                                 
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存流水表';
	
	
	  -- =============================================                                                                      
  -- 权限体系建表 DDL                                                                                                   
  -- =============================================                                                    
                                                                                                                        
  CREATE TABLE sys_user (                                                                                               
    id          BIGINT       NOT NULL AUTO_INCREMENT,                                                 
    username    VARCHAR(64)  NOT NULL UNIQUE,                                                                           
    password    VARCHAR(128) NOT NULL,                                                                                  
    real_name   VARCHAR(64)  DEFAULT NULL,                                                            
    avatar      VARCHAR(256) DEFAULT NULL,                                                                              
    status      VARCHAR(20)  NOT NULL DEFAULT 'ENABLE' COMMENT 'ENABLE/DISABLE',                                        
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,                                                                 
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,                                     
    PRIMARY KEY (id)                                                                                                    
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';                                       
                                                                                                                        
  CREATE TABLE sys_role (                
    id          BIGINT       NOT NULL AUTO_INCREMENT,                                                                   
    role_code   VARCHAR(64)  NOT NULL UNIQUE,
    role_name   VARCHAR(64)  NOT NULL,                                                                                  
    status      VARCHAR(20)  NOT NULL DEFAULT 'ENABLE',
    remark      VARCHAR(256) DEFAULT NULL,                                                                              
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,                                                                 
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,                   
    PRIMARY KEY (id)                                                                                                    
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
                                                                                                                        
  CREATE TABLE sys_permission (          
    id              BIGINT       NOT NULL AUTO_INCREMENT,                                                               
    permission_code VARCHAR(128) NOT NULL UNIQUE,
    permission_name VARCHAR(128) NOT NULL,                                                            
    module_code     VARCHAR(64)  DEFAULT NULL,                                                                          
    remark          VARCHAR(256) DEFAULT NULL,
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,                                                             
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,                                 
    PRIMARY KEY (id)                                                                                  
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限码表';                                                           
                                       
  CREATE TABLE sys_menu (                                                                                               
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    parent_id       BIGINT       NOT NULL DEFAULT 0,                                                                    
    menu_name       VARCHAR(64)  NOT NULL,
    route_name      VARCHAR(64)  DEFAULT NULL,                                                        
    route_path      VARCHAR(128) DEFAULT NULL,                                                                          
    component       VARCHAR(128) DEFAULT NULL COMMENT 'BasicLayout 或视图路径',
    icon            VARCHAR(64)  DEFAULT NULL,                                                                          
    sort            INT          NOT NULL DEFAULT 0,
    type            VARCHAR(20)  NOT NULL DEFAULT 'MENU' COMMENT 'CATALOG/MENU/BUTTON',                                 
    permission_code VARCHAR(128) DEFAULT NULL,                                                                          
    status          TINYINT      NOT NULL DEFAULT 1,                                                                    
    keep_alive      TINYINT      NOT NULL DEFAULT 0,                                                                    
    hide_in_menu    TINYINT      NOT NULL DEFAULT 0,                                                                    
    affix_tab       TINYINT      NOT NULL DEFAULT 0,                                                  
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,                                                             
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),                                                                                                   
    KEY idx_parent_id (parent_id)                                                                                       
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';                                           
                                                                                                                        
  CREATE TABLE sys_user_role (         
    id      BIGINT NOT NULL AUTO_INCREMENT,                                                           
    user_id BIGINT NOT NULL,                                                                                            
    role_id BIGINT NOT NULL,
    PRIMARY KEY (id),                                                                                                   
    UNIQUE KEY uk_user_role (user_id, role_id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';                                                     
                                         
  CREATE TABLE sys_role_permission (                                                                                    
    id            BIGINT NOT NULL AUTO_INCREMENT,
    role_id       BIGINT NOT NULL,                                                                                      
    permission_id BIGINT NOT NULL,       
    PRIMARY KEY (id),                                                                                 
    UNIQUE KEY uk_role_permission (role_id, permission_id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';                                                     
  
  CREATE TABLE sys_role_menu (                                                                                          
    id      BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL,                                                                                            
    menu_id BIGINT NOT NULL,             
    PRIMARY KEY (id),                                                                                                   
    UNIQUE KEY uk_role_menu (role_id, menu_id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';                                   
                                                                                                                        
  
  -- =============================================                                                                      
  -- 初始化数据（admin 用户 / super 角色 / 全量权限 / 全量菜单）
  -- =============================================                                                                      
                                                                                                                        
  -- 用户（密码明文 123456）                                                                                            
  INSERT INTO sys_user (id, username, password, real_name, avatar, status)                                              
  VALUES (1, 'admin', '123456', '管理员',                                                                               
          'https://unpkg.com/@vbenjs/static-source@0.1.7/source/avatar-v1.webp', 'ENABLE');           
                                                                                                                        
  -- 角色                                                                                                               
  INSERT INTO sys_role (id, role_code, role_name, status, remark)                                                       
  VALUES (1, 'super', '超级管理员', 'ENABLE', '拥有全部权限');                                                          
                                                                                                                        
  -- 用户角色关联                                                                                     
  INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);                                                           
                                                                                                                        
  -- 权限码                                                                                           
  INSERT INTO sys_permission (permission_code, permission_name, module_code) VALUES                                     
  -- 物料管理                          
  ('material:view',          '查看物料',     'material'),                                                               
  ('material:add',           '新增物料',     'material'),                                                               
  ('material:edit',          '编辑物料',     'material'),                                                               
  ('material:delete',        '删除物料',     'material'),                                                               
  -- 采购订单                                                                                         
  ('purchaseOrder:view',     '查看采购订单', 'purchaseOrder'),                                                          
  ('purchaseOrder:detail',   '采购订单详情', 'purchaseOrder'),                                                          
  ('purchaseOrder:add',      '新增采购订单', 'purchaseOrder'),                                        
  ('purchaseOrder:edit',     '编辑采购订单', 'purchaseOrder'),                                                          
  ('purchaseOrder:delete',   '删除采购订单', 'purchaseOrder'),
  ('purchaseOrder:submit',   '提交采购订单', 'purchaseOrder'),                                                          
  ('purchaseOrder:approve',  '审核采购订单', 'purchaseOrder'),                                                          
  ('purchaseOrder:close',    '关闭采购订单', 'purchaseOrder'),                                        
  -- 入库管理                                                                                                           
  ('stockInOrder:view',      '查看入库单',   'stockInOrder'),
  ('stockInOrder:detail',    '入库单详情',   'stockInOrder'),                                                           
  ('stockInOrder:add',       '新增入库单',   'stockInOrder'),
  ('stockInOrder:edit',      '编辑入库单',   'stockInOrder'),                                                           
  ('stockInOrder:delete',    '删除入库单',   'stockInOrder'),
  ('stockInOrder:submit',    '提交入库单',   'stockInOrder'),                                                           
  ('stockInOrder:confirm',   '入库确认',     'stockInOrder'),                                                           
  ('stockInOrder:cancel',    '作废入库单',   'stockInOrder');                                         
                                                                                                                        
  -- 角色权限关联（super 拥有全部权限）
  INSERT INTO sys_role_permission (role_id, permission_id)                                                              
  SELECT 1, id FROM sys_permission;      
                                                                                                                        
  -- 菜单                                
  INSERT INTO sys_menu (id, parent_id, menu_name, route_name, route_path, component, icon, sort, type, keep_alive,      
  affix_tab) VALUES                                                                                                     
  -- Dashboard 目录                                                                                   
  (1, 0, 'Dashboard', 'Dashboard', '/dashboard', 'BasicLayout', 'lucide:layout-dashboard', 100, 'CATALOG', 0, 0),       
  (2, 1, '分析页', 'Analytics', '/analytics', '/dashboard/analytics/index', 'lucide:area-chart', 10, 'MENU', 1, 1),     
  (3, 1, '工作台', 'Workspace', '/workspace', '/dashboard/workspace/index', 'carbon:workspace', 20, 'MENU', 0, 0),      
  -- 物料管理                                                                                                           
  (4, 0, '物料管理', 'Material', '/material', 'BasicLayout', 'lucide:package', 1100, 'CATALOG', 0, 0),                  
  (5, 4, '物料管理', 'MaterialPage', 'index', '/material/index', NULL, 10, 'MENU', 1, 0),                               
  -- 采购订单                                                                                                           
  (6, 0, '采购订单', 'PurchaseOrder', '/purchase-order', 'BasicLayout', 'lucide:shopping-cart', 1200, 'CATALOG', 0, 0), 
  (7, 6, '采购订单', 'PurchaseOrderPage', 'index', '/purchase-order/index', NULL, 10, 'MENU', 1, 0),                    
  -- 入库管理                                                                                                           
  (8, 0, '入库管理', 'StockInOrder', '/stock-in-order', 'BasicLayout', 'lucide:package-check', 1300, 'CATALOG', 0, 0),  
  (9, 8, '入库管理', 'StockInOrderPage', 'index', '/stock-in-order/index', NULL, 10, 'MENU', 1, 0);                     
                                                                                                                        
  -- 角色菜单关联（super 拥有全部菜单）                                                                                 
  INSERT INTO sys_role_menu (role_id, menu_id)                                                                          
  SELECT 1, id FROM sys_menu;               
	
	
	 -- =============================================
  -- 追加执行：系统管理模块                                                                                             
  -- =============================================                                                                      
                                                                                                                        
  -- 新增权限码                                                                                                         
  INSERT INTO sys_permission (permission_code, permission_name, module_code) VALUES                   
  ('system:user:view',           '查看用户',     'system:user'),                                                        
  ('system:user:add',            '新增用户',     'system:user'),                                                        
  ('system:user:edit',           '编辑用户',     'system:user'),                                                        
  ('system:user:delete',         '删除用户',     'system:user'),                                                        
  ('system:user:assignRole',     '分配角色',     'system:user'),                                                        
  ('system:role:view',           '查看角色',     'system:role'),                                                        
  ('system:role:add',            '新增角色',     'system:role'),                                                        
  ('system:role:edit',           '编辑角色',     'system:role'),                                                        
  ('system:role:delete',         '删除角色',     'system:role'),                                                        
  ('system:role:assignPermission','分配权限',    'system:role');                                                        
                                                                                                      
  -- 角色权限关联（super 追加新权限）                                                                                   
  INSERT INTO sys_role_permission (role_id, permission_id)
  SELECT 1, id FROM sys_permission                                                                                      
  WHERE permission_code LIKE 'system:%';                                                                                
                                                                                                      
  -- 新增菜单                                                                                                           
  INSERT INTO sys_menu (id, parent_id, menu_name, route_name, route_path, component, icon, sort, type) VALUES
  (10, 0,  '系统管理', 'System',     '/system',      'BasicLayout',           'lucide:settings',  9000, 'CATALOG'),     
  (11, 10, '用户管理', 'SystemUser', 'user/index',   '/system/user/index',    NULL,               10,   'MENU'),        
  (12, 10, '角色管理', 'SystemRole', 'role/index',   '/system/role/index',    NULL,               20,   'MENU');        
                                                                                                                        
  -- 角色菜单关联（super 追加新菜单）                                                                                   
  INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 10), (1, 11), (1, 12);
