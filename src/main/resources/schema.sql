CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  email TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  firstname TEXT,
  lastname TEXT,
  phone TEXT,
  birthday TEXT,
  member_code TEXT UNIQUE,
  membership_level TEXT,
  register_date TEXT,
  points INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transfers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  from_user_id INTEGER NOT NULL,
  to_user_id INTEGER NOT NULL,
  amount INTEGER NOT NULL,
  note TEXT,
  created_at TEXT DEFAULT (datetime('now')),
  FOREIGN KEY(from_user_id) REFERENCES users(id),
  FOREIGN KEY(to_user_id) REFERENCES users(id)
);
