package ${pkg};

import org.springframework.stereotype.Service;

import com.ccloomi.core.common.dao.AbstractDao;
import com.dao.${up1(name)}Dao;
import com.entity.${pf(prefix)}${up1(name)}Entity;

/**© 2015-${now().format("yyyy")} ${copyright} Copyright
 * 类    名：${up1(name)}DaoImp
 * 类 描 述：
 * 作    者：${author}
 * 邮    箱：${email}
 * 日    期：${now().format("yyyy-MM-dd HH:mm:ss")}
 */
@Service("${name}Dao")
public class ${up1(name)}DaoImpl extends AbstractDao<${up1(name)}Entity> implements ${up1(name)}Dao{
	
}
