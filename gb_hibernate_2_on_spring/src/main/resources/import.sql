DROP TABLE orders_detail IF EXISTS;
CREATE TABLE IF NOT EXISTS orders_detail (id bigserial, customer_id bigint, product_id bigint, price numeric, order_id bigint, PRIMARY KEY (id));
INSERT INTO orders_detail (customer_id, product_id, price, order_id) VALUES (1,1,12,1),(1,2,14,1),(1,2,12,2),(1,1,11,2),(2,5,4,3), (2,6 ,1,3), (2,4 ,18,4), (3, 5, 4, 5), (3, 2, 15, 5), (3, 1, 10, 6);

DROP TABLE orders IF EXISTS;
CREATE TABLE IF NOT EXISTS orders (id bigserial, order_date date, customer_id bigint, PRIMARY KEY (id));
INSERT INTO orders (order_date, customer_id) VALUES ('2021-01-10', 1), ('2021-06-01', 1), ('2021-03-15', 2), ('2021-05-27', 2), ('2021-07-01', 3), ('2021-04-17', 3);

DROP TABLE customers IF EXISTS;
CREATE TABLE IF NOT EXISTS customers (id bigserial, name VARCHAR(255), PRIMARY KEY (id));
INSERT INTO customers (name) VALUES ('Bob'), ('Jhon'), ('Nick');

DROP TABLE products IF EXISTS;
CREATE TABLE IF NOT EXISTS products (id bigserial, price numeric, title VARCHAR(255), PRIMARY KEY (id));
INSERT INTO products (price, title) VALUES (10,'product 1'),(15,'product 2'),(15,'product 3'),(20,'product 4'),(5,'product 5'),(1,'product 6');
