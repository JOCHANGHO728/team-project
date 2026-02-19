import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

// ... 기존 코드 ...

// 장바구니 상품 결제 (트랜잭션 적용 필수!)
@Transactional
public void createOrder(OrderRequestDto request) {
    // 1. DB에서 직접 상품 가격을 조회하여 총 결제 금액(total_amount) 안전하게 계산
    BigDecimal totalAmount = BigDecimal.ZERO;
    String priceSql = "SELECT p_price FROM productdb WHERE p_id = ?";

    for (CartItemDto item : request.getCartItems()) {
        BigDecimal price = jdbcTemplate.queryForObject(priceSql, BigDecimal.class, item.getProductId());
        totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    // 2. orders 테이블에 주문 내역 저장 후, 자동 생성된 order_id 가져오기
    String orderSql = "INSERT INTO orders (customer_id, order_date, total_amount) VALUES (?, NOW(), ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, request.getCustomerId());
        ps.setBigDecimal(2, totalAmount);
        return ps;
    }, keyHolder);

    Long newOrderId = keyHolder.getKey().longValue();

    // 3. 추출한 order_id를 사용하여 order_detail 테이블에 장바구니 상품들 일괄(Batch) 저장
    String detailSql = "INSERT INTO order_detail (order_id, product_id, quantity) VALUES (?, ?, ?)";

    jdbcTemplate.batchUpdate(detailSql, new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            CartItemDto item = request.getCartItems().get(i);
            ps.setLong(1, newOrderId);          // 방금 생성된 주문 번호
            ps.setLong(2, item.getProductId()); // 상품 번호
            ps.setInt(3, item.getQuantity());   // 수량
        }

        @Override
        public int getBatchSize() {
            return request.getCartItems().size();
        }
    });
}