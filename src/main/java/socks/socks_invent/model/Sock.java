package socks.socks_invent.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
//@Table(name = "socks")
@AllArgsConstructor
@NoArgsConstructor
public class Sock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String color;

    @Min(0)
    private int cottonPercentage;

    @Min(0)
    private int quantity;

    public Sock(String color, int cottonPercentage, int quantity) {
        this.color = color;
        this.cottonPercentage = cottonPercentage;
        this.quantity = quantity;
    }
}
