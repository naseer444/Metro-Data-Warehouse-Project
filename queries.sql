
##########################################  Task1  ##############################################

##### Both Quarter and Month Wise #####
select supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.QUARTER, time.MONTH, sum(sales.TOTAL_SALE) as Total_sales
from metro_dwh.sales sales
join metro_dwh.time time
join metro_dwh.product product
join metro_dwh.supplier supplier
on 
	sales.time_id = time.time_id and
    sales.product_id = product.product_id and
    sales.supplier_id = supplier.supplier_id
group by supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.QUARTER, time.MONTH
order by supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.QUARTER, time.MONTH;

##### Only Quarter Wise  #####
select supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.QUARTER, sum(sales.TOTAL_SALE) as Total_sales
from metro_dwh.sales sales
join metro_dwh.time time
join metro_dwh.product product
join metro_dwh.supplier supplier
on 
	sales.time_id = time.time_id and
    sales.product_id = product.product_id and
    sales.supplier_id = supplier.supplier_id
group by supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.QUARTER
order by supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.QUARTER;

##### Only Month Wise #####

select supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.MONTH, sum(sales.TOTAL_SALE) as Total_sales
from metro_dwh.sales sales
join metro_dwh.time time
join metro_dwh.product product
join metro_dwh.supplier supplier
on 
	sales.time_id = time.time_id and
    sales.product_id = product.product_id and
    sales.supplier_id = supplier.supplier_id
group by supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.MONTH
order by supplier.SUPPLIER_NAME, product.PRODUCT_NAME, time.MONTH;

##########################################  Task2  ##################################
##### Q2 Present total sales of each product sold by each store. The output should be organised
##### store wise and then product wise under each store.

select store.store_name, product.PRODUCT_NAME, sum(sales.TOTAL_SALE) as Total_Sales
from metro_dwh.sales sales
join metro_dwh.product product
join metro_dwh.store store
on 
    sales.product_id = product.product_id and
    sales.store_id = store.store_id
group by store.store_name, product.PRODUCT_NAME
order by store.store_name, product.PRODUCT_NAME;

######################################## Task3 ####################################
##### Q3 Find the 5 most popular products sold over the weekends.

select product.PRODUCT_NAME, sum(sales.QUANTITY) as Sold_Units
from metro_dwh.sales sales
join metro_dwh.time time
join metro_dwh.product product
on 
	sales.time_id = time.time_id and
    sales.product_id = product.product_id and
    time.DAY_OF_WEEK IN ('Saturday', 'Sunday')
group by product.PRODUCT_NAME
order by sum(sales.QUANTITY) desc
limit 5;


####################################### Task4 #################################
##### Q4 Present the quarterly sales of each product for year 2016 using drill down query concept.

select product.product_id, product.product_name,
	sum(case when time.quarter = '1' then sales.total_sale end) Quarter1,
    sum(case when time.quarter = '2' then sales.total_sale end) Quarter2,
    sum(case when time.quarter = '3' then sales.total_sale end) Quarter3,
    sum(case when time.quarter = '4' then sales.total_sale end) Quarter4
from metro_dwh.sales sales
join metro_dwh.product product
join metro_dwh.time time
on(product.product_id = sales.product_id and time.time_id = sales.time_id)
where time.year = '2016'
group by product.product_id,time.year
order by product.product_name asc, time.quarter asc;

##################################### Task5 ##############################
##### Q5 Extract total sales of each product for the first and second half of year 2016 along with its
##### total yearly sales.

select product.product_id, product.product_name,
	sum(case when time.quarter = '1' or time.quarter = '2' then sales.total_sale end) half1,
    sum(case when time.quarter = '3' or time.quarter = '4' then sales.total_sale end) half2,
    sum(sales.total_sale) as Yearly_Sale
from metro_dwh.sales sales
join metro_dwh.product product
join metro_dwh.time time
on(product.product_id = sales.product_id and time.time_id = sales.time_id)
where time.year = '2016'
group by product.product_id,time.year
order by product.product_name asc, time.quarter asc;

####################################### Task6 ################################
####### Anamoly

select count(distinct t.CUSTOMER_ID, t.STORE_ID, t.PRODUCT_ID, m.SUPPLIER_ID, t.T_DATE)
from mdb.transactions t join mdb.masterdata m
on
	t.PRODUCT_ID = m.PRODUCT_ID;



######################################### Task7 ###############################
##### Q7 Create a materialised view with name “STOREANALYSIS_MV” that presents the productwise sales analysis for each store.

CREATE OR REPLACE VIEW metro_dwh.STOREANALYSIS_MV AS
select store.STORE_ID, store.STORE_NAME,product.PRODUCT_ID, product.PRODUCT_NAME, sum(sales.TOTAL_SALE) as STORE_TOTAL
from metro_dwh.sales sales
join metro_dwh.product product
join metro_dwh.store store
on 
    sales.product_id = product.product_id and
    sales.STORE_ID = store.STORE_ID
group by store.STORE_NAME, product.PRODUCT_NAME with rollup;

select * from metro_dwh.STOREANALYSIS_MV;

############################################   for checking   #########################################

###  select * from metro_dwh.sales;
