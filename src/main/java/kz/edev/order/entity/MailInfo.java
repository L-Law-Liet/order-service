package kz.edev.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailInfo implements Serializable {
    private String email;
    private Double total;
    private List<Product> products;
}
