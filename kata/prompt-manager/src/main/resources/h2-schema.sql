CREATE TABLE prompt (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    system_prompt TEXT,
    user_prompt TEXT,
    additional_prompt TEXT
);

CREATE TABLE tag (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE variable (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    value VARCHAR(255)
);

CREATE TABLE prompt_tag (
    prompt_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    PRIMARY KEY (prompt_id, tag_id),
    FOREIGN KEY (prompt_id) REFERENCES prompt (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);

CREATE TABLE prompt_variable (
    prompt_id UUID NOT NULL,
    variable_id UUID NOT NULL,
    PRIMARY KEY (prompt_id, variable_id),
    FOREIGN KEY (prompt_id) REFERENCES prompt (id),
    FOREIGN KEY (variable_id) REFERENCES variable (id)
);
