package com.fanyamin.promptmgr;
import org.apache.ibatis.annotations.*;

public interface PromptMapper {

    @Select("SELECT * FROM Prompt WHERE id = #{id}")
    Prompt getPromptById(@Param("id") String id);

    @Insert("INSERT INTO Prompt (id, name, description, system_prompt, user_prompt, additional_prompt) " +
            "VALUES (#{id}, #{name}, #{description}, #{systemPrompt}, #{userPrompt}, #{additionalPrompt})")
    void insertPrompt(Prompt prompt);

    @Update("UPDATE Prompt SET name = #{name}, description = #{description}, " +
            "system_prompt = #{systemPrompt}, user_prompt = #{userPrompt}, " +
            "additional_prompt = #{additionalPrompt} WHERE id = #{id}")
    void updatePrompt(Prompt prompt);

    @Delete("DELETE FROM Prompt WHERE id = #{id}")
    void deletePrompt(@Param("id") String id);

    @Insert("INSERT INTO Prompt_Tag (prompt_id, tag_id) VALUES (#{promptId}, #{tagId})")
    void insertPromptTag(@Param("promptId") String promptId, @Param("tagId") String tagId);

    @Insert("INSERT INTO Variable (id, prompt_id, variable_key, variable_value) " +
            "VALUES (#{id}, #{promptId}, #{key}, #{value})")
    void insertVariable(@Param("id") String id, @Param("promptId") String promptId,
                        @Param("key") String key, @Param("value") String value);

    @Delete("DELETE FROM Prompt_Tag WHERE prompt_id = #{promptId}")
    void deletePromptTags(@Param("promptId") String promptId);

    @Delete("DELETE FROM Variable WHERE prompt_id = #{promptId}")
    void deletePromptVariables(@Param("promptId") String promptId);
}