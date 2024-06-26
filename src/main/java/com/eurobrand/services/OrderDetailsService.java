package com.eurobrand.services;

import com.eurobrand.dto.FormValues;
import com.eurobrand.dto.OrderDetailsDto;
import com.eurobrand.dto.OrderDetailsProductDto;
import com.eurobrand.dto.Predracun;
import com.eurobrand.entities.OrderDetailsEntity;
import com.eurobrand.entities.OrderProductEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.OrderDetailsRepository;
import com.eurobrand.repositories.OrderDetailsStatusRepository;
import com.eurobrand.repositories.OrderProductRepository;
import com.eurobrand.repositories.ProductRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private  OrderProductsService orderProductsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderDetailsStatusRepository orderDetailsStatusRepository;

    @Transactional
    public OrderDetailsEntity postOrder(OrderDetailsDto orderDetails) throws MessagingException {
        OrderDetailsEntity orderDetailsEntity = orderDetails.getOrder();

        orderDetailsEntity.setTimestamp(LocalDateTime.now());
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
        String bodyAdmin = createAdminMailText(orderDetailsEntity);
        String subject = "Eurobrand - online narudžba";
        String to = orderDetailsEntity.getEmail();
        if(to != null && !to.isEmpty()) {
            emailService.sendEmail(to, subject, body);
        }
        emailService.sendEmail("prodajaeurobrand@gmail.com", subject, bodyAdmin);
    }

    private String createAdminMailText(OrderDetailsEntity orderDetailsEntity) {
        StringBuilder sb = new StringBuilder();

        // Start building the HTML content
        sb.append("<html><body>");

        // Add text content
        sb.append("<h2>").append("Imate novu narudžbu!").append("</h2>");
        sb.append("<p>").append("Za pregled narudžbe posjetite <a href=\"https://www.eurobrand.ba/admin/orders/" + orderDetailsEntity.getId() + "\">ovaj link</a>!").append("</p>");

        sb.append("<p>Ovo je generički email.").append("</p>");
            // Add closing tags
        sb.append("</body></html>");

        return sb.toString();
    }

    private String createMailText(List<OrderProductEntity> productEntities, OrderDetailsEntity orderDetailsEntity) {
        StringBuilder sb = new StringBuilder();

        // Start building the HTML content
        sb.append("<html><body>");

        // Add text content
        sb.append("<h1> Postovani ").append(orderDetailsEntity.getFirstName()).append(", Hvala za Vašu narudžbu!</h1>");
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
        return repository.findAll(buildSpecifications());
    }

    private Specification<OrderDetailsEntity> buildSpecifications() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add order by clause
            query.orderBy(criteriaBuilder.desc(root.get("timestamp")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<OrderDetailsEntity> buildSpecificationsForNewOrders() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add filter where column hasSeen is false
            predicates.add(criteriaBuilder.isFalse(root.get("hasSeen")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
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

    public void deleteOrderById(Integer orderId) {
        deleteOrderProducts(orderId);
        repository.deleteById(orderId);
    }

    private void deleteOrderProducts(Integer orderId) {
        orderProductsService.deleteByOrderId(orderId);
    }

    public List<OrderDetailsEntity> getAllNewOrders() {
        return repository.findAll(buildSpecificationsForNewOrders());
    }

    public void markSeenOrder(Integer orderId) {
        OrderDetailsEntity order = repository.findById(orderId).orElse(null);
        if(order != null) {
            order.setHasSeen(true);
            repository.save(order);
        }
    }

    public void sendInvoiceEmail(String to, FormValues formValues, Predracun[] bills) {
        try {
            String subject = "Predracun - Eurobrand";
            String body = "<h1>Predracun za online narudzbu</h1><p>U prilogu se nalaze detalji narudzbe.</p>";

            emailService.sendEmailWithPdf(to, subject, body, formValues, bills);
        } catch (MessagingException e) {
            // Handle exception
        }
    }
}
