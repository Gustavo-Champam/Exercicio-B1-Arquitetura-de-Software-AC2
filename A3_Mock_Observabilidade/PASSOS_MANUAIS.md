# O que precisa ser feito manualmente

## Para demonstrar os mocks
Nenhuma conta externa é necessária. Executar o build, abrir o relatório JaCoCo e os testes de ordem/rollback. O modo padrão não envia SMS nem e-mail reais.

## Para provar recebimento de e-mail real
Criar ou acessar a conta gratuita Brevo, verificar o remetente e gerar a chave de API. Configurar as variáveis de CONFIGURAR_PROVEDORES.md somente no computador. Usar seu próprio e-mail como destinatário autorizado, executar uma requisição e conferir a mensagem recebida e o status no painel. Não enviar senhas ao chat ou versionar chaves.

## Para SMS com a escolha de usar somente trial
Criar ou acessar a conta Twilio Trial, verificar o telefone e conferir os modelos disponíveis. Configurar SID, token, remetente, destinatário e modelo conforme o guia. Confirmar recebimento no próprio telefone e guardar a evidência sem expor credenciais ou números completos.

O trial não oferece liberdade para redigir o aviso personalizado de vitória. Se a professora exigir receber exatamente esse aviso por SMS, confirmar com ela se aceita a demonstração por mocks e o teste de transporte separado. Sem essa aceitação, essa exigência permanece pendente: não contratar plano pago sem decisão expressa da equipe.

## Entrega acadêmica
Revisar as respostas em dupla e saber explicar o commit antes da notificação, o papel de mocks/stubs e as decisões arquiteturais. Enviar no Canvas os PDFs atualizados e o link direto do projeto correspondente. Nenhuma submissão foi feita automaticamente.
