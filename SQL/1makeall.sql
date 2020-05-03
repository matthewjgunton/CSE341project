CREATE TABLE customer
             (
                          dob    DATE not null,
                          first  VARCHAR(30) not null,
                          last   VARCHAR(988) not null,
                          middle VARCHAR(30) not null,
                          id     INT,
                          CONSTRAINT pk PRIMARY KEY(id)
             );CREATE TABLE loan
             (
                          loannum    INT,
                          balance    NUMERIC(12,2) CONSTRAINT loanBal CHECK (balance >= 0),
                          interest   INT CONSTRAINT loanInt CHECK (interest >= 0),
                          paymentdue NUMERIC(12,2) CONSTRAINT loanPayDue CHECK (paymentdue >= 0),
                          principal  NUMERIC(12,2) CONSTRAINT loanPrincipal CHECK (principal >= 0),
                          CONSTRAINT pk1 PRIMARY KEY(loannum)
             );CREATE TABLE account
             (
                          balance      NUMERIC(12,2) CONSTRAINT notNeg4 CHECK (balance >= 0),
                          interestrate INT CONSTRAINT notNeg3 CHECK (interestrate >= 0),
                          accountnum   INT,
                          CONSTRAINT pk2 PRIMARY KEY(accountnum)
             );CREATE TABLE checking
             (
                          accountnum     INT,
                          penalty        NUMERIC(12,2) CONSTRAINT notNeg2 CHECK (penalty >= 0),
                          minimumbalance NUMERIC(10,2) CONSTRAINT notNeg CHECK (minimumbalance > 0),
                          CONSTRAINT pk333 PRIMARY KEY(accountnum),
                          CONSTRAINT fk FOREIGN KEY(accountnum) REFERENCES account(accountnum)
             );CREATE TABLE location
             (
                          state      VARCHAR(20) not null,
                          city       VARCHAR(30) not null,
                          zip        NUMERIC(5,0) not null,
                          locationid INT,
                          TYPE       VARCHAR(30) not null,
                          CONSTRAINT pk3 PRIMARY KEY(locationid)
             );CREATE TABLE transactions
             (
                          txnum      INT,
                          amount     NUMERIC(7,2) CONSTRAINT txAmount CHECK (amount > 0),
                          CONSTRAINT pk4 PRIMARY KEY(txnum)

             );CREATE TABLE loantx
             (
                          txnum                 INT,
                          accountnum INT,
                          loanNum               INT,
                          CONSTRAINT pk50 PRIMARY KEY(txnum),
                          CONSTRAINT fk77 FOREIGN KEY(loanNum) REFERENCES loan(loanNum),
                          CONSTRAINT fk50 FOREIGN KEY(txnum) REFERENCES transactions(txnum),
                          CONSTRAINT fk401 FOREIGN KEY(accountnum) REFERENCES account(accountnum)
             );CREATE TABLE purchasetx
             (
                          txnum                 INT,
                          txType                INT CONSTRAINT type CHECK (txType >= 1 AND txType <= 2),
                          VENDOR                VARCHAR(50),
                          CONSTRAINT pk551 PRIMARY KEY(txnum),
                          CONSTRAINT fk551 FOREIGN KEY(txnum) REFERENCES transactions(txnum)
             );CREATE TABLE deposittx
             (
                          txnum                 INT,
                          accountnum INT,
                          CONSTRAINT pk51 PRIMARY KEY(txnum),
                          CONSTRAINT fk51 FOREIGN KEY(txnum) REFERENCES transactions(txnum),
                          CONSTRAINT fk402 FOREIGN KEY(accountnum) REFERENCES account(accountnum)
             );CREATE TABLE withdrawaltx
             (
                          txnum                 INT,
                          accountnum INT,
                          CONSTRAINT pk52 PRIMARY KEY(txnum),
                          CONSTRAINT fk52 FOREIGN KEY(txnum) REFERENCES transactions(txnum),
                          CONSTRAINT fk403 FOREIGN KEY(accountnum) REFERENCES account(accountnum)
             );CREATE TABLE transfertx
             (
                          txnum                 INT,
                          accountnum INT,
                          destinationaccountnum INT,
                          CONSTRAINT pk53 PRIMARY KEY(txnum),
                          CONSTRAINT fk53 FOREIGN KEY(txnum) REFERENCES transactions(txnum),
                          CONSTRAINT fk54 FOREIGN KEY(destinationaccountnum) REFERENCES account(accountnum),
                          CONSTRAINT fk404 FOREIGN KEY(accountnum) REFERENCES account(accountnum)
             );CREATE TABLE asset
             (
                          assetnum   INT,
                          TYPE       VARCHAR(50) not null,
                          value      NUMERIC(12,2) CONSTRAINT assetValue CHECK (value > 0),
                          CONSTRAINT pk5 PRIMARY KEY(assetnum)
             );CREATE TABLE card
             (
                          cardnum INT,
                          CONSTRAINT pk6 PRIMARY KEY(cardnum)
             );CREATE TABLE credit_card
             (
                          cardnum     INT,
                          loannum     INT not null,
                          creditlimit NUMERIC(6,2) CONSTRAINT creditlimCheck CHECK (creditlimit > 0),
                          CONSTRAINT pk7 PRIMARY KEY(cardnum),
                          CONSTRAINT fk222 FOREIGN KEY (loannum) REFERENCES loan(loannum),
                          CONSTRAINT fk2 FOREIGN KEY(cardnum) REFERENCES card(cardnum)
             );CREATE TABLE debit_card
             (
                          cardnum    INT,
                          accountnum INT,
                          CONSTRAINT pk8 PRIMARY KEY(cardnum),
                          CONSTRAINT fk3 FOREIGN KEY(cardnum) REFERENCES card(cardnum)
             );CREATE TABLE card_owner
             (
                          cardnum    INT,
                          customerid INT,
                          CONSTRAINT pk20 PRIMARY KEY(cardnum, customerid),
                          CONSTRAINT fk4 FOREIGN KEY (customerid) REFERENCES customer(id),
                          CONSTRAINT fk5 FOREIGN KEY (cardnum) REFERENCES card(cardnum)
             );CREATE TABLE owes
             (
                          customerid INT,
                          loannum    INT,
                          CONSTRAINT fk7 FOREIGN KEY (customerid) REFERENCES customer(id),
                          CONSTRAINT fk8 FOREIGN KEY(loannum) REFERENCES loan(loannum),
                          CONSTRAINT pk9 PRIMARY KEY(customerid, loannum)
             );CREATE TABLE is_secured
             (
                          loannum  INT,
                          assetnum INT,
                          CONSTRAINT fk9 FOREIGN KEY (loannum) REFERENCES loan(loannum),
                          CONSTRAINT fk10 FOREIGN KEY(assetnum) REFERENCES asset(assetnum),
                          CONSTRAINT pk10 PRIMARY KEY(loannum, assetnum)
             );CREATE TABLE custodian
             (
                          customerid INT,
                          accountnum INT,
                          CONSTRAINT fk11 FOREIGN KEY (customerid) REFERENCES customer(id),
                          CONSTRAINT fk12 FOREIGN KEY (accountnum) REFERENCES account(accountnum),
                          CONSTRAINT pk11 PRIMARY KEY(customerid, accountnum)
             );CREATE TABLE owns
             (
                          assetnum   INT,
                          customerid INT,
                          CONSTRAINT fk13 FOREIGN KEY(assetnum) REFERENCES asset(assetnum),
                          CONSTRAINT fk14 FOREIGN KEY(customerid) REFERENCES customer(id),
                          CONSTRAINT pk12 PRIMARY KEY(assetnum, customerid)
             );CREATE Table used_card
             (
                         cardnum     INT,
                         txnum       INT,
                         CONSTRAINT pk70 PRIMARY KEY(cardnum, txnum),
                         CONSTRAINT fk80 FOREIGN KEY(cardnum) REFERENCES card(cardnum),
                         CONSTRAINT fk48 FOREIGN KEY(txnum) REFERENCES transactions(txnum)
             );CREATE TABLE occured
             (
                          txnum      INT,
                          locationid INT,
                          utc_time   NUMBER(19) CONSTRAINT timeCheck CHECK (utc_time >= 915148800) ,
                          CONSTRAINT pk13 PRIMARY KEY(txnum),
                          CONSTRAINT fk15 FOREIGN KEY(txnum) REFERENCES transactions(txnum),
                          CONSTRAINT fk99 FOREIGN KEY(locationid) references location(locationid)
             );
