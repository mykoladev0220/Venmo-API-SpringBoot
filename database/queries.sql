SELECT accounts.balance FROM accounts
        INNER JOIN users
        ON accounts.user_id = users.user_id
        WHERE users.username = 'Martin';
        
SELECT balance FROM accounts WHERE user_id = ?

BEGIN TRANSACTION;

INSERT INTO transfers ( transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES ( 1, 2, 2001, 2002, 500)
RETURNING transfer_id;

SELECT * FROM transfers WHERE transfer_id = 3002;


BEGIN TRANSACTION;

UPDATE accounts SET balance = 1500.00 WHERE account_id = 2001;

SELECT balance FROM accounts WHERE account_id = 2001;

ROLLBACK;

UPDATE accounts SET account_id = 0, user_id = 0, balance = 0 WHERE account_id = <condition>;

UPDATE accounts SET balance = 9999 WHERE account_id = 2001


INSERT INTO transfers (transfer_type_id,transfer_status_id, account_from, account_to , amount) VALUES (1, 2, 2001, 2002, 100) RETURNING *;
