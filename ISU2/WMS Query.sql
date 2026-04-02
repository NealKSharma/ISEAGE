use wms_db

SHOW TABLES

UPDATE orders SET order_status = 'PENDING' WHERE order_id = 2

INSERT INTO users (first_name, last_name, address) 
VALUES ('Neal', 'Sharma', '3222 Seth Street, San Angelo, TX 76901');

SELECT * FROM users
SELECT * FROM items
SELECT * FROM orders
SELECT * FROM order_items

DROP TABLE items;
DROP TABLE order_items;
DROP TABLE orders;
DROP TABLE users

CREATE TABLE users (
  user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(20) NOT NULL,
  last_name VARCHAR(20) NOT NULL,
  address VARCHAR(255) NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  admin BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
  order_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL,
  total_price DECIMAL(10,2) NOT NULL,
  order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CHECK (total_price > 0),
  CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE RESTRICT
);

CREATE TABLE order_items (
  order_item_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT UNSIGNED NOT NULL,
  item_id BIGINT UNSIGNED NOT NULL,
  ordered_quantity INT NOT NULL,
  item_price DECIMAL(10,2) NOT NULL,
  CHECK (ordered_quantity > 0),
  CHECK (item_price > 0),

  CONSTRAINT fk_orderitems_order
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
    ON DELETE CASCADE,

  CONSTRAINT fk_orderitems_item
    FOREIGN KEY (item_id) REFERENCES items(id)
    ON DELETE RESTRICT
)