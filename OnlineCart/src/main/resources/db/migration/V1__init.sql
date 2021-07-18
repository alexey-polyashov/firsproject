CREATE TABLE products (id bigserial primary key, price numeric, title VARCHAR(255));
INSERT INTO products (price, title)
VALUES
(10,'product 1'),
(15,'product 2'),
(15,'product 3'),
(20,'product 4'),
(5,'product 5'),
(1,'product 6');
