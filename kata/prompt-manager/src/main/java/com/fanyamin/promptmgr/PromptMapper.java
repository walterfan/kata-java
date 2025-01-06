package com.fanyamin.promptmgr;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PromptMapper {

    @Select("SELECT * FROM prompt")
    @Results(id = "PromptResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "systemPrompt", column = "system_prompt"),
        @Result(property = "userPrompt", column = "user_prompt"),
        @Result(property = "additionalPrompt", column = "additional_prompt"),
        @Result(property = "tags", column = "id", many = @Many(select = "findTagsByPromptId")),
        @Result(property = "variables", column = "id", many = @Many(select = "findVariablesByPromptId"))
    })
    List<Prompt> findAll();

    @Select("SELECT * FROM prompt WHERE id = #{id}")
    @ResultMap("PromptResultMap")
    Prompt findById(String id);

    @Insert("INSERT INTO prompt (name, description, system_prompt, user_prompt, additional_prompt) VALUES (#{name}, #{description}, #{systemPrompt}, #{userPrompt}, #{additionalPrompt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Prompt prompt);

    @Update("UPDATE prompt SET name = #{name}, description = #{description}, system_prompt = #{systemPrompt}, user_prompt = #{userPrompt}, additional_prompt = #{additionalPrompt} WHERE id = #{id}")
    void update(Prompt prompt);

    @Delete("DELETE FROM prompt WHERE id = #{id}")
    void deleteById(String id);

    @Select("SELECT * FROM prompt WHERE name LIKE CONCAT('%', #{name}, '%') OR description LIKE CONCAT('%', #{description}, '%')")
    @ResultMap("PromptResultMap")
    List<Prompt> searchByNameOrDescription(String name, String description);

    @Select("SELECT * FROM prompt_tag WHERE prompt_id = #{id}")
    List<Tag> findTagsByPromptId(String id);

    @Select("SELECT variable_key, variable_value FROM prompt_variable WHERE prompt_id = #{id}")
    List<Map.Entry<String, String>> findVariablesByPromptId(String id);

    @Insert("INSERT INTO Prompt_Tag (prompt_id, tag_id) VALUES (#{promptId}, #{tagId})")
    void insertPromptTag(@Param("promptId") String promptId, @Param("tagId") String tagId);

    @Insert("INSERT INTO Variable (id, prompt_id, variable_key, variable_value) " +
            "VALUES (#{id}, #{promptId}, #{key}, #{value})")
    void insertVariable(@Param("id") String id, @Param("promptId") String promptId,
                        @Param("key") String key, @Param("value") String value);

    @Delete("DELETE FROM Prompt_Tag WHERE prompt_id = #{promptId}")
    void deletePromptTags(String promptId);

    @Delete("DELETE FROM Variable WHERE prompt_id = #{promptId}")
    void deletePromptVariables(String promptId);

}