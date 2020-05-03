CREATE OR REPLACE TRIGGER savingsTrigger
AFTER UPDATE ON account
FOR EACH ROW
DECLARE
amt number;
penalty checking.penalty%type;

BEGIN

  select count(*) into amt from checking where accountnum = :NEW.accountnum;
  if(amt = 0 and :new.balance < 0)
  then RAISE_APPLICATION_ERROR(-20001, 'Savings accounts cannot be overdrawn');
  end if;

END;
