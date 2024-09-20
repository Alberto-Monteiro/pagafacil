CREATE TABLE contas
(
    id              SERIAL PRIMARY KEY,
    data_vencimento DATE        NOT NULL,
    data_pagamento DATE,
    valor           DECIMAL     NOT NULL,
    descricao       VARCHAR(255),
    situacao        VARCHAR(20) NOT NULL
);
