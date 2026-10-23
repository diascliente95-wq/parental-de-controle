# Guardião Família — app Android nativo (fase 1)

Esta pasta contém o início de um app Android nativo separado do site publicado. O site da raiz (`index.html`) foi preservado.

## O que esta fase faz
- Abre as configurações oficiais do Android para o responsável conceder **Acesso ao uso**.
- Lê estatísticas de tempo em primeiro plano por aplicativo com `UsageStatsManager`.
- Permite configurar um limite diário e mostra um aviso dentro do app quando o total registrado chega ao limite.
- Guarda o limite localmente no aparelho.

## O que ainda NÃO faz
- Não bloqueia nem encerra outros aplicativos.
- Não envia alertas remotos nem sincroniza com o celular do responsável.
- Não coleta localização, mensagens, câmera, microfone ou conteúdo de tela.
- Os números podem variar por versão/fabricante e dependem da permissão especial.

## Desenvolver usando somente o celular
1. Instale um ambiente Android compatível com Kotlin/Gradle, como AndroidIDE, apenas de uma fonte confiável.
2. Abra o repositório no GitHub pelo navegador e use **Code → Download ZIP**.
3. Extraia o ZIP no armazenamento do celular.
4. Abra/import o diretório `android` no IDE.
5. Se o IDE pedir Gradle Wrapper, use a ferramenta do próprio IDE para configurar um wrapper compatível com Android Gradle Plugin 8.7.3.
6. Aguarde a sincronização. Se necessário, instale Android SDK 35 no gerenciador do IDE.
7. Execute o app em um aparelho de teste ou use Build para gerar um APK de depuração.
8. Abra o app e toque em **Configurar acesso ao uso**. Nas configurações, encontre Guardião Família, permita o acesso e volte ao app.
9. Compare os resultados com Bem-estar digital. Os números podem diferir entre fabricantes.

## Preservação do site
O app está dentro de `android/`; não substitui o site `index.html` nem exige alterar a publicação Vercel/GitHub Pages.

## Próxima fase
Para bloquear apps, será preciso implementar um serviço de acessibilidade transparente, ativado manualmente no aparelho acompanhado, com lista de apps escolhida pela família e opção de desligar. Para sincronizar dois aparelhos, será necessário backend HTTPS autenticado. Não coloque senhas ou chaves secretas no código público.
