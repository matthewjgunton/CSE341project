create or replace TRIGGER display_salary_changes
AFTER UPDATE ON account
FOR EACH ROW
DECLARE
amt number;
penal checking.penalty%type;

BEGIN
    dbms_output.put_line('penalty');
  select count(*) into amt from checking where accountnum = :NEW.accountnum;
  if(amt = 1 and :new.balance < 1500)
    then select penalty into penal from checking where accountnum = :new.accountnum;
    if(:new.balance < 1500)
    then penal := penal + 50;
    end if;
    if(:new.balance < 0)
    then penal := penal + 100;
    end if;
    dbms_output.put_line('penalty'||penal||'  '||:new.accountnum);
    update Checking set penalty = penal where accountnum = :new.accountnum;
  end if;

END;
