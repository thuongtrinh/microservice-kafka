package com.txt.kafka.stock.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionTotalWithProduct {
    private Transaction transaction;
    private Integer productId;
}
