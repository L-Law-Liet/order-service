package kz.edev.order.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.edev.order.constant.HttpExchange;
import kz.edev.order.entity.*;
import kz.edev.order.repository.OrderItemRepository;
import kz.edev.order.repository.OrderRepository;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    @Autowired
    HttpExchange httpExchange;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Order create(OrderBody body) {
        Order tmporder = new Order();
        tmporder.setTotal(body.getTotal());
        tmporder.setUser_id(body.getUser_id());
        Order order = orderRepository.saveAndFlush(tmporder);
        saveOrderItems(body.getProduct_ids(), order.getId());
        List<Product> products = getProductsByIds(body.getProduct_ids());
        String email = getEmailById(order.getUser_id());
        sendEmail(products, email, order.getTotal());
        return order;
    }

    public void sendEmail(List<Product> products, String email, Double total) {
        MailInfo mail = new MailInfo();
        mail.setEmail(email);
        mail.setTotal(total);
        mail.setProducts(products);
        System.out.println(mail.toString());
        System.out.println(
                restTemplate
                .exchange(
                        "http://store-mail-service/mail",
                        HttpMethod.POST,
                        httpExchange.getEntity(mail),
                        String.class
                ).getBody()
        );
    }

    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        return orderItemRepository.saveAll(orderItems);
    }

    public List<OrderItem> saveOrderItems(List<Long> ids, Long id) {
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem;
        for (Long product_id : ids) {
            orderItem = new OrderItem();
            orderItem.setOrder_id(id);
            orderItem.setProduct_id(product_id);
            orderItems.add(orderItem);
        }
        return saveAll(orderItems);
    }

    public List<Product> getProductsByIds(List<Long> ids) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String list = null;
//        try {
//            list = objectMapper.writeValueAsString(ids);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        List<Product> products = restTemplate.exchange("http://store-information-service/products/ids",
                HttpMethod.POST, httpExchange.getEntity(ids.toString()),
                new ParameterizedTypeReference<List<Product>>() {}).getBody();
        System.out.println(products.toString());
        return products;
    }

    public String getEmailById(Long id) {
        return restTemplate.exchange("http://store-profile-service/profile/email/" + id,
                HttpMethod.GET, httpExchange.getEntity(), String.class).getBody();
    }
}
