package com.example.fiap.archburgers.adapters.controllers;

import com.example.fiap.archburgers.adapters.auth.UsuarioLogadoTokenParser;
import com.example.fiap.archburgers.adapters.datasource.TransactionManager;
import com.example.fiap.archburgers.adapters.dto.PedidoDto;
import com.example.fiap.archburgers.adapters.presenters.PedidoPresenter;
import com.example.fiap.archburgers.apiutils.WebUtils;
import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.usecaseparam.CriarPedidoParam;
import com.example.fiap.archburgers.domain.usecases.PedidoUseCases;
import com.example.fiap.archburgers.domain.utils.StringUtils;
import com.example.fiap.archburgers.domain.valueobjects.PedidoDetalhe;
import com.example.fiap.archburgers.domain.valueobjects.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PedidoApiHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoApiHandler.class);

    private final PedidoUseCases pedidoUseCases;
    private final UsuarioLogadoTokenParser usuarioLogadoTokenParser;
    private final TransactionManager transactionManager;

    @Autowired
    public PedidoApiHandler(PedidoUseCases pedidoUseCases,
                            UsuarioLogadoTokenParser usuarioLogadoTokenParser,
                            TransactionManager transactionManager) {
        this.pedidoUseCases = pedidoUseCases;
        this.usuarioLogadoTokenParser = usuarioLogadoTokenParser;
        this.transactionManager = transactionManager;
    }

    @Operation(summary = "Cria um pedido a partir do carrinho informado",
            description = "O pedido recebe todos os itens do carrinho, e após a criação do pedido o carrinho é excluído")
    @PostMapping(path = "/pedidos")
    public ResponseEntity<PedidoDto> criarPedido(
            @RequestHeader HttpHeaders headers,
            @RequestBody CriarPedidoParam param) {

        PedidoDetalhe pedido;
        try {
            UsuarioLogado usuarioLogado = usuarioLogadoTokenParser.verificarUsuarioLogado(headers);

            pedido = transactionManager.runInTransaction(() -> pedidoUseCases.criarPedido(param, usuarioLogado));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (DomainPermissionException dpe) {
            return WebUtils.errorResponse(HttpStatus.FORBIDDEN, dpe.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao criar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao criar pedido");
        }

        return WebUtils.okResponse(PedidoPresenter.entityToPresentationDto(pedido));
    }

    @GetMapping(path = "/pedidos/{id}")
    public ResponseEntity<PedidoDto> getPedido(@PathVariable("id") Integer id) {
        try {
            PedidoDetalhe pedido = pedidoUseCases.getPedido(id);

            if (pedido == null) {
                return WebUtils.errorResponse(HttpStatus.NOT_FOUND, "Pedido " + id + " nao encontrado");
            }

            return WebUtils.okResponse(PedidoPresenter.entityToPresentationDto(pedido));
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao obter pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao obter pedido");
        }
    }

    @Operation(summary = "Lista os pedidos conforme critério informado",
            description = "Se não for informado nenhum dos filtros será feita busca de todos os pedidos ativos (excluindo Finalizado e Cancelado) seguindo ordenação padrão",
            parameters = {
                    @Parameter(name = "status", description = "Filtra por um status de pedido específico"),
                    @Parameter(name = "atraso (boolean)", description = "Filtra pedidos em atraso (RECEBIDO ou EM PREPARAÇÃO criados há mais de 20 minutos)")
            })
    @GetMapping(path = "/pedidos")
    public ResponseEntity<List<PedidoDto>> listarPedidos(
            @RequestParam(value = "status", required = false) String filtroStatus,
            @RequestParam(value = "atraso", required = false) String filtroAtraso) {
        List<PedidoDetalhe> result;
        try {
            StatusPedido parsedFiltroStatus = StringUtils.isEmpty(filtroStatus)
                    ? null : StatusPedido.valueOf(filtroStatus);

            boolean isFiltroAtraso = Boolean.parseBoolean(filtroAtraso);

            if (isFiltroAtraso) {
                result = pedidoUseCases.listarPedidosComAtraso();
            } else if (parsedFiltroStatus != null) {
                result = pedidoUseCases.listarPedidosByStatus(parsedFiltroStatus);
            } else {
                result = pedidoUseCases.listarPedidosAtivos();
            }

        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao listar pedidos: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao listar pedidos");
        }

        return WebUtils.okResponse(result.stream().map(PedidoPresenter::entityToPresentationDto).toList());
    }

    @PostMapping(path = "/pedidos/{idPedido}/validar")
    public ResponseEntity<PedidoDto> validarPedido(@PathVariable("idPedido") Integer idPedido) {
        PedidoDetalhe pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoUseCases.validarPedido(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoPresenter.entityToPresentationDto(pedido));
    }

    @PostMapping(path = "/pedidos/{idPedido}/cancelar")
    public ResponseEntity<PedidoDto> cancelarPedido(@PathVariable("idPedido") Integer idPedido) {
        PedidoDetalhe pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoUseCases.cancelarPedido(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoPresenter.entityToPresentationDto(pedido));
    }

    @PostMapping(path = "/pedidos/{idPedido}/setPronto")
    public ResponseEntity<PedidoDto> setPedidoPronto(@PathVariable("idPedido") Integer idPedido) {
        PedidoDetalhe pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoUseCases.setPronto(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoPresenter.entityToPresentationDto(pedido));
    }

    @PostMapping(path = "/pedidos/{idPedido}/finalizar")
    public ResponseEntity<PedidoDto> finalizarPedido(@PathVariable("idPedido") Integer idPedido) {
        PedidoDetalhe pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoUseCases.finalizarPedido(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoPresenter.entityToPresentationDto(pedido));
    }
}
