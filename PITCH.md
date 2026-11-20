# Roteiro do Pitch (≤ 5 min) – CitizenTech

**0:00 – Problema (45 s)** Dado real: o Brasil tem mais de 32 milhões de pessoas com 60+ anos (IBGE, Censo 2022) e quedas são uma das principais causas de internação de idosos. Muitos passam horas sozinhos e o socorro demora. Contar uma situação: "Dona Helena cai às 14h e a família só descobre à noite."

**0:45 – Solução (45 s)** Dispositivo em casa (botão de emergência, sensor na caixa de remédios, sensor de movimento) → API (Java/Spring Boot) → app do cuidador recebe o alerta na hora. Mostrar o diagrama de arquitetura do repositório.

**1:30 – Demo (90 s)** No Swagger: login (`admin`) → Authorize → `POST /api/eventos` (BOTAO_EMERGENCIA) → `GET /api/eventos?status=PENDENTE` → `GET /api/cuidadores/1/resumo` (pendentes subiu) → `PATCH /resolver` → resumo atualiza. Mostrar rapidamente 401 sem token e 403 no DELETE com `cuidador`.

**3:00 – Modelo de negócio (60 s)** Kit do dispositivo + assinatura mensal para famílias; plano para planos de saúde e cuidadores profissionais; parcerias com prefeituras/UBS.

**4:00 – Impacto (60 s)** Menos tempo até o socorro, adesão ao tratamento (remédios), tranquilidade da família. ODS 3 e 11. Próximos passos: push notification, geolocalização, integração com SAMU.
