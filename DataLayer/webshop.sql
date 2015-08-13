SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

----------------------------------------------------------------------------------------------------------------------
-------------------------------				Products Database			----------------------
----------------------------------------------------------------------------------------------------------------------
DROP DATABASE IF EXISTS Products;

CREATE DATABASE Products;

use Products;

------------------------
---- Products table ----
------------------------

--------- DDL ----------

CREATE TABLE products (
	product_id 				INTEGER 		UNSIGNED	NOT NULL	AUTO_INCREMENT,
	product_name				VARCHAR(25) 			 	NOT NULL,
	product_description			VARCHAR(50)						DEFAULT "",
	unit					VARCHAR(20)				NOT NULL,
	price					DECIMAL(10, 2)				NOT NULL,
	currency				CHAR(3)					NOT NULL,
	PRIMARY KEY (product_id),
	KEY idx_product_name (product_name),
	KEY idx_currency_code (currency)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--------- DML ----------
insert into products values
			(default, 'Product 1', default, 'Kilos', 1000, 'USD'),
                        (default, 'Product 2', default, 'Kilos', 1001, 'USD'),
                        (default, 'Product 3', default, 'Kilos', 1002, 'USD'),
                        (default, 'Product 4', default, 'Kilos', 1003, 'USD')
                        ;



----------------------------------------------------------------------------------------------------------------------
-------------------------------                	 Customers Databae			------------------------------
----------------------------------------------------------------------------------------------------------------------
DROP DATABASE IF EXISTS Customers;

CREATE DATABASE Customers;
use Customers;

------------------------
---- customers table ----
------------------------

--------- DDL ----------

CREATE TABLE customers (
	customer_id 			INTEGER 		UNSIGNED	NOT NULL	AUTO_INCREMENT,
	first_name			VARCHAR(25) 				NOT NULL,
	last_name			VARCHAR(50)				NOT NULL,
	email_address			VARCHAR(50) 				NOT NULL,
	street_address			VARCHAR(70)				NOT NULL,
	city				VARCHAR(30)				NOT NULL,
	country				CHAR(3)					NOT NULL	DEFAULT "USA",
	PRIMARY KEY (customer_id),
	KEY idx_fist_name (first_name),
	KEY idx_last_name (last_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--------- DML ----------

insert into customers values
                        (default, 'Martin', 'Hofman', 'mhofman@email.com', 'Street Ave 1', 'NYC', default),
                        (default, 'Jack', 'Woznyac', 'jwoz@email.com', 'Street Ave 2', 'San Francisco', default),
                        (default, 'John', 'Doe', 'jd@email.com', 'Street Ave 3', 'LA', 'USA'),
                        (default, 'Ramesh', 'Pindar', 'rpindar@email.com', 'Street Ave 4', 'Chicago', 'USA')
			;







----------------------------------------------------------------------------------------------------------------------
-------------------------------                  OrdersInfo Databse                      -----------------------------
----------------------------------------------------------------------------------------------------------------------
DROP DATABASE IF EXISTS OrdersInfo;

CREATE DATABASE OrdersInfo;
use OrdersInfo;

------------------------
---- orders table ----
------------------------

--------- DDL ----------

CREATE TABLE orders (
	order_id				INTEGER		UNSIGNED		NOT NULL	AUTO_INCREMENT,
	customer_id				INTEGER		UNSIGNED		NOT NULL,
	order_date				TIMESTAMP				NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (order_id),
	KEY idx_customer_id (customer_id),
	KEY idx_order_date (order_date)
--	CONSTRAINT FOREIGN KEY fk_Orders_Customers_customer_id (customer_id) REFERENCES Customers(customer_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------
--- orderItems table ---
------------------------

--------- DDL ----------
CREATE TABLE orderItems (
	order_item_id				BIGINT 			UNSIGNED	NOT NULL	AUTO_INCREMENT,
	order_id				INTEGER			UNSIGNED	NOT NULL,
	product_id				INTEGER			UNSIGNED	NOT NULL,
	quantity				DECIMAL(10,2)	NOT NULL,
	price					DECIMAL(10,2)	NOT NULL,
	PRIMARY KEY (order_item_id),
	KEY idx_order_items_order_id (order_id),
	KEY idx_product_id (product_id),
	UNIQUE idx_orde_idr_product_id (order_id, product_id),
	CONSTRAINT FOREIGN KEY fk_order_items_orders_orderid (order_id) REFERENCES Orders(order_id) ON DELETE RESTRICT ON UPDATE CASCADE
--	CONSTRAINT FOREIGN KEY fk_order_item_products_product_id (product_id) REFERENCES Products(product_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------
---- orders table ----
------------------------

--------- DML ----------
insert into orders values
			(default, 1, default), -- 1
                        (default, 1, default), -- 2
                        (default, 2, default), -- 3
                        (default, 2, default), -- 4
                        (default, 3, default), -- 5
                        (default, 3, default), -- 6
                        (default, 3, default), -- 7
                        (default, 3, default), -- 8
                        (default, 4, default) -- 9
                         ;

------------------------
--- orderItems table ---
------------------------

--------- DML ----------
insert into orderItems values 
			(default, 1, 1, 100, 1000),
			(default, 1, 2, 20, 1200),
			(default, 1, 3, 10, 1500),
			(default, 1, 4, 100, 1000),
			(default, 2, 4, 100, 1500),
			(default, 2, 1, 120, 1000),
			(default, 3, 1, 2, 1000),
			(default, 3, 2, 4, 1000),
			(default, 3, 3, 50, 1000),
			(default, 3, 4, 30, 1000),
			(default, 4, 2, 1000, 1000),
			(default, 4, 3, 150, 1000),
			(default, 5, 3, 10, 1000),
			(default, 6, 2, 11, 1000),
			(default, 6, 3, 13, 1000),
			(default, 6, 4, 15, 1000),
			(default, 7, 1, 20, 1000),
			(default, 7, 2, 19, 1000),
			(default, 8, 2, 25, 1000),
			(default, 8, 3, 120, 1000),
			(default, 9, 1, 1, 1000),
			(default, 9, 3, 7, 1000),
			(default, 9, 4, 9, 1000)
			;
