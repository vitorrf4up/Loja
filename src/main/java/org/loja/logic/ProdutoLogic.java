package org.loja.logic;

import org.loja.classes.MovimentoProduto;
import org.loja.classes.Produto;
import org.loja.repos.ProdutoRepo;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.stream.Stream;

public class ProdutoLogic {
    ProdutoRepo db = new ProdutoRepo();

    public boolean cadastrarProduto(Produto produto) {
        try {
            db.persistirProduto(produto);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            return false;
        }
    }

    public boolean movimentarProduto (MovimentoProduto movimento) {
        // Calcula a quantidade atual do produto
        int qntd = calcularQntdProduto(movimento.getProduto().getIdProduto());

        // Calcula quantidade de produtos que terá depois da movimentação
        if (movimento.getTipoMovimento() == MovimentoProduto.Movimentos.ENTRADA) {
            qntd += movimento.getQuantidadeMovimento();
        } else {
            qntd -= movimento.getQuantidadeMovimento();
        }

        if (qntd < 0)
            return false;

        try {
            db.persistirMovimento(movimento);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            throw e;
        }
    }

    public Produto consultarProduto (int id) {
        return db.consultarProduto(id);
    }

    public int calcularQntdProduto(int id) {
        List<MovimentoProduto> movimentos = db.consultarTodosMovimentos();

        //Filtra a lista pra somente movimentos do produto com o id enviado
        movimentos = movimentos.stream().filter(m -> m.getProduto().getIdProduto() == id).toList();

        int qntdTotal = 0;

        for (MovimentoProduto m : movimentos) {
            if (m.getTipoMovimento() == MovimentoProduto.Movimentos.ENTRADA)
                qntdTotal += m.getQuantidadeMovimento();
            else
                qntdTotal -= m.getQuantidadeMovimento();
        }

        return qntdTotal;
    }

    public List<Produto> listarProdutos() {
        return (List<Produto>) db.consultarTodosProdutos();
    }
}
