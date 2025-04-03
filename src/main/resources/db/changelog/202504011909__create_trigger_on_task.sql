CREATE OR REPLACE FUNCTION tt_tasks.trg_task_ins_upd() RETURNS trigger AS $trg_task_ins_upd$
    BEGIN
        IF TG_OP = 'INSERT' THEN
            NEW.created_at := CURRENT_TIMESTAMP;
        ELSIF TG_OP = 'UPDATE' THEN
            NEW.updated_at := CURRENT_TIMESTAMP;
        END IF;
    RETURN NEW;
    END;
$trg_task_ins_upd$ LANGUAGE plpgsql;

CREATE TRIGGER trg_task_ins_upd
BEFORE INSERT OR UPDATE
    ON tt_tasks.task
FOR EACH ROW
EXECUTE FUNCTION tt_tasks.trg_task_ins_upd();