package org.fkjava.commons.repository;

import org.fkjava.commons.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    
	//不需要实现此方法，spring会自动实现（利用动态代理实现）
	//最终生成语句： select*from wx1user where open_id = ?
	//丙炔会自动把查询结果转换为user类的实例
	User findByOpenId(String openId);

}
