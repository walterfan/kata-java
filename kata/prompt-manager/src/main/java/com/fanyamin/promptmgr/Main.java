package com.fanyamin.promptmgr;

import java.util.UUID;

import org.apache.ibatis.session.SqlSession;


public class Main {
    public static void main(String[] args) {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PromptMapper mapper = session.getMapper(PromptMapper.class);

            // Create a new Prompt
            Prompt prompt = new Prompt();
            prompt.setName("Sample Prompt");
            prompt.setDescription("This is a test description.");
            prompt.setSystemPrompt("System message.");
            prompt.setUserPrompt("User message.");
            prompt.setAdditionalPrompt("Additional message.");
            mapper.save(prompt);

            // Add tags and variables
            Tag tag = new Tag();
            tag.setId(UUID.randomUUID().toString());
            tag.setName("Test Tag");
            mapper.insertPromptTag(prompt.getId(), tag.getId()); // Assume tag ID is 1

            mapper.insertVariable(UUID.randomUUID().toString(), prompt.getId(), "key1", "value1");
            session.commit();

            // Fetch Prompt by ID
            Prompt fetchedPrompt = mapper.findById(prompt.getId());
            System.out.println(fetchedPrompt);
        }
    }
}