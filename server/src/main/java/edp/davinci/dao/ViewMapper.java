/*
 * <<
 *  Davinci
 *  ==
 *  Copyright (C) 2016 - 2019 EDP
 *  ==
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  >>
 *
 */

package edp.davinci.dao;

import edp.davinci.dto.viewDto.*;
import edp.davinci.model.View;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface ViewMapper {

    int insert(View view);

    @Select({"select id from `view` where project_id = #{projectId} and `name` = #{name}"})
    Long getByNameWithProjectId(@Param("name") String name, @Param("projectId") Long projectId);

    ViewWithProjectAndSource getViewWithProjectAndSourceById(@Param("id") Long id);

    ViewWithProjectAndSource getViewWithProjectAndSourceByWidgetId(@Param("widgetId") Long widgetId);

    @Delete({"delete from `view` where id = #{id}"})
    int deleteById(Long id);

    @Select({"select * from `view` where id = #{id}"})
    View getById(Long id);

    @Select({"select id, name, model, variable from `view` where id = #{id}"})
    SimpleView getSimpleViewById(Long id);

    @Update({
            "update `view`",
            "set `name` = #{name,jdbcType=VARCHAR},",
            "`description` = #{description,jdbcType=VARCHAR},",
            "`project_id` = #{projectId,jdbcType=BIGINT},",
            "`source_id` = #{sourceId,jdbcType=BIGINT},",
            "`sql` = #{sql,jdbcType=LONGVARCHAR},",
            "`model` = #{model,jdbcType=LONGVARCHAR},",
            "`variable` = #{variable,jdbcType=LONGVARCHAR},",
            "`config` = #{config,jdbcType=LONGVARCHAR},",
            "`update_by` = #{updateBy,jdbcType=BIGINT},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(View view);

    @Select({"select * from `view` where source_id = #{sourceId}"})
    List<View> getBySourceId(@Param("sourceId") Long sourceId);

    @Select({
            "select v.*,",
            "s.id as 'source.id', s.`name` as 'source.name' from `view` v ",
            "left join source s on s.id = v.source_id ",
            "where v.id = #{id}"
    })
    ViewWithSourceBaseInfo getViewWithSourceBaseInfo(@Param("id") Long id);

    @Select({
            "select v.id, v.`name`, v.`description`, s.name as 'sourceName'",
            "from `view` v ",
            "left join source s on s.id = v.source_id ",
            "where v.project_id = #{projectId}",
            "union all ",
            "select v2.id, v2.`name`, '共享', s.name as 'sourceName'",
            "from view v2, ",
            "rel_role_user rru, ",
            "share_view_info svi, ",
            "source s ",
            "where v2.id = svi.view_id ",
            "and rru.role_id = svi.role_id ",
            "and v2.source_id = s.id ",
            "and #{projectId} in (select id from project where create_by = #{userId}) ",
            "and rru.user_id  = #{userId}"
    })
    List<ViewBaseInfo> getViewBaseInfoByProject(@Param("projectId") Long projectId, @Param("userId") Long userId);

    int insertBatch(@Param("list") List<View> sourceList);

    @Delete({"delete from `view` where project_id = #{projectId}"})
    int deleteByProject(@Param("projectId") Long projectId);

    @Select({
            "SELECT ",
            "	v.*,",
            "	s.`id` 'source.id',",
            "	s.`name` 'source.name',",
            "	s.`description` 'source.description',",
            "	s.`config` 'source.config',",
            "	s.`project_id` 'source.projectId',",
            "	s.`type` 'source.type'",
            "FROM `view` v",
            "	LEFT JOIN project p on p.id = v.project_id",
            "	LEFT JOIN source s on s.id = v.source_id",
            "WHERE v.id = #{id}"
    })
    ViewWithSource getViewWithSource(Long id);

    Set<View> selectByWidgetIds(@Param("widgetIds") Set<Long> widgetIds);

    Set<SimpleView> selectSimpleByWidgetIds(@Param("widgetIds") Set<Long> widgetIds);
}