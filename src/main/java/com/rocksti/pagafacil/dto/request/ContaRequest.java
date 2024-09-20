package com.rocksti.pagafacil.dto.request;

import com.rocksti.pagafacil.enumeration.SituacaoConta;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
public class ContaRequest {
    private String descricao;
    private SituacaoConta situacao;
    private BigDecimal valor;
    private LocalDate dataVencimento;
}
