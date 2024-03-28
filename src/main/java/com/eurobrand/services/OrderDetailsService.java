package com.eurobrand.services;

import com.eurobrand.dto.OrderDetailsDto;
import com.eurobrand.dto.OrderDetailsProductDto;
import com.eurobrand.entities.OrderDetailsEntity;
import com.eurobrand.entities.OrderProductEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.OrderDetailsRepository;
import com.eurobrand.repositories.OrderDetailsStatusRepository;
import com.eurobrand.repositories.OrderProductRepository;
import com.eurobrand.repositories.ProductRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class OrderDetailsService {
    @Autowired
    private OrderDetailsRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderDetailsStatusRepository orderDetailsStatusRepository;

    @Transactional
    public OrderDetailsEntity postOrder(OrderDetailsDto orderDetails) throws MessagingException {
        OrderDetailsEntity orderDetailsEntity = orderDetails.getOrder();


        OrderDetailsEntity order = repository.save(orderDetailsEntity);

        handleProductSave(orderDetails.getProducts(), order);

        return order;
    }

    private void handleProductSave(List<OrderDetailsProductDto> products, OrderDetailsEntity orderDetailsEntity) throws MessagingException {
        List<OrderProductEntity> productEntities = new ArrayList<>();

        for(OrderDetailsProductDto productDto : products){
            OrderProductEntity orderProductEntity = new OrderProductEntity();
            ProductEntity productEntity = productRepository.findById(productDto.getProductId()).orElse(null);


            orderProductEntity.setProduct(productEntity);
            orderProductEntity.setOrderDetails(orderDetailsEntity);
            orderProductEntity.setQuantity(productDto.getQuantity());
            assert productEntity != null;
            productEntity.setStock(productEntity.getStock() - productDto.getQuantity());

            productEntities.add(orderProductRepository.save(orderProductEntity));

            orderProductRepository.save(orderProductEntity);
            productRepository.save(productEntity);
        }

        String body = createMailText(productEntities, orderDetailsEntity);
        String subject = "Eurobrand - online narudžba";
        String to = orderDetailsEntity.getEmail();
        if(to != null && to != "") {
            emailService.sendEmail(to, subject, body);
        }
    }

    private String createMailText(List<OrderProductEntity> productEntities, OrderDetailsEntity orderDetailsEntity) {
        StringBuilder sb = new StringBuilder();

        // Start building the HTML content
        sb.append("<html><body>");

        // Add text content
        sb.append("<h1> Postovani ").append(orderDetailsEntity.getFirstName()).append(" Hvala za Vašu narudžbu!</h1>");
        sb.append("<h2>").append("Vaša narudžba je zaprimljena!").append("</h2>");
        sb.append("<p>").append("Uskoro ćemo Vas kontaktirati").append("</p>");

        // Add products table
        sb.append("<table border='1'>");
        sb.append("<tr><th>Brand</th><th>Model</th><th>Opis</th><th>Cijena</th><th>Količina</th></tr>");
        for (OrderProductEntity productEntity : productEntities) {
            sb.append("<tr>");
            sb.append("<td>").append(productEntity.getProduct().getBrand()).append("</td>");
            sb.append("<td>").append(productEntity.getProduct().getModel()).append("</td>");
            sb.append("<td>").append(productEntity.getProduct().getDescription()).append("</td>");
            sb.append("<td>").append(productEntity.getProduct().getPrice()).append("</td>");
            sb.append("<td>").append(productEntity.getQuantity()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        // Add order total
        double total = orderDetailsEntity.getTotalPrice();
        sb.append("<p>Ukupna cijena: ").append(total).append("KM</p>");


        sb.append("<p>Ovo je generički email.").append("</p>");
        // Add closing tags
        sb.append("</body></html>");

        return sb.toString();
    }

    public List<OrderDetailsEntity> getAllOrders() {
        return repository.findAll();
    }

    public OrderDetailsEntity getOrderById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public OrderDetailsEntity checkDeliveredOrder(Integer orderId) {
        OrderDetailsEntity orderDetailsEntity = repository.findById(orderId).orElse(null);

        if(orderDetailsEntity != null) {
            orderDetailsEntity.setOrderStatus(orderDetailsStatusRepository.findById(2).orElse(null));
        }

        assert orderDetailsEntity != null;
        return repository.saveAndFlush(orderDetailsEntity);
    }
}
