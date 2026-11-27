# Devolutiva da Entrega 2 — Mentalistas

## Feedback

Há divergências do contrato no ciclo de reserva, na ordenação do histórico, na exceção para data inválida e na persistência. Transformem os cenários que falharam em testes próprios, concluam a persistência configurável e registrem as decisões de design.

## Checklist de ajustes sugeridos

- [ ] Implementar `buscarPorCategoria` com validação da entrada, comparação sem distinguir maiúsculas de minúsculas e ordenação por título.
- [ ] Na devolução, separar o exemplar para a primeira reserva que ainda espera um exemplar e lançar `DadosInvalidosException` se a data for anterior à do empréstimo.
- [ ] Ordenar o histórico de empréstimos do mais recente para o mais antigo.
- [ ] Usar a propriedade `biblioteca.dados`, esvaziar o estado ao carregar um diretório ausente e preservar a memória se a carga falhar.
- [ ] Criar a entidade `Reserva`, tornar as identidades imutáveis e encapsular as coleções.
- [ ] Criar `docs/decisoes.md` e registrar revisões substantivas nos pull requests.
