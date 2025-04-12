CREATE TABLE tt_tasks.task (
    id SERIAL PRIMARY KEY,
    title VARCHAR NOT NULL,
    description TEXT,
    status VARCHAR NOT NULL,
    dead_line timestamp NOT NULL,
    assignee integer NOT NULL,
    author integer NOT NULL,
    created_at timestamp,
    updated_at timestamp
)