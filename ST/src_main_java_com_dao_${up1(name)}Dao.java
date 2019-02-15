package ${pkg};

import com.ccloomi.core.common.dao.BaseDao;
import com.entity.${pf(prefix)}${up1(name)}Entity;

/**© 2015-${now().format("yyyy")} ${copyright} Copyright
 * 类    名：${up1(name)}Dao
 * 类 描 述：
 * 作    者：${author}
 * 邮    箱：${email}
 * 日    期：${now().format("yyyy-MM-dd HH:mm:ss")}
 */
public interface ${up1(name)}Dao extends BaseDao<${up1(name)}Entity>{
	
}