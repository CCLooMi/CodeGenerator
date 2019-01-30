package com.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ccloomi.core.common.bean.EntityInfo;
import com.ccloomi.core.common.service.AbstractService;
import com.ccloomi.core.util.EntityUtil;
import com.ccloomi.core.util.StringUtil;
import com.dao.${up1(name)}Dao;
import com.entity.${pf(prefix)}${up1(name)}Entity;
import com.service.${up1(name)}Service;

/**© 2015-${now().format("yyyy")} ${copyright} Copyright
 * 类    名：${up1(name)}ServiceImpl
 * 类 描 述：
 * 作    者：${author}
 * 邮    箱：${email}
 * 日    期：${now().format("yyyy-MM-dd HH:mm:ss")}
 */
@Service("${name}Service")
public class ${up1(name)}ServiceImpl extends AbstractService implements ${up1(name)}Service{
	@Autowired
	private ${up1(name)}Dao ${name}Dao;
	@Override
	public Map<String, Object> find${up1(name)}sByPage(Map<String, Object> map) {
		return byPage(map,(sm,m)->{
			String keyword=(String) map.get("keyword");
			String orderBy=(String) map.get("orderBy");
			sm.SELECT("*")
			.FROM(${up1(name)}Entity.class, "${name}");
			EntityInfo ei=EntityUtil.getEntityInfo(${up1(name)}Entity.class);
			for(String p:ei.getPropertiesA()) {
				if(map.containsKey(p)) {
					sm.AND("${name}."+p+"=?",map.get(p));
				}
			}
			if(keyword!=null){
				keyword=StringUtil.toLike(keyword);
				List<String>ls=new ArrayList<>();
				StringBuilder sb=new StringBuilder();
				sb.append('(');
				for(String p:ei.getPropertiesA()) {
					if("id".equalsIgnoreCase(p)) {
						continue;
					}
					sb.append("${name}.").append(p)
					.append(" LIKE ? OR ");
					ls.add(keyword);
				}
				sb.append("FALSE)");
				String[]sa=new String[ls.size()];
				ls.toArray(sa);
				sm.AND(sb.toString(), sa);
			}
			if(orderBy!=null){
				sm.ORDER_BY("${name}."+orderBy);
			}
		});
	}
	@Override
	public Object save(${up1(name)}Entity ${name}) {
		return ${name}Dao.save(${name});
	}
	@Override
	public int add(${up1(name)}Entity ${name}) {
		return ${name}Dao.add(${name});
	}
	@Override
	public int update(${up1(name)}Entity ${name}) {
		return ${name}Dao.update(${name});
	}
	@Override
	public int delete(Object id) {
		return ${name}Dao.delete(id);
	}
}
