package cn.bootx.mybatis.table.modify.mybatis.mysq;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Actable 自动配置扫描类
 *
 * @author xxm
 * @date 2023/4/6
 */
@ComponentScan
@MapperScan(annotationClass = Mapper.class)
@ConfigurationPropertiesScan
public class MybatisTableModifyAutoConfiguration {

}
