package com.txt.kafka.stock.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionTotal {
    private int count;
    private int productCount;
    private long amount;
}
