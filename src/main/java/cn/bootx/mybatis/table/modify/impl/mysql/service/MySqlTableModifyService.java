package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.constants.UpdateType;
import cn.bootx.mybatis.table.modify.domain.BaseTableMap;
import cn.bootx.mybatis.table.modify.domain.CreateTableParam;
import cn.bootx.mybatis.table.modify.domain.TableConfig;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * MySQL表信息修改处理
 * @author xxm
 * @date 2023/4/7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MySqlTableModifyService {

    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    /**
     * 根据map结构创建表
     * @param newTableMap 用于存需要创建的表名+结构
     */
    public void createTable(Map<String, TableConfig> newTableMap) {
        // 做创建表操作
        for (Map.Entry<String, TableConfig> entry : newTableMap.entrySet()) {
            Map<String, TableConfig> map = new HashMap<>();
            map.put(entry.getKey(), entry.getValue());
            log.info("开始创建表：" + entry.getKey());
            mysqlTableModifyMapper.createTable(map);
            log.info("完成创建表：" + entry.getKey());
        }
    }

    /**
     * 根据传入的map创建或修改表结构
     * @param baseTableMap 操作sql的数据结构
     */
    public void modifyTableConstruct(BaseTableMap baseTableMap, UpdateType updateType) {

        // CREATE模式不做删除和修改操作
        if (updateType != UpdateType.CREATE) {
            // 1. 删除要变更主键的表的原来的字段的主键
            dropFieldsKey(baseTableMap.getDropKeyTable());
            // 2. 删除索引和约束
            dropIndexAndUnique(baseTableMap.getDropIndexAndUniqueTable());
            // 3. 删除字段
            removeFields(baseTableMap.getRemoveTable());
            // 4. 修改表注释
            modifyTableComment(baseTableMap.getModifyTableProperty());
            // 5. 修改字段类型等
            modifyFields(baseTableMap.getModifyTable());
        }

        // 6. 添加新的字段
        addFields(baseTableMap.getAddTable());

        // 7. 创建索引
        addIndex(baseTableMap.getAddIndexTable());

        // 8. 创建约束
        addUnique(baseTableMap.getAddUniqueTable());

    }


    /**
     * 根据map结构删除索引和唯一约束
     * @param dropIndexAndUniqueMap 用于删除索引和唯一约束
     */
    private void dropIndexAndUnique(Map<String, TableConfig> dropIndexAndUniqueMap) {
        if (dropIndexAndUniqueMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : dropIndexAndUniqueMap.entrySet()) {
                String key = entry.getKey();
                TableConfig value = entry.getValue();
                for (Object obj : value.getList()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(key, obj);
                    log.info("开始删除表" + key + "中的索引" + obj);
                    mysqlTableModifyMapper.dropTabelIndex(map);
                    log.info("完成删除表" + key + "中的索引" + obj);
                }
            }
        }
    }

    /**
     * 根据map结构创建索引
     * @param addIndexMap 用于创建索引和唯一约束
     */
    private void addIndex(Map<String, TableConfig> addIndexMap) {
        if (addIndexMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : addIndexMap.entrySet()) {
                for (Object obj : entry.getValue().getList()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(entry.getKey(), obj);
                    CreateTableParam fieldProperties = (CreateTableParam) obj;
                    if (null != fieldProperties.getFiledIndexName()) {
                        log.info("开始创建表" + entry.getKey() + "中的索引" + fieldProperties.getFiledIndexName());
                        mysqlTableModifyMapper.addTableIndex(map);
                        log.info("完成创建表" + entry.getKey() + "中的索引" + fieldProperties.getFiledIndexName());
                    }
                }
            }
        }
    }

    /**
     * 根据map结构创建唯一约束
     * @param addUniqueMap 用于创建索引和唯一约束
     */
    private void addUnique(Map<String, TableConfig> addUniqueMap) {
        if (addUniqueMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : addUniqueMap.entrySet()) {
                for (Object obj : entry.getValue().getList()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(entry.getKey(), obj);
                    CreateTableParam fieldProperties = (CreateTableParam) obj;
                    if (null != fieldProperties.getFiledUniqueName()) {
                        log.info("开始创建表" + entry.getKey() + "中的唯一约束" + fieldProperties.getFiledUniqueName());
                        mysqlTableModifyMapper.addTableUnique(map);
                        log.info("完成创建表" + entry.getKey() + "中的唯一约束" + fieldProperties.getFiledUniqueName());
                    }
                }
            }
        }
    }

    /**
     * 根据map结构修改表中的字段类型等
     * @param modifyTableMap 用于存需要更新字段类型等的表名+结构
     */
    private void modifyFields(Map<String, TableConfig> modifyTableMap) {
        // 做修改字段操作
        if (modifyTableMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : modifyTableMap.entrySet()) {
                for (Object obj : entry.getValue().getList()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(entry.getKey(), obj);
                    CreateTableParam fieldProperties = (CreateTableParam) obj;
                    log.info("开始修改表" + entry.getKey() + "中的字段" + fieldProperties.getFieldName());
                    mysqlTableModifyMapper.modifyTableField(map);
                    log.info("完成修改表" + entry.getKey() + "中的字段" + fieldProperties.getFieldName());
                }
            }
        }
    }

    /**
     * 根据map结构删除表中的字段
     * @param removeTableMap 用于存需要删除字段的表名+结构
     */
    private void removeFields(Map<String, TableConfig> removeTableMap) {
        // 做删除字段操作
        if (removeTableMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : removeTableMap.entrySet()) {
                for (Object obj : entry.getValue().getList()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(entry.getKey(), obj);
                    String fieldName = (String) obj;
                    log.info("开始删除表" + entry.getKey() + "中的字段" + fieldName);
                    mysqlTableModifyMapper.removeTableField(map);
                    log.info("完成删除表" + entry.getKey() + "中的字段" + fieldName);
                }
            }
        }
    }

    /**
     * 根据map结构更新表的注释
     * @param modifyTableCommentMap 用于存需要更新表名+注释
     */
    private void modifyTableComment(Map<String, TableConfig> modifyTableCommentMap) {
        // 做更新的表注释
        if (modifyTableCommentMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : modifyTableCommentMap.entrySet()) {
                for (String property : entry.getValue().getMap().keySet()) {
                    Map<String, TableConfig> map = new HashMap<>();
                    Map<String, Object> tcMap = new HashMap<>();
                    Object value = entry.getValue().getMap().get(property);
                    tcMap.put(property, value);
                    map.put(entry.getKey(), new TableConfig(tcMap));
                    log.info("开始更新表" + entry.getKey() + "的" + property + "为" + value);
                    mysqlTableModifyMapper.modifyTableProperty(map);
                    log.info("完成更新表" + entry.getKey() + "的" + property + "为" + value);
                }
            }
        }
    }

    /**
     * 根据map结构对表中添加新的字段
     * @param addTableMap 用于存需要增加字段的表名+结构
     */
    private void addFields(Map<String, TableConfig> addTableMap) {
        // 做增加字段操作
        if (addTableMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : addTableMap.entrySet()) {
                for (Object obj : entry.getValue().getList()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(entry.getKey(), obj);
                    CreateTableParam fieldProperties = (CreateTableParam) obj;
                    log.info("开始为表" + entry.getKey() + "增加字段" + fieldProperties.getFieldName());
                    mysqlTableModifyMapper.addTableField(map);
                    log.info("完成为表" + entry.getKey() + "增加字段" + fieldProperties.getFieldName());
                }
            }
        }
    }

    /**
     * 根据map结构删除要变更表中字段的主键
     * @param dropKeyTableMap 用于存需要删除主键的表名+结构
     */
    private void dropFieldsKey(Map<String, TableConfig> dropKeyTableMap) {
        // 先去做删除主键的操作，这步操作必须在增加和修改字段之前！
        if (dropKeyTableMap.size() > 0) {
            for (Map.Entry<String, TableConfig> entry : dropKeyTableMap.entrySet()) {
                for (Object obj : entry.getValue().getList()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(entry.getKey(), obj);
                    CreateTableParam fieldProperties = (CreateTableParam) obj;
                    log.info("开始为表" + entry.getKey() + "删除主键" + fieldProperties.getFieldName());
                    mysqlTableModifyMapper.dropKeyTableField(map);
                    log.info("完成为表" + entry.getKey() + "删除主键" + fieldProperties.getFieldName());
                }
            }
        }
    }

}
