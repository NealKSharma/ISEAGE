# Warehouse Management System (WMS)

The WMS acts as the backend for the WWW storefront, handling all warehouse-related 
API calls and database access. It also generates JSON files upon each order creation 
for label printing, which are shared via SMB.

The application is built using Spring Boot (Java) and is organized into separate packages 
for stock management, order management, and label printing.

## API Documentation

### 1. Stock Management ('/api/item')
- `GET '/all'`: Retrieves all items in the warehouse inventory.
- `GET '/get?iid={id}'`: Retrieves all details about a specific item.
- `POST '/create'`: Adds a new item to the inventory.
- `PUT '/update?iid={id}'`: Updates the details of an existing item.
- `DELETE '/delete?iid={id}'`: Deletes an item from the inventory.

### 2. Order Management ('/api/order')
- `POST '/place'`: Creates a new order and automatically triggers the label generation process.
- `PATCH '/complete?oid={id}'`: Marks a PENDING order as COMPLETE.
- `GET '/all?uid={id}'`: Retrieves all orders for a specific user.
- `GET '/get?oid={id}'`: Retrieves all details about a specific order.
- `PATCH '/cancel?oid={id}'`: Cancels a PENDING order and restores the items to warehouse inventory.

## Label Generation

When an order is placed, the 'LabelGenerator' service automatically exports a JSON file which is then shared via an SMB
share with the label printing service.

The file includes data such as the order ID, warehouse address, shipping address, and recipient’s full name.