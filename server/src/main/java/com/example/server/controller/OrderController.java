import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// ... 기존 코드 ...

@PostMapping("/checkout")
public ResponseEntity<String> checkout(@RequestBody OrderRequestDto orderRequest) {
    orderRepository.createOrder(orderRequest);
    return ResponseEntity.ok("결제가 성공적으로 처리되었습니다.");
}