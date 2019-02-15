package ${pkg};

import java.util.Map;
import com.ccloomi.core.common.service.BaseService;
import com.entity.${pf(prefix)}${up1(name)}Entity;

/**© 2015-${now().format("yyyy")} ${copyright} Copyright
 * 类    名：${up1(name)}Service
 * 类 描 述：
 * 作    者：${author}
 * 邮    箱：${email}
 * 日    期：${now().format("yyyy-MM-dd HH:mm:ss")}
 */
public interface ${up1(name)}Service extends BaseService{

	/**描述：
	 * 作者：${author}
	 * 日期：${now().format("yyyy-MM-dd HH:mm:ss")}
	 * @param map
	 * @return
	 */
	public Map<String, Object> find${up1(name)}sByPage(Map<String, Object> map);
	
	/**描述：
	 * 作者：${author}
	 * 日期：${now().format("yyyy-MM-dd HH:mm:ss")}
	 * @param ${name}
	 * @return
	 */
	public Object save(${up1(name)}Entity ${name});
	/**
	 * 描述：
	 * 作者：${author}
	 * 日期：${now().format("yyyy-MM-dd HH:mm:ss")}
	 * @param ${name}
	 * @return
	 */
	public int add(${up1(name)}Entity ${name});
	/**描述：
	 * 作者：${author}
	 * 日期：${now().format("yyyy-MM-dd HH:mm:ss")}
	 * @param ${name}
	 * @return
	 */
	public int update(${up1(name)}Entity ${name});

	/**描述：
	 * 作者：${author}
	 * 日期：${now().format("yyyy-MM-dd HH:mm:ss")}
	 * @param id
	 * @return
	 */
	public int delete(Object id);
}
