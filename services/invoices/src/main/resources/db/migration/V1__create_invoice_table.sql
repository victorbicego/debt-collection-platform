CREATE TABLE invoice (
    id          UUID          PRIMARY KEY,
    debtor      VARCHAR(255)  NOT NULL,
    amount      NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    currency    CHAR(3)       NOT NULL,
    status      VARCHAR(20)   NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL
);

CREATE INDEX idx_invoice_status ON invoice (status);
