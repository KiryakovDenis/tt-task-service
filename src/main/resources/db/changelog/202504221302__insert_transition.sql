INSERT INTO tt_state_machine.transition (from_state, to_state) VALUES
    ('TO_DO', 'IN_PROGRESS'),
    ('TO_DO', 'DELETE'),
    ('IN_PROGRESS', 'DONE'),
    ('IN_PROGRESS', 'DELETE'),
    ('DONE', 'DELETE');

COMMIT;