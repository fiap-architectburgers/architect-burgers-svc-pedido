package com.example.fiap.archburgers.adapters.presenters;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;

import java.util.Map;

public class UsuarioLogadoPresenter {
    public static Map<String, Object> toMap(UsuarioLogado usuarioLogado) {
        return Map.of(
                "nome", usuarioLogado.getNome(),
                "email", usuarioLogado.getEmail(),
                "cpf", usuarioLogado.getCpf(),
                "grupo", usuarioLogado.getGrupo() != null ? usuarioLogado.getGrupo().name() : "",
                "token", usuarioLogado.identityToken()
        );
    }
}
