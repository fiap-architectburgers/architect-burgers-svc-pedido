package com.example.fiap.archburgers.domain.usecaseparam;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CadastrarClienteParamTest {

    @Test
    void testFromMap_AllFields() {
        Map<String, String> testMap = new HashMap<>();
        testMap.put("nome", "Test Name");
        testMap.put("cpf", "Test CPF");
        testMap.put("email", "Test Email");
        testMap.put("senha", "Test Password");

        CadastrarClienteParam param = CadastrarClienteParam.fromMap(testMap);

        assertEquals("Test Name", param.nome());
        assertEquals("Test CPF", param.cpf());
        assertEquals("Test Email", param.email());
        assertEquals("Test Password", param.senha());
    }

    @Test
    void testFromMap_MissingFields() {
        Map<String, String> testMap = new HashMap<>();
        testMap.put("nome", "Test Name");
        testMap.put("cpf", "Test CPF");

        CadastrarClienteParam param = CadastrarClienteParam.fromMap(testMap);

        assertEquals("Test Name", param.nome());
        assertEquals("Test CPF", param.cpf());
        assertNull(param.email());
        assertNull(param.senha());
    }

    @Test
    void testFromMap_EmptyMap() {
        Map<String, String> testMap = new HashMap<>();

        CadastrarClienteParam param = CadastrarClienteParam.fromMap(testMap);

        assertNull(param.nome());
        assertNull(param.cpf());
        assertNull(param.email());
        assertNull(param.senha());
    }

    @Test
    void testEqualsAndHashCode() {
        CadastrarClienteParam param1 = new CadastrarClienteParam("Test Name", "Test CPF", "Test Email", "Test Password");
        CadastrarClienteParam param2 = new CadastrarClienteParam("Test Name", "Test CPF", "Test Email", "Test Password");

        assertThat(param1).isEqualTo(param2);
        assertThat(param1.hashCode()).isEqualTo(param2.hashCode());
    }
}