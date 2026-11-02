# E-mail real pelo plano gratuito do Brevo

Implementamos o adapter HTTP BrevoEmail. O perfil padrão mantém os envios simulados; o perfil email-real usa a API do Brevo. O destinatário é buscado pelo nome do vencedor no cadastro JSON. Não usamos endereço fixo para todos os vencedores.

1. Criar sua conta Free em https://www.brevo.com/ e verificar o remetente no painel.
2. Gerar uma chave de API e defini-la localmente, sem enviar ao chat ou ao Git.
3. No mesmo terminal PowerShell que executará Java, definir:

```powershell
$env:BREVO_API_KEY = Read-Host 'Chave de API Brevo' -MaskInput
$env:BREVO_REMETENTE = 'seu-remetente-verificado@exemplo.com'
$env:BREVO_DESTINATARIOS = '{"Ana":"email-real-da-ana@exemplo.com"}'
.\mvnw.cmd clean install
java -jar target/a3-jogos-1.0.0.jar --server.port=8083 --spring.profiles.active=email-real
```

Read-Host -MaskInput requer PowerShell 7. Não salvar a chave em scripts compartilhados. Para cada vencedor de teste, preencher seu contato real autorizado no JSON. Ausência de contato causa erro, não envio para outra pessoa. Após usar, remover a variável da sessão.

O plano Free anunciado permite 300 e-mails por dia; cadastros e remetentes dependem da aprovação do Brevo. Não contratar plano pago nem comprar créditos para este laboratório. Os testes automatizados interceptam HTTP localmente e não consomem quota. Aceitação HTTP pelo provedor não comprova recebimento na caixa de entrada.

SMS agora possui adapter Twilio no perfil sms-trial. Não há integração SMS permanentemente gratuita confirmada. Nenhuma conta foi criada ou mensagem real enviada nesta configuração.

Limitações: sem outbox/retry persistente; um erro de envio ocorre após o commit do banco. O relatório PDF anterior descreve a versão simulada; este documento registra a nova integração. Não usar a API didática exposta à internet.

Referências: https://developers.brevo.com/reference/send-transac-email e https://help.brevo.com/hc/en-us/articles/208589409-About-Brevo-s-pricing-plans


## SMS Twilio somente em Trial

Criar a conta Trial no console Twilio, confirmar se o país é elegível, verificar o telefone de destino e consultar o exemplo de SMS permitido pelo painel. Não fazer upgrade nem comprar créditos. A documentação atual limita o trial a templates predefinidos: o SMS real de demonstração não contém o aviso personalizado de vitória. A regra de vitória continua testada com mocks. Não apresentamos este teste de transporte como entrega real do aviso de vitória.

No PowerShell 7, preencher com dados da sua conta (não enviar ao chat):
```powershell
$env:TWILIO_ACCOUNT_SID = Read-Host 'Account SID'
$env:TWILIO_AUTH_TOKEN = Read-Host 'Auth Token' -MaskInput
$env:TWILIO_FROM = Read-Host 'Remetente Trial indicado no console'
$env:TWILIO_DESTINATARIOS = '{"Ana":"+SEU_NUMERO_VERIFICADO"}'
$env:TWILIO_TRIAL_MESSAGE = Read-Host 'Texto exato do exemplo permitido pela Twilio'
java -jar target/a3-jogos-1.0.0.jar --server.port=8083 --spring.profiles.active=email-real,sms-trial
```
Definir também as três variáveis Brevo acima para ativar ambos os canais. Sem perfis, continua a simulação. Sem as credenciais obrigatórias, o perfil real não inicia. Antes de cada POST de SMS, consultamos a conta e exigimos type=Trial; conta Full ou erro de consulta bloqueia o envio. Não fazemos upgrades, compras ou retries automáticos. Essa checagem não substitui limites e condições aplicados pela Twilio.

A aplicação só chama os adapters depois do commit. Como o envio é sequencial, falha em SMS impede a etapa de e-mail. HTTP de aceitação não prova entrega ao telefone; conferir o status no console. Identificar vencedores pelo nome é uma simplificação didática; nomes devem ser únicos no cadastro de contatos.

Documentação consultada: https://www.twilio.com/docs/usage/trials e https://www.twilio.com/docs/iam/api/account . Não foi feita validação ponta a ponta com conta real: faltam cadastro, credenciais, remetente e destinatário verificado.
