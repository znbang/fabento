create table menu_items (
    id int generated always as identity primary key,
    menu_id int,
    provider_id int,
    provider_name text,
    product_id int,
    product_name text,
    product_price int
);

create table menus (
    id int generated always as identity primary key,
    name text,
    begin_at timestamp without time zone,
    end_at timestamp without time zone,
    comment text,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    meal_type text
);

create table orders (
    id int generated always as identity primary key,
    user_id int,
    menu_id int,
    menu_item_id int,
    product_price int,
    quantity int,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    paid boolean,
    product_name text,
    provider_name text,
    year_month int,
    meal_type text
);

create table products (
    id int generated always as identity primary key,
    provider_id int,
    name text,
    price int,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);

create table providers (
    id int generated always as identity primary key,
    name text,
    phone text,
    address text,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    status int
);

create table users (
    id int generated always as identity primary key,
    user_name text,
    display_name text,
    role text,
    enabled int
);

CREATE UNIQUE INDEX ix_orders_user_menu_item ON orders (user_id, menu_item_id);

ALTER TABLE menu_items ADD CONSTRAINT fk_menu FOREIGN KEY (menu_id) REFERENCES menus(id);

ALTER TABLE orders ADD CONSTRAINT fk_menu FOREIGN KEY (menu_id) REFERENCES menus(id);

ALTER TABLE orders ADD CONSTRAINT fk_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id);

ALTER TABLE menu_items ADD CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE menu_items ADD CONSTRAINT fk_provider FOREIGN KEY (provider_id) REFERENCES providers(id);

ALTER TABLE products ADD CONSTRAINT fk_provider FOREIGN KEY (provider_id) REFERENCES providers(id);

ALTER TABLE orders ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id);
