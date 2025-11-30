CREATE TABLE users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE portfolios (
    id            UUID PRIMARY KEY,
    user_id       UUID NOT NULL REFERENCES users(id),
    name          VARCHAR(255) NOT NULL,
    base_currency VARCHAR(3)  NOT NULL,
    description   TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_archived   BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_portfolios_user_id ON portfolios(user_id);

CREATE TABLE assets (
    id             UUID PRIMARY KEY,
    name           VARCHAR(255),
    symbol         VARCHAR(50) NOT NULL UNIQUE,
    type           VARCHAR(20) NOT NULL,
    quote_currency VARCHAR(3)  NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE asset_prices (
    id         UUID PRIMARY KEY,
    asset_id   UUID NOT NULL REFERENCES assets(id),
    price      NUMERIC(18,8) NOT NULL,
    fetched_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_asset_prices_asset_time UNIQUE (asset_id, fetched_at)
);

CREATE INDEX idx_asset_prices_asset_id ON asset_prices(asset_id);

CREATE TABLE transactions (
    id          UUID PRIMARY KEY,
    asset_id    UUID REFERENCES assets(id),
    portfolio_id UUID NOT NULL REFERENCES portfolios(id),
    type        VARCHAR(20) NOT NULL,
    quantity    NUMERIC(18,8) NOT NULL,
    price       NUMERIC(18,8) NOT NULL,
    fee         NUMERIC(18,8) NOT NULL DEFAULT 0,
    timestamp   TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_portfolio_id ON transactions(portfolio_id);
CREATE INDEX idx_transactions_portfolio_time ON transactions(portfolio_id, timestamp);
CREATE INDEX idx_transactions_asset_id ON transactions(asset_id);
