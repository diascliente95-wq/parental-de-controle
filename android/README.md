# Guardião Família — app Android nativo (fase de teste)

Esta pasta contém o projeto Android separado do site. O arquivo raiz `index.html` não foi alterado nesta atualização.

## Recursos preparados
- Acesso às configurações oficiais para o responsável autorizar estatísticas de uso.
- Relatório estimado de tempo em primeiro plano por aplicativo.
- Limite diário configurável, usado para comparar e mostrar um aviso.
- Lista de apps lançáveis para o responsável selecionar quais deseja pausar.
- Serviço de acessibilidade com descrição visível, ativado manualmente pelo responsável. Quando um app selecionado passa para primeiro plano, o serviço mostra uma tela de pausa com opção de voltar à tela inicial.
- Preferências armazenadas localmente no aparelho; sem transmissão remota.

## Limites importantes
- O limite diário ainda não bloqueia automaticamente todos os apps quando o total é atingido. O bloqueio desta fase é por seleção de aplicativos.
- A pausa é uma barreira visual local, não um bloqueio impossível de contornar. O usuário pode sair para a tela inicial ou desativar a acessibilidade nas configurações Android.
- O relatório de uso pode variar conforme fabricante, versão do Android e modo como o sistema registra eventos.
- Ainda não existe conexão entre o telefone do responsável e o telefone acompanhado.
- O projeto foi escrito no repositório, mas precisa ser compilado e testado em um aparelho real; não há APK validado nesta atualização.

## Como testar pelo celular
1. No GitHub, abra o repositório e use **Code → Download ZIP**.
2. Extraia o ZIP e importe a pasta `android` em um IDE Android compatível com Kotlin/Gradle.
3. Configure Gradle Wrapper compatível com Android Gradle Plugin 8.7.3 e instale Android SDK 35, se necessário.
4. Compile e instale a versão de depuração em um aparelho de teste.
5. Abra Guardião Família → **Configurar acesso ao uso** → habilite a permissão para Guardião Família e volte ao app.
6. Toque em **Ativar pausa de aplicativos**. Nas configurações de Acessibilidade, leia a descrição e habilite Guardião Família apenas se concordar com a função.
7. Volte ao app, marque um aplicativo não essencial (por exemplo, um jogo de teste) e toque em **Salvar aplicativos selecionados**.
8. Abra o app selecionado e confirme se aparece a tela de pausa. Teste também o botão para voltar à tela inicial e confirme que apps essenciais não foram selecionados.
9. Desative o serviço em Configurações → Acessibilidade para interromper o bloqueio de teste.

## Transparência e privacidade
O serviço recebe eventos de mudança de janela para identificar se um app escolhido foi aberto; ele não solicita leitura do conteúdo da tela (`canRetrieveWindowContent=false)) e não captura mensagens, senhas, câmera, microfone ou localização. O responsável deve explicar o funcionamento à criança/adolescente e manter o controle sobre as permissões.

## Próxima etapa
Para sincronização remota, será necessário backend HTTPS com autenticação, vínculo explícito entre dispositivos e controles para revogar o vínculo. Para distribuição pública, revise as políticas atuais do Google Play para serviços de acessibilidade e controles parentais.
