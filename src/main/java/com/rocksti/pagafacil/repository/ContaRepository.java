package com.rocksti.pagafacil.repository;

import com.rocksti.pagafacil.entity.ContaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ContaRepository extends JpaRepository<ContaEntity, Long> {

    @Query("SELECT SUM(c.valor) FROM ContaEntity c WHERE c.dataPagamento BETWEEN :dataInicio AND :dataFim AND c.situacao = 'PAGO'")
    BigDecimal obterValorTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    @Query("SELECT c FROM ContaEntity c WHERE " +
           "c.situacao != 'PAGO' AND " +
           "(COALESCE(:dataVencimento, c.dataVencimento) = c.dataVencimento) AND " +
           "(COALESCE(:descricao, '') = '' OR c.descricao = :descricao)")
    Page<ContaEntity> findByAPagarComFiltro(Pageable pageable,
                                            @Param("dataVencimento") LocalDate dataVencimento,
                                            @Param("descricao") String descricao);
}
