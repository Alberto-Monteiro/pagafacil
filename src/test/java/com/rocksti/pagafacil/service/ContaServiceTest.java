package com.rocksti.pagafacil.service;

import com.rocksti.pagafacil.dto.filtro.FiltroPesquisaConta;
import com.rocksti.pagafacil.dto.request.ContaRequest;
import com.rocksti.pagafacil.entity.ContaEntity;
import com.rocksti.pagafacil.enumeration.SituacaoConta;
import com.rocksti.pagafacil.repository.ContaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
class ContaServiceTest {

    @MockBean
    private ContaRepository contaRepository;

    @Autowired
    private ContaService contaService;

    private static List<ContaEntity> getContas() {
        return List.of(
                new ContaEntity()
                        .setId(1L)
                        .setSituacao(SituacaoConta.PENDENTE)
                        .setValor(BigDecimal.valueOf(100.00))
                        .setDescricao("Conta de luz")
                        .setDataVencimento(LocalDate.now()),
                new ContaEntity()
                        .setId(2L)
                        .setSituacao(SituacaoConta.PENDENTE)
                        .setValor(BigDecimal.valueOf(200.00))
                        .setDescricao("Conta de água")
                        .setDataVencimento(LocalDate.now())
        );
    }

    @Test
    void cadastrarConta() {

        when(contaRepository.save(any(ContaEntity.class)))
                .thenReturn(getContas().get(0));

        ContaRequest contaRequest = new ContaRequest()
                .setValor(getContas().get(0).getValor())
                .setDescricao(getContas().get(0).getDescricao())
                .setDataVencimento(getContas().get(0).getDataVencimento());

        ContaEntity conta = contaService.cadastrarConta(contaRequest);

        assertNotNull(conta);
        assertEquals(conta.getId(), 1L);
        assertEquals(conta.getValor(), getContas().get(0).getValor());
        assertEquals(conta.getDescricao(), getContas().get(0).getDescricao());
        assertEquals(conta.getDataVencimento(), getContas().get(0).getDataVencimento());
        assertEquals(conta.getSituacao(), SituacaoConta.PENDENTE);
    }

    @Test
    void atualizarConta() {

        when(contaRepository.findById(1L))
                .thenReturn(java.util.Optional.of(getContas().get(0)));

        when(contaRepository.save(any(ContaEntity.class)))
                .thenReturn(getContas().get(0));

        ContaRequest contaRequest = new ContaRequest()
                .setValor(getContas().get(0).getValor())
                .setDescricao(getContas().get(0).getDescricao())
                .setDataVencimento(getContas().get(0).getDataVencimento());

        ContaEntity conta = contaService.atualizarConta(1L, contaRequest);

        assertNotNull(conta);
        assertEquals(conta.getId(), 1L);
        assertEquals(conta.getValor(), getContas().get(0).getValor());
        assertEquals(conta.getDescricao(), getContas().get(0).getDescricao());
        assertEquals(conta.getDataVencimento(), getContas().get(0).getDataVencimento());
        assertEquals(conta.getSituacao(), SituacaoConta.PENDENTE);
    }

    @Test
    void atualizarContaComErro() {

        when(contaRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        ContaRequest contaRequest = new ContaRequest()
                .setValor(getContas().get(0).getValor())
                .setDescricao(getContas().get(0).getDescricao())
                .setDataVencimento(getContas().get(0).getDataVencimento());

        try {
            contaService.atualizarConta(1L, contaRequest);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Conta não encontrada");
        }
    }

    @Test
    void alterarSituacaoConta() {

        when(contaRepository.findById(1L))
                .thenReturn(java.util.Optional.of(getContas().get(0)));

        when(contaRepository.save(any(ContaEntity.class)))
                .thenReturn(getContas().get(0).setSituacao(SituacaoConta.PAGO));

        ContaEntity conta = contaService.alterarSituacaoConta(1L, SituacaoConta.PAGO);

        assertNotNull(conta);
        assertEquals(conta.getId(), 1L);
        assertEquals(conta.getValor(), getContas().get(0).getValor());
        assertEquals(conta.getDescricao(), getContas().get(0).getDescricao());
        assertEquals(conta.getDataVencimento(), getContas().get(0).getDataVencimento());
        assertEquals(conta.getSituacao(), SituacaoConta.PAGO);
    }

    @Test
    void alterarSituacaoContaComErro() {

        when(contaRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        try {
            contaService.alterarSituacaoConta(1L, SituacaoConta.PAGO);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Conta não encontrada");
        }
    }

    @Test
    void obterValorTotalPagoPorPeriodo() {

        when(contaRepository.obterValorTotalPagoPorPeriodo(LocalDate.now(), LocalDate.now()))
                .thenReturn(BigDecimal.valueOf(300.00));

        BigDecimal valorTotalPago = contaService.obterValorTotalPagoPorPeriodo(LocalDate.now(), LocalDate.now()).get("valorTotalPago");

        assertNotNull(valorTotalPago);
        assertEquals(valorTotalPago, BigDecimal.valueOf(300.00));
    }

    @Test
    void buscarContasAPagar() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<ContaEntity> contaPage = new PageImpl<>(getContas(), pageable, getContas().size());

        when(contaRepository.findByAPagarComFiltro(any(PageRequest.class), any(LocalDate.class), any(String.class)))
                .thenReturn(contaPage);

        Page<ContaEntity> resultado = contaService.buscarContasAPagar(
                pageable,
                new FiltroPesquisaConta()
                        .setDataVencimento(LocalDate.now())
                        .setDescricao(""));

        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
        assertEquals(1L, resultado.getContent().get(0).getId());
        assertEquals(2L, resultado.getContent().get(1).getId());
        assertEquals(2, resultado.getTotalElements());
        assertEquals(1, resultado.getTotalPages());
    }

    @Test
    void buscarContaPorId() {
        when(contaRepository.findById(1L))
                .thenReturn(java.util.Optional.of(getContas().get(0)));

        ContaEntity conta = contaService.buscarContaPorId(1L);

        assertNotNull(conta);
        assertEquals(conta.getId(), 1L);
        assertEquals(conta.getValor(), getContas().get(0).getValor());
        assertEquals(conta.getDescricao(), getContas().get(0).getDescricao());
        assertEquals(conta.getDataVencimento(), getContas().get(0).getDataVencimento());
        assertEquals(conta.getSituacao(), SituacaoConta.PENDENTE);
    }

    @Test
    void buscarContaPorIdComErro() {
        when(contaRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        try {
            contaService.buscarContaPorId(1L);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Conta não encontrada");
        }
    }

    @Test
    void testarImportarContas() throws IOException {
        String csvContent = "descricao,valor,dataVencimento\n"
                            + "Conta 1,100.0,2024-09-25\n"
                            + "Conta 2,200.0,2024-09-26\n";

        MockMultipartFile fileCsv = new MockMultipartFile(
                "file",
                "contas.csv",
                "text/csv",
                new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8))
        );

        List<ContaEntity> contasCsv = new ArrayList<>();
        contasCsv.add(new ContaEntity()
                .setDescricao("Conta 1")
                .setValor(BigDecimal.valueOf(100.0))
                .setDataVencimento(LocalDate.of(2024, 9, 25)));
        contasCsv.add(new ContaEntity()
                .setDescricao("Conta 2")
                .setValor(BigDecimal.valueOf(200.0))
                .setDataVencimento(LocalDate.of(2024, 9, 26)));

        when(contaRepository.saveAll(anyList())).thenReturn(contasCsv);

        List<ContaEntity> contaEntities = contaService.importarContas(fileCsv);

        assertEquals(2, contaEntities.size());
        assertEquals(contasCsv.get(0).getDescricao(), contaEntities.get(0).getDescricao());
        assertEquals(contasCsv.get(0).getValor(), contaEntities.get(0).getValor());
        assertEquals(contasCsv.get(0).getDataVencimento(), contaEntities.get(0).getDataVencimento());
        assertEquals(contasCsv.get(1).getDescricao(), contaEntities.get(1).getDescricao());
        assertEquals(contasCsv.get(1).getValor(), contaEntities.get(1).getValor());
        assertEquals(contasCsv.get(1).getDataVencimento(), contaEntities.get(1).getDataVencimento());
    }

    @Test
    void testarImportarContasComErro() throws IOException {
        String csvContent = "descricao,valor,dataVencimento\n"
                            + "Conta 1,100.0,2024-09-25\n"
                            + "Conta 2,200.0,2024-09-26\n";

        MockMultipartFile fileCsv = new MockMultipartFile(
                "file",
                "contas.csv",
                "text/csv",
                new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8))
        );

        when(contaRepository.saveAll(anyList())).thenThrow(new RuntimeException("Erro ao importar CSV"));

        try {
            contaService.importarContas(fileCsv);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Erro ao importar CSV");
        }
    }
}
