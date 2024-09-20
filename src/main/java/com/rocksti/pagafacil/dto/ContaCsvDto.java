package com.rocksti.pagafacil.dto;

import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContaCsvDto {
    private String descricao;
    private BigDecimal valor;
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate dataVencimento;
}
