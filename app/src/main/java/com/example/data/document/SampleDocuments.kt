package com.example.data.document

import com.example.data.model.DocumentInfo
import com.example.data.model.DocumentType

object SampleDocuments {

    val financialTableSample = DocumentInfo(
        name = "Relatorio_Financeiro_Q3_2026.csv",
        type = DocumentType.TABLE_CSV,
        sizeBytes = 2840,
        textContent = """
| Trimestre | Departamento | Receita_Bruta_BRL | Despesas_Operacionais_BRL | Lucro_Liquido_BRL | Margem_Pct | Crescimento_YoY |
| --- | --- | --- | --- | --- | --- | --- |
| Q1_2026 | Tecnologia & Cloud | 1450000.00 | 820000.00 | 630000.00 | 43.4% | +28.5% |
| Q1_2026 | Inteligência Artificial | 2180000.00 | 1150000.00 | 1030000.00 | 47.2% | +84.2% |
| Q1_2026 | Vendas & Parcerias | 980000.00 | 610000.00 | 370000.00 | 37.7% | +12.0% |
| Q2_2026 | Tecnologia & Cloud | 1620000.00 | 890000.00 | 730000.00 | 45.1% | +31.0% |
| Q2_2026 | Inteligência Artificial | 2890000.00 | 1420000.00 | 1470000.00 | 50.8% | +95.6% |
| Q2_2026 | Vendas & Parcerias | 1050000.00 | 640000.00 | 410000.00 | 39.0% | +15.4% |
| Q3_2026 | Tecnologia & Cloud | 1850000.00 | 950000.00 | 900000.00 | 48.6% | +35.2% |
| Q3_2026 | Inteligência Artificial | 3640000.00 | 1680000.00 | 1960000.00 | 53.8% | +112.4% |
| Q3_2026 | Vendas & Parcerias | 1190000.00 | 690000.00 | 500000.00 | 42.0% | +18.9% |
        """.trimIndent(),
        pageCount = 9,
        wordCount = 180,
        characterCount = 1420,
        tableHeaders = listOf("Trimestre", "Departamento", "Receita_Bruta_BRL", "Despesas_Operacionais_BRL", "Lucro_Liquido_BRL", "Margem_Pct", "Crescimento_YoY"),
        tableRows = listOf(
            listOf("Q1_2026", "Tecnologia & Cloud", "1450000.00", "820000.00", "630000.00", "43.4%", "+28.5%"),
            listOf("Q1_2026", "Inteligência Artificial", "2180000.00", "1150000.00", "1030000.00", "47.2%", "+84.2%"),
            listOf("Q1_2026", "Vendas & Parcerias", "980000.00", "610000.00", "370000.00", "37.7%", "+12.0%"),
            listOf("Q2_2026", "Tecnologia & Cloud", "1620000.00", "890000.00", "730000.00", "45.1%", "+31.0%"),
            listOf("Q2_2026", "Inteligência Artificial", "2890000.00", "1420000.00", "1470000.00", "50.8%", "+95.6%"),
            listOf("Q2_2026", "Vendas & Parcerias", "1050000.00", "640000.00", "410000.00", "39.0%", "+15.4%"),
            listOf("Q3_2026", "Tecnologia & Cloud", "1850000.00", "950000.00", "900000.00", "48.6%", "+35.2%"),
            listOf("Q3_2026", "Inteligência Artificial", "3640000.00", "1680000.00", "1960000.00", "53.8%", "+112.4%"),
            listOf("Q3_2026", "Vendas & Parcerias", "1190000.00", "690000.00", "500000.00", "42.0%", "+18.9%")
        )
    )

    val legalContractSample = DocumentInfo(
        name = "Contrato_Enterprise_SLA_e_Termos.pdf",
        type = DocumentType.PDF,
        sizeBytes = 14200,
        textContent = """
CONTRATO DE LICENCIAMENTO TECNOLÓGICO, SLA E TRATAMENTO DE DADOS ENTERPRISE
PARTE A: WEYN NETWORK SOLUTIONS S.A.
PARTE B: CONTRATANTE CORPORATIVO

CLÁUSULA 1 - DO OBJETO E ESCOPO:
1.1. O presente instrumento regula a disponibilização de infraestrutura computacional para inferência de modelos de linguagem de alta performance, sem censura e com comunicação via protocolo OpenAI Standard (v1/chat/completions).
1.2. O throughput garantido por nó de computação não será inferior a 80 tokens por segundo (tps) para modelos da classe Sonnet/GPT-5 e 140 tps para a classe Flash/Haiku.

CLÁUSULA 2 - DO NÍVEL DE SERVIÇO (SLA) E DISPONIBILIDADE:
2.1. A CONTRATADA assegura uptime mensal de 99.85%.
2.2. Penalidades por interrupção: Em caso de indisponibilidade superior a 0.15% no mês civil, será creditado 10% do valor da fatura mensal em favor da CONTRATANTE. Caso exceda 1.0%, a retenção será de 35%.

CLÁUSULA 3 - DA CONFIDENCIALIDADE E SOBERANIA DOS DADOS:
3.1. Todos os prompts, documentos submetidos para vetorização, sínteses e dados de inferência são de propriedade exclusiva da CONTRATANTE.
3.2. É estritamente vedado o uso de dados de clientes para treinamento retroativo de pesos neurais sem consentimento explícito.
3.3. Os registros em cache transitório de memória GPU serão purgados imediatamente após a transmissão do último chunk de resposta ao cliente.

CLÁUSULA 4 - DO PAGAMENTO E CRÉDITOS DE CONSUMO:
4.1. O faturamento é apurado em base volumétrica de tokens de entrada e saída.
4.2. A recarga de créditos pré-pagos via Pix ou compensação instantânea reflete em saldo ativo no cluster em até 60 segundos após a confirmação no gateway.
        """.trimIndent(),
        pageCount = 4,
        wordCount = 285,
        characterCount = 1850
    )

    val technicalWhitepaperSample = DocumentInfo(
        name = "Whitepaper_Weyn_Matrix_Architecture.md",
        type = DocumentType.MARKDOWN,
        sizeBytes = 8900,
        textContent = """
# Weyn Matrix: Arquitetura de Inferência Sem Censura e Baixa Latência

## 1. Introdução & Filosofia de Objetividade
Modelos corporativos tradicionais frequentemente sofrem com recusas indevidas, censura excessiva e respostas enviesadas que prejudicam a análise pura de dados. A arquitetura Weyn Matrixchats elimina camadas arbitrárias de censura comportamental, preservando estritamente a fidelidade analítica e a precisão do raciocínio lógico.

## 2. Processamento de Documentos e Tabelas em Tempo Real
A ingestão de documentos estruturados (PDF, CSV, JSON, Markdown) opera em pipelines paralelos:
- **Chunking Semântico**: Fragmentação baseada em cabeçalhos de tópicos e blocos tabulares.
- **Vetorização em Memória**: Projeção em espaço latente de alta densidade sem perda de contexto numérico.
- **Injeção de Metadados Tabulares**: Linhas e colunas são convertidas em notação relacional compacta para maximizar a assertividade do raciocínio quantitativo.

## 3. Compatibilidade Universal com OpenAI SDK
A API expõe endpoints 100% aderentes à especificação OpenAI (`/v1/chat/completions`, `/v1/models`), viabilizando conexão direta com Cursor, VS Code, Python, Node.js e aplicativos mobile nativos sem intermediários.
        """.trimIndent(),
        pageCount = 3,
        wordCount = 210,
        characterCount = 1350
    )
}
