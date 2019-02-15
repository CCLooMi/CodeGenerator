package ${pkg};

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ccloomi.core.common.bean.Message;
import com.entity.${pf(prefix)}${up1(name)}Entity;
import com.service.${up1(name)}Service;

/**© 2015-${now().format("yyyy")} ${copyright} Copyright
 * 类    名：${up1(name)}Controller
 * 类 描 述：
 * 作    者：${author}
 * 邮    箱：${email}
 * 日    期：${now().format("yyyy-MM-dd HH:mm:ss")}
 */
@Controller
@RequestMapping("/${name}")
public class ${up1(name)}Controller extends GenericController{
	@Autowired
	private ${up1(name)}Service ${name}Service;
	
	@RequestMapping("/byPage")
	@ResponseBody
	public Map<String, Object>byPage(@RequestBody Map<String, Object>map){
		return ${name}Service.find${up1(name)}sByPage(map);
	}
	@RequestMapping("/add")
	@ResponseBody
	public Message add(@RequestBody ${up1(name)}Entity ${name}){
		Object id=${name}Service.save(${name});
		if(id==null){
			return Msg.error("添加${comment}失败");
		}else{
			return Msg.ok(id);
		}
	}
	@RequestMapping("/update")
	@ResponseBody
	public Message update(@RequestBody ${up1(name)}Entity ${name}){
		int i=${name}Service.update(${name});
		if(i>0){
			return Msg.ok();
		}else{
			return Msg.error("修改${comment}失败");
		}
	}
	@RequestMapping("/remove")
	@ResponseBody
	public Message remove(@RequestBody ${up1(name)}Entity ${name}){
		int i=${name}Service.delete(${name}.getId());
		if(i>0){
			return Msg.ok();
		}else{
			return Msg.error("删除${comment}失败");
		}
	}
}

