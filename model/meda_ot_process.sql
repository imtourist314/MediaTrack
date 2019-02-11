

create table media_ot_process (
	id INT NOT NULL AUTO_INCREMENT,
	insert_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	update_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	oper_type_id int, -- type operation
	oper_type varchar2(255),   -- text of operation type
	src_path varchar2(255),  
	dest_path varchar2(255),
	status int, // 0 success, 1 running, -1 error
	start_ts TIMESTAMP,
	end_ts TIMESTAMP,
	PRIMARY KEY(id)	
)