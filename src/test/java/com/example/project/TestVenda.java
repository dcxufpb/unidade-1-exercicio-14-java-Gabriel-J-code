package com.example.project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestVenda {

    private void rodarTestarRetorno(String expected, Venda venda) {

		// action
		String retorno = venda.dadosVenda();

		// assertion
		assertEquals(expected, retorno);
	}

	private void verificarCampoObrigatorio(String mensagemEsperada, Venda venda) {
		try {
			venda.dadosVenda();
		} catch (RuntimeException e) {
			assertEquals(mensagemEsperada, e.getMessage());
		}
	}
	private String NOME_LOJA = "Loja 1";
	private String LOGRADOURO = "Log 1";
	private int NUMERO = 10;
	private String COMPLEMENTO = "C1";
	private String BAIRRO = "Bai 1";
	private String MUNICIPIO = "Mun 1";
	private String ESTADO = "E1";
	private String CEP = "11111-111";
	private String TELEFONE = "(11) 1111-1111";
	private String OBSERVACAO = "Obs 1";
	private String CNPJ = "11.111.111/1111-11";
	private String INSCRICAO_ESTADUAL = "123456789";

	Loja LOJA_COMPLETA = new Loja(NOME_LOJA,
				new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, MUNICIPIO, ESTADO, CEP), TELEFONE, OBSERVACAO,
				CNPJ, INSCRICAO_ESTADUAL);

	//testes venda

	Calendar datahora = new GregorianCalendar(2015,10-1,29,11,9,47);
	int ccf = 21784;
	int coo = 35804;	

	//venda
	String TEXTO_ESPERADO_VENDA = "29/10/2015 11:09:47V CCF:021784 COO:035804";
	@Test

	public void test_venda(){
		rodarTestarRetorno(TEXTO_ESPERADO_VENDA,LOJA_COMPLETA.vender(datahora,ccf,coo));		
	}
	
	//venda sem ccf
	@Test
	public void test_venda_valida_ccf(){
		Venda VENDA_CCF_0 = LOJA_COMPLETA.vender(datahora,0,coo);
		verificarCampoObrigatorio("O campo ccf da venda não é valido", VENDA_CCF_0);
	}
	//venda sem coo
	@Test
	public void test_venda_valida_coo(){
		Venda VENDA_COO_0 = LOJA_COMPLETA.vender(datahora,ccf,0);
		verificarCampoObrigatorio("O campo coo da venda não é valido", VENDA_COO_0);
    }
    
    //tests dados itens
    public void verifica_campo_obrigatorio_itens_venda(String mensagemEsperada, Venda venda){
        try {
			venda.dados_itens();
		} catch (RuntimeException e) {
			assertEquals(mensagemEsperada, e.getMessage());
		}        
    }

    private String BREAK = System.lineSeparator();

    Produto produto1 = new Produto(123456, "Produto1", "kg", 4.35, "");
    Produto produto2 = new Produto(234567, "Produto2", "m", 1.01, "");

    String TEXTO_ESPERADO_DADOS_ITEM_fUNCIONAL = "ITEM CODIGO DESCRICAO QTD UN VL UNIT(R$) ST VL ITEM(R$)" + BREAK +
    "1 123456 Produto1 2 kg 4.35  8.70" + BREAK +
    "2 234567 Produto2 4 m 1.01  4.04";    

    //dados_itens
    @Test
    public void test_itens_venda(){
        Venda venda = LOJA_COMPLETA.vender(datahora,ccf,coo);
        venda.adicionar_item(1,produto1,2);
        venda.adicionar_item(2,produto2,4);
        assertEquals(TEXTO_ESPERADO_DADOS_ITEM_fUNCIONAL, venda.dados_itens());
    }

    //Venda sem itens - o cupom fiscal não pode ser impresso
    @Test
    public void test_venda_sem_itens(){
        Venda VENDA_SEM_ITENS = LOJA_COMPLETA.vender(datahora,ccf,coo);
        verifica_campo_obrigatorio_itens_venda("Não há itens na venda para que possa ser impressa", VENDA_SEM_ITENS);
    }

    //Venda com dois itens diferentes apontando para o mesmo produto - lança erro ao adicionar o item com produto repetido
    @Test
    public void test_venda_2_itens_mesmo_produto(){
        Venda VENDA_2_ITENS_MESMO_PRODUTO = LOJA_COMPLETA.vender(datahora,ccf,coo);
        VENDA_2_ITENS_MESMO_PRODUTO.adicionar_item(1,produto1,2);
        try{
            VENDA_2_ITENS_MESMO_PRODUTO.adicionar_item(2,produto1,3);
        }catch (RuntimeException e) {
            assertEquals("A venda ja possui um item com o produto", e.getMessage());
        }
    }

    //Item de Venda com quantidade zero ou negativa - não pode ser adicionado na venda
    @Test
    public void test_venda_itens_quant_0(){
        Venda VendaItemQuant0 = LOJA_COMPLETA.vender(datahora,ccf,coo);
        try{
            VendaItemQuant0.adicionar_item(1,produto1,0);
        }catch (RuntimeException e) {
            assertEquals( "Itens com quantidade invalida (0 ou negativa)", e.getMessage());
        }
    }

    //Produto com valor unitário zero ou negativo - item não pode ser adicionado na venda com produto nesse estado
    @Test
    public void test_venda_iten_produto_sem_valor(){
        Venda VENDA_ITEM_PRODUTO_SEM_VALOR = LOJA_COMPLETA.vender(datahora,ccf,coo);
        Produto PRODUTO_SEM_VALOR = new Produto(000000, "Produto0", "nenhum", 0.0, "");
        try{
            VENDA_ITEM_PRODUTO_SEM_VALOR.adicionar_item(1,PRODUTO_SEM_VALOR,1);
        }catch (RuntimeException e) {
            assertEquals("Produto com valor invalido (0 ou negativo)", e.getMessage());
        }
    }

    //cupom complemento
    String TEXTO_ESPERADO_CUPOM_FISCAL_UM_ITEM = "Loja 1"+ BREAK +
    "Log 1, 10 C1"+ BREAK +
    "Bai 1 - Mun 1 - E1"+ BREAK +
    "CEP:11111-111 Tel (11) 1111-1111"+ BREAK +
    "Obs 1"+ BREAK +
    "CNPJ: 11.111.111/1111-11"+ BREAK +
    "IE: 123456789"+ BREAK +
    "------------------------------"+ BREAK +
    "29/10/2015 11:09:47V CCF:021784 COO:035804"+ BREAK +
    "   CUPOM FISCAL"+ BREAK +
    "ITEM CODIGO DESCRICAO QTD UN VL UNIT(R$) ST VL ITEM(R$)"+ BREAK +
    "1 123456 Produto1 2 kg 4.35  8.70"+ BREAK +
    "2 234567 Produto2 4 m 1.01  4.04"+ BREAK +
    "------------------------------"+ BREAK +
    "TOTAL R$ 12.74";
    @Test
    public void test_venda_imprimir_cupom(){
        Venda venda = LOJA_COMPLETA.vender(datahora,ccf,coo);
        venda.adicionar_item(1,produto1,2);
        venda.adicionar_item(2,produto2,4);
        assertEquals(TEXTO_ESPERADO_CUPOM_FISCAL_UM_ITEM, venda.imprimir_cupom());
    }
}