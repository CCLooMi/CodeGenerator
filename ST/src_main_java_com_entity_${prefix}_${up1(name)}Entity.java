package ${pkg};
${imports(columns)}
import com.ccloomi.core.annotation.Table;
import com.ccloomi.core.common.entity.IdEntity;

/**© 2015-${now().format("yyyy")} ${copyright} Copyright
 * 类    名：${up1(name)}Entity
 * 类 描 述：
 * 作    者：${author}
 * 邮    箱：${email}
 * 日    期：${now().format("yyyy-MM-dd HH:mm:ss")}
 */
@Table(name="${tableName}")
public class ${up1(name)}Entity extends ${IF(hasId,"Id","Base")}Entity{
	private static final long serialVersionUID = 1L;
	#for(column:columns)
	${properties(column)}
	#end
	#for(column:columns)
	/**获取 ${column[4]}*/
	public ${javaType(column)} get${up1(column[0])}() {
		return ${column[0]};
	}
	/**设置 ${column[4]}*/
	public void set${up1(column[0])}(${javaType(column)} ${column[0]}) {
		this.${column[0]} = ${column[0]};
	}
	#end
}
