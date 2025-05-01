CREATE TABLE tt_tasks.transition(
    from_state VARCHAR,
    to_state VARCHAR,
    CONSTRAINT transition_uk1 UNIQUE (from_state, to_state)
    );