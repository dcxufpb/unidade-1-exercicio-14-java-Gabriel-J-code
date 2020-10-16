package com.example.project;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;  

public class Venda {
    private Loja loja;
    private Calendar DataHora;
    private String ccf;
    private String coo;
    private ArrayList<ItemVenda> itens;

    public Loja getLoja() {
        return loja;
    }
    public Calendar getDataHora() {
        return DataHora;
    }
    public String getCoo() {
        return coo;
    }
    public String getCcf() {
        return ccf;
    }
    public ArrayList<ItemVenda> getItens(){
        return itens;
    }

    public Venda(Loja loja, Calendar DataHora, String ccf, String coo) {
        this.loja = loja;
        this.DataHora = DataHora;
        this.ccf = ccf;
        this.coo = coo;
        this.itens = new ArrayList<ItemVenda>();        
    }

    public String dadosVenda() {
        this.validarCamposObrigatorios();

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 

        String _datatime = dtf.format(this.DataHora.getTime());

        String dados = String.format("%sV CCF:%s COO:%s",_datatime,this.getCcf(),this.getCoo());        
        return dados;
    }
    
    private void validarCamposObrigatorios() {
        if (isEmpty(this.ccf)){
            throw new RuntimeException("O campo ccf da venda não é valido");
        }
        if (isEmpty(this.coo)){
            throw new RuntimeException("O campo coo da venda não é valido");
        }        
    }

    private static boolean isEmpty(String s){
		if (s == null) return true;
		if (s.equals("")) return true;
		return false;
	}

    public void adicionar_item(int item, Produto produto,int quantidade){
		validar_item(produto, quantidade);		
		ItemVenda item_venda = new ItemVenda(this, item, produto, quantidade);
        itens.add(item_venda);
    }

	private void validar_item(Produto produto, int quantidade){
		//Venda com dois itens diferentes apontando para o mesmo produto
		for (ItemVenda i : itens){
			if (produto == i.getProduto()){
                throw new RuntimeException("A venda ja possui um item com o produto");
            }
        }
		//Item de Venda com quantidade zero ou negativa - não pode ser adicionado na venda
		if (quantidade <= 0){
            throw new RuntimeException("Itens com quantidade invalida (0 ou negativa)");
        }
		//Produto com valor unitário zero ou negativo - item não pode ser adicionado na venda com produto nesse estado
		if (produto.getValor_unitario() <= 0){
            throw new RuntimeException("Produto com valor invalido (0 ou negativo)");
        }
    }
            
    public String dados_itens(){
        if (itens.size() == 0){
            throw new RuntimeException("Não há itens na venda para que possa ser impressa");
        }
        String BREAK = System.lineSeparator();       
        StringBuilder dados = new StringBuilder();
        dados.append("ITEM CODIGO DESCRICAO QTD UN VL UNIT(R$) ST VL ITEM(R$)");
        for (ItemVenda item_linha : itens){
            Double valor_item = item_linha.getQuantidade() * item_linha.getProduto().getValor_unitario();
            Produto p = item_linha.getProduto();
            String linha = String.format("%d %d %s %d %s %.2f %s %.2f",item_linha.getItem(),p.getCodigo(),p.getDescricao(),item_linha.getQuantidade(),p.getUnidade(),p.getValor_unitario(),p.getSubstituicao_tributaria(),valor_item);
            dados.append(BREAK + linha);
        }
        return dados.toString();
    }

    public double calcular_total(){
        Double totais = 0.0;
        for (ItemVenda item_linha : itens){
            totais += (item_linha.getQuantidade() * item_linha.getProduto().getValor_unitario());
        }
        return totais;
    }

    public String imprimir_cupom(){	
        String BREAK = System.lineSeparator();
        StringBuilder cupom = new StringBuilder();	
        cupom.append(loja.dadosLoja()+ BREAK);
        cupom.append("------------------------------"+ BREAK);
        cupom.append(dadosVenda()+ BREAK);
        cupom.append("   CUPOM FISCAL"+ BREAK);
        cupom.append(dados_itens()+ BREAK);
        cupom.append("------------------------------"+ BREAK);
        cupom.append( String.format("TOTAL R$ %.2f",calcular_total()));
        return cupom.toString();
    }
}
