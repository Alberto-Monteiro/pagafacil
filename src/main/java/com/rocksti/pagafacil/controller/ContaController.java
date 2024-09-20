package com.rocksti.pagafacil.controller;

import com.rocksti.pagafacil.dto.filtro.FiltroPesquisaConta;
import com.rocksti.pagafacil.dto.request.ContaRequest;
import com.rocksti.pagafacil.entity.ContaEntity;
import com.rocksti.pagafacil.enumeration.SituacaoConta;
import com.rocksti.pagafacil.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    @PostMapping("/cadastrar")
    public ResponseEntity<ContaEntity> cadastrarConta(@RequestBody ContaRequest contaRequest) {
        return ResponseEntity.ok(contaService.cadastrarConta(contaRequest));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ContaEntity> atualizarConta(@PathVariable Long id, @RequestBody ContaRequest contaRequest) {
        return ResponseEntity.ok(contaService.atualizarConta(id, contaRequest));
    }

    @PatchMapping("/alterar-situacao/{id}")
    public ResponseEntity<ContaEntity> alterarSituacaoConta(@PathVariable Long id, @RequestParam SituacaoConta situacao) {
        return ResponseEntity.ok(contaService.alterarSituacaoConta(id, situacao));
    }

    @GetMapping("/buscar-contas-a-pagar")
    public ResponseEntity<Page<ContaEntity>> buscarContasAPagar(Pageable pageable, FiltroPesquisaConta filtro) {
        return ResponseEntity.ok(contaService.buscarContasAPagar(pageable, filtro));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ContaEntity> buscarContaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarContaPorId(id));
    }

    @GetMapping("/valor-total-pago")
    public ResponseEntity<Map<String, BigDecimal>> obterValorTotalPagoPorPeriodo(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return ResponseEntity.ok(contaService.obterValorTotalPagoPorPeriodo(dataInicio, dataFim));
    }

    @PostMapping("/importar-csv")
    public ResponseEntity<Void> importarContas(@RequestParam("arquivo") MultipartFile arquivo) {
        contaService.importarContas(arquivo);

        return ResponseEntity.ok().build();
    }
}
