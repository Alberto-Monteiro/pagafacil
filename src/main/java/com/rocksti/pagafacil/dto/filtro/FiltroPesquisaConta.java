package com.rocksti.pagafacil.dto.filtro;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Setter
@Getter
@Accessors(chain = true)
public class FiltroPesquisaConta {
    private LocalDate dataVencimento;
    private String descricao;
}
