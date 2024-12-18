DROP TABLE IF EXISTS book;
CREATE TABLE Book (
    id BIGSERIAL PRIMARY KEY, -- Auto-incrementing ID as the primary key
    isbn VARCHAR(13) NOT NULL CHECK (isbn ~ '^[0-9]{10}$' OR isbn ~ '^[0-9]{13}$'), -- Enforces 10 or 13 digit format
    title TEXT NOT NULL, -- Title must be defined
    author TEXT NOT NULL, -- Author must be defined
    price NUMERIC(10, 2) NOT NULL CHECK (price > 0), -- Positive price with up to 2 decimal places
    borrow_time TIMESTAMP, -- Optional borrowing time
    return_time TIMESTAMP, -- Optional return time
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Automatically set on record creation
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Automatically set on update
    version INTEGER NOT NULL DEFAULT 0 -- Version for optimistic locking
);
CREATE INDEX book_isbn_idx ON Book (isbn);