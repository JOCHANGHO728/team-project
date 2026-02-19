package com.example.server.repository;

import com.example.server.dto.CartItemDto;
import com.example.server.dto.OrderHistoryDto;
import com.example.server.dto.OrderRequestDto;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 주문 내역 전체 조회
    public List<OrderHistoryDto> getOrderHistory() {
        String sql = """
            SELECT 
                o.order_id,
                u.name AS customer_name,
                o.order_date,
                p.p_name,
                od.quantity,
                p.p_price,
                (p.p_price * od.quantity) AS item_total_price,
                o.total_amount
            FROM orders o
            JOIN userdb u ON o.customer_id = u.id
            JOIN order_detail od ON o.order_id = od.order_id
            JOIN productdb p ON od.product_id = p.p_id
            ORDER BY o.order_date DESC, o.order_id ASC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new OrderHistoryDto(
                rs.getLong("order_id"),
                rs.getString("customer_name"),
                rs.getTimestamp("order_date").toLocalDateTime(),
                rs.getString("p_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("p_price"),
                rs.getBigDecimal("item_total_price"),
                rs.getBigDecimal("total_amount")
        ));
    }

    // 2. 장바구니 상품 결제
    @Transactional
    public void createOrder(OrderRequestDto request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        String priceSql = "SELECT p_price FROM productdb WHERE p_id = ?";
        
        for (CartItemDto item : request.getCartItems()) {
            BigDecimal price = jdbcTemplate.queryForObject(priceSql, BigDecimal.class, item.getProductId());
            if (price != null) {
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        String orderSql = "INSERT INTO orders (customer_id, order_date, total_amount) VALUES (?, NOW(), ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        final BigDecimal finalTotalAmount = totalAmount; 
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, request.getCustomerId());
            ps.setBigDecimal(2, finalTotalAmount);
            return ps;
        }, keyHolder);

        Long newOrderId = keyHolder.getKey().longValue();

        String detailSql = "INSERT INTO order_detail (order_id, product_id, quantity) VALUES (?, ?, ?)";
        
        jdbcTemplate.batchUpdate(detailSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CartItemDto item = request.getCartItems().get(i);
                ps.setLong(1, newOrderId);          
                ps.setLong(2, item.getProductId()); 
                ps.setInt(3, item.getQuantity());   
            }

            @Override
            public int getBatchSize() {
                return request.getCartItems().size();
            }
        });
    }
}
