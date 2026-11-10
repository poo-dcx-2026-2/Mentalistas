# Devolutiva da Entrega 1 — Mentalistas

## Nota: 7,5 de 10 (75 de 100 pontos)

Avaliei o último commit da branch `main` até 12/10/2026 às 23h59 (o prazo original, 05/10, mais uma semana de extensão), pela leitura do código, dos testes, do histórico de commits, dos pull requests e da documentação. 

## Pontos por critério

| Critério | O que é avaliado | Pontos | Nível |
|---|---|---|---|
| 4.1 | Correção funcional e aderência ao contrato | 18 de 20 | Adequado |
| 4.2 | Modelagem OO, responsabilidades, coesão e acoplamento | 18 de 25 | Adequado |
| 4.3 | Testes e evidências de depuração | 7 de 15 | Parcial |
| 4.4 | Robustez, exceções e casos-limite | 8 de 10 | Adequado |
| 4.5 | Evolução, refatoração e qualidade do código | 8 de 10 | Adequado |
| 4.6 | Git, build, pipeline e documentação | 8 de 10 | Parcial |
| 4.7 | Processo, checkpoints, explicação e autoria | 8 de 10 | Adequado |
| | **Total** | **75 de 100** | |

## Justificativa (onde tiro pontos)

- Ausência de ordenação por tombo em listarExemplares.

- Identidades continuam mutáveis, Jogo expõe a lista interna de exemplares e usuários são guardados como UsuarioView, deixando responsabilidade de domínio no serviço.

- Não encontrei um teste para o defeito plantado nem cobertura própria suficiente dos cenários exigidos.

- Exposição de setters e da coleção interna que permite alterar o estado sem passar pelas regras do acervo.

- Faltam revisão formal registrada no GitHub, imagem do UML e, no README, justificativa das coleções e relato completo do defeito.

## Checklist de correções

- [ ] **Ordenar a resposta de `listarExemplares` pelo tombo.** O tombo é o número de registro de cada exemplar. O enunciado pede os exemplares "ordenados por tombo", e hoje a lista sai na ordem de cadastro: foi o único teste oculto que falhou. Uma forma de fazer: antes do `return`, ordenar com `lista.sort(Comparator.comparing(ExemplarView::tombo))`. Para conferir, escrevam um teste que cadastra os tombos fora de ordem (por exemplo `T3`, `T1`, `T2`) e verifica que a lista volta `T1`, `T2`, `T3`.
- [ ] **Tornar a identidade das entidades imutável.** Identidade é o dado que diz *quem* o objeto é: o código do item, o tombo do exemplar, a matrícula do usuário. Se existe um `setCodigo(...)`, qualquer parte do programa pode trocar o código de um item que já está guardado num mapa, e o mapa passa a apontar para o lugar errado. Declarem esses campos como `private final`, preencham-nos só no construtor e apaguem os *setters* (métodos `setAlgo`) correspondentes.
- [ ] **Não devolver a lista interna de exemplares.** Quando um método faz `return exemplares;`, quem chama recebe a *mesma* lista que o objeto usa por dentro e pode adicionar ou apagar exemplares sem passar pelas regras (tombo repetido, item inexistente). Devolvam uma cópia que não pode ser alterada: `return List.copyOf(exemplares);` (ou `Collections.unmodifiableList(exemplares)`). Para conferir, escrevam um teste que tenta `add` na lista devolvida e espera `UnsupportedOperationException`.
- [ ] **Criar uma entidade `Usuario` própria.** Hoje os usuários ficam guardados como `UsuarioView`, que é um objeto do contrato feito só para *mostrar* dados para fora. Criem uma classe `Usuario` de domínio (matrícula `final`, nome, situação), guardem-na num `Map<String, Usuario>` e convertam para `UsuarioView` só na hora de devolver.
- [ ] **Criar um teste de regressão para o defeito investigado.** Teste de regressão é o teste que reproduz um defeito já corrigido, para que ele não volte sem ninguém perceber. Para o defeito da comparação de `String` com `==`: montem o código em tempo de execução (por exemplo `new String("L1")` ou `"L" + 1`), cadastrem o item e verifiquem que `buscarItem` o encontra e que um segundo cadastro com o mesmo código lança `RecursoDuplicadoException`. Deem ao teste um nome que conte a história, como `buscaFuncionaComCodigoMontadoEmTempoDeExecucao`.
- [ ] **Escrever testes próprios para os cenários exigidos.** Além dos testes do template, a equipe precisa de testes seus para: tombo repetido, item inexistente, entradas em branco ou nulas, e o isolamento (uma operação que falha não deixa nada pela metade).
- [ ] **Documentar os métodos públicos com Javadoc.** Javadoc é o comentário `/** ... */` logo acima do método, que descreve o que ele faz, os parâmetros (`@param`) e as exceções (`@throws`).
- [ ] **Registrar a revisão de colega no pull request.** Pull request (PR) é o pedido para juntar uma *branch* (linha de trabalho separada) na `main`. A revisão só conta como evidência se aparecer no GitHub: na aba *Files changed* do PR, o colega que **não** escreveu o código clica em *Review changes*, escreve pelo menos um comentário sobre o que leu e escolhe *Approve* ou *Request changes*. Mensagem de commit dizendo "revisado" não substitui isso.
- [ ] **Versionar a imagem do UML.** Além do `docs/modelo.puml` (o texto do diagrama), o enunciado pede a imagem gerada a partir dele (PNG ou SVG) dentro de `docs/`, adicionada ao repositório com `git add` e `commit`. Gerem a imagem pelo plugin PlantUML do IntelliJ ou pelo site do PlantUML e confiram que ela aparece no GitHub.
- [ ] **Justificar no README cada coleção escolhida.** Para cada `List`, `Set` ou `Map` do código, uma frase dizendo qual operação ela torna fácil. Exemplo: "usamos `Map<String, Item>` com o código como chave porque `buscarItem` precisa achar um item pelo código sem percorrer a lista inteira".
- [ ] **Escrever no README o relato do defeito.** Uma seção curta com: o que acontecia (o sintoma), como vocês reproduziram, qual era a causa e qual teste agora protege contra ele.
