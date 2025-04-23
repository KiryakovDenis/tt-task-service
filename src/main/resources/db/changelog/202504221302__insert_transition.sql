INSERT INTO tt_tasks.transition (from_state, to_state) VALUES
    ('TO_DO', 'IN_PROGRESS'),
    ('TO_DO', 'DELETE'),
    ('IN_PROGRESS', 'DONE'),
    ('IN_PROGRESS', 'DELETE'),
    ('DONE', 'DELETE');

COMMIT;