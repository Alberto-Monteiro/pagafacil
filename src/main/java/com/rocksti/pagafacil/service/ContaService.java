package com.rocksti.pagafacil.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.rocksti.pagafacil.dto.ContaCsvDto;
import com.rocksti.pagafacil.dto.filtro.FiltroPesquisaConta;
import com.rocksti.pagafacil.dto.request.ContaRequest;
import com.rocksti.pagafacil.entity.ContaEntity;
import com.rocksti.pagafacil.enumeration.SituacaoConta;
import com.rocksti.pagafacil.exception.BadRequestException;
import com.rocksti.pagafacil.exception.NotFoundException;
import com.rocksti.pagafacil.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ContaService {
    private static final String CONTA_NAO_ENCONTRADA = "Conta não encontrada";
    private static final String ERRO_AO_IMPORTAR_CSV = "Erro ao importar CSV";

    private final ContaRepository contaRepository;

    public ContaEntity cadastrarConta(ContaRequest contaRequest) {
        ContaEntity novaConta = new ContaEntity()
                .setSituacao(SituacaoConta.PENDENTE)
                .setValor(contaRequest.getValor())
                .setDescricao(contaRequest.getDescricao())
                .setDataVencimento(contaRequest.getDataVencimento());

        return contaRepository.save(novaConta);
    }

    public ContaEntity atualizarConta(Long id, ContaRequest contaRequest) {
        return contaRepository.findById(id)
                .map(contaExistente -> {
                    contaExistente.setValor(contaRequest.getValor());
                    contaExistente.setDescricao(contaRequest.getDescricao());
                    contaExistente.setDataVencimento(contaRequest.getDataVencimento());
                    return contaRepository.save(contaExistente);
                })
                .orElseThrow(() -> new NotFoundException(CONTA_NAO_ENCONTRADA));
    }

    public ContaEntity alterarSituacaoConta(Long id, SituacaoConta situacao) {
        return contaRepository.findById(id)
                .map(contaExistente -> {
                    contaExistente.setSituacao(situacao);
                    contaExistente.setDataPagamento(Objects.equals(situacao, SituacaoConta.PAGO)
                            ? LocalDate.now()
                            : null);
                    return contaRepository.save(contaExistente);
                })
                .orElseThrow(() -> new NotFoundException(CONTA_NAO_ENCONTRADA));
    }

    public Page<ContaEntity> buscarContasAPagar(Pageable pageable, FiltroPesquisaConta filtro) {
        return contaRepository.findByAPagarComFiltro(pageable,
                filtro.getDataVencimento(),
                filtro.getDescricao());
    }

    public ContaEntity buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CONTA_NAO_ENCONTRADA));
    }

    public Map<String, BigDecimal> obterValorTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal resultado = contaRepository.obterValorTotalPagoPorPeriodo(dataInicio, dataFim);

        return Map.of("valorTotalPago", resultado);
    }

    public List<ContaEntity> importarContas(MultipartFile fileCsv) {
        try (InputStreamReader reader = new InputStreamReader(fileCsv.getInputStream())) {
            HeaderColumnNameMappingStrategy<ContaCsvDto> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ContaCsvDto.class);

            CsvToBean<ContaCsvDto> csvToBean = new CsvToBeanBuilder<ContaCsvDto>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<ContaEntity> contas = csvToBean.parse().stream()
                    .map(contaCsvDto -> new ContaEntity()
                            .setSituacao(SituacaoConta.PENDENTE)
                            .setDescricao(contaCsvDto.getDescricao())
                            .setValor(contaCsvDto.getValor())
                            .setDataVencimento(contaCsvDto.getDataVencimento()))
                    .toList();

            return contaRepository.saveAll(contas);
        } catch (IOException e) {
            throw new BadRequestException(ERRO_AO_IMPORTAR_CSV, e);
        }
    }
}
